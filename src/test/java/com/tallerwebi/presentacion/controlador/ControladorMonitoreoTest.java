package com.tallerwebi.presentacion.controlador;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.servicio.AlertaService;
import com.tallerwebi.dominio.servicio.OrquestadorService;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

public class ControladorMonitoreoTest {

  private ControladorMonitoreo controlador;
  private OrquestadorService orquestadorServiceMock;
  private AlertaService alertaServiceMock;
  private ServicioMascota servicioMascotaMock;
  private Model modelMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    orquestadorServiceMock = mock(OrquestadorService.class);
    alertaServiceMock = mock(AlertaService.class);
    servicioMascotaMock = mock(ServicioMascota.class);
    modelMock = mock(Model.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);

    controlador =
      new ControladorMonitoreo(orquestadorServiceMock, alertaServiceMock, servicioMascotaMock);
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

    String vista = controlador.verPantallaDeAlertas(mascotaId, modelMock, requestMock);

    verify(modelMock).addAttribute("idMascota", mascotaId);

    assertThat(vista, equalTo("alertas"));
  }

  @Test
  public void cuandoIngresoALaVistaDeSimulacionRetornaLaVistaCorrecta() {
    Long mascotaId = 1L;

    String vista = controlador.vista(mascotaId, modelMock, requestMock);

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

    String vista = controlador.vistaDashboard(mascotaId, modelMock, requestMock);

    verify(modelMock).addAttribute("idMascota", mascotaId);
    verify(modelMock).addAttribute("mascotaNombre", "Firulais");

    assertThat(vista, equalTo("dashboard"));
  }

  @Test
  public void cuandoProcesarMascotaDevuelveNombreCorrecto() {
    Long mascotaId = 2L;

    ResultadoSimulacionDto dto = new ResultadoSimulacionDto(
      "Firulais",
      EstadoMascota.CAMINANDO,
      null,
      null
    );

    when(orquestadorServiceMock.procesarMascota(mascotaId)).thenReturn(dto);

    ResultadoSimulacionDto resultado = controlador.procesarMascota(mascotaId);

    assertThat(resultado.getNombreMascota(), equalTo("Firulais"));
  }

  @Test
  public void cuandoIngresoAlDashboardYElEstadoEsNuloElNombreEsMascota() {
    Long mascotaId = 1L;

    when(orquestadorServiceMock.obtenerUltimoEstado(mascotaId)).thenReturn(null);

    String vista = controlador.vistaDashboard(mascotaId, modelMock, requestMock);

    verify(modelMock).addAttribute("mascotaNombre", "Mascota");
    assertThat(vista, equalTo("dashboard"));
  }

  @Test
  public void cuandoSolicitoEstadoConEstadoCaminandoDevuelveElEstadoCorrecto() {
    Long mascotaId = 3L;

    ResultadoSimulacionDto dto = new ResultadoSimulacionDto(
      "Rex",
      EstadoMascota.CAMINANDO,
      null,
      null
    );

    when(orquestadorServiceMock.obtenerUltimoEstado(mascotaId)).thenReturn(dto);

    ResultadoSimulacionDto resultado = controlador.obtenerEstado(mascotaId);

    assertThat(resultado.getEstado(), equalTo(EstadoMascota.CAMINANDO));
  }

  @Test
  public void cuandoIngresoALaVistaDeSimulacionAgregaElIdAlModelo() {
    Long mascotaId = 5L;

    controlador.vista(mascotaId, modelMock, requestMock);

    verify(modelMock).addAttribute("idMascota", 5L);
  }

  @Test
  public void cuandoIngresoALaVistaDeAlertasAgregaElIdAlModelo() {
    Long mascotaId = 7L;

    controlador.verPantallaDeAlertas(mascotaId, modelMock, requestMock);

    verify(modelMock).addAttribute("idMascota", 7L);
  }

  @Test
  public void cuandoIngresoAlDashboardAgregaElIdAlModelo() {
    Long mascotaId = 4L;

    ResultadoSimulacionDto dto = new ResultadoSimulacionDto(
      "Luna",
      EstadoMascota.REPOSO,
      null,
      null
    );

    when(orquestadorServiceMock.obtenerUltimoEstado(mascotaId)).thenReturn(dto);

    controlador.vistaDashboard(mascotaId, modelMock, requestMock);

    verify(modelMock).addAttribute("idMascota", 4L);
  }

  @Test
  public void cuandoProcesarMascotaSeLlamaAlServicioUnaVez() {
    Long mascotaId = 1L;

    when(orquestadorServiceMock.procesarMascota(mascotaId))
      .thenReturn(new ResultadoSimulacionDto("Toby", EstadoMascota.CAMINANDO, null, null));

    controlador.procesarMascota(mascotaId);

    verify(orquestadorServiceMock, times(1)).procesarMascota(mascotaId);
  }

  @Test
  public void cuandoObtengoAlertasSeLlamaAlServicioUnaVez() {
    Long mascotaId = 1L;

    when(alertaServiceMock.obtenerAlertasPorMascota(mascotaId)).thenReturn(new ArrayList<>());

    controlador.obtenerAlertasDeMascota(mascotaId);

    verify(alertaServiceMock, times(1)).obtenerAlertasPorMascota(mascotaId);
  }
}
