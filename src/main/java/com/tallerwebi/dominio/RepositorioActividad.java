package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Actividad;
import java.util.List;

/**
 * Repositorio de acceso a datos.
 */
public interface RepositorioActividad {
  void guardar(Actividad actividad);
  Double obtenerDistanciaTotalPorMascota(Long mascotaId);
  List<Actividad> buscarPorMascota(Long idMascota);
}
