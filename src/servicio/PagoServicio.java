package servicio;

import archivos.GestorArchivos;
import conexion.ConexionBD;
import dao.PagoDAO;
import excepciones.CrediYaException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import modelo.Pago;
import modelo.Prestamo;
import util.Formato;

// Logica del modulo de pagos: registrar abonos y consultar el historico.
public class PagoServicio {

    private static final String ARCHIVO = "pagos.txt";

    private List<Pago> pagos = new ArrayList<>();
    private PagoDAO dao = new PagoDAO();
    private PrestamoServicio prestamos;

    public PagoServicio(PrestamoServicio prestamos) {
        this.prestamos = prestamos;
        cargar();
        // Al iniciar se vuelve a sumar cada pago a su prestamo para tener el saldo correcto
        for (Pago pago : pagos) {
            Prestamo p = prestamos.buscarPorId(pago.getPrestamoId());
            if (p != null) {
                p.agregarAbono(pago.getMonto());
            }
        }
    }

    private void cargar() {
        boolean cargadoDeBD = false;
        if (ConexionBD.getInstancia().hayConexion()) {
            try {
                pagos = dao.listar();
                cargadoDeBD = true;
                guardarArchivo();
            } catch (SQLException e) {
                System.out.println("Error leyendo pagos de MySQL: " + e.getMessage());
            }
        }
        if (!cargadoDeBD) {
            for (String linea : GestorArchivos.leerLineas(ARCHIVO)) {
                try {
                    pagos.add(Pago.desdeLinea(linea));
                } catch (Exception e) {
                    System.out.println("Linea invalida en " + ARCHIVO + ": " + linea);
                }
            }
        }
    }

    // Registra un abono: valida, actualiza el saldo y guarda.
    // Si el saldo llega a 0, el prestamo pasa a "pagado".
    public Pago registrarAbono(int prestamoId, double monto) throws CrediYaException {
        Prestamo p = prestamos.buscarPorId(prestamoId);
        if (p == null) {
            throw new CrediYaException("No existe un prestamo con id " + prestamoId);
        }
        if (p.getEstado().equals(Prestamo.PAGADO)) {
            throw new CrediYaException("Ese prestamo ya esta pagado.");
        }
        if (monto <= 0) {
            throw new CrediYaException("El abono debe ser mayor que 0.");
        }
        if (monto > p.calcularSaldo()) {
            throw new CrediYaException("El abono supera el saldo pendiente (" + Formato.dinero(p.calcularSaldo()) + ").");
        }
        int id = pagos.stream().mapToInt(Pago::getId).max().orElse(0) + 1;
        Pago nuevo = new Pago(id, prestamoId, LocalDate.now(), monto);
        pagos.add(nuevo);
        p.agregarAbono(monto); // actualiza el saldo pendiente
        guardarArchivo();
        try {
            dao.guardar(nuevo);
        } catch (SQLException e) {
            System.out.println("Aviso: no se pudo guardar en MySQL: " + e.getMessage());
        }
        if (p.calcularSaldo() <= 0) {
            prestamos.cambiarEstado(prestamoId, Prestamo.PAGADO);
        }
        return nuevo;
    }

    // Historico de pagos de un prestamo
    public List<Pago> historial(int prestamoId) {
        return pagos.stream()
                .filter(pago -> pago.getPrestamoId() == prestamoId)
                .collect(Collectors.toList());
    }

    private void guardarArchivo() {
        List<String> lineas = new ArrayList<>();
        for (Pago p : pagos) {
            lineas.add(p.aLinea());
        }
        GestorArchivos.escribirLineas(ARCHIVO, lineas);
    }
}
