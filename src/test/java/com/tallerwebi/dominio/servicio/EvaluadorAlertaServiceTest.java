package com.tallerwebi.dominio.servicio;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.contains;

import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EvaluadorAlertaServiceTest {

  private AlertaService alertaServiceMock;
  private EvaluadorAlertaService evaluadorAlertaService;
  private Mascota mascota;

  @BeforeEach
  public void init() {
    alertaServiceMock = mock(AlertaService.class);
    evaluadorAlertaService = new EvaluadorAlertaService(alertaServiceMock);

    mascota = new Mascota();
    mascota.setNombre("Firulais");
    mascota.setTamano(TamanoMascota.MEDIANO);
  }

  @Test
  void debeGenerarAlertaPorBajoPeso() {
    mascota.setPeso(8.0);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock)
      .crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Atencion: El peso de Firulais (8.0 kg) esta por debajo del minimo recomendado (11.0 kg)."
      );
  }

  @Test
  void debeGenerarAlertaPorAltoPeso() {
    mascota.setPeso(28.0);
    evaluadorAlertaService.evaluarPeso(mascota);
    verify(alertaServiceMock)
      .crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Atencion: El peso de Firulais (28.0 kg) esta por encima del maximo recomendado (25.0 kg)."
      );
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
  void noDebeGenerarAlertaSiLecturaEsNull() {
    evaluadorAlertaService.evaluarLectura(mascota, null);
    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaCardiacaAlta() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(170);
    lectura.setTemperatura(38.5);
    lectura.setPresionSistolica(120);

    evaluadorAlertaService.evaluarLectura(mascota, lectura);

    verify(alertaServiceMock)
      .crearAlerta(
        mascota,
        TipoAlerta.EMERGENCIA,
        "Emergencia: Frecuencia cardiaca inusualmente alta detectada (170 lpm)."
      );
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaCardiacaBaja() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(50);
    lectura.setTemperatura(38.5);
    lectura.setPresionSistolica(120);

    evaluadorAlertaService.evaluarLectura(mascota, lectura);

    verify(alertaServiceMock)
      .crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Frecuencia cardiaca inusualmente baja detectada (50 lpm)."
      );
  }

  @Test
  void debeGenerarAlertaPorTemperaturaAlta() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(100);
    lectura.setTemperatura(40.0);
    lectura.setPresionSistolica(120);

    evaluadorAlertaService.evaluarLectura(mascota, lectura);

    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.EMERGENCIA), anyString());
  }

  @Test
  void debeGenerarAlertaPorTemperaturaBaja() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(100);
    lectura.setTemperatura(37.0);
    lectura.setPresionSistolica(120);

    evaluadorAlertaService.evaluarLectura(mascota, lectura);

    verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), anyString());
  }

  @Test
  void debeGenerarAlertaPorPresionAlta() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(100);
    lectura.setTemperatura(38.5);
    lectura.setPresionSistolica(170);

    evaluadorAlertaService.evaluarLectura(mascota, lectura);

    verify(alertaServiceMock)
      .crearAlerta(
        mascota,
        TipoAlerta.ALERTA,
        "Alerta: Fluctuacion significativa en la presion arterial detectada."
      );
  }

  @Test
  void noDebeGenerarAlertaSiSignosNormales() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(100);
    lectura.setTemperatura(38.5);
    lectura.setPresionSistolica(120);

    evaluadorAlertaService.evaluarLectura(mascota, lectura);

    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiFrecuenciaEsNull() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(null);
    lectura.setTemperatura(38.5);
    lectura.setPresionSistolica(120);

    evaluadorAlertaService.evaluarLectura(mascota, lectura);

    verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }
  
  // Tests extra commit 31/5
  
  @Test
  void noDebeGenerarAlertaSiTemperaturaEsNull() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(100);
      lectura.setTemperatura(null);
      lectura.setPresionSistolica(120);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiPresionEsNull() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(100);
      lectura.setTemperatura(38.5);
      lectura.setPresionSistolica(null);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaExactamenteEnElLimiteAlto() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(161);
      lectura.setTemperatura(38.5);
      lectura.setPresionSistolica(120);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock).crearAlerta(mascota, TipoAlerta.EMERGENCIA,
          "Emergencia: Frecuencia cardiaca inusualmente alta detectada (161 lpm).");
  }

  @Test
  void noDebeGenerarAlertaPorFrecuenciaExactamenteEnElLimiteMaximo() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(160);
      lectura.setTemperatura(38.5);
      lectura.setPresionSistolica(120);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock, never()).crearAlerta(any(), eq(TipoAlerta.EMERGENCIA), any());
  }

  @Test
  void noDebeGenerarAlertaPorFrecuenciaExactamenteEnElLimiteMinimo() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(60);
      lectura.setTemperatura(38.5);
      lectura.setPresionSistolica(120);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaExactamenteDebajoDeLimiteMinimo() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(59);
      lectura.setTemperatura(38.5);
      lectura.setPresionSistolica(120);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock).crearAlerta(mascota, TipoAlerta.ALERTA,
          "Alerta: Frecuencia cardiaca inusualmente baja detectada (59 lpm).");
  }

  @Test
  void debeGenerarAlertaPorTemperaturaExactamenteEnElLimiteAlto() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(100);
      lectura.setTemperatura(39.6);
      lectura.setPresionSistolica(120);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.EMERGENCIA), anyString());
  }

  @Test
  void noDebeGenerarAlertaPorTemperaturaExactamenteEnElMaximo() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(100);
      lectura.setTemperatura(39.5);
      lectura.setPresionSistolica(120);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void noDebeGenerarAlertaPorTemperaturaExactamenteEnElMinimo() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(100);
      lectura.setTemperatura(37.5);
      lectura.setPresionSistolica(120);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorTemperaturaExactamenteDebajoDeLimiteMinimo() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(100);
      lectura.setTemperatura(37.4);
      lectura.setPresionSistolica(120);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock).crearAlerta(eq(mascota), eq(TipoAlerta.ALERTA), anyString());
  }

  @Test
  void noDebeGenerarAlertaPorPresionExactamenteEnElLimite() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(100);
      lectura.setTemperatura(38.5);
      lectura.setPresionSistolica(125);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
  }

  @Test
  void debeGenerarAlertaPorPresionExactamenteUnPorEncimaDelLimite() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(100);
      lectura.setTemperatura(38.5);
      lectura.setPresionSistolica(126);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock).crearAlerta(mascota, TipoAlerta.ALERTA,
          "Alerta: Fluctuacion significativa en la presion arterial detectada.");
  }

  @Test
  void debeGenerarMultiplesAlertasCuandoVariosSignosSonAnormales() {
      LecturaSensor lectura = new LecturaSensor();
      lectura.setFrecuenciaCardiaca(170);
      lectura.setTemperatura(40.0);
      lectura.setPresionSistolica(170);

      evaluadorAlertaService.evaluarLectura(mascota, lectura);

      verify(alertaServiceMock, times(3)).crearAlerta(eq(mascota), any(), any());
  }

  @Test
  void noDebeGenerarAlertaSiTamanoEsNull() {
      mascota.setTamano(null);
      mascota.setPeso(15.0);

      evaluadorAlertaService.evaluarPeso(mascota);

      verify(alertaServiceMock, never()).crearAlerta(any(), any(), any());
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

      verify(alertaServiceMock).crearAlerta(
          eq(mascota),
          eq(TipoAlerta.ALERTA),
          contains("Firulais")
      );
  }

  @Test
  void elMensajeDeAlertaPorAltoPesoContieneElPesoActual() {
      mascota.setPeso(28.0);

      evaluadorAlertaService.evaluarPeso(mascota);

      verify(alertaServiceMock).crearAlerta(
          eq(mascota),
          eq(TipoAlerta.ALERTA),
          contains("28.0")
      );
  }
}
