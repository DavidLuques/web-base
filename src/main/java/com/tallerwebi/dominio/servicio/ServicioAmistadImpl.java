package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dao.SolicitudAmistadDao;
import com.tallerwebi.dominio.enums.EstadoAmistad;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.excepcion.AccionNoPermitidaEnEsteEstadoException;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.modelo.SolicitudAmistad;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioAmistadImpl implements ServicioAmistad {

  private final SolicitudAmistadDao solicitudAmistadDao;
  private final RepositorioUsuario repositorioUsuario;
  private final ServicioAlerta servicioAlerta;

  @Autowired
  public ServicioAmistadImpl(
    SolicitudAmistadDao solicitudAmistadDao,
    RepositorioUsuario repositorioUsuario,
    ServicioAlerta servicioAlerta
  ) {
    this.solicitudAmistadDao = solicitudAmistadDao;
    this.repositorioUsuario = repositorioUsuario;
    this.servicioAlerta = servicioAlerta;
  }

  @Override
  public SolicitudAmistad enviarSolicitud(Long idEmisor, Long idReceptor) {
    SolicitudAmistad existente = solicitudAmistadDao.buscarEntreUsuarios(idEmisor, idReceptor);
    if (existente != null) {
      throw new AccionNoPermitidaEnEsteEstadoException(
        "Ya existe una solicitud entre estos usuarios"
      );
    }

    Usuario emisor = repositorioUsuario.buscarPorId(idEmisor);
    Usuario receptor = repositorioUsuario.buscarPorId(idReceptor);

    SolicitudAmistad solicitud = new SolicitudAmistad();
    solicitud.setEmisor(emisor);
    solicitud.setReceptor(receptor);
    solicitudAmistadDao.guardar(solicitud);

    servicioAlerta.crearAlertaUsuario(
      receptor,
      TipoAlerta.INFO,
      emisor.getNombre() + " te envio una solicitud de amistad."
    );

    return solicitud;
  }

  @Override
  public void aceptarSolicitud(Long idSolicitud) {
    SolicitudAmistad solicitud = solicitudAmistadDao.buscarPorId(idSolicitud);
    if (!solicitud.getEstado().getComportamiento().puedeAceptar()) {
      throw new AccionNoPermitidaEnEsteEstadoException(
        "No se puede aceptar una solicitud en estado " +
        solicitud.getEstado().getComportamiento().getNombre()
      );
    }
    solicitud.setEstado(EstadoAmistad.ACEPTADA);
    solicitudAmistadDao.modificar(solicitud);

    servicioAlerta.crearAlertaUsuario(
      solicitud.getEmisor(),
      TipoAlerta.INFO,
      solicitud.getReceptor().getNombre() + " acepto tu solicitud de amistad."
    );
  }

  @Override
  public void rechazarSolicitud(Long idSolicitud) {
    SolicitudAmistad solicitud = solicitudAmistadDao.buscarPorId(idSolicitud);
    if (!solicitud.getEstado().getComportamiento().puedeRechazar()) {
      throw new AccionNoPermitidaEnEsteEstadoException(
        "No se puede rechazar una solicitud en estado " +
        solicitud.getEstado().getComportamiento().getNombre()
      );
    }
    solicitud.setEstado(EstadoAmistad.RECHAZADA);
    solicitudAmistadDao.modificar(solicitud);

    servicioAlerta.crearAlertaUsuario(
      solicitud.getEmisor(),
      TipoAlerta.INFO,
      solicitud.getReceptor().getNombre() + " rechazo tu solicitud de amistad."
    );
  }

  @Override
  public boolean sonAmigos(Long idUsuario1, Long idUsuario2) {
    SolicitudAmistad solicitud = solicitudAmistadDao.buscarEntreUsuarios(idUsuario1, idUsuario2);
    return solicitud != null && solicitud.getEstado().getComportamiento().sonAmigos();
  }

  @Override
  public List<Usuario> obtenerAmigos(Long idUsuario) {
    List<SolicitudAmistad> aceptadas = solicitudAmistadDao.buscarAceptadasPorUsuario(idUsuario);
    List<Usuario> amigos = new ArrayList<>();
    for (SolicitudAmistad solicitud : aceptadas) {
      if (solicitud.getEmisor().getId().equals(idUsuario)) {
        amigos.add(solicitud.getReceptor());
      } else {
        amigos.add(solicitud.getEmisor());
      }
    }
    return amigos;
  }

  @Override
  public List<SolicitudAmistad> obtenerSolicitudesPendientes(Long idUsuario) {
    return solicitudAmistadDao.buscarPendientesPorReceptor(idUsuario);
  }

  @Override
  public SolicitudAmistad enviarSolicitudPorEmail(Long idEmisor, String emailReceptor) {
    Usuario receptor = repositorioUsuario.buscar(emailReceptor);
    if (receptor == null) {
      throw new UsuarioNoEncontrado("No existe un usuario con ese email");
    }
    if (receptor.getId().equals(idEmisor)) {
      throw new AccionNoPermitidaEnEsteEstadoException("No podes agregarte a vos mismo");
    }
    return enviarSolicitud(idEmisor, receptor.getId());
  }

  @Override
  public List<SolicitudAmistad> obtenerSolicitudesEnviadas(Long idUsuario) {
    return solicitudAmistadDao.buscarEnviadasPorEmisor(idUsuario);
  }

  @Override
  public void cancelarSolicitud(Long idSolicitud) {
    SolicitudAmistad solicitud = solicitudAmistadDao.buscarPorId(idSolicitud);
    if (!solicitud.getEstado().getComportamiento().puedeCancelar()) {
      throw new AccionNoPermitidaEnEsteEstadoException(
        "No se puede cancelar una solicitud en estado " +
        solicitud.getEstado().getComportamiento().getNombre()
      );
    }
    solicitudAmistadDao.eliminar(solicitud);
  }

  @Override
  public void eliminarAmigo(Long idUsuario, Long idAmigo) {
    SolicitudAmistad solicitud = solicitudAmistadDao.buscarEntreUsuarios(idUsuario, idAmigo);
    if (solicitud == null || !solicitud.getEstado().getComportamiento().puedeEliminar()) {
      throw new AccionNoPermitidaEnEsteEstadoException("No se puede eliminar esta amistad");
    }
    solicitudAmistadDao.eliminar(solicitud);
  }
}
