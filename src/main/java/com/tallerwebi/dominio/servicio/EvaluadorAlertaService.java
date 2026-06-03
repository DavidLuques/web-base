package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

/**
 * Servicio de lógica de negocio.
 */
@Service
public class EvaluadorAlertaService {

  private static final String SUFIJO_LPM = " lpm).";
  private static final String SUFIJO_GRADOS = "°C";
  private static final String SUFIJO_MMHG = " mmHg";
  private static final String SUFIJO_VALOR_MAXIMO =
    ", cuando el valor maximo normal deberia ser de hasta ";
  private static final String SUFIJO_VALOR_MINIMO =
    ", cuando el valor minimo normal deberia ser de hasta ";
  private static final String PREFIJO_ANOMALIA_PRESION =
    "Alerta: Anomalia en la medicion de la presion arterial de ";
  private static final String INFIJO_LA_PRESION = ". La presion ";

  private final AlertaService alertaService;

  public EvaluadorAlertaService(AlertaService alertaService) {
    this.alertaService = alertaService;
  }

  public void evaluarLectura(Mascota mascota, LecturaSensor lectura, RangoVitalPorTamano rango) {
    if (lectura == null) return;
    evaluarPeso(mascota);
    evaluarFrecuenciaCardiacaLectura(mascota, lectura, rango);
    evaluarTemperaturaLectura(mascota, lectura, rango);
    evaluarPresionSistolicaLectura(mascota, lectura, rango);
    evaluarPresionDiastolicaLectura(mascota, lectura, rango);
  }

  public void evaluarPeso(Mascota mascota) {
    if (mascota == null || mascota.getTamano() == null || mascota.getPeso() == null) return;
    Double peso = mascota.getPeso();
    if (estaBajoDePeso(mascota, peso)) return;
    evaluarSobrePeso(mascota, peso);
  }

