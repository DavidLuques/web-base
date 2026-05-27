package com.tallerwebi.dominio;

public interface RepositorioUsuario {
  void guardar(Usuario usuario);
  Usuario buscar(String email);
  void modificar(Usuario usuario);
  Usuario buscarPorId(Long id);
}
