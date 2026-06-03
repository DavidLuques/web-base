package com.tallerwebi.dominio;

/**
 * Repositorio de acceso a datos.
 */
public interface RepositorioUsuario {
  void guardar(Usuario usuario);
  Usuario buscar(String email);
  void modificar(Usuario usuario);
  Usuario buscarPorId(Long id);
}
