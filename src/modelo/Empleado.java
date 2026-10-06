package modelo;

import util.Formato;

// Empleado de CrediYa. Hereda de Persona y agrega rol y salario.
public class Empleado extends Persona {

    private String rol;
    private double salario;

    public Empleado(int id, String nombre, String documento, String rol, String correo, double salario) {
        super(id, nombre, documento, correo); // llama al constructor del padre
        this.rol = rol;
        this.salario = salario;
    }

    public String getRol() { return rol; }
    public double getSalario() { return salario; }

    @Override
    public String getTipo() { return "Empleado"; }

    // Formato de archivo: id;nombre;documento;rol;correo;salario
    @Override
    public String aLinea() {
        return id + ";" + nombre + ";" + documento + ";" + rol + ";" + correo + ";" + Formato.dinero(salario);
    }

    // Lee una linea del archivo y crea el objeto Empleado.
    public static Empleado desdeLinea(String linea) {
        String[] p = linea.split(";");
        return new Empleado(Integer.parseInt(p[0]), p[1], p[2], p[3], p[4], Double.parseDouble(p[5]));
    }

    @Override
    public String toString() {
        return "Empleado #" + id + " | " + nombre + " | Doc: " + documento + " | Rol: " + rol
                + " | Correo: " + correo + " | Salario: " + Formato.dinero(salario);
    }
}
