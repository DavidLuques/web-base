package com.tallerwebi.dominio.servicio;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dao.ValladoDao;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EvaluadorServicioAlertaTest {

  private ServicioAlerta servicioAlertaMock;
  private ValladoDao valladoDaoMock;
  private ServicioEvaluadorAlerta servicioEvaluadorAlerta;
  private Mascota mascota;
  private RangoVitalPorTamano rango;

  private static final int FRECUENCIA_MINIMA = 80;
  private static final int FRECUENCIA_MAXIMA = 120;
  private static final int SISTOLICA_MINIMA = 115;
  private static final int SISTOLICA_MAXIMA = 140;
  private static final int DIASTOLICA_MINIMA = 75;
  private static final int DIASTOLICA_MAXIMA = 90;
  private static final double TEMP_MINIMA = 37.8;
  private static final double TEMP_MAXIMA = 39.2;

  @BeforeEach
  public void init() {
    servicioAlertaMock = mock(ServicioAlerta.class);
    valladoDaoMock = mock(ValladoDao.class);
    servicioEvaluadorAlerta = new ServicioEvaluadorAlertaImpl(servicioAlertaMock, valladoDaoMock);

    mascota = new Mascota();
    mascota.setNombre("Firulais");
    mascota.setTamano(TamanoMascota.MEDIANO);
    mascota.setPeso(15.0);

    rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(FRECUENCIA_MINIMA);
    rango.setFrecuenciaMaxima(FRECUENCIA_MAXIMA);
    rango.setSistolicaMinima(SISTOLICA_MINIMA);
    rango.setSistolicaMaxima(SISTOLICA_MAXIMA);
    rango.setDiastolicaMinima(DIASTOLICA_MINIMA);
    rango.setDiastolicaMaxima(DIASTOLICA_MAXIMA);
    rango.setTemperaturaMinima(TEMP_MINIMA);
    rango.setTemperaturaMaxima(TEMP_MAXIMA);
  }

  // PESO

  @Test
  void debeGenerarAlertaPorBajoPeso() {
    mascota.setPeso(8.0);
    when(servicioAlertaMock.buscarUltimaAlertaDePeso(any())).thenReturn(null);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("debajo"));
  }

  @Test
  void debeGenerarAlertaPorAltoPeso() {
    mascota.setPeso(28.0);
    when(servicioAlertaMock.buscarUltimaAlertaDePeso(any())).thenReturn(null);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("encima"));
  }

  @Test
  void noDebeGenerarAlertaPorPesoNormal() {
    mascota.setPeso(15.0);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiMascotaEsNull() {
    servicioEvaluadorAlerta.evaluarPeso(null);
    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiPesoEsNull() {
    mascota.setPeso(null);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiTamanoEsNull() {
    mascota.setTamano(null);
    mascota.setPeso(15.0);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorBajoPesoMascotaPequena() {
    mascota.setTamano(TamanoMascota.PEQUENO);
    mascota.setPeso(1.0);
    when(servicioAlertaMock.buscarUltimaAlertaDePeso(any())).thenReturn(null);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), anyString());
  }

  @Test
  void debeGenerarAlertaPorAltoPesoMascotaGrande() {
    mascota.setTamano(TamanoMascota.GRANDE);
    mascota.setPeso(50.0);
    when(servicioAlertaMock.buscarUltimaAlertaDePeso(any())).thenReturn(null);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), anyString());
  }

  @Test
  void noDebeGenerarAlertaPorPesoExactamenteEnElMinimoPermitido() {
    mascota.setPeso(11.0);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaPorPesoExactamenteEnElMaximoPermitido() {
    mascota.setPeso(25.0);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorPesoExactamenteUnDecimalDebajoDeLimiteMinimo() {
    mascota.setPeso(10.9);
    when(servicioAlertaMock.buscarUltimaAlertaDePeso(any())).thenReturn(null);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), anyString());
  }

  @Test
  void debeGenerarAlertaPorPesoExactamenteUnDecimalEncimaDeLimiteMaximo() {
    mascota.setPeso(25.1);
    when(servicioAlertaMock.buscarUltimaAlertaDePeso(any())).thenReturn(null);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), anyString());
  }

  @Test
  void elMensajeDeAlertaPorBajoPesoContieneElNombreDeLaMascota() {
    mascota.setPeso(8.0);
    when(servicioAlertaMock.buscarUltimaAlertaDePeso(any())).thenReturn(null);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock)
      .crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("Firulais"));
  }

  @Test
  void elMensajeDeAlertaPorAltoPesoContieneElPesoActual() {
    mascota.setPeso(28.0);
    when(servicioAlertaMock.buscarUltimaAlertaDePeso(any())).thenReturn(null);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("28.0"));
  }

  @Test
  void noDebeGenerarAlertaSiElPesoNoCambioDesdeUltimaAlerta() {
    mascota.setPeso(8.0);
    Alerta ultimaAlerta = new Alerta();
    ultimaAlerta.setMensaje(
      "Atencion: El peso de Firulais es 8.0 kg y esta por debajo del minimo recomendado (11.0 kg)."
    );
    when(servicioAlertaMock.buscarUltimaAlertaDePeso(any())).thenReturn(ultimaAlerta);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaSiElPesoCambioDesdeUltimaAlerta() {
    mascota.setPeso(7.5);
    Alerta ultimaAlerta = new Alerta();
    ultimaAlerta.setMensaje(
      "Atencion: El peso de Firulais es 8.0 kg y esta por debajo del minimo recomendado (11.0 kg)."
    );
    when(servicioAlertaMock.buscarUltimaAlertaDePeso(any())).thenReturn(ultimaAlerta);
    servicioEvaluadorAlerta.evaluarPeso(mascota);
    verify(servicioAlertaMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("7.5"));
  }

  // LECTURA

  @Test
  void noDebeGenerarAlertaSiLecturaEsNull() {
    servicioEvaluadorAlerta.evaluarLectura(mascota, null, rango);
    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaCardiacaAlta() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setFrecuenciaCardiaca(FRECUENCIA_MAXIMA + 1);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock)
      .crearAlerta(eq(mascota), eq(TipoAlerta.EMERGENCIA), contains("alta"));
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaCardiacaBaja() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setFrecuenciaCardiaca(FRECUENCIA_MINIMA - 1);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("baja"));
  }

  @Test
  void noDebeGenerarAlertaPorFrecuenciaExactamenteEnElLimiteMaximo() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setFrecuenciaCardiaca(FRECUENCIA_MAXIMA);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock, never()).crearAlerta(any(), eq(TipoAlerta.EMERGENCIA), any());
  }

  @Test
  void noDebeGenerarAlertaPorFrecuenciaExactamenteEnElLimiteMinimo() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setFrecuenciaCardiaca(FRECUENCIA_MINIMA);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiFrecuenciaEsNull() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setFrecuenciaCardiaca(null);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorTemperaturaAlta() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setTemperatura(TEMP_MAXIMA + 0.1);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock)
      .crearAlerta(eq(mascota), eq(TipoAlerta.EMERGENCIA), contains("alta"));
  }

  @Test
  void debeGenerarAlertaPorTemperaturaBaja() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setTemperatura(TEMP_MINIMA - 0.1);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("baja"));
  }

  @Test
  void noDebeGenerarAlertaPorTemperaturaExactamenteEnElMaximo() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setTemperatura(TEMP_MAXIMA);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaPorTemperaturaExactamenteEnElMinimo() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setTemperatura(TEMP_MINIMA);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiTemperaturaEsNull() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setTemperatura(null);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorPresionSistolicaAlta() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionSistolica(SISTOLICA_MAXIMA + 1);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock)
      .crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("sistolica"));
  }

  @Test
  void debeGenerarAlertaPorPresionSistolicaBaja() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionSistolica(SISTOLICA_MINIMA - 1);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock)
      .crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("sistolica"));
  }

  @Test
  void noDebeGenerarAlertaSiPresionSistolicaEsNull() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionSistolica(null);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorPresionDiastolicaAlta() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionDiastolica(DIASTOLICA_MAXIMA + 1);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock)
      .crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("diastolica"));
  }

  @Test
  void debeGenerarAlertaPorPresionDiastolicaBaja() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionDiastolica(DIASTOLICA_MINIMA - 1);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock)
      .crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("diastolica"));
  }

  @Test
  void noDebeGenerarAlertaSiPresionDiastolicaEsNull() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionDiastolica(null);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiTodosLosSignosSonNormales() {
    servicioEvaluadorAlerta.evaluarLectura(mascota, lecturaConSignosNormales(), rango);
    verify(servicioAlertaMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarMultiplesAlertasCuandoVariosSignosSonAnormales() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(FRECUENCIA_MAXIMA + 10);
    lectura.setTemperatura(TEMP_MAXIMA + 1.0);
    lectura.setPresionSistolica(SISTOLICA_MAXIMA + 10);
    lectura.setPresionDiastolica(DIASTOLICA_MAXIMA + 10);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    verify(servicioAlertaMock, times(4)).crearAlerta(eq(mascota), any(), any());
  }

  // HELPER

  private LecturaSensor lecturaConSignosNormales() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(100);
    lectura.setTemperatura(38.5);
    lectura.setPresionSistolica(125);
    lectura.setPresionDiastolica(82);
    return lectura;
  }
}
