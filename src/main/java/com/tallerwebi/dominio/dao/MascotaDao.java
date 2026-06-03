package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.Mascota;
import java.util.List;

/**
 * Repositorio de acceso a datos.
 */
public interface MascotaDao {
  Mascota buscarPorId(Long id);

  void modificar(Mascota mascota);

  List<Mascota> buscarTodas();
  List<Mascota> buscarPorUsuarioId(Long usuarioId);

  void guardar(Mascota mascota);
  void eliminar(Mascota mascota);
}
