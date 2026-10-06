package modelo;

// Cliente de CrediYa. Hereda de Persona y agrega el telefono.
public class Cliente extends Persona {

    private String telefono;

    public Cliente(int id, String nombre, String documento, String correo, String telefono) {
        super(id, nombre, documento, correo);
        this.telefono = telefono;
    }

    public String getTelefono() { return telefono; }

    @Override
    public String getTipo() { return "Cliente"; }

    // Formato de archivo: id;nombre;documento;correo;telefono
    @Override
    public String aLinea() {
        return id + ";" + nombre + ";" + documento + ";" + correo + ";" + telefono;
    }

    public static Cliente desdeLinea(String linea) {
        String[] p = linea.split(";");
        return new Cliente(Integer.parseInt(p[0]), p[1], p[2], p[3], p[4]);
    }

    @Override
    public String toString() {
        return "Cliente #" + id + " | " + nombre + " | Doc: " + documento
                + " | Correo: " + correo + " | Tel: " + telefono;
    }
}
