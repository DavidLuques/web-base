package com.tallerwebi.dominio.servicio;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ActualizadorEstadoMascotaSchedulerTest {

  private SimulacionActividadService simulacionActividadServiceMock;
  private ActualizadorEstadoMascotaScheduler scheduler;

  @BeforeEach
  public void init() {
    simulacionActividadServiceMock = mock(SimulacionActividadService.class);
    scheduler = new ActualizadorEstadoMascotaScheduler(simulacionActividadServiceMock);
  }

  @Test
  public void cuandoSeEjecutaElSchedulerDeActualizarEstadoEntoncesDebeLlamarAlServicio() {
    // Ejecución
    scheduler.actualizarEstadosDeMascotas();

    // Verificación
    verify(simulacionActividadServiceMock, times(1)).simularDetalleParaTodas();
  }

  @Test
  public void cuandoSeEjecutaElSchedulerDeActualizarFrecuenciaEntoncesDebeLlamarAlServicio() {
    // Ejecución
    scheduler.actualizarFrecuenciaCardiaca();

    // Verificación
    verify(simulacionActividadServiceMock, times(1)).actualizarFrecuenciaParaTodas();
  }
}