  private boolean estaBajoDePeso(Mascota mascota, Double peso) {
    Double pesoMinimo = obtenerPesoMinimo(mascota);
    if (pesoMinimo == null || peso >= pesoMinimo) return false;
    if (pesoDistintoAlAnterior(mascota, peso)) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Atencion: El peso de " +
        mascota.getNombre() +
        " es " +
        peso +
        " kg y esta por debajo del minimo recomendado (" +
        pesoMinimo +
        " kg)."
      );
    }
    return true;
  }

  private void evaluarSobrePeso(Mascota mascota, Double peso) {
    Double pesoMaximo = obtenerPesoMaximo(mascota);
    if (pesoMaximo == null || peso <= pesoMaximo) return;
    if (pesoDistintoAlAnterior(mascota, peso)) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Atencion: El peso de " +
        mascota.getNombre() +
        " es " +
        peso +
        " kg y esta por encima del maximo recomendado (" +
        pesoMaximo +
        " kg)."
      );
    }
  }

  private boolean pesoDistintoAlAnterior(Mascota mascota, Double pesoActual) {
    Alerta ultima = alertaService.buscarUltimaAlertaDePeso(mascota.getId());
    if (ultima == null) return true; // primera vez, siempre dispara
    return !ultima.getMensaje().contains("" + pesoActual);
  }

  private void evaluarFrecuenciaCardiacaLectura(
    Mascota mascota,
    LecturaSensor lectura,
    RangoVitalPorTamano rango
  ) {
    Integer frecuenciaCardiaca = lectura.getFrecuenciaCardiaca();
    if (frecuenciaCardiaca == null) return;
    Integer frecuenciaCardiacaMaxima = rango.getFrecuenciaMaxima();
    if (frecuenciaCardiaca > frecuenciaCardiacaMaxima) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "Emergencia: La frecuencia cardiaca de" +
        mascota.getNombre() +
        " es inusualmente alta. La " +
        "frecuencia cardiaca es de " +
        frecuenciaCardiaca +
        SUFIJO_LPM +
        SUFIJO_VALOR_MAXIMO +
        frecuenciaCardiacaMaxima +
        SUFIJO_LPM +
        "."
      );
      return;
    }
    Integer frecuenciaCardiacaMinima = rango.getFrecuenciaMinima();
    if (frecuenciaCardiaca < frecuenciaCardiacaMinima) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Emergencia: La frecuencia cardiaca de" +
        mascota.getNombre() +
        " es inusualmente baja. La " +
        "frecuencia cardiaca es de " +
        frecuenciaCardiaca +
        SUFIJO_LPM +
        SUFIJO_VALOR_MINIMO +
        frecuenciaCardiacaMinima +
        SUFIJO_LPM +
        "."
      );
    }
  }

  private void evaluarTemperaturaLectura(
    Mascota mascota,
    LecturaSensor lectura,
    RangoVitalPorTamano rango
  ) {
    if (lectura.getTemperatura() == null) return;
    BigDecimal temp = BigDecimal.valueOf(lectura.getTemperatura());
    BigDecimal tempMax = BigDecimal.valueOf(rango.getTemperaturaMaxima());
    if (temp.compareTo(tempMax) > 0) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "Emergencia: La temperatura corporal de " +
        mascota.getNombre() +
        " es alta. La temperatura corporal " +
        "es de " +
        temp +
        SUFIJO_GRADOS +
        SUFIJO_VALOR_MAXIMO +
        tempMax +
        SUFIJO_GRADOS +
        "."
      );
      return;
    }
    BigDecimal tempMin = BigDecimal.valueOf(rango.getTemperaturaMinima());
    if (temp.compareTo(tempMin) < 0) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Emergencia: La temperatura corporal de " +
        mascota.getNombre() +
        " es baja. La temperatura corporal " +
        "es de " +
        temp +
        SUFIJO_GRADOS +
        SUFIJO_VALOR_MINIMO +
        tempMin +
        SUFIJO_GRADOS +
        "."
      );
    }
  }

  private void evaluarPresionSistolicaLectura(
    Mascota mascota,
    LecturaSensor lectura,
    RangoVitalPorTamano rango
  ) {
    Integer presionSistolica = lectura.getPresionSistolica();
    Integer presionSistolicaMaxima = rango.getSistolicaMaxima();
    if (presionSistolica != null && presionSistolica > presionSistolicaMaxima) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        PREFIJO_ANOMALIA_PRESION +
        mascota.getNombre() +
        INFIJO_LA_PRESION +
        "arterial sistolica es de " +
        presionSistolica +
        SUFIJO_MMHG +
        SUFIJO_VALOR_MAXIMO +
        presionSistolicaMaxima +
        SUFIJO_MMHG +
        "."
      );
      return;
    }
    Integer presionSistolicaMinima = rango.getSistolicaMinima();
    if (presionSistolica != null && presionSistolica < presionSistolicaMinima) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        PREFIJO_ANOMALIA_PRESION +
        mascota.getNombre() +
        INFIJO_LA_PRESION +
        "arterial sistolica es de " +
        presionSistolica +
        SUFIJO_MMHG +
        SUFIJO_VALOR_MINIMO +
        presionSistolicaMinima +
        SUFIJO_MMHG +
        "."
      );
    }
  }

  private void evaluarPresionDiastolicaLectura(
    Mascota mascota,
    LecturaSensor lectura,
    RangoVitalPorTamano rango
  ) {
    Integer presionDiastolica = lectura.getPresionDiastolica();
    Integer presionDiastolicaMaxima = rango.getDiastolicaMaxima();
    if (presionDiastolica != null && presionDiastolica > presionDiastolicaMaxima) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        PREFIJO_ANOMALIA_PRESION +
        mascota.getNombre() +
        INFIJO_LA_PRESION +
        "arterial diastolica es de " +
        presionDiastolica +
        SUFIJO_MMHG +
        SUFIJO_VALOR_MAXIMO +
        presionDiastolicaMaxima +
        SUFIJO_MMHG +
        "."
      );
      return;
    }
    Integer presionDiastolicaMinima = rango.getDiastolicaMinima();
    if (presionDiastolica != null && presionDiastolica < presionDiastolicaMinima) {
      alertaService.crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        PREFIJO_ANOMALIA_PRESION +
        mascota.getNombre() +
        INFIJO_LA_PRESION +
        "arterial diastolica es de " +
        presionDiastolica +
        SUFIJO_MMHG +
        SUFIJO_VALOR_MINIMO +
        presionDiastolicaMinima +
        SUFIJO_MMHG +
        "."
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
