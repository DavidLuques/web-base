package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.RegistroSueno;

public interface RepositorioSueno {
  void guardar(RegistroSueno registro);
  Integer obtenerTotalMinutosDormidosPorMascota(Long mascotaId);
}
