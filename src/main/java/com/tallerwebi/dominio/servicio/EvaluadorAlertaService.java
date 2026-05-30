package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class EvaluadorAlertaService {

  private static final int FRECUENCIA_MAXIMA = 160;
  private static final int FRECUENCIA_MINIMA = 60;
  private static final int PRESION_SISTOLICA_CRITICA = 125;
  private static final BigDecimal TEMP_MAXIMA = new BigDecimal("39.5");
  private static final BigDecimal TEMP_MINIMA = new BigDecimal("37.5");
  private static final String SUFIJO_LPM = " lpm).";
  private static final String SUFIJO_GRADOS = "°C).";

  private final AlertaService alertaService;

  public EvaluadorAlertaService(AlertaService alertaService) {
    this.alertaService = alertaService;
  }

  public void evaluarPeso(Mascota mascota) {
    if (mascota == null || mascota.getTamano() == null || mascota.getPeso() == null) return;
    Double peso = mascota.getPeso();
    Double pesoMinimo = obtenerPesoMinimo(mascota);
    if (pesoMinimo != null && peso < pesoMinimo) {
      alertaService.crearAlerta(
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
      alertaService.crearAlerta(
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
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "Emergencia: Frecuencia cardiaca inusualmente alta detectada (" + fc + SUFIJO_LPM
      );
    } else if (fc < FRECUENCIA_MINIMA) {
      alertaService.crearAlerta(
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
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "Emergencia: Temperatura corporal alta detectada (" + temp + SUFIJO_GRADOS
      );
    } else if (temp.compareTo(TEMP_MINIMA) < 0) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Temperatura corporal baja detectada (" + temp + SUFIJO_GRADOS
      );
    }
  }

  private void evaluarPresionLectura(Mascota mascota, LecturaSensor lectura) {
    Integer presion = lectura.getPresionSistolica();
    if (presion != null && presion > PRESION_SISTOLICA_CRITICA) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Fluctuacion significativa en la presion arterial detectada."
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
}
