package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.Mascota;

public interface MascotaDao {
  Mascota buscarPorId(Long id);

  void modificar(Mascota mascota);
}
