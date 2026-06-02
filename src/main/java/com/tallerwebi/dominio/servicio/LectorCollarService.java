package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.LecturaSensor;

public interface LectorCollarService {
  LecturaSensor obtenerLectura(Long idMascota);
}
