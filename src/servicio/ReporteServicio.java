package servicio;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import modelo.Cliente;
import modelo.Prestamo;

// Modulo de reportes. Todo se resuelve con expresiones LAMBDA y STREAM API.
public class ReporteServicio {

    private PrestamoServicio prestamos;
    private ClienteServicio clientes;

    public ReporteServicio(PrestamoServicio prestamos, ClienteServicio clientes) {
        this.prestamos = prestamos;
        this.clientes = clientes;
    }

    // Prestamos activos = los que estan en estado pendiente
    public List<Prestamo> prestamosActivos() {
        return prestamos.listar().stream()
                .filter(p -> p.getEstado().equals(Prestamo.PENDIENTE))
                .collect(Collectors.toList());
    }

    // Prestamos vencidos = pendientes cuya fecha final ya paso
    public List<Prestamo> prestamosVencidos() {
        return prestamos.listar().stream()
                .filter(p -> p.estaVencido())
                .collect(Collectors.toList());
    }

    // Clientes morosos = clientes que tienen al menos un prestamo vencido
    public List<Cliente> clientesMorosos() {
        return prestamosVencidos().stream()
                .map(p -> p.getClienteId())      // pasa de prestamo a id de cliente
                .distinct()                      // quita ids repetidos
                .map(id -> clientes.buscarPorId(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Suma del saldo de todos los prestamos pendientes (dinero por cobrar)
    public double totalPorCobrar() {
        return prestamosActivos().stream()
                .mapToDouble(p -> p.calcularSaldo())
                .sum();
    }

    // Prestamos con monto mayor al indicado, ordenados de mayor a menor
    public List<Prestamo> prestamosMayoresA(double minimo) {
        return prestamos.listar().stream()
                .filter(p -> p.getMonto() > minimo)
                .sorted(Comparator.comparingDouble(Prestamo::getMonto).reversed())
                .collect(Collectors.toList());
    }
}
