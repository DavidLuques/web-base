package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AlertaService {

  private static final int FRECUENCIA_MAXIMA = 160;
  private static final int FRECUENCIA_MINIMA = 60;
  //  private static final int SUENO_MINIMO = 6;
  //  private static final int DIFERENCIA_PRESION_MAXIMA = 20;
  private static final int PRESION_SISTOLICA_CRITICA = 125;
  private static final BigDecimal TEMP_MAXIMA = new BigDecimal("39.5");
  private static final BigDecimal TEMP_MINIMA = new BigDecimal("37.5");
  //  private static final BigDecimal OXIGENACION_MINIMA = new BigDecimal("90");
  //  private static final BigDecimal DIFERENCIA_TEMP_MAXIMA = new BigDecimal("1.5");
  private static final String SUFIJO_LPM = " lpm).";
  private static final String SUFIJO_GRADOS = "°C).";

  private final RepositorioAlerta repositorioAlerta;

  public AlertaService(RepositorioAlerta repositorioAlerta) {
    this.repositorioAlerta = repositorioAlerta;
  }

  public void evaluarPeso(Mascota mascota) {
    if (mascota == null || mascota.getTamano() == null || mascota.getPeso() == null) {
      return;
    }
    Double peso = mascota.getPeso();
    Double pesoMinimo = obtenerPesoMinimo(mascota);
    if (pesoMinimo != null && peso < pesoMinimo) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Atencion: El peso de " +
        mascota.getNombre() +
        " (" +
        peso +
        " kg) esta por debajo del minimo recomendado (" +
        pesoMinimo +
        " kg)."
      );
      return;
    }
    Double pesoMaximo = obtenerPesoMaximo(mascota);
    if (pesoMaximo != null && peso > pesoMaximo) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Atencion: El peso de " +
        mascota.getNombre() +
        " (" +
        peso +
        " kg) esta por encima del maximo recomendado (" +
        pesoMaximo +
        " kg)."
      );
    }
  }

  private Double obtenerPesoMinimo(Mascota mascota) {
    switch (mascota.getTamano()) {
      case PEQUENO:
        return 2.0;
      case MEDIANO:
        return 11.0;
      case GRANDE:
        return 25.0;
      default:
        return null;
    }
  }

  private Double obtenerPesoMaximo(Mascota mascota) {
    switch (mascota.getTamano()) {
      case PEQUENO:
        return 10.0;
      case MEDIANO:
        return 25.0;
      case GRANDE:
        return 45.0;
      default:
        return null;
    }
  }

  public void evaluarLectura(Mascota mascota, LecturaSensor lectura) {
    if (lectura == null) return;
    evaluarFrecuenciaLectura(mascota, lectura);
    evaluarTemperaturaLectura(mascota, lectura);
    evaluarPresionLectura(mascota, lectura);
  }

  private void evaluarFrecuenciaLectura(Mascota mascota, LecturaSensor lectura) {
    Integer fc = lectura.getFrecuenciaCardiaca();
    if (fc == null) return;
    if (fc > FRECUENCIA_MAXIMA) {
      crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "Emergencia: Frecuencia cardiaca inusualmente alta detectada (" + fc + SUFIJO_LPM
      );
    } else if (fc < FRECUENCIA_MINIMA) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Frecuencia cardiaca inusualmente baja detectada (" + fc + SUFIJO_LPM
      );
    }
  }

  private void evaluarTemperaturaLectura(Mascota mascota, LecturaSensor lectura) {
    if (lectura.getTemperatura() == null) return;
    BigDecimal temp = BigDecimal.valueOf(lectura.getTemperatura());
    if (temp.compareTo(TEMP_MAXIMA) > 0) {
      crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "Emergencia: Temperatura corporal alta detectada (" + temp + SUFIJO_GRADOS
      );
    } else if (temp.compareTo(TEMP_MINIMA) < 0) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Temperatura corporal baja detectada (" + temp + SUFIJO_GRADOS
      );
    }
  }

  private void evaluarPresionLectura(Mascota mascota, LecturaSensor lectura) {
    Integer presion = lectura.getPresionSistolica();
    if (presion != null && presion > PRESION_SISTOLICA_CRITICA) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Fluctuacion significativa en la presion arterial detectada."
      );
    }
  }

  private void crearAlerta(Mascota mascota, TipoAlerta tipo, String mensaje) {
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
    if (idMascota == null) return java.util.Collections.emptyList();
    return repositorioAlerta
      .buscarPorMascota(idMascota)
      .stream()
      .map(a -> new AlertaDto(a.getId(), a.getTipo(), a.getMensaje()))
      .collect(java.util.stream.Collectors.toList());
  }
}
