INSERT INTO Usuario(id, email, password, rol, activo) VALUES(null, 'test@unlam.edu.ar', 'test', 'ADMIN', true);

INSERT INTO rango_vital_por_tamano (tamano, frecuenciaMinima, frecuenciaMaxima)
VALUES ('PEQUENO', 100, 140);

INSERT INTO rango_vital_por_tamano (tamano, frecuenciaMinima, frecuenciaMaxima)
VALUES ('MEDIANO', 80, 120);

INSERT INTO rango_vital_por_tamano (tamano, frecuenciaMinima, frecuenciaMaxima)
VALUES ('GRANDE', 60, 100);

INSERT INTO mascota (nombre, peso, tamano)
VALUES ('Toby', 18.5, 'MEDIANO');