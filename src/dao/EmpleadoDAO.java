package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Empleado;

// Acceso a la tabla "empleados" con JDBC.
public class EmpleadoDAO implements CrudDAO<Empleado> {

    @Override
    public void guardar(Empleado e) throws SQLException {
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) {
            return; // sin base de datos no se hace nada
        }
        String sql = "INSERT INTO empleados (id, nombre, documento, rol, correo, salario) VALUES (?,?,?,?,?,?)";
        // PreparedStatement evita inyeccion SQL: los ? se reemplazan de forma segura
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getId());
            ps.setString(2, e.getNombre());
            ps.setString(3, e.getDocumento());
            ps.setString(4, e.getRol());
            ps.setString(5, e.getCorreo());
            ps.setDouble(6, e.getSalario());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Empleado> listar() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) {
            return lista;
        }
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM empleados ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Empleado(rs.getInt("id"), rs.getString("nombre"), rs.getString("documento"),
                        rs.getString("rol"), rs.getString("correo"), rs.getDouble("salario")));
            }
        }
        return lista;
    }
}
