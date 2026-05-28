INSERT INTO Usuario(id, email, password, rol, activo, nombre, telefono, calle, ciudad, pais, codigoPostal, provincia, fechaCreacion, avatarUrl)
VALUES(null, 'test@unlam.edu.ar', '$2a$10$Zo1IHBSLamGSh6XEwmLXf.4b2vTFv.sUyxcnzBE4sp7KKt2Wz925y', 'USER', true, 'Test User', null, null, null, null, null, null, null, null);

INSERT INTO Usuario(id, email, password, rol, activo, nombre, telefono, calle, ciudad, pais, codigoPostal, provincia, fechaCreacion, avatarUrl)
VALUES(null, 'prueba@prueba.com', '$2a$10$lp3skFsst8lv9wQ.Xwz5Ae/4tjgpzanFRIQxd/WrLJAL0HrNFp.BO', 'USER', true, 'Usuario prueba', null, null, null, null, null, null, null, null);

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

CREATE TABLE IF NOT EXISTS alerta (
                                      pk_id_alerta BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      fk_id_mascota BIGINT NOT NULL,
                                      tipo VARCHAR(50) NOT NULL,
    mensaje LONGTEXT NOT NULL,
    fecha_y_hora DATETIME,
    leido BOOLEAN NOT NULL
    );