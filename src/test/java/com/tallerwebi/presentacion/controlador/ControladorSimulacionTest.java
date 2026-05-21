package com.tallerwebi.presentacion.controlador;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.servicio.SimulacionActividadService;
import com.tallerwebi.presentacion.controlador.ControladorSimulacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ControladorSimulacionTest {

  private ControladorSimulacion controladorSimulacion;
  private SimulacionActividadService simulacionActividadServiceMock;

  @BeforeEach
  public void init() {
    simulacionActividadServiceMock = mock(SimulacionActividadService.class);
    controladorSimulacion = new ControladorSimulacion(simulacionActividadServiceMock);
  }

  @Test
  public void dadaUnaMascotaExistenteCuandoSolicitoSimulacionDevuelveResultadoSimulado() {
    Long mascotaId = 1L;

    ResultadoSimulacionDto resultadoEsperado = new ResultadoSimulacionDto(
      "Toby",
      EstadoMascota.CAMINANDO,
      null,
      null
    );

    when(simulacionActividadServiceMock.simularDetalle(mascotaId)).thenReturn(resultadoEsperado);

    ResultadoSimulacionDto resultadoObtenido = controladorSimulacion.simular(mascotaId);

    assertThat(resultadoObtenido.getNombreMascota(), equalTo("Toby"));

    assertThat(resultadoObtenido.getEstado(), equalTo(EstadoMascota.CAMINANDO));
  }
}
