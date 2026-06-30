package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.RegistroHistorial;
import java.util.List;

public interface RegistroHistorialDao {
  void guardar(RegistroHistorial registro);
  RegistroHistorial buscarPorId(Long id);
  List<RegistroHistorial> buscarPorMascota(Long idMascota);
}
