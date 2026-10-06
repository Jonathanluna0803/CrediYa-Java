package servicio;

import archivos.GestorArchivos;
import conexion.ConexionBD;
import dao.PrestamoDAO;
import excepciones.CrediYaException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import modelo.Prestamo;

// Logica del modulo de prestamos.
// Necesita ClienteServicio y EmpleadoServicio para comprobar que existan.
public class PrestamoServicio {

    private static final String ARCHIVO = "prestamos.txt";

    private List<Prestamo> prestamos = new ArrayList<>();
    private PrestamoDAO dao = new PrestamoDAO();
    private ClienteServicio clientes;
    private EmpleadoServicio empleados;

    public PrestamoServicio(ClienteServicio clientes, EmpleadoServicio empleados) {
        this.clientes = clientes;
        this.empleados = empleados;
        cargar();
    }

    private void cargar() {
        boolean cargadoDeBD = false;
        if (ConexionBD.getInstancia().hayConexion()) {
            try {
                prestamos = dao.listar();
                cargadoDeBD = true;
                guardarArchivo();
            } catch (SQLException e) {
                System.out.println("Error leyendo prestamos de MySQL: " + e.getMessage());
            }
        }
        if (!cargadoDeBD) {
            for (String linea : GestorArchivos.leerLineas(ARCHIVO)) {
                try {
                    prestamos.add(Prestamo.desdeLinea(linea));
                } catch (Exception e) {
                    System.out.println("Linea invalida en " + ARCHIVO + ": " + linea);
                }
            }
        }
    }

    // Crea un prestamo nuevo asociando cliente y empleado.
    // El total, la cuota y el saldo los calcula la clase Prestamo automaticamente.
    public Prestamo crear(int clienteId, int empleadoId, double monto, double interes,
                          int cuotas, LocalDate fechaInicio) throws CrediYaException {
        if (clientes.buscarPorId(clienteId) == null) {
            throw new CrediYaException("No existe un cliente con id " + clienteId);
        }
        if (empleados.buscarPorId(empleadoId) == null) {
            throw new CrediYaException("No existe un empleado con id " + empleadoId);
        }
        if (monto <= 0) {
            throw new CrediYaException("El monto debe ser mayor que 0.");
        }
        if (interes < 0) {
            throw new CrediYaException("El interes no puede ser negativo.");
        }
        if (cuotas <= 0) {
            throw new CrediYaException("Las cuotas deben ser mayor que 0.");
        }
        int id = prestamos.stream().mapToInt(Prestamo::getId).max().orElse(0) + 1;
        Prestamo nuevo = new Prestamo(id, clienteId, empleadoId, monto, interes, cuotas,
                fechaInicio, Prestamo.PENDIENTE);
        prestamos.add(nuevo);
        guardarArchivo();
        try {
            dao.guardar(nuevo);
        } catch (SQLException e) {
            System.out.println("Aviso: no se pudo guardar en MySQL: " + e.getMessage());
        }
        return nuevo;
    }

    // Cambia el estado entre "pendiente" y "pagado" (archivo y base de datos)
    public void cambiarEstado(int id, String nuevoEstado) throws CrediYaException {
        Prestamo p = buscarPorId(id);
        if (p == null) {
            throw new CrediYaException("No existe un prestamo con id " + id);
        }
        if (!nuevoEstado.equals(Prestamo.PENDIENTE) && !nuevoEstado.equals(Prestamo.PAGADO)) {
            throw new CrediYaException("Estado invalido. Use pendiente o pagado.");
        }
        p.setEstado(nuevoEstado);
        guardarArchivo();
        try {
            dao.actualizarEstado(id, nuevoEstado);
        } catch (SQLException e) {
            System.out.println("Aviso: no se pudo actualizar en MySQL: " + e.getMessage());
        }
    }

    public List<Prestamo> listar() {
        return prestamos;
    }

    public Prestamo buscarPorId(int id) {
        for (Prestamo p : prestamos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Prestamos de un cliente: se filtra la lista con Stream y lambda
    public List<Prestamo> prestamosDeCliente(int clienteId) {
        return prestamos.stream()
                .filter(p -> p.getClienteId() == clienteId)
                .collect(Collectors.toList());
    }

    private void guardarArchivo() {
        List<String> lineas = new ArrayList<>();
        for (Prestamo p : prestamos) {
            lineas.add(p.aLinea());
        }
        GestorArchivos.escribirLineas(ARCHIVO, lineas);
    }
}
