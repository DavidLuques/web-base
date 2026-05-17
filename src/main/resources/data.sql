INSERT INTO Usuario(id, email, password, rol, activo, nombre, telefono, calle, ciudad, pais, codigoPostal, provincia, fechaCreacion, avatarUrl)
VALUES(null, 'test@unlam.edu.ar', 'test', 'ADMIN', true, 'Test User', null, null, null, null, null, null, null, null);

INSERT INTO Usuario(id, email, password, rol, activo, nombre, telefono, calle, ciudad, pais, codigoPostal, provincia, fechaCreacion, avatarUrl)
VALUES(null, 'prueba@prueba.com', 'prueba', 'ADMIN', true, 'Usuario prueba', null, null, null, null, null, null, null, null);

INSERT INTO rango_vital_por_tamano (tamano, frecuenciaMinima, frecuenciaMaxima)
VALUES ('PEQUENO', 100, 140);

INSERT INTO rango_vital_por_tamano (tamano, frecuenciaMinima, frecuenciaMaxima)
VALUES ('MEDIANO', 80, 120);

INSERT INTO rango_vital_por_tamano (tamano, frecuenciaMinima, frecuenciaMaxima)
VALUES ('GRANDE', 60, 100);

INSERT INTO mascota(id, nombre, esteril, fecha_nacimiento, peso, raza, genero, tipo, imagen_mascota, tamano, usuario_id)
VALUES (null, 'Firulais', true, '2020-01-01', 10.5, 'Labrador', 'Macho', 'Perro', null, 'MEDIANO', 2);