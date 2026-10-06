package principal;

import conexion.ConexionBD;
import excepciones.CrediYaException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import modelo.Cliente;
import modelo.Empleado;
import modelo.Pago;
import modelo.Prestamo;
import servicio.ClienteServicio;
import servicio.EmpleadoServicio;
import servicio.PagoServicio;
import servicio.PrestamoServicio;
import servicio.ReporteServicio;
import util.Formato;

// Clase principal: muestra los menus por consola y llama a los servicios.
// No tiene logica de negocio, solo pide datos y muestra resultados.
public class Main {

    private static Scanner teclado = new Scanner(System.in);
    private static EmpleadoServicio empleados;
    private static ClienteServicio clientes;
    private static PrestamoServicio prestamos;
    private static PagoServicio pagos;
    private static ReporteServicio reportes;

    public static void main(String[] args) {
        // Se crean los servicios. Cada uno carga sus datos (MySQL o archivos).
        empleados = new EmpleadoServicio();
        clientes = new ClienteServicio();
        prestamos = new PrestamoServicio(clientes, empleados);
        pagos = new PagoServicio(prestamos);
        reportes = new ReporteServicio(prestamos, clientes);

        int opcion = -1;
        while (opcion != 0) {
            System.out.println("");
            System.out.println("===== CREDIYA - SISTEMA DE COBROS =====");
            System.out.println("1. Empleados");
            System.out.println("2. Clientes");
            System.out.println("3. Prestamos");
            System.out.println("4. Pagos");
            System.out.println("5. Reportes");
            System.out.println("0. Salir");
            opcion = leerEntero("Opcion: ");
            switch (opcion) {
                case 1:
                    menuEmpleados();
                    break;
                case 2:
                    menuClientes();
                    break;
                case 3:
                    menuPrestamos();
                    break;
                case 4:
                    menuPagos();
                    break;
                case 5:
                    menuReportes();
                    break;
                case 0:
                    System.out.println("Hasta luego.");
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
        ConexionBD.getInstancia().cerrar();
    }

    // ---------------- EMPLEADOS ----------------
    private static void menuEmpleados() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("");
            System.out.println("--- EMPLEADOS ---");
            System.out.println("1. Registrar empleado");
            System.out.println("2. Listar empleados");
            System.out.println("3. Buscar empleado por id");
            System.out.println("0. Volver");
            opcion = leerEntero("Opcion: ");
            switch (opcion) {
                case 1:
                    String nombre = leerTexto("Nombre: ");
                    String documento = leerTexto("Documento: ");
                    String rol = leerTexto("Rol: ");
                    String correo = leerTexto("Correo: ");
                    double salario = leerDecimal("Salario: ");
                    try {
                        Empleado e = empleados.registrar(nombre, documento, rol, correo, salario);
                        System.out.println("Empleado registrado: " + e);
                    } catch (CrediYaException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                    break;
                case 2:
                    imprimirLista(empleados.listar());
                    break;
                case 3:
                    Empleado buscado = empleados.buscarPorId(leerEntero("Id del empleado: "));
                    System.out.println(buscado == null ? "No se encontro el empleado." : buscado.toString());
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    // ---------------- CLIENTES ----------------
    private static void menuClientes() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("");
            System.out.println("--- CLIENTES ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Consultar prestamos de un cliente");
            System.out.println("0. Volver");
            opcion = leerEntero("Opcion: ");
            switch (opcion) {
                case 1:
                    String nombre = leerTexto("Nombre: ");
                    String documento = leerTexto("Documento: ");
                    String correo = leerTexto("Correo: ");
                    String telefono = leerTexto("Telefono: ");
                    try {
                        Cliente c = clientes.registrar(nombre, documento, correo, telefono);
                        System.out.println("Cliente registrado: " + c);
                    } catch (CrediYaException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                    break;
                case 2:
                    imprimirLista(clientes.listar());
                    break;
                case 3:
                    int id = leerEntero("Id del cliente: ");
                    if (clientes.buscarPorId(id) == null) {
                        System.out.println("No se encontro el cliente.");
                    } else {
                        imprimirLista(prestamos.prestamosDeCliente(id));
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    // ---------------- PRESTAMOS ----------------
    private static void menuPrestamos() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("");
            System.out.println("--- PRESTAMOS ---");
            System.out.println("1. Crear prestamo");
            System.out.println("2. Listar prestamos");
            System.out.println("3. Cambiar estado (pendiente / pagado)");
            System.out.println("0. Volver");
            opcion = leerEntero("Opcion: ");
            switch (opcion) {
                case 1:
                    int clienteId = leerEntero("Id del cliente: ");
                    int empleadoId = leerEntero("Id del empleado: ");
                    double monto = leerDecimal("Monto a prestar: ");
                    double interes = leerDecimal("Interes total en porcentaje (ejemplo 10): ");
                    int cuotas = leerEntero("Numero de cuotas mensuales: ");
                    LocalDate fecha = leerFecha();
                    try {
                        Prestamo p = prestamos.crear(clienteId, empleadoId, monto, interes, cuotas, fecha);
                        System.out.println("Prestamo creado: " + p);
                    } catch (CrediYaException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                    break;
                case 2:
                    imprimirLista(prestamos.listar());
                    break;
                case 3:
                    int id = leerEntero("Id del prestamo: ");
                    String estado = leerTexto("Nuevo estado (pendiente / pagado): ").toLowerCase();
                    try {
                        prestamos.cambiarEstado(id, estado);
                        System.out.println("Estado actualizado.");
                    } catch (CrediYaException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    // ---------------- PAGOS ----------------
    private static void menuPagos() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("");
            System.out.println("--- PAGOS ---");
            System.out.println("1. Registrar abono");
            System.out.println("2. Ver historico de pagos de un prestamo");
            System.out.println("0. Volver");
            opcion = leerEntero("Opcion: ");
            switch (opcion) {
                case 1:
                    int prestamoId = leerEntero("Id del prestamo: ");
                    double monto = leerDecimal("Valor del abono: ");
                    try {
                        Pago pago = pagos.registrarAbono(prestamoId, monto);
                        System.out.println("Abono registrado: " + pago);
                        System.out.println("Saldo pendiente: "
                                + Formato.dinero(prestamos.buscarPorId(prestamoId).calcularSaldo()));
                    } catch (CrediYaException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                    break;
                case 2:
                    imprimirLista(pagos.historial(leerEntero("Id del prestamo: ")));
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    // ---------------- REPORTES ----------------
    private static void menuReportes() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("");
            System.out.println("--- REPORTES ---");
            System.out.println("1. Prestamos activos");
            System.out.println("2. Prestamos vencidos");
            System.out.println("3. Clientes morosos");
            System.out.println("4. Total por cobrar");
            System.out.println("5. Prestamos con monto mayor a un valor");
            System.out.println("0. Volver");
            opcion = leerEntero("Opcion: ");
            switch (opcion) {
                case 1:
                    imprimirLista(reportes.prestamosActivos());
                    break;
                case 2:
                    imprimirLista(reportes.prestamosVencidos());
                    break;
                case 3:
                    imprimirLista(reportes.clientesMorosos());
                    break;
                case 4:
                    System.out.println("Total por cobrar: " + Formato.dinero(reportes.totalPorCobrar()));
                    break;
                case 5:
                    imprimirLista(reportes.prestamosMayoresA(leerDecimal("Monto minimo: ")));
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    // ---------------- METODOS DE AYUDA ----------------

    // Imprime cualquier lista (usa el toString de cada objeto)
    private static void imprimirLista(List<?> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay resultados.");
        } else {
            for (Object o : lista) {
                System.out.println(o);
            }
        }
    }

    // Lee un texto. Cambia ";" por "," porque ";" separa los campos en los archivos.
    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine().trim().replace(";", ",");
    }

    // Lee un entero; si el usuario escribe otra cosa, vuelve a preguntar.
    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un numero entero.");
            }
        }
    }

    // Lee un decimal (con punto, ejemplo 1500000.50)
    private static double leerDecimal(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un numero (use punto para decimales).");
            }
        }
    }

    // Lee una fecha aaaa-mm-dd. Si se deja vacia se usa la fecha de hoy.
    private static LocalDate leerFecha() {
        while (true) {
            String texto = leerTexto("Fecha de inicio (aaaa-mm-dd) o Enter para hoy: ");
            if (texto.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(texto);
            } catch (Exception e) {
                System.out.println("Fecha invalida. Ejemplo: 2026-01-15");
            }
        }
    }
}
