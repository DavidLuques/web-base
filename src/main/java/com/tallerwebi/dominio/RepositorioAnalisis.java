package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Analisis;
import java.util.List;

/**
 * Repositorio de acceso a datos.
 */
public interface RepositorioAnalisis {
  void guardar(Analisis nuevoAnalisis);
  Analisis obtenerUltimoAnalisis(Long id);

  List<Analisis> buscarPorMascota(Long idMascota);
  List<Analisis> buscarPorMascotaAsc(Long idMascota);
}
