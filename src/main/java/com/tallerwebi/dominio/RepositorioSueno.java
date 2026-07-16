package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.RegistroSueno;
import java.util.List;

/**
 * Repositorio de acceso a datos.
 */
public interface RepositorioSueno {
  void guardar(RegistroSueno registro);
  Integer obtenerTotalMinutosDormidosPorMascota(Long mascotaId);
  List<RegistroSueno> buscarPorMascota(Long idMascota);
}
