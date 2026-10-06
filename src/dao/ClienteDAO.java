package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;

// Acceso a la tabla "clientes" con JDBC.
public class ClienteDAO implements CrudDAO<Cliente> {

    @Override
    public void guardar(Cliente c) throws SQLException {
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) {
            return;
        }
        String sql = "INSERT INTO clientes (id, nombre, documento, correo, telefono) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getId());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getDocumento());
            ps.setString(4, c.getCorreo());
            ps.setString(5, c.getTelefono());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) {
            return lista;
        }
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM clientes ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(rs.getInt("id"), rs.getString("nombre"), rs.getString("documento"),
                        rs.getString("correo"), rs.getString("telefono")));
            }
        }
        return lista;
    }
}
