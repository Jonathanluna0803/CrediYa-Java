package excepciones;

// Excepcion propia del sistema. Se usa para avisar errores de validacion
// (por ejemplo: documento repetido, monto negativo, prestamo inexistente).
public class CrediYaException extends Exception {

    public CrediYaException(String mensaje) {
        super(mensaje); // guarda el mensaje que se mostrara al usuario
    }
}
