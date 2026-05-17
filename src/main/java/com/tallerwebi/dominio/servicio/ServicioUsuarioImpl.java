package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioUsuario")
@Transactional
public class ServicioUsuarioImpl implements ServicioUsuario {

  private RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioUsuarioImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public Usuario obtenerPerfil(Long id) {
    return repositorioUsuario.buscarPorId(id);
  }

  @Override
  public void eliminar(Long id) {
    Usuario usuario = repositorioUsuario.buscarPorId(id);
    if (usuario != null) {
      usuario.setActivo(false); // queda el usuario como inactivo en lugar de eliminarlo fisicamente
    }
  }
}
