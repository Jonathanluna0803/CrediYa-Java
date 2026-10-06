package servicio;

import archivos.GestorArchivos;
import conexion.ConexionBD;
import dao.EmpleadoDAO;
import excepciones.CrediYaException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Empleado;
import modelo.Persona;

// Logica del modulo de empleados: validar, guardar en memoria, archivo y MySQL.
public class EmpleadoServicio {

    private static final String ARCHIVO = "empleados.txt";

    private List<Empleado> empleados = new ArrayList<>(); // lista en memoria (coleccion)
    private EmpleadoDAO dao = new EmpleadoDAO();

    public EmpleadoServicio() {
        cargar();
    }

    // Al iniciar: si hay MySQL se leen los datos de alli, si no, del archivo.
    private void cargar() {
        boolean cargadoDeBD = false;
        if (ConexionBD.getInstancia().hayConexion()) {
            try {
                empleados = dao.listar();
                cargadoDeBD = true;
                guardarArchivo(); // deja el archivo igual a la base de datos
            } catch (SQLException e) {
                System.out.println("Error leyendo empleados de MySQL: " + e.getMessage());
            }
        }
        if (!cargadoDeBD) {
            for (String linea : GestorArchivos.leerLineas(ARCHIVO)) {
                try {
                    empleados.add(Empleado.desdeLinea(linea));
                } catch (Exception e) {
                    System.out.println("Linea invalida en " + ARCHIVO + ": " + linea);
                }
            }
        }
    }

    // Valida los datos, crea el empleado y lo guarda en archivo y base de datos.
    public Empleado registrar(String nombre, String documento, String rol, String correo, double salario)
            throws CrediYaException {
        if (nombre.isEmpty() || documento.isEmpty() || rol.isEmpty()) {
            throw new CrediYaException("Nombre, documento y rol son obligatorios.");
        }
        if (!correo.contains("@")) {
            throw new CrediYaException("El correo no es valido.");
        }
        if (salario < 0) {
            throw new CrediYaException("El salario no puede ser negativo.");
        }
        for (Empleado e : empleados) {
            if (e.getDocumento().equals(documento)) {
                throw new CrediYaException("Ya existe un empleado con ese documento.");
            }
        }
        Empleado nuevo = new Empleado(siguienteId(), nombre, documento, rol, correo, salario);
        empleados.add(nuevo);
        guardarArchivo();
        try {
            dao.guardar(nuevo);
        } catch (SQLException e) {
            System.out.println("Aviso: no se pudo guardar en MySQL: " + e.getMessage());
        }
        return nuevo;
    }

    public List<Empleado> listar() {
        return empleados;
    }

    // Busca por id. Devuelve null si no existe.
    public Empleado buscarPorId(int id) {
        for (Empleado e : empleados) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    // Siguiente id = mayor id actual + 1 (usa Stream)
    private int siguienteId() {
        return empleados.stream().mapToInt(Persona::getId).max().orElse(0) + 1;
    }

    private void guardarArchivo() {
        List<String> lineas = new ArrayList<>();
        for (Empleado e : empleados) {
            lineas.add(e.aLinea());
        }
        GestorArchivos.escribirLineas(ARCHIVO, lineas);
    }
}
