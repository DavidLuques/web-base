package com.tallerwebi.dominio.servicio;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ActualizadorEstadoMascotaSchedulerTest {

  private ActualizadorEstadoMascotaScheduler scheduler;
  private SimulacionActividadService simulacionActividadServiceMock;

  @BeforeEach
  public void init() {
    simulacionActividadServiceMock = mock(SimulacionActividadService.class);
    scheduler = new ActualizadorEstadoMascotaScheduler(simulacionActividadServiceMock);
  }

  @Test
  public void cuandoActualizoEntoncesSeLlamaASimularDetalleParaTodas() {
    scheduler.actualizarEstadosDeMascotas();

    verify(simulacionActividadServiceMock, times(1)).simularDetalleParaTodas();
  }

  @Test
  public void cuandoActualizoVariasVecesEntoncesSeLlamaVariasVeces() {
    scheduler.actualizarEstadosDeMascotas();
    scheduler.actualizarEstadosDeMascotas();
    scheduler.actualizarEstadosDeMascotas();

    verify(simulacionActividadServiceMock, times(3)).simularDetalleParaTodas();
  }
}
