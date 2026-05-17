-- Usuarios
INSERT INTO Usuario(email, password, rol, activo, nombre, calle, ciudad, provincia, pais, codigoPostal)
VALUES('test@unlam.edu.ar', 'test', 'ADMIN', true, 'Test User', null, null, null, null, null);

INSERT INTO Usuario(email, password, rol, activo, nombre, calle, ciudad, provincia, pais, codigoPostal)
VALUES('prueba@prueba.com', 'prueba', 'ADMIN', true, 'Usuario prueba', null, null, null, null, null);

-- Rangos Vitales
INSERT INTO rango_vital_por_tamano (tamano, frecuenciaMinima, frecuenciaMaxima)
VALUES ('PEQUENO', 100, 140);

INSERT INTO rango_vital_por_tamano (tamano, frecuenciaMinima, frecuenciaMaxima)
VALUES ('MEDIANO', 80, 120);

INSERT INTO rango_vital_por_tamano (tamano, frecuenciaMinima, frecuenciaMaxima)
VALUES ('GRANDE', 60, 100);

-- Mascotas
INSERT INTO mascota(nombre, esteril, fecha_nacimiento, peso, raza, genero, tipo, usuario_id)
VALUES ('Firulais', true, '2020-01-01', 10.5, 'Labrador', 'Macho', 'Perro', 2);
