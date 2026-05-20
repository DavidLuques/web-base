package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Mascota;

public interface RepositorioMascota {
  Mascota buscarPorId(Long id);
  void guardar(Mascota mascota);
}
