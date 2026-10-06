# CrediYa - Sistema de Cobros de Cartera

Sistema de consola en Java para gestionar empleados, clientes, prestamos y pagos de la
empresa CrediYa S.A.S. Guarda la informacion en **archivos de texto** y en **MySQL (JDBC)**.

## Requisitos
- Java JDK 11 o superior
- MySQL 8 (opcional: si no esta disponible, el programa funciona solo con archivos)
- Conector JDBC de MySQL (mysql-connector-j-x.x.x.jar), guardarlo en la carpeta `lib/`

## Estructura del proyecto
```
CrediYa/
  src/
    modelo/       Persona, Empleado, Cliente, Prestamo, Pago
    excepciones/  CrediYaException
    conexion/     ConexionBD (Singleton)
    dao/          CrudDAO (interfaz) y EmpleadoDAO, ClienteDAO, PrestamoDAO, PagoDAO
    archivos/     GestorArchivos (lee y escribe .txt)
    servicio/     EmpleadoServicio, ClienteServicio, PrestamoServicio, PagoServicio, ReporteServicio
    util/         Formato (muestra dinero con 2 decimales)
    principal/    Main (menus por consola)
  sql/crediya.sql        Script de la base de datos
  datos/                 Archivos de texto (empleados.txt, clientes.txt, prestamos.txt, pagos.txt)
  docs/diagrama_uml.md   Diagrama UML de clases
```

## Configuracion de MySQL
1. Ejecutar el script: `mysql -u root -p < sql/crediya.sql`
2. Abrir `src/conexion/ConexionBD.java` y cambiar `USUARIO` y `CLAVE` por los de tu MySQL.

## Como compilar y ejecutar
Ejecutar siempre desde la carpeta raiz `CrediYa` (asi encuentra la carpeta `datos`).

**Linux / Mac**
```
mkdir out
javac -d out $(find src -name "*.java")
java -cp "out:lib/mysql-connector-j-8.4.0.jar" principal.Main
```

**Windows (CMD)**
```
mkdir out
dir /s /b src\*.java > fuentes.txt
javac -d out @fuentes.txt
java -cp "out;lib\mysql-connector-j-8.4.0.jar" principal.Main
```
(Cambiar el nombre del .jar por la version que descargaste.)

## Como funciona la persistencia
- Al iniciar: si hay conexion a MySQL, los datos se leen de la base de datos y se copian a los
  archivos. Si no hay conexion, se leen de los archivos `.txt`.
- Cada vez que se registra algo: se guarda en la lista en memoria, en el archivo y en MySQL.
- Formato de los archivos: un registro por linea con los campos separados por `;`.

## Reglas de negocio
- Monto total = monto + (monto x interes / 100). Ejemplo: 1.000.000 al 10% = 1.100.000.
- Cuota mensual = monto total / numero de cuotas. Ejemplo: 1.100.000 / 4 = 275.000.
- Saldo pendiente = monto total - suma de abonos.
- Cuando el saldo llega a 0 el prestamo pasa automaticamente a **pagado**.
- Un prestamo esta **vencido** si sigue pendiente y ya paso (fecha de inicio + numero de cuotas en meses).
- Un cliente es **moroso** si tiene al menos un prestamo vencido.

## Conceptos aplicados
| Concepto | Donde |
|---|---|
| Herencia | `Empleado` y `Cliente` heredan de `Persona` |
| Polimorfismo | `getTipo()` y `aLinea()` de `Persona` se implementan distinto en cada hijo |
| Encapsulamiento | Atributos privados/protegidos con getters |
| Colecciones | `ArrayList` en los servicios |
| Archivos | `GestorArchivos` (BufferedReader / PrintWriter) |
| JDBC | Paquete `dao` con `PreparedStatement` |
| Excepciones | `CrediYaException` y `try/catch` para SQL y archivos |
| Lambdas y Streams | `ReporteServicio`, `prestamosDeCliente`, `historial` |
| SOLID | Cada clase tiene una sola responsabilidad (modelo, dao, servicio, menu); `CrudDAO` es una interfaz |
| Patrones | Singleton (`ConexionBD`) y DAO |

## Ejemplo de uso
```
===== CREDIYA - SISTEMA DE COBROS =====
1. Empleados  2. Clientes  3. Prestamos  4. Pagos  5. Reportes  0. Salir

Empleados > Registrar:  Carlos Ruiz | 1001 | Asesor | carlos@crediya.com | 2500000
Clientes  > Registrar:  Maria Lopez | 2001 | maria@mail.com | 3001112222
Prestamos > Crear:      cliente 1, empleado 1, monto 1000000, interes 10, cuotas 4
   -> Prestamo #1 | Total: 1100000.00 | Cuota mensual: 275000.00 | Saldo: 1100000.00 | Estado: pendiente
Pagos     > Abono:      prestamo 1, valor 275000
   -> Saldo pendiente: 825000.00
Pagos     > Abono:      prestamo 1, valor 825000
   -> Saldo pendiente: 0.00  (el prestamo pasa a "pagado")
Reportes  > Total por cobrar, prestamos activos, vencidos, clientes morosos...
```
Para probar los **vencidos** y **morosos**, crear un prestamo con una fecha de inicio antigua
(por ejemplo 2025-01-01) y luego consultar el menu de reportes.

## Notas
- Los decimales se escriben con punto (ejemplo 1500000.50).
- Si se escribe `;` en un texto, el programa lo cambia por `,` para no danar los archivos.
- Los archivos de la carpeta `datos/` incluidos son un ejemplo generado por el propio programa.

## Repositorio en GitHub
```
git init
git add .
git commit -m "Proyecto CrediYa"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/crediya.git
git push -u origin main
```
