package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Actividad;

/**
 * Repositorio de acceso a datos.
 */
public interface RepositorioActividad {
  void guardar(Actividad actividad);
  Double obtenerDistanciaTotalPorMascota(Long mascotaId);
}
