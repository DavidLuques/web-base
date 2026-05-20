package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Actividad;

public interface RepositorioActividad {
  void guardar(Actividad actividad);
  Double obtenerDistanciaTotalPorMascota(Long mascotaId);
}
