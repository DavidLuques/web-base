package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Mascota;

/**
 * Repositorio de acceso a datos.
 */
public interface RepositorioMascota {
  Mascota buscarPorId(Long id);
  void guardar(Mascota mascota);
  void actualizar(Mascota mascota);
  void eliminar(Mascota mascota);
}
