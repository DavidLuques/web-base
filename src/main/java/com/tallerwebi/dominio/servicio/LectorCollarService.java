package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.LecturaSensor;

/**
 * Servicio de lógica de negocio.
 */
public interface LectorCollarService {
  LecturaSensor obtenerLectura(Long idMascota);
}
