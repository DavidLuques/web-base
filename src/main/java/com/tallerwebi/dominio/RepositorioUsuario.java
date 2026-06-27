package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Usuario;

/**
 * Repositorio de acceso a datos.
 */
public interface RepositorioUsuario {
  void guardar(Usuario usuario);
  Usuario buscar(String email);
  void modificar(Usuario usuario);
  Usuario buscarPorId(Long id);
  void eliminar(Usuario usuario);
}
