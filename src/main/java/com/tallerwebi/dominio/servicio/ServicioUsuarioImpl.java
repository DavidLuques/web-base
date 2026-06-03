package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.modelo.Direccion;
import com.tallerwebi.presentacion.DatosPerfil;
import javax.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio de lógica de negocio.
 */
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
  public DatosPerfil obtenerDatosPerfil(Long id) {
    Usuario usuario = repositorioUsuario.buscarPorId(id);
    if (usuario == null) {
      throw new UsuarioNoEncontrado("Usuario no encontrado");
    }

    DatosPerfil datosPerfil = new DatosPerfil();
    datosPerfil.setNombre(usuario.getNombre());
    datosPerfil.setEmail(usuario.getEmail());
    datosPerfil.setTelefono(usuario.getTelefono());

    if (usuario.getUbicacion() != null) {
      datosPerfil.setCalle(usuario.getUbicacion().getCalle());
      datosPerfil.setCiudad(usuario.getUbicacion().getCiudad());
      datosPerfil.setProvincia(usuario.getUbicacion().getProvincia());
      datosPerfil.setPais(usuario.getUbicacion().getPais());
      datosPerfil.setCodigoPostal(usuario.getUbicacion().getCodigoPostal());
    }

    return datosPerfil;
  }

  @Override
  public void eliminar(Long id) {
    Usuario usuario = repositorioUsuario.buscarPorId(id);
    if (usuario != null) {
      usuario.setActivo(false); // queda el usuario como inactivo en lugar de eliminarlo fisicamente
    }
  }

  @Override
  public void actualizarPerfil(Long id, DatosPerfil datosPerfil) {
    Usuario usuario = repositorioUsuario.buscarPorId(id);
    if (usuario == null) {
      throw new UsuarioNoEncontrado("Usuario no encontrado");
    }

    usuario.setNombre(datosPerfil.getNombre());
    usuario.setEmail(datosPerfil.getEmail());
    usuario.setTelefono(datosPerfil.getTelefono());

    if (datosPerfil.getPassword() != null && !datosPerfil.getPassword().isEmpty()) {
      usuario.setPassword(BCrypt.hashpw(datosPerfil.getPassword(), BCrypt.gensalt()));
    }

    Direccion ubicacion = usuario.getUbicacion();
    if (ubicacion == null) {
      ubicacion = new Direccion();
    }
    ubicacion.setCalle(datosPerfil.getCalle());
    ubicacion.setCiudad(datosPerfil.getCiudad());
    ubicacion.setProvincia(datosPerfil.getProvincia());
    ubicacion.setPais(datosPerfil.getPais());
    ubicacion.setCodigoPostal(datosPerfil.getCodigoPostal());
    usuario.setUbicacion(ubicacion);

    repositorioUsuario.modificar(usuario);
  }
}
