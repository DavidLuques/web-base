package com.tallerwebi.presentacion.controlador;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dto.RangosVitalesDto;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
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
  private ServicioMascota servicioMascotaMock;
  private Model modelMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    orquestadorServiceMock = mock(OrquestadorService.class);
    servicioMascotaMock = mock(ServicioMascota.class);
    modelMock = mock(Model.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);

    controlador = new ControladorMonitoreo(orquestadorServiceMock, servicioMascotaMock);
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
  public void cuandoSolicitoEstadoDevuelveElUltimoEstado() {
    Long mascotaId = 1L;

    ResultadoSimulacionDto dto = new ResultadoSimulacionDto(
      "Toby",
      EstadoMascota.REPOSO,
      null,
      null
    );

    when(orquestadorServiceMock.refrescarLectura(mascotaId)).thenReturn(dto);

    ResultadoSimulacionDto resultado = controlador.obtenerEstado(mascotaId);

    assertThat(resultado.getEstado(), equalTo(EstadoMascota.REPOSO));
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
  public void cuandoIngresoALaVistaDeSimulacionConUsuarioLogueadoAgregaMisMascotas() {
    Long mascotaId = 1L;
    Long idUsuario = 20L;

    List<?> mascotas = new ArrayList<>();

    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    when(servicioMascotaMock.obtenerMascotasPorUsuario(idUsuario)).thenReturn((List) mascotas);

    controlador.vista(mascotaId, modelMock, requestMock);

    verify(modelMock).addAttribute("idMascota", mascotaId);
    verify(modelMock).addAttribute("misMascotas", mascotas);
    verify(servicioMascotaMock).obtenerMascotasPorUsuario(idUsuario);
  }

  @Test
  public void cuandoIngresoAlDashboardConUsuarioLogueadoAgregaMisMascotas() {
    Long mascotaId = 1L;
    Long idUsuario = 30L;

    List<?> mascotas = new ArrayList<>();

    ResultadoSimulacionDto dto = new ResultadoSimulacionDto(
      "Luna",
      EstadoMascota.REPOSO,
      null,
      null
    );

    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    when(servicioMascotaMock.obtenerMascotasPorUsuario(idUsuario)).thenReturn((List) mascotas);
    when(orquestadorServiceMock.obtenerUltimoEstado(mascotaId)).thenReturn(dto);

    controlador.vistaDashboard(mascotaId, modelMock, requestMock);

    verify(modelMock).addAttribute("misMascotas", mascotas);
    verify(servicioMascotaMock).obtenerMascotasPorUsuario(idUsuario);
  }

  @Test
  public void cuandoNoHayUsuarioEnSesionNoBuscaMascotas() {
    Long mascotaId = 1L;

    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    controlador.vista(mascotaId, modelMock, requestMock);

    verify(servicioMascotaMock, never()).obtenerMascotasPorUsuario(anyLong());
  }

  @Test
  public void cuandoSolicitoRangosVitalesDevuelveLosRangosCorrectos() {
    Long mascotaId = 1L;

    RangosVitalesDto rangosMock = mock(RangosVitalesDto.class);

    when(orquestadorServiceMock.obtenerRangosVitales(mascotaId)).thenReturn(rangosMock);

    RangosVitalesDto resultado = controlador.obtenerRangosVitales(mascotaId);

    assertThat(resultado, equalTo(rangosMock));
    verify(orquestadorServiceMock).obtenerRangosVitales(mascotaId);
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

    when(orquestadorServiceMock.refrescarLectura(mascotaId)).thenReturn(dto);

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
}
