package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.DatosAnalisis;
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
  private static final int SUENO_MINIMO = 6;
  private static final int DIFERENCIA_PRESION_MAXIMA = 20;
  private static final int PRESION_SISTOLICA_CRITICA = 125;
  private static final BigDecimal TEMP_MAXIMA = new BigDecimal("39.5");
  private static final BigDecimal TEMP_MINIMA = new BigDecimal("37.5");
  private static final BigDecimal OXIGENACION_MINIMA = new BigDecimal("90");
  private static final BigDecimal DIFERENCIA_TEMP_MAXIMA = new BigDecimal("1.5");
  private static final String SUFIJO_LPM = " lpm).";
  private static final String SUFIJO_GRADOS = "\u00b0C).";

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
        "Atenci\u00f3n: El peso de " +
        mascota.getNombre() +
        " (" +
        peso +
        " kg) est\u00e1 por debajo del m\u00ednimo recomendado (" +
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
        "Atenci\u00f3n: El peso de " +
        mascota.getNombre() +
        " (" +
        peso +
        " kg) est\u00e1 por encima del m\u00edximo recomendado (" +
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

  public void evaluarSignosVitales(Mascota mascota, Analisis actual, Analisis anterior) {
    if (actual == null || actual.getDatos() == null) return;
    DatosAnalisis datosActuales = actual.getDatos();
    evaluarFrecuenciaCardiaca(mascota, datosActuales);
    evaluarTemperaturaCorporal(mascota, datosActuales);
    evaluarOxigenacion(mascota, datosActuales);
    evaluarHorasSueno(mascota, datosActuales);
    if (anterior != null && anterior.getDatos() != null) {
      evaluarCambiosDrasticos(mascota, datosActuales, anterior.getDatos());
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
        "Emergencia: Frecuencia card\u00edaca inusualmente alta detectada (" + fc + SUFIJO_LPM
      );
    } else if (fc < FRECUENCIA_MINIMA) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Frecuencia card\u00edaca inusualmente baja detectada (" + fc + SUFIJO_LPM
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
        "Alerta: Fluctuaci\u00f3n significativa en la presi\u00f3n arterial detectada."
      );
    }
  }

  private void evaluarFrecuenciaCardiaca(Mascota mascota, DatosAnalisis actual) {
    Integer fc = actual.getFrecuenciaCardiaca();
    if (fc == null) return;
    if (fc > FRECUENCIA_MAXIMA) {
      crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "Emergencia: Frecuencia card\u00edaca inusualmente alta detectada (" + fc + SUFIJO_LPM
      );
    } else if (fc < FRECUENCIA_MINIMA) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Frecuencia card\u00edaca inusualmente baja detectada (" + fc + SUFIJO_LPM
      );
    }
  }

  private void evaluarTemperaturaCorporal(Mascota mascota, DatosAnalisis actual) {
    BigDecimal temp = actual.getTemperaturaBigDecimal();
    if (temp == null) return;
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

  private void evaluarOxigenacion(Mascota mascota, DatosAnalisis actual) {
    BigDecimal oxigeno = actual.getOxigenacionBigDecimal();
    if (oxigeno != null && oxigeno.compareTo(OXIGENACION_MINIMA) < 0) {
      crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "Emergencia: Nivel de oxigenaci\u00f3n bajo detectado (" + oxigeno + "%)."
      );
    }
  }

  private void evaluarHorasSueno(Mascota mascota, DatosAnalisis actual) {
    Integer sueno = actual.getHorasSueno();
    if (sueno != null && sueno < SUENO_MINIMO) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Horas de sueño bajas detectadas (" + sueno + " horas)."
      );
    }
  }

  private void evaluarCambiosDrasticos(
    Mascota mascota,
    DatosAnalisis actual,
    DatosAnalisis anterior
  ) {
    BigDecimal tempActual = actual.getTemperaturaBigDecimal();
    BigDecimal tempAnterior = anterior.getTemperaturaBigDecimal();
    if (tempActual != null && tempAnterior != null) {
      BigDecimal difTemp = tempActual.subtract(tempAnterior).abs();
      if (difTemp.compareTo(DIFERENCIA_TEMP_MAXIMA) >= 0) {
        crearAlerta(
          mascota,
          TipoAlerta.EMERGENCIA,
          String.format(
            "Emergencia: Cambio dr\u00e1stico de temperatura detectado. De %s°C a %s°C.",
            tempAnterior,
            tempActual
          )
        );
      }
    }
    Integer presionActual = actual.getPresionSistolica();
    Integer presionAnterior = anterior.getPresionSistolica();
    if (presionActual != null && presionAnterior != null) {
      int difPresion = Math.abs(presionActual - presionAnterior);
      if (difPresion > DIFERENCIA_PRESION_MAXIMA) {
        crearAlerta(
          mascota,
          TipoAlerta.ALERTA,
          "Alerta: Fluctuaci\u00f3n significativa en la presi\u00f3n arterial detectada."
        );
      }
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
