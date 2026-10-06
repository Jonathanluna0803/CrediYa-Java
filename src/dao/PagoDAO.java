package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Pago;

// Acceso a la tabla "pagos" con JDBC.
public class PagoDAO implements CrudDAO<Pago> {

    @Override
    public void guardar(Pago p) throws SQLException {
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) {
            return;
        }
        String sql = "INSERT INTO pagos (id, prestamo_id, fecha_pago, monto) VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.setInt(2, p.getPrestamoId());
            ps.setDate(3, Date.valueOf(p.getFecha()));
            ps.setDouble(4, p.getMonto());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Pago> listar() throws SQLException {
        List<Pago> lista = new ArrayList<>();
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) {
            return lista;
        }
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM pagos ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Pago(rs.getInt("id"), rs.getInt("prestamo_id"),
                        rs.getDate("fecha_pago").toLocalDate(), rs.getDouble("monto")));
            }
        }
        return lista;
    }
}
