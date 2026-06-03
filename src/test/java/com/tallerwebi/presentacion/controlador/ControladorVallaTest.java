package com.tallerwebi.presentacion.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.servicio.OrquestadorService;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

public class ControladorVallaTest {

  private OrquestadorService orquestadorServiceMock;
  private ControladorValla controladorValla;

  @BeforeEach
  public void inicializar() {
    orquestadorServiceMock = mock(OrquestadorService.class);
    controladorValla = new ControladorValla(orquestadorServiceMock);
  }

  @Test
  public void deberiaResponderConEstado200YLasCoordenadasDeLaMascota() {
    Long idMascota = 1L;
    Map<String, Object> ubicacionSimulada = new HashMap<>();
    ubicacionSimulada.put("latitud", -34.7222);
    ubicacionSimulada.put("longitud", -58.5250);

    when(orquestadorServiceMock.obtenerUltimaUbicacion(idMascota)).thenReturn(ubicacionSimulada);

    ResponseEntity<Map<String, Object>> respuesta = controladorValla.obtenerUbicacionActual(
      idMascota
    );

    assertEquals(
      HttpStatus.OK,
      respuesta.getStatusCode(),
      "El código de respuesta debe ser 200 OK"
    );
    assertEquals(-34.7222, respuesta.getBody().get("latitud"));
    assertEquals(-58.5250, respuesta.getBody().get("longitud"));

    verify(orquestadorServiceMock, times(1)).obtenerUltimaUbicacion(idMascota);
  }

  @Test
  public void cuandoNoHayUsuarioEnSesionDebeRedirigirAlLogin() {
    HttpServletRequest requestMock = mock(HttpServletRequest.class);
    javax.servlet.http.HttpSession sessionMock = mock(javax.servlet.http.HttpSession.class);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView resultado = controladorValla.verVallado(requestMock, 1L);

    assertEquals("redirect:/login", resultado.getViewName());
  }

  @Test
  public void cuandoHayUsuarioEnSesionDebeMostrarVistaValla() {
    HttpServletRequest requestMock = mock(HttpServletRequest.class);
    javax.servlet.http.HttpSession sessionMock = mock(javax.servlet.http.HttpSession.class);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(10L);

    ModelAndView resultado = controladorValla.verVallado(requestMock, 5L);

    assertEquals("valla-mascota", resultado.getViewName());
  }

  @Test
  public void cuandoHayUsuarioEnSesionDebeAgregarIdMascotaAlModelo() {
    HttpServletRequest requestMock = mock(HttpServletRequest.class);
    javax.servlet.http.HttpSession sessionMock = mock(javax.servlet.http.HttpSession.class);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(20L);

    ModelAndView resultado = controladorValla.verVallado(requestMock, 99L);

    assertEquals(99L, resultado.getModel().get("idMascota"));
  }

  @Test
  public void obtenerUbicacionActualDebeConsultarAlOrquestador() {
    Map<String, Object> ubicacion = new HashMap<>();
    ubicacion.put("latitud", 1.0);
    ubicacion.put("longitud", 2.0);

    when(orquestadorServiceMock.obtenerUltimaUbicacion(7L)).thenReturn(ubicacion);

    controladorValla.obtenerUbicacionActual(7L);

    verify(orquestadorServiceMock).obtenerUltimaUbicacion(7L);
  }
}
