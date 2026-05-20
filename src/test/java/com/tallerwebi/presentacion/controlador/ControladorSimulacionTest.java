package com.tallerwebi.presentacion.controlador;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.servicio.SimulacionActividadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

public class ControladorSimulacionTest {

  private ControladorSimulacion controladorSimulacion;
  private SimulacionActividadService simulacionActividadServiceMock;
  private Model modelMock;

  @BeforeEach
  public void init() {
    simulacionActividadServiceMock = mock(SimulacionActividadService.class);
    modelMock = mock(Model.class);
    controladorSimulacion = new ControladorSimulacion(simulacionActividadServiceMock);
  }

  @Test
  public void dadaUnaMascotaExistenteCuandoSolicitoSimulacionDevuelveResultadoSimulado() {
    Long mascotaId = 1L;

    ResultadoSimulacionDto resultadoEsperado = new ResultadoSimulacionDto(
      "Toby",
      EstadoMascota.CAMINANDO,
      null
    );

    when(simulacionActividadServiceMock.simularDetalle(mascotaId)).thenReturn(resultadoEsperado);

    ResultadoSimulacionDto resultadoObtenido = controladorSimulacion.simular(mascotaId);

    assertThat(resultadoObtenido.getNombreMascota(), equalTo("Toby"));
    assertThat(resultadoObtenido.getEstado(), equalTo(EstadoMascota.CAMINANDO));
  }

  @Test
  public void dadaUnaMascotaExistenteCuandoSolicitoSimulacionAlertaDevuelveResultadoSimuladoConAlerta() {
    Long mascotaId = 1L;

    ResultadoSimulacionDto resultadoEsperado = new ResultadoSimulacionDto(
      "Firulais",
      EstadoMascota.CORRIENDO,
      null
    );

    when(simulacionActividadServiceMock.simularAlertaDetalle(mascotaId)).thenReturn(resultadoEsperado);

    ResultadoSimulacionDto resultadoObtenido = controladorSimulacion.simularAlerta(mascotaId);

    assertThat(resultadoObtenido.getNombreMascota(), equalTo("Firulais"));
    assertThat(resultadoObtenido.getEstado(), equalTo(EstadoMascota.CORRIENDO));
    verify(simulacionActividadServiceMock, times(1)).simularAlertaDetalle(mascotaId);
  }

  @Test
  public void dadaUnaMascotaCuandoPidoVistaMeDevuelveElNombreDeLaVistaConElIdMascotaEnElModelo() {
    Long mascotaId = 1L;

    String vista = controladorSimulacion.vista(mascotaId, modelMock);

    assertThat(vista, equalTo("simulacion"));
    verify(modelMock, times(1)).addAttribute("idMascota", mascotaId);
  }

  @Test
  public void dadaUnaMascotaCuandoPidoElEstadoActualEntoncesMeDevuelveElEstado() {
    Long mascotaId = 1L;
    ResultadoSimulacionDto estadoEsperado = new ResultadoSimulacionDto("Toby", EstadoMascota.REPOSO, null);

    when(simulacionActividadServiceMock.obtenerEstadoActual(mascotaId)).thenReturn(estadoEsperado);

    ResultadoSimulacionDto resultadoObtenido = controladorSimulacion.obtenerEstado(mascotaId);

    assertThat(resultadoObtenido.getNombreMascota(), equalTo("Toby"));
    assertThat(resultadoObtenido.getEstado(), equalTo(EstadoMascota.REPOSO));
    verify(simulacionActividadServiceMock, times(1)).obtenerEstadoActual(mascotaId);
  }
}