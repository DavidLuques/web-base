package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class AlertaServiceTest {

  private RepositorioAlerta repositorioAlertaMock;
  private AlertaService alertaService;

  private Mascota mascotaMacho;
  private Mascota mascotaHembra;
  private Analisis analisisActual;
  private Analisis analisisAnterior;

  @BeforeEach
  public void init() {
    repositorioAlertaMock = mock(RepositorioAlerta.class);
    alertaService = new AlertaService(repositorioAlertaMock);

    Raza razaPrueba = new Raza();
    razaPrueba.setNombre("Raza de Prueba");
    razaPrueba.setPesoMinMacho(new BigDecimal("10.0"));
    razaPrueba.setPesoMaxMacho(new BigDecimal("20.0"));
    razaPrueba.setPesoMinHembra(new BigDecimal("8.0"));
    razaPrueba.setPesoMaxHembra(new BigDecimal("18.0"));

    mascotaMacho = new Mascota();
    mascotaMacho.setNombre("Firulais");
    mascotaMacho.setGenero("Macho");
    mascotaMacho.setRaza(razaPrueba);

    mascotaHembra = new Mascota();
    mascotaHembra.setNombre("Luna");
    mascotaHembra.setGenero("Hembra");
    mascotaHembra.setRaza(razaPrueba);

    analisisAnterior = new Analisis();
    analisisAnterior.setDatos(new DatosAnalisis());
    analisisAnterior.getDatos().setTemperatura(38.5);
    analisisAnterior.getDatos().setPresionSistolica(120);
    analisisAnterior.getDatos().setFrecuenciaCardiaca(80);
    analisisAnterior.getDatos().setOxigenacion(98.0);
    analisisAnterior.getDatos().setHorasSueno(8);
    analisisAnterior.setFechaYHora(LocalDateTime.now().minusHours(1));

    analisisActual = new Analisis();
    analisisActual.setDatos(new DatosAnalisis());
    analisisActual.setFechaYHora(LocalDateTime.now());
  }

  @Test
  void debeGenerarAlertaPorBajoPesoMacho() {
    mascotaMacho.setPeso(new BigDecimal("8.0"));

    alertaService.evaluarPeso(mascotaMacho);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    assertEquals(TipoAlerta.ALERTA, captor.getValue().getTipo());
    assertEquals(
      "Atención: El peso de Firulais (8.0 kg) está por debajo del mínimo recomendado (10.0 kg).",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void debeGenerarAlertaPorAltoPesoMacho() {
    mascotaMacho.setPeso(new BigDecimal("22.0"));

    alertaService.evaluarPeso(mascotaMacho);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    assertEquals(TipoAlerta.ALERTA, captor.getValue().getTipo());
    assertEquals(
      "Atención: El peso de Firulais (22.0 kg) está por encima del máximo recomendado (20.0 kg).",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void noDebeGenerarAlertaPorPesoNormal() {
    mascotaMacho.setPeso(new BigDecimal("15.0"));

    alertaService.evaluarPeso(mascotaMacho);

    verify(repositorioAlertaMock, never()).save(any(Alerta.class));
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaCardiacaAlta() {
    analisisActual.getDatos().setFrecuenciaCardiaca(170);

    alertaService.evaluarSignosVitales(mascotaMacho, analisisActual, analisisAnterior);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    assertEquals(TipoAlerta.EMERGENCIA, captor.getValue().getTipo());
    assertEquals(
      "Emergencia: Frecuencia cardíaca inusualmente alta detectada (170 lpm).",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaCardiacaBaja() {
    analisisActual.getDatos().setFrecuenciaCardiaca(50);

    alertaService.evaluarSignosVitales(mascotaMacho, analisisActual, analisisAnterior);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    assertEquals(TipoAlerta.ALERTA, captor.getValue().getTipo());
    assertEquals(
      "Alerta: Frecuencia cardíaca inusualmente baja detectada (50 lpm).",
      captor.getValue().getMensaje()
    );
  }

  //  @Test
  //  void debeGenerarAlertaPorTemperaturaCorporalAlta() {
  //    analisisActual.getDatos().setTemperatura(40.0);
  //
  //    alertaService.evaluarSignosVitales(mascotaMacho, analisisActual, analisisAnterior);
  //
  //    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
  //    verify(repositorioAlertaMock).save(captor.capture());
  //
  //    assertEquals(TipoAlerta.EMERGENCIA, captor.getValue().getTipo());
  //    assertEquals(
  //            "Emergencia: Temperatura corporal alta detectada (40.0°C).",
  //            captor.getValue().getMensaje()
  //    );
  //  }
  //
  //  @Test
  //  void debeGenerarAlertaPorTemperaturaCorporalBaja() {
  //    analisisActual.getDatos().setTemperatura(37.0);
  //
  //    alertaService.evaluarSignosVitales(mascotaMacho, analisisActual, analisisAnterior);
  //
  //    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
  //    verify(repositorioAlertaMock).save(captor.capture());
  //
  //    assertEquals(TipoAlerta.ALERTA, captor.getValue().getTipo());
  //    assertEquals(
  //            "Alerta: Temperatura corporal baja detectada (37.0°C).",
  //            captor.getValue().getMensaje()
  //    );
  //  }

  @Test
  void debeGenerarAlertaPorCambioDrasticoTemperatura() {
    analisisAnterior.getDatos().setTemperatura(37.6);
    analisisActual.getDatos().setTemperatura(39.2);

    analisisActual.getDatos().setFrecuenciaCardiaca(85);
    analisisActual.getDatos().setPresionSistolica(125);

    alertaService.evaluarSignosVitales(mascotaMacho, analisisActual, analisisAnterior);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    assertEquals(TipoAlerta.EMERGENCIA, captor.getValue().getTipo());
    assertEquals(
      "Emergencia: Cambio drástico de temperatura detectado. De 37.6°C a 39.2°C.",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void debeGenerarAlertaPorNivelOxigenacionBajo() {
    analisisActual.getDatos().setOxigenacion(88.0);

    alertaService.evaluarSignosVitales(mascotaMacho, analisisActual, analisisAnterior);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    assertEquals(TipoAlerta.EMERGENCIA, captor.getValue().getTipo());
    assertEquals(
      "Emergencia: Nivel de oxigenación bajo detectado (88.0%).",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void debeGenerarAlertaPorHorasSuenoBajas() {
    analisisActual.getDatos().setHorasSueno(5);

    alertaService.evaluarSignosVitales(mascotaMacho, analisisActual, analisisAnterior);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    assertEquals(TipoAlerta.ALERTA, captor.getValue().getTipo());
    assertEquals(
      "Alerta: Horas de sueño bajas detectadas (5 horas).",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void debeGenerarAlertaPorCambioDrasticoPresionSistolica() {
    analisisActual.getDatos().setPresionSistolica(145);
    analisisActual.getDatos().setFrecuenciaCardiaca(85);
    analisisActual.getDatos().setTemperatura(38.6);

    alertaService.evaluarSignosVitales(mascotaMacho, analisisActual, analisisAnterior);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    assertEquals(TipoAlerta.ALERTA, captor.getValue().getTipo());
    assertEquals(
      "Alerta: Fluctuación significativa en la presión arterial detectada.",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void noDebeGenerarAlertaSiNoHayDatosActuales() {
    alertaService.evaluarSignosVitales(mascotaMacho, new Analisis(), analisisAnterior);
    verify(repositorioAlertaMock, never()).save(any(Alerta.class));
  }

  @Test
  void noDebeGenerarAlertaSiNoHayDatosAnterioresParaCambiosDrasticos() {
    analisisActual.getDatos().setFrecuenciaCardiaca(85);
    analisisActual.getDatos().setTemperatura(38.6);
    analisisActual.getDatos().setOxigenacion(95.0);
    analisisActual.getDatos().setHorasSueno(7);
    analisisActual.getDatos().setPresionSistolica(125);

    alertaService.evaluarSignosVitales(mascotaMacho, analisisActual, null);
    verify(repositorioAlertaMock, never()).save(any(Alerta.class));
  }
}
