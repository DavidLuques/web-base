package com.tallerwebi.dominio.servicio;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EvaluadorAlertaServiceTest {

  private AlertaService alertaServiceMock;
  private EvaluadorAlertaService evaluadorAlertaService;
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
    alertaServiceMock = mock(AlertaService.class);
    evaluadorAlertaService = new EvaluadorAlertaService(alertaServiceMock);

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
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("debajo"));
  }

  @Test
  void debeGenerarAlertaPorAltoPeso() {
    mascota.setPeso(28.0);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("encima"));
  }

  @Test
  void noDebeGenerarAlertaPorPesoNormal() {
    mascota.setPeso(15.0);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiMascotaEsNull() {
    evaluadorAlertaService.evaluarPeso(null);
    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiPesoEsNull() {
    mascota.setPeso(null);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiTamanoEsNull() {
    mascota.setTamano(null);
    mascota.setPeso(15.0);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorBajoPesoMascotaPequena() {
    mascota.setTamano(TamanoMascota.PEQUENO);
    mascota.setPeso(1.0);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), anyString());
  }

  @Test
  void debeGenerarAlertaPorAltoPesoMascotaGrande() {
    mascota.setTamano(TamanoMascota.GRANDE);
    mascota.setPeso(50.0);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), anyString());
  }

  @Test
  void noDebeGenerarAlertaPorPesoExactamenteEnElMinimoPermitido() {
    mascota.setPeso(11.0);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaPorPesoExactamenteEnElMaximoPermitido() {
    mascota.setPeso(25.0);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorPesoExactamenteUnDecimalDebajoDeLimiteMinimo() {
    mascota.setPeso(10.9);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), anyString());
  }

  @Test
  void debeGenerarAlertaPorPesoExactamenteUnDecimalEncimaDeLimiteMaximo() {
    mascota.setPeso(25.1);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), anyString());
  }

  @Test
  void elMensajeDeAlertaPorBajoPesoContieneElNombreDeLaMascota() {
    mascota.setPeso(8.0);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("Firulais"));
  }

  @Test
  void elMensajeDeAlertaPorAltoPesoContieneElPesoActual() {
    mascota.setPeso(28.0);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("28.0"));
  }

  // LECTURA

  @Test
  void noDebeGenerarAlertaSiLecturaEsNull() {
    evaluadorAlertaService.evaluarLectura(mascota, null, rango);
    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaCardiacaAlta() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setFrecuenciaCardiaca(FRECUENCIA_MAXIMA + 1);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.EMERGENCIA), contains("alta"));
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaCardiacaBaja() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setFrecuenciaCardiaca(FRECUENCIA_MINIMA - 1);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("baja"));
  }

  @Test
  void noDebeGenerarAlertaPorFrecuenciaExactamenteEnElLimiteMaximo() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setFrecuenciaCardiaca(FRECUENCIA_MAXIMA);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock, never()).crearAlerta(any(), eq(TipoAlerta.EMERGENCIA), any());
  }

  @Test
  void noDebeGenerarAlertaPorFrecuenciaExactamenteEnElLimiteMinimo() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setFrecuenciaCardiaca(FRECUENCIA_MINIMA);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiFrecuenciaEsNull() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setFrecuenciaCardiaca(null);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorTemperaturaAlta() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setTemperatura(TEMP_MAXIMA + 0.1);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.EMERGENCIA), contains("alta"));
  }

  @Test
  void debeGenerarAlertaPorTemperaturaBaja() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setTemperatura(TEMP_MINIMA - 0.1);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("baja"));
  }

  @Test
  void noDebeGenerarAlertaPorTemperaturaExactamenteEnElMaximo() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setTemperatura(TEMP_MAXIMA);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaPorTemperaturaExactamenteEnElMinimo() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setTemperatura(TEMP_MINIMA);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiTemperaturaEsNull() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setTemperatura(null);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorPresionSistolicaAlta() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionSistolica(SISTOLICA_MAXIMA + 1);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock)
      .crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("sistolica"));
  }

  @Test
  void debeGenerarAlertaPorPresionSistolicaBaja() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionSistolica(SISTOLICA_MINIMA - 1);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock)
      .crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("sistolica"));
  }

  @Test
  void noDebeGenerarAlertaSiPresionSistolicaEsNull() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionSistolica(null);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorPresionDiastolicaAlta() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionDiastolica(DIASTOLICA_MAXIMA + 1);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock)
      .crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("diastolica"));
  }

  @Test
  void debeGenerarAlertaPorPresionDiastolicaBaja() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionDiastolica(DIASTOLICA_MINIMA - 1);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock)
      .crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), contains("diastolica"));
  }

  @Test
  void noDebeGenerarAlertaSiPresionDiastolicaEsNull() {
    LecturaSensor lectura = lecturaConSignosNormales();
    lectura.setPresionDiastolica(null);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiTodosLosSignosSonNormales() {
    evaluadorAlertaService.evaluarLectura(mascota, lecturaConSignosNormales(), rango);
    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarMultiplesAlertasCuandoVariosSignosSonAnormales() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(FRECUENCIA_MAXIMA + 10);
    lectura.setTemperatura(TEMP_MAXIMA + 1.0);
    lectura.setPresionSistolica(SISTOLICA_MAXIMA + 10);
    lectura.setPresionDiastolica(DIASTOLICA_MAXIMA + 10);

    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    verify(alertaServiceMock, times(4)).crearAlerta(eq(mascota), any(), any());
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
