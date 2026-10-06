package modelo;

import java.time.LocalDate;
import util.Formato;

// Un abono que hace un cliente a un prestamo.
public class Pago {

    private int id;
    private int prestamoId;
    private LocalDate fecha;
    private double monto;

    public Pago(int id, int prestamoId, LocalDate fecha, double monto) {
        this.id = id;
        this.prestamoId = prestamoId;
        this.fecha = fecha;
        this.monto = monto;
    }

    public int getId() { return id; }
    public int getPrestamoId() { return prestamoId; }
    public LocalDate getFecha() { return fecha; }
    public double getMonto() { return monto; }

    // Formato de archivo: id;prestamoId;fecha;monto
    public String aLinea() {
        return id + ";" + prestamoId + ";" + fecha + ";" + Formato.dinero(monto);
    }

    public static Pago desdeLinea(String linea) {
        String[] p = linea.split(";");
        return new Pago(Integer.parseInt(p[0]), Integer.parseInt(p[1]),
                LocalDate.parse(p[2]), Double.parseDouble(p[3]));
    }

    @Override
    public String toString() {
        return "Pago #" + id + " | Prestamo: " + prestamoId + " | Fecha: " + fecha
                + " | Monto: " + Formato.dinero(monto);
    }
}
