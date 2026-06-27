package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.modelo.Direccion;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.presentacion.DatosPerfil;
import java.util.List;
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
  private com.tallerwebi.dominio.dao.MascotaDao mascotaDao;
  private com.tallerwebi.dominio.RepositorioAlerta repositorioAlerta;
  private com.tallerwebi.dominio.dao.SolicitudAmistadDao solicitudAmistadDao;
  private com.tallerwebi.dominio.dao.SolicitudTransferenciaDao solicitudTransferenciaDao;

  @Autowired
  public ServicioUsuarioImpl(
    RepositorioUsuario repositorioUsuario,
    com.tallerwebi.dominio.dao.MascotaDao mascotaDao,
    com.tallerwebi.dominio.RepositorioAlerta repositorioAlerta,
    com.tallerwebi.dominio.dao.SolicitudAmistadDao solicitudAmistadDao,
    com.tallerwebi.dominio.dao.SolicitudTransferenciaDao solicitudTransferenciaDao
  ) {
    this.repositorioUsuario = repositorioUsuario;
    this.mascotaDao = mascotaDao;
    this.repositorioAlerta = repositorioAlerta;
    this.solicitudAmistadDao = solicitudAmistadDao;
    this.solicitudTransferenciaDao = solicitudTransferenciaDao;
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
      usuario.setActivo(false);
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

  @Override
  public void eliminarCuenta(Long id) {
    List<com.tallerwebi.dominio.modelo.Mascota> mascotas = mascotaDao.buscarTodoPorUsuarioId(id);
    if (mascotas != null) {
      for (com.tallerwebi.dominio.modelo.Mascota mascota : mascotas) {
        mascota.setUsuario(null);
        mascotaDao.modificar(mascota);
      }
    }
    repositorioAlerta.eliminarPorUsuario(id);
    solicitudTransferenciaDao.eliminarPorUsuario(id);
    solicitudAmistadDao.eliminarPorUsuario(id);
    Usuario usuario = repositorioUsuario.buscarPorId(id);
    if (usuario != null) {
      repositorioUsuario.eliminar(usuario);
    }
  }
}
