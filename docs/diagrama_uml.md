# Diagrama UML de clases - CrediYa

Este diagrama esta escrito en Mermaid. GitHub lo dibuja automaticamente al abrir este archivo.
Tambien se puede pegar en https://mermaid.live para exportarlo como imagen (PNG o SVG).

```mermaid
classDiagram
    class Persona {
        <<abstract>>
        #int id
        #String nombre
        #String documento
        #String correo
        +getTipo() String
        +aLinea() String
    }
    class Empleado {
        -String rol
        -double salario
    }
    class Cliente {
        -String telefono
    }
    class Prestamo {
        -int id
        -int clienteId
        -int empleadoId
        -double monto
        -double interes
        -int cuotas
        -LocalDate fechaInicio
        -String estado
        +calcularTotal() double
        +calcularCuota() double
        +calcularSaldo() double
        +estaVencido() boolean
        +agregarAbono(double)
    }
    class Pago {
        -int id
        -int prestamoId
        -LocalDate fecha
        -double monto
    }
    class CrudDAO~T~ {
        <<interface>>
        +guardar(T)
        +listar() List~T~
    }
    class EmpleadoDAO
    class ClienteDAO
    class PrestamoDAO
    class PagoDAO
    class ConexionBD {
        <<singleton>>
        +getInstancia() ConexionBD
        +getConexion() Connection
    }
    class GestorArchivos {
        +leerLineas(String) List
        +escribirLineas(String, List)
    }
    class EmpleadoServicio
    class ClienteServicio
    class PrestamoServicio
    class PagoServicio
    class ReporteServicio
    class CrediYaException

    Persona <|-- Empleado
    Persona <|-- Cliente
    CrudDAO <|.. EmpleadoDAO
    CrudDAO <|.. ClienteDAO
    CrudDAO <|.. PrestamoDAO
    CrudDAO <|.. PagoDAO
    Exception <|-- CrediYaException

    Cliente "1" --> "0..*" Prestamo : tiene
    Empleado "1" --> "0..*" Prestamo : registra
    Prestamo "1" --> "0..*" Pago : recibe

    EmpleadoServicio --> EmpleadoDAO
    ClienteServicio --> ClienteDAO
    PrestamoServicio --> PrestamoDAO
    PagoServicio --> PagoDAO
    PrestamoServicio --> ClienteServicio
    PrestamoServicio --> EmpleadoServicio
    PagoServicio --> PrestamoServicio
    ReporteServicio --> PrestamoServicio
    ReporteServicio --> ClienteServicio
    EmpleadoDAO --> ConexionBD
    ClienteDAO --> ConexionBD
    PrestamoDAO --> ConexionBD
    PagoDAO --> ConexionBD
    EmpleadoServicio --> GestorArchivos
    ClienteServicio --> GestorArchivos
    PrestamoServicio --> GestorArchivos
    PagoServicio --> GestorArchivos
```
