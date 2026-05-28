package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.*;
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

    mascotaMacho = new Mascota();
    mascotaMacho.setNombre("Firulais");
    mascotaMacho.setGenero("Macho");
    mascotaMacho.setRaza("Labrador");
    mascotaMacho.setTamano(TamanoMascota.MEDIANO); // Asignamos tamaño para la alerta

    mascotaHembra = new Mascota();
    mascotaHembra.setNombre("Luna");
    mascotaHembra.setGenero("Hembra");
    mascotaHembra.setRaza("Caniche");
    mascotaHembra.setTamano(TamanoMascota.MEDIANO);

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
    mascotaMacho.setPeso(8.0); // Inferior a 11.0 (Mínimo para Mediano)

    alertaService.evaluarPeso(mascotaMacho);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    assertEquals(TipoAlerta.ALERTA, captor.getValue().getTipo());
    assertEquals(
      "Atencion: El peso de Firulais (8.0 kg) esta por debajo del minimo recomendado (11.0 kg).",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void debeGenerarAlertaPorAltoPesoMacho() {
    mascotaMacho.setPeso(28.0); // Superior a 25.0 (Máximo para Mediano)

    alertaService.evaluarPeso(mascotaMacho);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    assertEquals(TipoAlerta.ALERTA, captor.getValue().getTipo());
    assertEquals(
      "Atencion: El peso de Firulais (28.0 kg) esta por encima del maximo recomendado (25.0 kg).",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void noDebeGenerarAlertaPorPesoNormal() {
    mascotaMacho.setPeso(15.0); // Entre 11.0 y 25.0

    alertaService.evaluarPeso(mascotaMacho);

    verify(repositorioAlertaMock, never()).save(any(Alerta.class));
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaCardiacaAltaEnLectura() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(170);
    lectura.setTemperatura(38.5);
    lectura.setPresionSistolica(120);

    alertaService.evaluarLectura(mascotaMacho, lectura);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());
    assertEquals(TipoAlerta.EMERGENCIA, captor.getValue().getTipo());
    assertEquals(
      "Emergencia: Frecuencia cardiaca inusualmente alta detectada (170 lpm).",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void debeGenerarAlertaPorFrecuenciaCardiacaBajaEnLectura() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(50);
    lectura.setTemperatura(38.5);
    lectura.setPresionSistolica(120);

    alertaService.evaluarLectura(mascotaMacho, lectura);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());
    assertEquals(TipoAlerta.ALERTA, captor.getValue().getTipo());
    assertEquals(
      "Alerta: Frecuencia cardiaca inusualmente baja detectada (50 lpm).",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void debeGenerarAlertaPorTemperaturaAltaEnLectura() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(100);
    lectura.setTemperatura(40.0);
    lectura.setPresionSistolica(120);

    alertaService.evaluarLectura(mascotaMacho, lectura);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());
    assertEquals(TipoAlerta.EMERGENCIA, captor.getValue().getTipo());
    assertEquals(
      "Emergencia: Temperatura corporal alta detectada (40.0°C).",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void debeGenerarAlertaPorTemperaturaBajaEnLectura() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(100);
    lectura.setTemperatura(37.0);
    lectura.setPresionSistolica(120);

    alertaService.evaluarLectura(mascotaMacho, lectura);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());
    assertEquals(TipoAlerta.ALERTA, captor.getValue().getTipo());
    assertEquals(
      "Alerta: Temperatura corporal baja detectada (37.0\u00b0C).",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void debeGenerarAlertaPorPresionAltaEnLectura() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(100);
    lectura.setTemperatura(38.5);
    lectura.setPresionSistolica(170);

    alertaService.evaluarLectura(mascotaMacho, lectura);

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());
    assertEquals(TipoAlerta.ALERTA, captor.getValue().getTipo());
    assertEquals(
      "Alerta: Fluctuacion significativa en la presion arterial detectada.",
      captor.getValue().getMensaje()
    );
  }

  @Test
  void noDebeGenerarAlertaSiLecturaEsNull() {
    alertaService.evaluarLectura(mascotaMacho, null);
    verify(repositorioAlertaMock, never()).save(any(Alerta.class));
  }

  @Test
  void noDebeGenerarAlertaSiSignosNormalesEnLectura() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(100);
    lectura.setTemperatura(38.5);
    lectura.setPresionSistolica(120);

    alertaService.evaluarLectura(mascotaMacho, lectura);
    verify(repositorioAlertaMock, never()).save(any(Alerta.class));
  }
}
