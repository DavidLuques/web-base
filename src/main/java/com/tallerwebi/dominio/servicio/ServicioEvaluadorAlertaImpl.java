package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dao.ValladoDao;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import com.tallerwebi.dominio.modelo.Vallado;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * Implementación de servicio evaluador de alertas.
 */
@Service
public class ServicioEvaluadorAlertaImpl implements ServicioEvaluadorAlerta {

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
  private static final int RADIO_TIERRA = 6371000;
  private static final long INTERVALO_ALERTA_VALLADO_SEGUNDOS = 30;

  private final ServicioAlerta servicioAlerta;
  private final ValladoDao valladoDao;

  public ServicioEvaluadorAlertaImpl(ServicioAlerta servicioAlerta, ValladoDao valladoDao) {
    this.servicioAlerta = servicioAlerta;
    this.valladoDao = valladoDao;
  }

  @Override
  public void evaluarLectura(Mascota mascota, LecturaSensor lectura, RangoVitalPorTamano rango) {
    if (lectura == null) return;
    evaluarPeso(mascota);
    evaluarFrecuenciaCardiacaLectura(mascota, lectura, rango);
    evaluarTemperaturaLectura(mascota, lectura, rango);
    evaluarPresionSistolicaLectura(mascota, lectura, rango);
    evaluarPresionDiastolicaLectura(mascota, lectura, rango);

    Vallado vallado = valladoDao.buscarPorMascota(mascota.getId());
    if (vallado != null) {
      double distancia = calcularDistanciaHaversine(
        vallado.getLatitudCentro(),
        vallado.getLongitudCentro(),
        lectura.getLatitud(),
        lectura.getLongitud()
      );
      evaluarVallado(mascota, lectura, vallado, distancia);
    }
  }

  private double calcularDistanciaHaversine(Double lat1, Double lon1, Double lat2, Double lon2) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double senoDLatMedio = Math.sin(dLat / 2);
    double senoDLonMedio = Math.sin(dLon / 2);
    double distanciaAngularMitad =
      senoDLatMedio * senoDLatMedio +
      Math.cos(Math.toRadians(lat1)) *
        Math.cos(Math.toRadians(lat2)) *
        senoDLonMedio *
        senoDLonMedio;
    double distanciaAngular = 2 * Math.asin(Math.sqrt(distanciaAngularMitad));
    return RADIO_TIERRA * distanciaAngular;
  }

  @Override
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
      servicioAlerta.crearAlerta(
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
      servicioAlerta.crearAlerta(
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
    Alerta ultima = servicioAlerta.buscarUltimaAlertaDePeso(mascota.getId());
    if (ultima == null) return true;
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
      servicioAlerta.crearAlerta(
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
      servicioAlerta.crearAlerta(
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
      servicioAlerta.crearAlerta(
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
      servicioAlerta.crearAlerta(
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
      servicioAlerta.crearAlerta(
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
      servicioAlerta.crearAlerta(
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
      servicioAlerta.crearAlerta(
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
      servicioAlerta.crearAlerta(
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

  @Override
  public void evaluarVallado(
    Mascota mascota,
    LecturaSensor lectura,
    Vallado vallado,
    double distanciaMetros
  ) {
    if (lectura == null || vallado == null) return;

    if (distanciaMetros > vallado.getRadioMetros() && vallado.getActivo() == true) {
      Alerta ultimaAlertaVallado = servicioAlerta.buscarUltimaAlertaDeVallado(mascota.getId());
      if (
        ultimaAlertaVallado != null &&
        Duration.between(ultimaAlertaVallado.getFechaYHora(), LocalDateTime.now()).getSeconds() <
          INTERVALO_ALERTA_VALLADO_SEGUNDOS
      ) {
        // Si ya se envió una alerta de vallado en los últimos 30 segundos, no enviar otra.
        return;
      }

      double distanciaExceso = distanciaMetros - vallado.getRadioMetros();
      servicioAlerta.crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "EMERGENCIA: " +
        mascota.getNombre() +
        " se alejo " +
        Math.round(distanciaExceso) +
        " metros del area permitida (Radio: " +
        Math.round(vallado.getRadioMetros()) +
        " metros)."
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
