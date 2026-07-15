INSERT INTO Usuario(id, email, password, rol, activo, nombre, telefono, calle, ciudad, pais, codigoPostal, provincia, notificaciones_mail_activas)
VALUES(null, 'test@unlam.edu.ar', '$2a$10$Zo1IHBSLamGSh6XEwmLXf.4b2vTFv.sUyxcnzBE4sp7KKt2Wz925y', 'USER', true, 'Test User', null, null, null, null, null, null, false);

INSERT INTO Usuario(id, email, password, rol, activo, nombre, telefono, calle, ciudad, pais, codigoPostal, provincia, notificaciones_mail_activas)
VALUES(null, 'prueba@prueba.com', '$2a$10$lp3skFsst8lv9wQ.Xwz5Ae/4tjgpzanFRIQxd/WrLJAL0HrNFp.BO', 'USER', true, 'Usuario prueba', null, null, null, null, null, null, false);

INSERT INTO rango_vital_por_tamano
(tipo_mascota, tamano, frecuenciaMinima, frecuenciaMaxima, sistolicaMinima, sistolicaMaxima, diastolicaMinima, diastolicaMaxima, temperaturaMinima, temperaturaMaxima)
VALUES ('PERRO', 'PEQUENO', 100, 140, 110, 130, 70, 85, 38.0, 39.2);

INSERT INTO rango_vital_por_tamano
(tipo_mascota, tamano, frecuenciaMinima, frecuenciaMaxima, sistolicaMinima, sistolicaMaxima, diastolicaMinima, diastolicaMaxima, temperaturaMinima, temperaturaMaxima)
VALUES ('PERRO', 'MEDIANO', 80, 120, 115, 140, 75, 90, 37.8, 39.2);

INSERT INTO rango_vital_por_tamano
(tipo_mascota, tamano, frecuenciaMinima, frecuenciaMaxima, sistolicaMinima, sistolicaMaxima, diastolicaMinima, diastolicaMaxima, temperaturaMinima, temperaturaMaxima)
VALUES ('PERRO', 'GRANDE', 60, 100, 120, 150, 80, 95, 37.5, 38.9);

INSERT INTO rango_vital_por_tamano
(tipo_mascota, tamano, frecuenciaMinima, frecuenciaMaxima, sistolicaMinima, sistolicaMaxima, diastolicaMinima, diastolicaMaxima, temperaturaMinima, temperaturaMaxima)
VALUES ('GATO', 'PEQUENO', 140, 220, 120, 160, 70, 110, 38.0, 39.2);

INSERT INTO mascota(nombre, estado_actual, tamano, raza, genero, tipo, peso, esteril, usuario_id, activo)
VALUES ('Firulais', 'CAMINANDO', 'MEDIANO', 'Labrador', 'Macho', 'PERRO', 10.5, true, 1, true);

INSERT INTO vallado (id_mascota, latitud_centro, longitud_centro, radio_metros)
VALUES (1, -34.7222, -58.5250, 150.0);

INSERT INTO Usuario(id, email, password, rol, activo, nombre, telefono, calle, ciudad, pais, codigoPostal, provincia, notificaciones_mail_activas)
VALUES(null, 'luques.cc@gmail.com', '$2a$10$BrBw0uvjc9DLu7vl0J.8POSMmw/UJJyF6J.KKxX/2V/LBRe.ySQcG', 'USER', true, 'David luques', '1123906876', 'av siempre viva 111', 'Saltavilla', 'Argentina', '1842', 'Buenos Aires', false);

INSERT INTO Usuario(id, email, password, rol, activo, nombre, telefono, calle, ciudad, pais, codigoPostal, provincia, notificaciones_mail_activas)
VALUES(null, 'marcheschi97@hotmail.com', '$2a$10$1UK5Mno2SyZKsXbHdlGgoO8ii7CPaBNK90As2IYg3xiXX0QStS4Fu', 'USER', true, 'Sebastian Marcheschi', '1122334455', 'av siempre viva 111', 'saltavilla', 'Uruguay', '1842', 'Montevideo', false);