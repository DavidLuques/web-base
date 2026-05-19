package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioAnalisis;
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
  private RepositorioAnalisis repositorioAnalisisMock;

  private static final int SISTOLICA_MINIMA = 115;
  private static final int SISTOLICA_MAXIMA = 140;
  private static final int DIASTOLICA_MINIMA = 75;
  private static final int DIASTOLICA_MAXIMA = 90;

  @BeforeEach
  public void init() {
    mascotaDaoMock = mock(MascotaDao.class);
    rangoVitalDaoMock = mock(RangoVitalDao.class);
    simuladorCollarServiceMock = mock(SimuladorCollarService.class);
    motorActividadServiceMock = mock(MotorActividadService.class);

    simulacionActividadService =
      new SimulacionActividadService(
        mascotaDaoMock,
        rangoVitalDaoMock,
        simuladorCollarServiceMock,
        motorActividadServiceMock,
        repositorioAnalisisMock
      );
  }

  private RangoVitalPorTamano rangoCompleto() {
    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(80);
    rango.setFrecuenciaMaxima(120);
    rango.setSistolicaMinima(SISTOLICA_MINIMA);
    rango.setSistolicaMaxima(SISTOLICA_MAXIMA);
    rango.setDiastolicaMinima(DIASTOLICA_MINIMA);
    rango.setDiastolicaMaxima(DIASTOLICA_MAXIMA);
    return rango;
  }

  private LecturaSensor lecturaCompleta() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(95);
    lectura.setPresionSistolica(125);
    lectura.setPresionDiastolica(80);
    lectura.setTemperatura(38.3);
    return lectura;
  }

  @Test
  public void debeSimularYPersistirEstadoDeMascota() {
    Long mascotaId = 1L;
    Mascota mascota = new Mascota();
    mascota.setId(mascotaId);
    mascota.setNombre("Toby");
    mascota.setTamano(TamanoMascota.MEDIANO);

    when(mascotaDaoMock.buscarPorId(mascotaId)).thenReturn(mascota);
    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rangoCompleto());
    when(
      simuladorCollarServiceMock.generarLectura(
        eq(mascotaId),
        any(),
        eq(80),
        eq(120),
        eq(SISTOLICA_MINIMA),
        eq(SISTOLICA_MAXIMA),
        eq(DIASTOLICA_MINIMA),
        eq(DIASTOLICA_MAXIMA)
      )
    )
      .thenReturn(lecturaCompleta());
    when(motorActividadServiceMock.analizar(eq(mascota), any())).thenReturn(EstadoMascota.REPOSO);

    ResultadoSimulacionDto resultado = simulacionActividadService.simularDetalle(mascotaId);

    assertThat(resultado.getEstado(), equalTo(EstadoMascota.REPOSO));
    verify(mascotaDaoMock).modificar(mascota);
    assertThat(mascota.getEstadoActual(), equalTo(EstadoMascota.REPOSO));
  }

  @Test
  public void cuandoSimuloDetalleEntoncesDtoIncluyeFrecuenciaPresionYTemperatura() {
    Long mascotaId = 1L;
    Mascota mascota = new Mascota();
    mascota.setId(mascotaId);
    mascota.setNombre("Toby");
    mascota.setTamano(TamanoMascota.MEDIANO);

    LecturaSensor lectura = lecturaCompleta();

    when(mascotaDaoMock.buscarPorId(mascotaId)).thenReturn(mascota);
    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rangoCompleto());
    when(
      simuladorCollarServiceMock.generarLectura(
        eq(mascotaId),
        any(),
        eq(80),
        eq(120),
        eq(SISTOLICA_MINIMA),
        eq(SISTOLICA_MAXIMA),
        eq(DIASTOLICA_MINIMA),
        eq(DIASTOLICA_MAXIMA)
      )
    )
      .thenReturn(lectura);
    when(motorActividadServiceMock.analizar(any(), any())).thenReturn(EstadoMascota.REPOSO);

    ResultadoSimulacionDto resultado = simulacionActividadService.simularDetalle(mascotaId);

    assertThat(resultado.getFrecuenciaCardiaca(), equalTo(95));
    assertThat(resultado.getPresionSistolica(), equalTo(125));
    assertThat(resultado.getPresionDiastolica(), equalTo(80));
    assertThat(resultado.getTemperatura(), equalTo(38.3));
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

    when(mascotaDaoMock.buscarTodas()).thenReturn(Arrays.asList(mascota1, mascota2));
    when(mascotaDaoMock.buscarPorId(anyLong()))
      .thenAnswer(inv -> {
        Long id = inv.getArgument(0);
        return id.equals(1L) ? mascota1 : mascota2;
      });
    when(rangoVitalDaoMock.buscarPorTamano(any())).thenReturn(rangoCompleto());
    when(
      simuladorCollarServiceMock.generarLectura(
        anyLong(),
        any(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt()
      )
    )
      .thenReturn(lecturaCompleta());
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

  @Test
  public void dadoQueHayMascotasCuandoActualizoFrecuenciaParaTodasEntoncesSeActualizaCadaUna() {
    Mascota mascota1 = new Mascota();
    mascota1.setId(1L);
    mascota1.setTamano(TamanoMascota.MEDIANO);
    mascota1.setEstadoActual(EstadoMascota.REPOSO);

    Mascota mascota2 = new Mascota();
    mascota2.setId(2L);
    mascota2.setTamano(TamanoMascota.GRANDE);
    mascota2.setEstadoActual(EstadoMascota.CAMINANDO);

    when(mascotaDaoMock.buscarTodas()).thenReturn(Arrays.asList(mascota1, mascota2));
    when(rangoVitalDaoMock.buscarPorTamano(any())).thenReturn(rangoCompleto());

    simulacionActividadService.actualizarFrecuenciaParaTodas();

    verify(simuladorCollarServiceMock, times(1))
      .actualizarFrecuencia(eq(1L), eq(EstadoMascota.REPOSO), eq(80), eq(120));
    verify(simuladorCollarServiceMock, times(1))
      .actualizarFrecuencia(eq(2L), eq(EstadoMascota.CAMINANDO), eq(80), eq(120));
  }

  @Test
  public void dadoQueNoHayMascotasCuandoActualizoFrecuenciaParaTodasEntoncesNoSeActualizaNada() {
    when(mascotaDaoMock.buscarTodas()).thenReturn(Collections.emptyList());

    simulacionActividadService.actualizarFrecuenciaParaTodas();

    verify(simuladorCollarServiceMock, never())
      .actualizarFrecuencia(anyLong(), any(), anyInt(), anyInt());
  }
}
