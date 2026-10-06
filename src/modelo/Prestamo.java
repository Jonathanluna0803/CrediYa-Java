package modelo;

import java.time.LocalDate;
import util.Formato;

// Prestamo que se le otorga a un cliente y que registra un empleado.
// Aqui viven los calculos: total con interes, cuota mensual y saldo.
public class Prestamo {

    // Los dos estados posibles del prestamo
    public static final String PENDIENTE = "pendiente";
    public static final String PAGADO = "pagado";

    private int id;
    private int clienteId;
    private int empleadoId;
    private double monto;        // dinero prestado
    private double interes;      // porcentaje de interes total (ejemplo: 10 = 10%)
    private int cuotas;          // cantidad de cuotas mensuales
    private LocalDate fechaInicio;
    private String estado;
    private double totalPagado;  // suma de abonos (no se guarda en archivo, se recalcula con los pagos)

    public Prestamo(int id, int clienteId, int empleadoId, double monto, double interes,
                    int cuotas, LocalDate fechaInicio, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.empleadoId = empleadoId;
        this.monto = monto;
        this.interes = interes;
        this.cuotas = cuotas;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
        this.totalPagado = 0;
    }

    public int getId() { return id; }
    public int getClienteId() { return clienteId; }
    public int getEmpleadoId() { return empleadoId; }
    public double getMonto() { return monto; }
    public double getInteres() { return interes; }
    public int getCuotas() { return cuotas; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Monto total = monto + (monto * interes / 100)
    public double calcularTotal() {
        return redondear(monto + monto * interes / 100);
    }

    // Cuota mensual = total / cantidad de cuotas
    public double calcularCuota() {
        return redondear(calcularTotal() / cuotas);
    }

    // Saldo pendiente = total - lo que ya se abono (nunca menor que 0)
    public double calcularSaldo() {
        double saldo = redondear(calcularTotal() - totalPagado);
        if (saldo < 0) {
            saldo = 0;
        }
        return saldo;
    }

    // Fecha en la que deberia estar pagado todo: inicio + numero de meses
    public LocalDate calcularFechaFin() {
        return fechaInicio.plusMonths(cuotas);
    }

    // Un prestamo esta vencido si sigue pendiente y ya paso su fecha final
    public boolean estaVencido() {
        return estado.equals(PENDIENTE) && LocalDate.now().isAfter(calcularFechaFin());
    }

    // Suma un abono al total pagado
    public void agregarAbono(double valor) {
        totalPagado = totalPagado + valor;
    }

    // Deja el numero con 2 decimales
    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    // Formato de archivo: id;clienteId;empleadoId;monto;interes;cuotas;fechaInicio;estado
    public String aLinea() {
        return id + ";" + clienteId + ";" + empleadoId + ";" + Formato.dinero(monto) + ";"
                + Formato.dinero(interes) + ";" + cuotas + ";" + fechaInicio + ";" + estado;
    }

    public static Prestamo desdeLinea(String linea) {
        String[] p = linea.split(";");
        return new Prestamo(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]),
                Double.parseDouble(p[3]), Double.parseDouble(p[4]), Integer.parseInt(p[5]),
                LocalDate.parse(p[6]), p[7]);
    }

    @Override
    public String toString() {
        return "Prestamo #" + id + " | Cliente: " + clienteId + " | Empleado: " + empleadoId
                + " | Monto: " + Formato.dinero(monto) + " | Interes: " + interes + "%"
                + " | Cuotas: " + cuotas + " | Total: " + Formato.dinero(calcularTotal())
                + " | Cuota mensual: " + Formato.dinero(calcularCuota())
                + " | Saldo: " + Formato.dinero(calcularSaldo())
                + " | Inicio: " + fechaInicio + " | Estado: " + estado;
    }
}
