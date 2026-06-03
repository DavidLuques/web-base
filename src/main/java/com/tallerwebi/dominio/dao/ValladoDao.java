package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.Vallado;

public interface ValladoDao {
  Vallado buscarPorMascota(Long idMascota);
  void guardar(Vallado vallado);
  void modificar(Vallado vallado);
  void eliminar(Long idVallado);
}
