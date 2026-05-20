package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.DatosAnalisis;
import com.tallerwebi.dominio.modelo.Mascota;
import java.math.BigDecimal;

public class AlertaService {

  // CONSTANTES
  private static final int FRECUENCIA_MAXIMA = 160;
  private static final int FRECUENCIA_MINIMA = 60;
  private static final int SUENO_MINIMO = 6;
  private static final int DIFERENCIA_PRESION_MAXIMA = 20;

  private static final BigDecimal TEMP_MAXIMA = new BigDecimal("39.5");
  private static final BigDecimal TEMP_MINIMA = new BigDecimal("37.5");
  private static final BigDecimal OXIGENACION_MINIMA = new BigDecimal("90");
  private static final BigDecimal DIFERENCIA_TEMP_MAXIMA = new BigDecimal("1.5");

  private final RepositorioAlerta repositorioAlerta;

  public AlertaService(RepositorioAlerta repositorioAlerta) {
    this.repositorioAlerta = repositorioAlerta;
  }

  public void evaluarPeso(Mascota mascota) {
    if (mascota == null || mascota.getRaza() == null || mascota.getPeso() == null) {
      return;
    }

    BigDecimal peso = mascota.getPeso();

    if (obtenerPesoMinimo(mascota) != null && peso.compareTo(obtenerPesoMinimo(mascota)) < 0) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Atención: El peso de " +
        mascota.getNombre() +
        " (" +
        peso +
        " kg) está por debajo del mínimo recomendado (" +
        obtenerPesoMinimo(mascota) +
        " kg)."
      );
    } else if (
      obtenerPesoMaximo(mascota) != null && peso.compareTo(obtenerPesoMaximo(mascota)) > 0
    ) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Atención: El peso de " +
        mascota.getNombre() +
        " (" +
        peso +
        " kg) está por encima del máximo recomendado (" +
        obtenerPesoMaximo(mascota) +
        " kg)."
      );
    }
  }

  private BigDecimal obtenerPesoMinimo(Mascota mascota) {
    if ("Macho".equalsIgnoreCase(mascota.getGenero())) {
      return mascota.getRaza().getPesoMinMacho();
    }
    if ("Hembra".equalsIgnoreCase(mascota.getGenero())) {
      return mascota.getRaza().getPesoMinHembra();
    }
    return null;
  }

  private BigDecimal obtenerPesoMaximo(Mascota mascota) {
    if ("Macho".equalsIgnoreCase(mascota.getGenero())) {
      return mascota.getRaza().getPesoMaxMacho();
    }
    if ("Hembra".equalsIgnoreCase(mascota.getGenero())) {
      return mascota.getRaza().getPesoMaxHembra();
    }
    return null;
  }

  public void evaluarSignosVitales(Mascota mascota, Analisis actual, Analisis anterior) {
    if (actual == null || actual.getDatos() == null) {
      return;
    }

    DatosAnalisis datosActuales = actual.getDatos();

    evaluarFrecuenciaCardiaca(mascota, datosActuales);
    evaluarTemperaturaCorporal(mascota, datosActuales);
    evaluarOxigenacion(mascota, datosActuales);
    evaluarHorasSueno(mascota, datosActuales);

    if (anterior != null && anterior.getDatos() != null) {
      evaluarCambiosDrasticos(mascota, datosActuales, anterior.getDatos());
    }
  }

  private void evaluarFrecuenciaCardiaca(Mascota mascota, DatosAnalisis actual) {
    Integer fc = actual.getFrecuenciaCardiaca();
    if (fc == null) return;

    if (fc > FRECUENCIA_MAXIMA) {
      crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "Emergencia: Frecuencia cardíaca inusualmente alta detectada (" + fc + " lpm)."
      );
    } else if (fc < FRECUENCIA_MINIMA) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Frecuencia cardíaca inusualmente baja detectada (" + fc + " lpm)."
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
        "Emergencia: Temperatura corporal alta detectada (" + temp + "°C)."
      );
    } else if (temp.compareTo(TEMP_MINIMA) < 0) {
      crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Temperatura corporal baja detectada (" + temp + "°C)."
      );
    }
  }

  private void evaluarOxigenacion(Mascota mascota, DatosAnalisis actual) {
    BigDecimal oxigeno = actual.getOxigenacionBigDecimal();
    if (oxigeno != null && oxigeno.compareTo(OXIGENACION_MINIMA) < 0) {
      crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "Emergencia: Nivel de oxigenación bajo detectado (" + oxigeno + "%)."
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
            "Emergencia: Cambio drástico de temperatura detectado. De %s°C a %s°C.",
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
          "Alerta: Fluctuación significativa en la presión arterial detectada."
        );
      }
    }
  }

  private void crearAlerta(Mascota mascota, TipoAlerta tipo, String mensaje) {
    Alerta alerta = new Alerta();
    alerta.setMascota(mascota);
    alerta.setTipo(tipo);
    alerta.setMensaje(mensaje);
    repositorioAlerta.save(alerta);
  }
}
