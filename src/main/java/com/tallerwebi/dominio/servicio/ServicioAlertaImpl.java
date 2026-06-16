package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.Mascota;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Implementación de servicio de alertas.
 */
@Service
public class ServicioAlertaImpl implements ServicioAlerta {

  private final RepositorioAlerta repositorioAlerta;

  public ServicioAlertaImpl(RepositorioAlerta repositorioAlerta) {
    this.repositorioAlerta = repositorioAlerta;
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
      .map(a -> new AlertaDto(a.getId(), a.getTipo(), a.getMensaje()))
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
