package com.tallerwebi.presentacion.controlador;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.servicio.ServicioAlerta;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

public class ControladorAlertaTest {

  private ControladorAlerta controlador;
  private ServicioAlerta servicioAlertaMock;
  private ServicioMascota servicioMascotaMock;
  private Model modelMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioAlertaMock = mock(ServicioAlerta.class);
    servicioMascotaMock = mock(ServicioMascota.class);
    modelMock = mock(Model.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);

    controlador = new ControladorAlerta(servicioAlertaMock, servicioMascotaMock);
  }

  @Test
  public void cuandoObtengoAlertasDevuelveLaListaCorrespondiente() {
    Long mascotaId = 1L;

    List<AlertaDto> alertasEsperadas = new ArrayList<>();

    when(servicioAlertaMock.obtenerAlertasPorMascota(mascotaId)).thenReturn(alertasEsperadas);

    List<AlertaDto> resultado = controlador.obtenerAlertasDeMascota(mascotaId);

    assertThat(resultado, equalTo(alertasEsperadas));
  }

  @Test
  public void cuandoIngresoALaVistaDeAlertasRetornaLaVistaCorrecta() {
    Long mascotaId = 1L;

    String vista = controlador.verPantallaDeAlertas(mascotaId, modelMock, requestMock);

    verify(modelMock).addAttribute("idMascota", mascotaId);
    assertThat(vista, equalTo("alertas"));
  }

  @Test
  public void cuandoIngresoALaVistaDeAlertasConUsuarioLogueadoAgregaMisMascotas() {
    Long mascotaId = 1L;
    Long idUsuario = 10L;

    List<?> mascotas = new ArrayList<>();

    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    when(servicioMascotaMock.obtenerMascotasPorUsuario(idUsuario)).thenReturn((List) mascotas);

    controlador.verPantallaDeAlertas(mascotaId, modelMock, requestMock);

    verify(modelMock).addAttribute("idMascota", mascotaId);
    verify(modelMock).addAttribute("misMascotas", mascotas);
    verify(servicioMascotaMock).obtenerMascotasPorUsuario(idUsuario);
  }

  @Test
  public void cuandoNoHayUsuarioEnSesionNoBuscaMascotas() {
    Long mascotaId = 1L;

    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    controlador.verPantallaDeAlertas(mascotaId, modelMock, requestMock);

    verify(servicioMascotaMock, never()).obtenerMascotasPorUsuario(anyLong());
  }

  @Test
  public void cuandoIngresoALaVistaDeAlertasAgregaElIdAlModelo() {
    Long mascotaId = 7L;

    controlador.verPantallaDeAlertas(mascotaId, modelMock, requestMock);

    verify(modelMock).addAttribute("idMascota", 7L);
  }

  @Test
  public void cuandoObtengoAlertasSeLlamaAlServicioUnaVez() {
    Long mascotaId = 1L;

    when(servicioAlertaMock.obtenerAlertasPorMascota(mascotaId)).thenReturn(new ArrayList<>());

    controlador.obtenerAlertasDeMascota(mascotaId);

    verify(servicioAlertaMock, times(1)).obtenerAlertasPorMascota(mascotaId);
  }

  @Test
  public void cuandoMarcoAlertaComoLeidaSeLlamaAlServicio() {
    Long idAlerta = 5L;

    controlador.marcarAlertaComoLeida(idAlerta);

    verify(servicioAlertaMock, times(1)).marcarComoLeida(idAlerta);
  }

  @Test
  public void cuandoMarcoAlertaComoLeidaDevuelveResponseOk() {
    Long idAlerta = 5L;

    ResponseEntity<Void> resultado = controlador.marcarAlertaComoLeida(idAlerta);

    assertThat(resultado.getStatusCode().value(), equalTo(200));
  }
}
