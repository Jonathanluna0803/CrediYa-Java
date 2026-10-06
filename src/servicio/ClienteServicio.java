package servicio;

import archivos.GestorArchivos;
import conexion.ConexionBD;
import dao.ClienteDAO;
import excepciones.CrediYaException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;
import modelo.Persona;

// Logica del modulo de clientes.
public class ClienteServicio {

    private static final String ARCHIVO = "clientes.txt";

    private List<Cliente> clientes = new ArrayList<>();
    private ClienteDAO dao = new ClienteDAO();

    public ClienteServicio() {
        cargar();
    }

    private void cargar() {
        boolean cargadoDeBD = false;
        if (ConexionBD.getInstancia().hayConexion()) {
            try {
                clientes = dao.listar();
                cargadoDeBD = true;
                guardarArchivo();
            } catch (SQLException e) {
                System.out.println("Error leyendo clientes de MySQL: " + e.getMessage());
            }
        }
        if (!cargadoDeBD) {
            for (String linea : GestorArchivos.leerLineas(ARCHIVO)) {
                try {
                    clientes.add(Cliente.desdeLinea(linea));
                } catch (Exception e) {
                    System.out.println("Linea invalida en " + ARCHIVO + ": " + linea);
                }
            }
        }
    }

    public Cliente registrar(String nombre, String documento, String correo, String telefono)
            throws CrediYaException {
        if (nombre.isEmpty() || documento.isEmpty() || telefono.isEmpty()) {
            throw new CrediYaException("Nombre, documento y telefono son obligatorios.");
        }
        if (!correo.contains("@")) {
            throw new CrediYaException("El correo no es valido.");
        }
        for (Cliente c : clientes) {
            if (c.getDocumento().equals(documento)) {
                throw new CrediYaException("Ya existe un cliente con ese documento.");
            }
        }
        Cliente nuevo = new Cliente(siguienteId(), nombre, documento, correo, telefono);
        clientes.add(nuevo);
        guardarArchivo();
        try {
            dao.guardar(nuevo);
        } catch (SQLException e) {
            System.out.println("Aviso: no se pudo guardar en MySQL: " + e.getMessage());
        }
        return nuevo;
    }

    public List<Cliente> listar() {
        return clientes;
    }

    // Devuelve el cliente o null si no existe
    public Cliente buscarPorId(int id) {
        for (Cliente c : clientes) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    private int siguienteId() {
        return clientes.stream().mapToInt(Persona::getId).max().orElse(0) + 1;
    }

    private void guardarArchivo() {
        List<String> lineas = new ArrayList<>();
        for (Cliente c : clientes) {
            lineas.add(c.aLinea());
        }
        GestorArchivos.escribirLineas(ARCHIVO, lineas);
    }
}
