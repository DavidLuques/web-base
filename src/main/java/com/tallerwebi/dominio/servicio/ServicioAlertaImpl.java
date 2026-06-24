package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.Mascota;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Implementación de servicio de alertas.
 */
@Service
public class ServicioAlertaImpl implements ServicioAlerta {

  private final RepositorioAlerta repositorioAlerta;
  // Servicio de notificaciones es opcional (en tests puede no existir JavaMailSender)
  private ServicioNotificaciones servicioNotificaciones;

  public ServicioAlertaImpl(RepositorioAlerta repositorioAlerta) {
    this.repositorioAlerta = repositorioAlerta;
  }

  @org.springframework.beans.factory.annotation.Autowired(required = false)
  public void setServicioNotificaciones(ServicioNotificaciones servicioNotificaciones) {
    this.servicioNotificaciones = servicioNotificaciones;
  }

  @Override
  public Alerta buscarUltimaAlertaDePeso(Long idMascota) {
    return repositorioAlerta.buscarUltimaAlertaDePesoPorMascota(idMascota);
  }

  @Override
  public Alerta buscarUltimaAlertaDeVallado(Long idMascota) {
    return repositorioAlerta.buscarUltimaAlertaDeValladoPorMascota(idMascota);
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

    if (TipoAlerta.EMERGENCIA.equals(tipo) && servicioNotificaciones != null) {
      servicioNotificaciones.enviarNotificacionEmergencia(alerta);
    }
  }

  @Override
  public void crearAlertaUsuario(Usuario usuario, TipoAlerta tipo, String mensaje) {
    Alerta alerta = new Alerta(usuario, tipo, mensaje);
    repositorioAlerta.save(alerta);

    if (TipoAlerta.EMERGENCIA.equals(tipo) && servicioNotificaciones != null) {
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
      .map(this::mapearADto)
      .collect(java.util.stream.Collectors.toList());
  }

  @Override
  @Transactional
  public List<AlertaDto> obtenerAlertasPorUsuario(Long idUsuario) {
    if (idUsuario == null) {
      return java.util.Collections.emptyList();
    }
    return repositorioAlerta
      .buscarPorUsuario(idUsuario)
      .stream()
      .map(this::mapearADto)
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

  @Override
  @Transactional
  public List<Map<String, Object>> obtenerEmergenciasActivasPorUsuario(Long idUsuario) {
    return repositorioAlerta
      .buscarEmergenciasActivasPorUsuario(idUsuario)
      .stream()
      .map(a -> {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", a.getId());
        map.put("mensaje", a.getMensaje());
        map.put("nombreMascota", a.getMascota().getNombre());
        return map;
      })
      .collect(java.util.stream.Collectors.toList());
  }

  private AlertaDto mapearADto(Alerta alerta) {
    return new AlertaDto(
      alerta.getId(),
      alerta.getTipo(),
      alerta.obtenerTipoFormato(),
      alerta.getMensaje(),
      alerta.getFechaYHora().toString(),
      alerta.getLeido()
    );
  }
}
