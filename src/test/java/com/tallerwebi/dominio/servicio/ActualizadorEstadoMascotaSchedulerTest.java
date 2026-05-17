package com.tallerwebi.dominio.servicio;

import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.modelo.Mascota;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ActualizadorEstadoMascotaSchedulerTest {

  private ActualizadorEstadoMascotaScheduler scheduler;
  private MascotaDao mascotaDaoMock;
  private SimulacionActividadService simulacionActividadServiceMock;

  @BeforeEach
  public void init() {
    mascotaDaoMock = mock(MascotaDao.class);
    simulacionActividadServiceMock = mock(SimulacionActividadService.class);
    scheduler =
      new ActualizadorEstadoMascotaScheduler(mascotaDaoMock, simulacionActividadServiceMock);
  }

  @Test
  public void dadoQueHayMascotasCuandoActualizoEntoncesSeSimulaDetalleParaCadaUna() {
    Mascota mascota1 = new Mascota();
    mascota1.setId(1L);

    Mascota mascota2 = new Mascota();
    mascota2.setId(2L);

    List<Mascota> mascotas = Arrays.asList(mascota1, mascota2);
    when(mascotaDaoMock.buscarTodas()).thenReturn(mascotas);

    scheduler.actualizarEstadosDeMascotas();

    verify(simulacionActividadServiceMock, times(1)).simularDetalle(1L);
    verify(simulacionActividadServiceMock, times(1)).simularDetalle(2L);
  }

  @Test
  public void dadoQueNoHayMascotasCuandoActualizoEntoncesNoSeSimulaNada() {
    when(mascotaDaoMock.buscarTodas()).thenReturn(Collections.emptyList());

    scheduler.actualizarEstadosDeMascotas();

    verify(simulacionActividadServiceMock, never()).simularDetalle(any());
  }

  @Test
  public void dadoQueHayUnaMascotaCuandoActualizoEntoncesSeSimulaDetalleUnaVez() {
    Mascota mascota = new Mascota();
    mascota.setId(42L);

    when(mascotaDaoMock.buscarTodas()).thenReturn(Collections.singletonList(mascota));

    scheduler.actualizarEstadosDeMascotas();

    verify(simulacionActividadServiceMock, times(1)).simularDetalle(42L);
  }

  @Test
  public void dadoQueHayMascotasCuandoActualizoEntoncesSiempreSeConsultanTodasLasMascotas() {
    when(mascotaDaoMock.buscarTodas()).thenReturn(Collections.emptyList());

    scheduler.actualizarEstadosDeMascotas();

    verify(mascotaDaoMock, times(1)).buscarTodas();
  }
}
