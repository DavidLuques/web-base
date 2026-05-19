package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioActividad;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimulacionActividadServiceTest {

  private SimulacionActividadService simulacionActividadService;

  private MascotaDao mascotaDaoMock;
  private RangoVitalDao rangoVitalDaoMock;
  private SimuladorCollarService simuladorCollarServiceMock;
  private MotorActividadService motorActividadServiceMock;
  private RepositorioActividad repositorioActividadMock;
  private ServicioAnalisis servicioAnalisisMock;

  @BeforeEach
  public void init() {
    mascotaDaoMock = mock(MascotaDao.class);
    rangoVitalDaoMock = mock(RangoVitalDao.class);
    simuladorCollarServiceMock = mock(SimuladorCollarService.class);
    motorActividadServiceMock = mock(MotorActividadService.class);
    repositorioActividadMock = mock(RepositorioActividad.class);
    servicioAnalisisMock = mock(ServicioAnalisis.class);

    simulacionActividadService =
      new SimulacionActividadService(
        mascotaDaoMock,
        rangoVitalDaoMock,
        simuladorCollarServiceMock,
        motorActividadServiceMock,
        repositorioActividadMock,
        servicioAnalisisMock
      );
  }

  @Test
  public void debeSimularYPersistirEstadoDeMascota() {
    Long mascotaId = 1L;

    Mascota mascota = new Mascota();
    mascota.setNombre("Toby");
    mascota.setTamano(TamanoMascota.MEDIANO);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(80);
    rango.setFrecuenciaMaxima(120);

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(85);

    when(mascotaDaoMock.buscarPorId(mascotaId)).thenReturn(mascota);
    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);
    when(simuladorCollarServiceMock.generarLectura(80, 120)).thenReturn(lectura);
    when(motorActividadServiceMock.analizar(mascota, lectura)).thenReturn(EstadoMascota.REPOSO);

    ResultadoSimulacionDto resultado = simulacionActividadService.simularDetalle(mascotaId);

    assertThat(resultado.getEstado(), equalTo(EstadoMascota.REPOSO));
    verify(mascotaDaoMock).modificar(mascota);
    assertThat(mascota.getEstadoActual(), equalTo(EstadoMascota.REPOSO));
  }

  @Test
  public void dadoQueHayMascotasCuandoSimuloTodasEntoncesSeSimulaCadaUna() {
    Mascota mascota1 = new Mascota();
    mascota1.setId(1L);
    mascota1.setNombre("Toby");
    mascota1.setTamano(TamanoMascota.MEDIANO);

    Mascota mascota2 = new Mascota();
    mascota2.setId(2L);
    mascota2.setNombre("Rex");
    mascota2.setTamano(TamanoMascota.GRANDE);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(80);
    rango.setFrecuenciaMaxima(120);

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(90);

    when(mascotaDaoMock.buscarTodas()).thenReturn(Arrays.asList(mascota1, mascota2));
    when(mascotaDaoMock.buscarPorId(anyLong()))
      .thenAnswer(inv -> {
        Long id = inv.getArgument(0);
        return id == 1L ? mascota1 : mascota2;
      });
    when(rangoVitalDaoMock.buscarPorTamano(any())).thenReturn(rango);
    when(simuladorCollarServiceMock.generarLectura(anyInt(), anyInt())).thenReturn(lectura);
    when(motorActividadServiceMock.analizar(any(), any())).thenReturn(EstadoMascota.CAMINANDO);

    simulacionActividadService.simularDetalleParaTodas();

    verify(mascotaDaoMock, times(2)).modificar(any());
  }

  @Test
  public void dadoQueNoHayMascotasCuandoSimuloTodasEntoncesNoSeModificaNada() {
    when(mascotaDaoMock.buscarTodas()).thenReturn(Collections.emptyList());

    simulacionActividadService.simularDetalleParaTodas();

    verify(mascotaDaoMock, never()).modificar(any());
  }
}
