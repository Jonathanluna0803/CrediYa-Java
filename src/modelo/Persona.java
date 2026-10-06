package modelo;

// Clase padre (abstracta) de Empleado y Cliente.
// Contiene los datos que comparten: id, nombre, documento y correo.
// Aqui se aplica HERENCIA y ENCAPSULAMIENTO (atributos protegidos + getters).
public abstract class Persona {

    protected int id;
    protected String nombre;
    protected String documento;
    protected String correo;

    public Persona(int id, String nombre, String documento, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.correo = correo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public String getCorreo() { return correo; }

    // Metodos abstractos: cada hijo los implementa a su manera (POLIMORFISMO).
    public abstract String getTipo();   // devuelve "Empleado" o "Cliente"
    public abstract String aLinea();    // convierte el objeto a una linea de texto para el archivo
}
