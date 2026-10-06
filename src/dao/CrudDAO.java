package dao;

import java.sql.SQLException;
import java.util.List;

// Interfaz generica para las clases que hablan con la base de datos.
// T es el tipo de objeto (Empleado, Cliente, etc). Todas las DAO cumplen este contrato.
public interface CrudDAO<T> {

    void guardar(T objeto) throws SQLException;   // INSERT

    List<T> listar() throws SQLException;         // SELECT
}
