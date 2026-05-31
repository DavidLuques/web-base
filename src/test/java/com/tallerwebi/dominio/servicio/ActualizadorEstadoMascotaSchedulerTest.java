package com.tallerwebi.dominio.servicio;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ActualizadorEstadoMascotaSchedulerTest {

  private ActualizadorEstadoMascotaScheduler scheduler;
  private OrquestadorService orquestadorServiceMock;

  @BeforeEach
  public void init() {
    orquestadorServiceMock = mock(OrquestadorService.class);

    scheduler = new ActualizadorEstadoMascotaScheduler(orquestadorServiceMock);
  }

  @Test
  public void cuandoSeActualizanLosEstadosDebeProcesarTodasLasMascotas() {
    scheduler.actualizarEstadosDeMascotas();

    verify(orquestadorServiceMock, times(1)).procesarTodasLasMascotas();
  }

  @Test
  public void cuandoSeRefrescanLasLecturasDebeActualizarTodasLasLecturas() {
    scheduler.refrescarLecturaDelCollar();

    verify(orquestadorServiceMock, times(1)).refrescarTodasLasLecturas();
  }
}
