CREATE DATABASE IF NOT EXISTS pasteleriaweb CHARACTER SET utf8mb4;
USE pasteleriaweb;

CREATE TABLE IF NOT EXISTS rol (
    id_rol     BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL,
    estado     INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_rol     BIGINT NOT NULL,
    nombre     VARCHAR(100) NOT NULL,
    apellido   VARCHAR(100) NOT NULL,
    correo     VARCHAR(150) NOT NULL,
    telefono   VARCHAR(20),
    contrasena VARCHAR(255) NOT NULL,
    estado     INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol)
);

CREATE TABLE IF NOT EXISTS direccion_usuario (
    id_direccion BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario   BIGINT NOT NULL,
    direccion    VARCHAR(255) NOT NULL,
    CONSTRAINT fk_direccion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    descripcion  VARCHAR(255),
    estado       INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS producto (
    id_producto  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_categoria BIGINT NOT NULL,
    nombre       VARCHAR(150) NOT NULL,
    descripcion  VARCHAR(500),
    precio       DOUBLE NOT NULL,
    imagen       VARCHAR(255),
    stock        INT NOT NULL DEFAULT 0,
    estado       INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria)
);

CREATE TABLE IF NOT EXISTS pedido (
    id_pedido         BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario        BIGINT NOT NULL,
    id_direccion      BIGINT NOT NULL,
    fecha_pedido      DATETIME,
    fecha_entrega     DATETIME,
    modalidad_entrega VARCHAR(30),
    estado            VARCHAR(30),
    total             DECIMAL(10,2),
    observacion       VARCHAR(500),
    CONSTRAINT fk_pedido_usuario   FOREIGN KEY (id_usuario)   REFERENCES usuario (id_usuario),
    CONSTRAINT fk_pedido_direccion FOREIGN KEY (id_direccion) REFERENCES direccion_usuario (id_direccion)
);

CREATE TABLE IF NOT EXISTS detallepedido (
    id_detalle      BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido       BIGINT NOT NULL,
    id_producto     BIGINT NOT NULL,
    cantidad        INT NOT NULL,
    precio_unitario DECIMAL(10,2),
    subtotal        DECIMAL(10,2),
    CONSTRAINT fk_detalle_pedido   FOREIGN KEY (id_pedido)   REFERENCES pedido (id_pedido),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
);

CREATE TABLE IF NOT EXISTS entrega (
    id_entrega       BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido        BIGINT NOT NULL UNIQUE,
    id_motorizado    BIGINT NOT NULL,
    fecha_asignacion DATETIME,
    fecha_entrega    DATETIME,
    estado           INT NOT NULL DEFAULT 0,
    costo_delivery   DECIMAL(10,2),
    observacion      VARCHAR(500),
    CONSTRAINT fk_entrega_pedido     FOREIGN KEY (id_pedido)     REFERENCES pedido (id_pedido),
    CONSTRAINT fk_entrega_motorizado FOREIGN KEY (id_motorizado) REFERENCES usuario (id_usuario)
);
