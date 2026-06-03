package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.Mascota;
import java.util.List;

public interface MascotaDao {
  Mascota buscarPorId(Long id);

  void modificar(Mascota mascota);

  List<Mascota> buscarTodas();
  List<Mascota> buscarPorUsuarioId(Long usuarioId);

  void guardar(Mascota mascota);
  void eliminar(Mascota mascota);
}
