-- Script de base de datos para CrediYa
-- Ejecutar completo en MySQL Workbench o en la consola: mysql -u root -p < sql/crediya.sql

CREATE DATABASE IF NOT EXISTS crediya_db;
USE crediya_db;

-- Empleados que registran los prestamos
CREATE TABLE IF NOT EXISTS empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80),
    documento VARCHAR(30),
    rol VARCHAR(30),
    correo VARCHAR(80),
    salario DECIMAL(10,2)
);

-- Clientes que reciben los prestamos
CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80),
    documento VARCHAR(30),
    correo VARCHAR(80),
    telefono VARCHAR(20)
);

-- Prestamos: cada uno pertenece a un cliente y a un empleado
CREATE TABLE IF NOT EXISTS prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT,
    empleado_id INT,
    monto DECIMAL(12,2),
    interes DECIMAL(5,2),
    cuotas INT,
    fecha_inicio DATE,
    estado VARCHAR(20),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id)
);

-- Pagos (abonos) de cada prestamo
CREATE TABLE IF NOT EXISTS pagos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prestamo_id INT,
    fecha_pago DATE,
    monto DECIMAL(10,2),
    FOREIGN KEY (prestamo_id) REFERENCES prestamos(id)
);

-- NOTA: no se modifico el script base. El saldo pendiente no se guarda en la tabla,
-- el programa lo calcula: total del prestamo - suma de los pagos.
