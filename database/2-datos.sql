USE pasteleriaweb;

-- Roles --
INSERT INTO rol (nombre_rol, estado)
SELECT 'Administrador', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre_rol = 'Administrador');
INSERT INTO rol (nombre_rol, estado)
SELECT 'Cliente', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre_rol = 'Cliente');
INSERT INTO rol (nombre_rol, estado)
SELECT 'Motorizado', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre_rol = 'Motorizado');

-- Usuarios --
INSERT INTO usuario (id_rol, nombre, apellido, correo, telefono, contrasena, estado)
SELECT r.id_rol, 'Lucía', 'Paredes', 'lucia.paredes@correo.com', '987654321', 'Cliente123', 1
FROM rol r WHERE r.nombre_rol = 'Cliente'
  AND NOT EXISTS (SELECT 1 FROM usuario WHERE correo = 'lucia.paredes@correo.com');

INSERT INTO usuario (id_rol, nombre, apellido, correo, telefono, contrasena, estado)
SELECT r.id_rol, 'Admin', 'Principal', 'admin@dolcevita.com', '900000001', 'Admin123', 1
FROM rol r WHERE r.nombre_rol = 'Administrador'
  AND NOT EXISTS (SELECT 1 FROM usuario WHERE correo = 'admin@dolcevita.com');

INSERT INTO usuario (id_rol, nombre, apellido, correo, telefono, contrasena, estado)
SELECT r.id_rol, 'Marco', 'Huamán', 'marco.huaman@dolcevita.com', '900000002', 'Moto123', 1
FROM rol r WHERE r.nombre_rol = 'Motorizado'
  AND NOT EXISTS (SELECT 1 FROM usuario WHERE correo = 'marco.huaman@dolcevita.com');

-- Dirección de la clienta de prueba --
INSERT INTO direccion_usuario (id_usuario, direccion)
SELECT u.id_usuario, 'Av. Primavera 123, Santiago de Surco'
FROM usuario u WHERE u.correo = 'lucia.paredes@correo.com'
  AND NOT EXISTS (SELECT 1 FROM direccion_usuario x WHERE x.id_usuario = u.id_usuario);

-- Categorías --
INSERT INTO categoria (nombre, descripcion, estado)
SELECT 'Tortas personalizadas', 'Diseños creados especialmente para cada celebración', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Tortas personalizadas');
INSERT INTO categoria (nombre, descripcion, estado)
SELECT 'Tortas clásicas', 'Sabores tradicionales para compartir en familia', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Tortas clásicas');
INSERT INTO categoria (nombre, descripcion, estado)
SELECT 'Postres', 'Postres individuales y para compartir', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Postres');
INSERT INTO categoria (nombre, descripcion, estado)
SELECT 'Bocaditos', 'Alfajores, brownies y más', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Bocaditos');
INSERT INTO categoria (nombre, descripcion, estado)
SELECT 'Keke artesanales', 'Keke de sabores variados', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Keke artesanales');

-- Productos ---
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Blooming Celebration', 'Torta personalizada floral elaborada artesanalmente para celebraciones especiales.', 150.00, 'torta-floral.jpg', 5, 1
FROM categoria c WHERE c.nombre = 'Tortas personalizadas'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Blooming Celebration');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Golden Butterfly Cake', 'Torta con mariposas doradas, ideal para cumpleaños y ocasiones únicas.', 165.00, 'torta-mariposas.jpg', 4, 1
FROM categoria c WHERE c.nombre = 'Tortas personalizadas'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Golden Butterfly Cake');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Royal Blue Cake', 'Torta azul con detalles dorados elaborada artesanalmente.', 155.00, 'torta-azul.jpg', 6, 1
FROM categoria c WHERE c.nombre = 'Tortas personalizadas'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Royal Blue Cake');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Chocolate Supreme', 'Torta clásica de chocolate para compartir en familia.', 85.00, 'clasica-chocolate.jpg', 10, 1
FROM categoria c WHERE c.nombre = 'Tortas clásicas'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Chocolate Supreme');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Strawberry Delight', 'Bizcocho suave con crema y fresas frescas.', 80.00, 'clasica-fresa.jpg', 8, 1
FROM categoria c WHERE c.nombre = 'Tortas clásicas'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Strawberry Delight');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Carrot Delight', 'Torta de zanahoria con crema y nueces.', 78.00, 'clasica-zanahoria.jpg', 7, 1
FROM categoria c WHERE c.nombre = 'Tortas clásicas'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Carrot Delight');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Oreo Cheesecake', 'Cheesecake cremoso con base de galleta Oreo.', 75.00, 'postre-oreo.jpg', 9, 1
FROM categoria c WHERE c.nombre = 'Postres'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Oreo Cheesecake');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Classic Tiramisu', 'Tiramisú clásico de café y cacao.', 68.00, 'postre-tiramisu.jpg', 6, 1
FROM categoria c WHERE c.nombre = 'Postres'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Classic Tiramisu');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Tartaleta de fresa', 'Tartaletas con crema pastelera y fresas.', 55.00, 'postre-tartaleta.jpg', 12, 1
FROM categoria c WHERE c.nombre = 'Postres'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Tartaleta de fresa');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Brownie clásico', 'Brownie de chocolate húmedo y suave.', 30.00, 'bocadito-brownie.jpg', 20, 1
FROM categoria c WHERE c.nombre = 'Bocaditos'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Brownie clásico');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Alfajor clásico', 'Alfajores rellenos de manjar blanco.', 28.00, 'bocadito-alfajor.jpg', 25, 1
FROM categoria c WHERE c.nombre = 'Bocaditos'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Alfajor clásico');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Keke de arándanos', 'Keke casero con arándanos, textura suave.', 28.00, 'keke-arandanos.jpg', 10, 1
FROM categoria c WHERE c.nombre = 'Keke artesanales'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Keke de arándanos');
INSERT INTO producto (id_categoria, nombre, descripcion, precio, imagen, stock, estado)
SELECT c.id_categoria, 'Keke de naranja', 'Keke artesanal con ralladura de naranja.', 26.00, 'keke-naranja.jpg', 10, 1
FROM categoria c WHERE c.nombre = 'Keke artesanales'
  AND NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Keke de naranja');
