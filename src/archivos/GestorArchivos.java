package archivos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

// Se encarga de LEER y ESCRIBIR archivos de texto (persistencia en archivos).
// Los archivos se guardan en la carpeta "datos". Cada linea es un registro.
public class GestorArchivos {

    private static final String CARPETA = "datos";

    // Lee todas las lineas de un archivo y las devuelve en una lista.
    // Si el archivo no existe devuelve una lista vacia.
    public static List<String> leerLineas(String nombreArchivo) {
        List<String> lineas = new ArrayList<>();
        File archivo = new File(CARPETA, nombreArchivo);
        if (!archivo.exists()) {
            return lineas;
        }
        // try-with-resources: cierra el lector solo, aunque ocurra un error
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lineas.add(linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo " + nombreArchivo + ": " + e.getMessage());
        }
        return lineas;
    }

    // Escribe la lista de lineas en el archivo (reemplaza el contenido anterior).
    public static void escribirLineas(String nombreArchivo, List<String> lineas) {
        File carpeta = new File(CARPETA);
        if (!carpeta.exists()) {
            carpeta.mkdirs(); // crea la carpeta datos si no existe
        }
        try (PrintWriter escritor = new PrintWriter(new FileWriter(new File(carpeta, nombreArchivo)))) {
            for (String linea : lineas) {
                escritor.println(linea);
            }
        } catch (IOException e) {
            System.out.println("Error escribiendo " + nombreArchivo + ": " + e.getMessage());
        }
    }
}
