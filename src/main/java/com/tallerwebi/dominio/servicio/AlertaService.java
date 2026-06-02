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

@Service
public class AlertaService {

  private final RepositorioAlerta repositorioAlerta;

  public AlertaService(RepositorioAlerta repositorioAlerta) {
    this.repositorioAlerta = repositorioAlerta;
  }

  public Alerta buscarUltimaAlertaDePeso(Long idMascota) {
    return repositorioAlerta.buscarUltimaAlertaDePesoPorMascota(idMascota);
  }

  public void crearAlerta(Mascota mascota, TipoAlerta tipo, String mensaje) {
    Alerta alerta = new Alerta();
    alerta.setMascota(mascota);
    alerta.setTipo(tipo);
    alerta.setMensaje(mensaje);
    alerta.setFechaYHora(LocalDateTime.now());
    alerta.setLeido(false);
    repositorioAlerta.save(alerta);
  }

  @Transactional
  public List<AlertaDto> obtenerAlertasPorMascota(Long idMascota) {
    if (idMascota == null) return java.util.Collections.emptyList(); //Evita la excepcion, devolviendo una lista vacia
    return repositorioAlerta
      .buscarPorMascota(idMascota)
      .stream()
      .map(a -> new AlertaDto(a.getId(), a.getTipo(), a.getMensaje()))
      .collect(java.util.stream.Collectors.toList());
  }
}
