INSERT INTO Usuario(id, email, password, rol, activo, nombre, telefono, calle, ciudad, pais, codigoPostal, provincia, fechaCreacion, avatarUrl)
VALUES(null, 'test@unlam.edu.ar', 'test', 'ADMIN', true, 'Test User', null, null, null, null, null, null, null, null);

INSERT INTO Usuario(id, email, password, rol, activo, nombre, telefono, calle, ciudad, pais, codigoPostal, provincia, fechaCreacion, avatarUrl)
VALUES(null, 'prueba@prueba.com', 'prueba', 'ADMIN', true, 'Usuario prueba', null, null, null, null, null, null, null, null);

INSERT INTO rango_vital_por_tamano
(tamano, frecuenciaMinima, frecuenciaMaxima, sistolicaMinima, sistolicaMaxima, diastolicaMinima, diastolicaMaxima)
VALUES
    ('PEQUENO', 100, 140, 110, 130, 70, 85);

INSERT INTO rango_vital_por_tamano
(tamano, frecuenciaMinima, frecuenciaMaxima, sistolicaMinima, sistolicaMaxima, diastolicaMinima, diastolicaMaxima)
VALUES
    ('MEDIANO', 80, 120, 115, 140, 75, 90);

INSERT INTO rango_vital_por_tamano
(tamano, frecuenciaMinima, frecuenciaMaxima, sistolicaMinima, sistolicaMaxima, diastolicaMinima, diastolicaMaxima)
VALUES
    ('GRANDE', 60, 100, 120, 150, 80, 95);

INSERT INTO mascota(nombre, estado_actual, tamano, raza, genero, tipo, peso, esteril, usuario_id)
VALUES ('Firulais', 'CAMINANDO', 'MEDIANO', 'Labrador', 'Macho', 'Perro', 10.5, true, 2);

CREATE TABLE IF NOT EXISTS registro_sueno (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              minutos_dormido INTEGER NOT NULL,
                                              fecha_y_hora DATETIME NOT NULL,
                                              mascota_id BIGINT NOT NULL,
                                              FOREIGN KEY (mascota_id) REFERENCES mascota(id)
);