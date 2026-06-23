package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.Mascota;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Implementación de servicio de alertas.
 */
@Service
public class ServicioAlertaImpl implements ServicioAlerta {

  private static final Logger logger = Logger.getLogger(ServicioAlertaImpl.class.getName());

  private final RepositorioAlerta repositorioAlerta;
  private final ServicioNotificaciones servicioNotificaciones;

  public ServicioAlertaImpl(
    RepositorioAlerta repositorioAlerta,
    ServicioNotificaciones servicioNotificaciones
  ) {
    this.repositorioAlerta = repositorioAlerta;
    this.servicioNotificaciones = servicioNotificaciones;
  }

  @Override
  public Alerta buscarUltimaAlertaDePeso(Long idMascota) {
    return repositorioAlerta.buscarUltimaAlertaDePesoPorMascota(idMascota);
  }

  @Override
  public void crearAlerta(Mascota mascota, TipoAlerta tipo, String mensaje) {
    Alerta alerta = new Alerta();
    alerta.setMascota(mascota);
    alerta.setTipo(tipo);
    alerta.setMensaje(mensaje);
    alerta.setFechaYHora(LocalDateTime.now());
    alerta.setLeido(false);
    repositorioAlerta.save(alerta);

    if (TipoAlerta.EMERGENCIA.equals(tipo)) {
      if (logger.isLoggable(java.util.logging.Level.INFO)) {
        logger.info(
          "Intentando enviar email de emergencia para mascota: " +
          (mascota != null ? mascota.getNombre() : "NULL")
        );
        logger.info(
          "Email del usuario: " +
          (mascota != null && mascota.getUsuario() != null
              ? mascota.getUsuario().getEmail()
              : "NULL")
        );
      }
      servicioNotificaciones.enviarNotificacionEmergencia(alerta);
    }
  }

  @Override
  @Transactional
  public List<AlertaDto> obtenerAlertasPorMascota(Long idMascota) {
    if (idMascota == null) {
      return java.util.Collections.emptyList();
    }
    return repositorioAlerta
      .buscarPorMascota(idMascota)
      .stream()
      .map(a ->
        new AlertaDto(
          a.getId(),
          a.getTipo(),
          a.obtenerTipoFormato(),
          a.getMensaje(),
          a.getFechaYHora().toString(),
          a.getLeido()
        )
      )
      .collect(java.util.stream.Collectors.toList());
  }

  @Override
  @Transactional
  public void marcarComoLeida(Long idAlerta) {
    Alerta alerta = repositorioAlerta.buscarPorId(idAlerta);
    if (alerta != null) {
      alerta.setLeido(true);
      repositorioAlerta.actualizar(alerta);
    }
  }
}
