package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Maneja la conexion a MySQL con JDBC.
// Usa el patron SINGLETON: solo existe una instancia y una conexion en todo el programa.
// Si MySQL no esta disponible, el programa sigue funcionando solo con archivos.
public class ConexionBD {

    // CAMBIAR estos datos segun tu MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/crediya_db";
    private static final String USUARIO = "root";
    private static final String CLAVE = "yotis0308";

    private static ConexionBD instancia;   // unica instancia
    private Connection conexion;           // conexion abierta (o null si fallo)
    private boolean intentoRealizado = false;

    private ConexionBD() { }               // constructor privado: nadie mas puede crear objetos

    public static ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    // Devuelve la conexion. Solo intenta conectarse la primera vez.
    public Connection getConexion() {
        if (!intentoRealizado) {
            intentoRealizado = true;
            try {
                conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
                System.out.println("Conexion a MySQL exitosa.");
            } catch (SQLException e) {
                conexion = null;
                System.out.println("No se pudo conectar a MySQL. Se trabajara solo con archivos.");
                System.out.println("Motivo: " + e.getMessage());
            }
        }
        return conexion;
    }

    public boolean hayConexion() {
        return getConexion() != null;
    }

    public void cerrar() {
        try {
            if (conexion != null) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.out.println("Error cerrando la conexion: " + e.getMessage());
        }
    }
}
