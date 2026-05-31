package com.tallerwebi.presentacion.controlador;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.servicio.AlertaService;
import com.tallerwebi.dominio.servicio.OrquestadorService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

public class ControladorMonitoreoTest {

  private ControladorMonitoreo controlador;
  private OrquestadorService orquestadorServiceMock;
  private AlertaService alertaServiceMock;
  private Model modelMock;

  @BeforeEach
  public void init() {
    orquestadorServiceMock = mock(OrquestadorService.class);
    alertaServiceMock = mock(AlertaService.class);
    modelMock = mock(Model.class);

    controlador = new ControladorMonitoreo(orquestadorServiceMock, alertaServiceMock);
  }

  @Test
  public void cuandoProcesoUnaMascotaDevuelveResultadoCorrecto() {
    Long mascotaId = 1L;

    ResultadoSimulacionDto resultadoEsperado = new ResultadoSimulacionDto(
      "Toby",
      EstadoMascota.CAMINANDO,
      null,
      null
    );

    when(orquestadorServiceMock.procesarMascota(mascotaId)).thenReturn(resultadoEsperado);

    ResultadoSimulacionDto resultadoObtenido = controlador.procesarMascota(mascotaId);

    assertThat(resultadoObtenido.getNombreMascota(), equalTo("Toby"));

    assertThat(resultadoObtenido.getEstado(), equalTo(EstadoMascota.CAMINANDO));
  }

  @Test
  public void cuandoObtengoAlertasDevuelveLaListaCorrespondiente() {
    Long mascotaId = 1L;

    List<AlertaDto> alertasEsperadas = new ArrayList<>();

    when(alertaServiceMock.obtenerAlertasPorMascota(mascotaId)).thenReturn(alertasEsperadas);

    List<AlertaDto> resultado = controlador.obtenerAlertasDeMascota(mascotaId);

    assertThat(resultado, equalTo(alertasEsperadas));
  }

  @Test
  public void cuandoSolicitoEstadoDevuelveElUltimoEstado() {
    Long mascotaId = 1L;

    ResultadoSimulacionDto dto = new ResultadoSimulacionDto(
      "Toby",
      EstadoMascota.REPOSO,
      null,
      null
    );

    when(orquestadorServiceMock.obtenerUltimoEstado(mascotaId)).thenReturn(dto);

    ResultadoSimulacionDto resultado = controlador.obtenerEstado(mascotaId);

    assertThat(resultado.getEstado(), equalTo(EstadoMascota.REPOSO));
  }

  @Test
  public void cuandoIngresoALaVistaDeAlertasRetornaLaVistaCorrecta() {
    Long mascotaId = 1L;

    String vista = controlador.verPantallaDeAlertas(mascotaId, modelMock);

    verify(modelMock).addAttribute("idMascota", mascotaId);

    assertThat(vista, equalTo("alertas"));
  }

  @Test
  public void cuandoIngresoALaVistaDeSimulacionRetornaLaVistaCorrecta() {
    Long mascotaId = 1L;

    String vista = controlador.vista(mascotaId, modelMock);

    verify(modelMock).addAttribute("idMascota", mascotaId);

    assertThat(vista, equalTo("simulacion"));
  }

  @Test
  public void cuandoIngresoAlDashboardRetornaLaVistaDashboard() {
    Long mascotaId = 1L;

    ResultadoSimulacionDto dto = new ResultadoSimulacionDto(
      "Firulais",
      EstadoMascota.REPOSO,
      null,
      null
    );

    when(orquestadorServiceMock.obtenerUltimoEstado(mascotaId)).thenReturn(dto);

    String vista = controlador.vistaDashboard(mascotaId, modelMock);

    verify(modelMock).addAttribute("idMascota", mascotaId);
    verify(modelMock).addAttribute("mascotaNombre", "Firulais");

    assertThat(vista, equalTo("dashboard"));
  }
}
