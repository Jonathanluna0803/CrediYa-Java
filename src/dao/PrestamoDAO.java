package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Prestamo;

// Acceso a la tabla "prestamos" con JDBC.
public class PrestamoDAO implements CrudDAO<Prestamo> {

    @Override
    public void guardar(Prestamo p) throws SQLException {
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) {
            return;
        }
        String sql = "INSERT INTO prestamos (id, cliente_id, empleado_id, monto, interes, cuotas, fecha_inicio, estado) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.setInt(2, p.getClienteId());
            ps.setInt(3, p.getEmpleadoId());
            ps.setDouble(4, p.getMonto());
            ps.setDouble(5, p.getInteres());
            ps.setInt(6, p.getCuotas());
            ps.setDate(7, Date.valueOf(p.getFechaInicio())); // LocalDate -> java.sql.Date
            ps.setString(8, p.getEstado());
            ps.executeUpdate();
        }
    }

    // Cambia solo el estado de un prestamo (UPDATE)
    public void actualizarEstado(int id, String estado) throws SQLException {
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE prestamos SET estado = ? WHERE id = ?")) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Prestamo> listar() throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) {
            return lista;
        }
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM prestamos ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Prestamo(rs.getInt("id"), rs.getInt("cliente_id"), rs.getInt("empleado_id"),
                        rs.getDouble("monto"), rs.getDouble("interes"), rs.getInt("cuotas"),
                        rs.getDate("fecha_inicio").toLocalDate(), rs.getString("estado")));
            }
        }
        return lista;
    }
}
