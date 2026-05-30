package com.tallerwebi.dominio.servicio;

import static org.mockito.Mockito.*;

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
}
