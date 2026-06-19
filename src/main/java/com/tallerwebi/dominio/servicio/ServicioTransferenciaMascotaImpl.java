package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.SolicitudTransferenciaDao;
import com.tallerwebi.dominio.enums.EstadoTransferencia;
import com.tallerwebi.dominio.excepcion.AccionNoPermitidaEnEsteEstadoException;
import com.tallerwebi.dominio.excepcion.NoSonAmigosException;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.SolicitudTransferencia;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioTransferenciaMascotaImpl implements ServicioTransferenciaMascota {

  private final SolicitudTransferenciaDao solicitudTransferenciaDao;
  private final MascotaDao mascotaDao;
  private final RepositorioUsuario repositorioUsuario;
  private final ServicioAmistad servicioAmistad;

  @Autowired
  public ServicioTransferenciaMascotaImpl(
    SolicitudTransferenciaDao solicitudTransferenciaDao,
    MascotaDao mascotaDao,
    RepositorioUsuario repositorioUsuario,
    ServicioAmistad servicioAmistad
  ) {
    this.solicitudTransferenciaDao = solicitudTransferenciaDao;
    this.mascotaDao = mascotaDao;
    this.repositorioUsuario = repositorioUsuario;
    this.servicioAmistad = servicioAmistad;
  }

  @Override
  public SolicitudTransferencia iniciarTransferencia(
    Long idMascota,
    Long idOrigen,
    Long idDestino
  ) {
    if (!servicioAmistad.sonAmigos(idOrigen, idDestino)) {
      throw new NoSonAmigosException("Solo podés transferir mascotas a tus amigos");
    }

    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    Usuario origen = repositorioUsuario.buscarPorId(idOrigen);
    Usuario destino = repositorioUsuario.buscarPorId(idDestino);

    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    solicitud.setMascota(mascota);
    solicitud.setUsuarioOrigen(origen);
    solicitud.setUsuarioDestino(destino);
    solicitudTransferenciaDao.guardar(solicitud);
    return solicitud;
  }

  @Override
  public SolicitudTransferencia confirmarPorOrigen(Long idSolicitud) {
    SolicitudTransferencia solicitud = solicitudTransferenciaDao.buscarPorId(idSolicitud);
    validarPuedeConfirmar(solicitud);
    solicitud.setConfirmadaPorOrigen(true);
    completarSiCorresponde(solicitud);
    solicitudTransferenciaDao.modificar(solicitud);
    return solicitud;
  }

  @Override
  public SolicitudTransferencia confirmarPorDestino(Long idSolicitud) {
    SolicitudTransferencia solicitud = solicitudTransferenciaDao.buscarPorId(idSolicitud);
    validarPuedeConfirmar(solicitud);
    solicitud.setConfirmadaPorDestino(true);
    completarSiCorresponde(solicitud);
    solicitudTransferenciaDao.modificar(solicitud);
    return solicitud;
  }

  @Override
  public void cancelarTransferencia(Long idSolicitud) {
    SolicitudTransferencia solicitud = solicitudTransferenciaDao.buscarPorId(idSolicitud);
    if (!solicitud.getEstado().getComportamiento().puedeCancelar()) {
      throw new AccionNoPermitidaEnEsteEstadoException(
        "No se puede cancelar una transferencia en estado " +
        solicitud.getEstado().getComportamiento().getNombre()
      );
    }
    solicitud.setEstado(EstadoTransferencia.CANCELADA);
    solicitudTransferenciaDao.modificar(solicitud);
  }

  @Override
  public List<SolicitudTransferencia> obtenerPendientesPorUsuario(Long idUsuario) {
    return solicitudTransferenciaDao.buscarPendientesPorUsuario(idUsuario);
  }

  // ── privados ─────────────────────────────────────────────

  private void validarPuedeConfirmar(SolicitudTransferencia solicitud) {
    boolean puedeConfirmar =
      solicitud.getEstado().getComportamiento().puedeConfirmarOrigen() ||
      solicitud.getEstado().getComportamiento().puedeConfirmarDestino();
    if (!puedeConfirmar) {
      throw new AccionNoPermitidaEnEsteEstadoException(
        "No se puede confirmar una transferencia en estado " +
        solicitud.getEstado().getComportamiento().getNombre()
      );
    }
  }

  private void completarSiCorresponde(SolicitudTransferencia solicitud) {
    if (
      Boolean.TRUE.equals(solicitud.getConfirmadaPorOrigen()) &&
      Boolean.TRUE.equals(solicitud.getConfirmadaPorDestino())
    ) {
      Mascota mascota = solicitud.getMascota();
      mascota.setUsuario(solicitud.getUsuarioDestino());
      mascotaDao.modificar(mascota);
      solicitud.setEstado(EstadoTransferencia.COMPLETADA);
    }
  }
}
