package com.tallerwebi.presentacion.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.modelo.Direccion;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import com.tallerwebi.presentacion.DatosPerfil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPerfilTest {

  private ControladorPerfil controlador;
  private ControladorConfiguracion controladorConfiguracion;
  private ServicioUsuario servicioUsuarioMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioUsuarioMock = mock(ServicioUsuario.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    controlador = new ControladorPerfil(servicioUsuarioMock);
    controladorConfiguracion = new ControladorConfiguracion(servicioUsuarioMock);
  }

  @Test
  public void queSiElUsuarioNoEstaLogueadoAlVerConfiguracionesRedirijaALogin() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView modelAndView = controladorConfiguracion.irAConfiguraciones(requestMock, null);

    assertEquals("redirect:/login", modelAndView.getViewName());
  }

  @Test
  public void queSiElUsuarioEstaLogueadoPuedaVerConfiguraciones() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    Usuario usuarioSimulado = new Usuario();
    when(servicioUsuarioMock.obtenerPerfil(idUsuario)).thenReturn(usuarioSimulado);

    ModelAndView modelAndView = controladorConfiguracion.irAConfiguraciones(requestMock, 5L);

    assertEquals("configuraciones", modelAndView.getViewName());
    assertEquals(5L, modelAndView.getModel().get("idMascota"));
  }

  @Test
  public void queSiElUsuarioNoEstaLogueadoAlVerPerfilRedirijaALogin() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView modelAndView = controlador.verPerfil(requestMock, null);

    assertEquals("redirect:/login", modelAndView.getViewName());
  }

  @Test
  public void queSiElUsuarioEstaLogueadoPuedaVerSuPerfil() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    Usuario usuarioSimulado = new Usuario();
    usuarioSimulado.setNombre("Test");
    when(servicioUsuarioMock.obtenerPerfil(idUsuario)).thenReturn(usuarioSimulado);

    ModelAndView modelAndView = controlador.verPerfil(requestMock, null);

    assertEquals("perfil", modelAndView.getViewName());
    DatosPerfil datosPerfil = (DatosPerfil) modelAndView.getModel().get("datosPerfil");
    assertEquals("Test", datosPerfil.getNombre());
  }

  @Test
  public void queAlVerPerfilConUbicacionSeCarguenLosDatosCorrectamente() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    Usuario usuarioSimulado = new Usuario();
    usuarioSimulado.setNombre("Test");
    Direccion direccion = new Direccion();
    direccion.setCalle("Av Siempre Viva 742");
    usuarioSimulado.setUbicacion(direccion);
    when(servicioUsuarioMock.obtenerPerfil(idUsuario)).thenReturn(usuarioSimulado);

    ModelAndView modelAndView = controlador.verPerfil(requestMock, 99L);

    assertEquals("perfil", modelAndView.getViewName());
    DatosPerfil datosPerfil = (DatosPerfil) modelAndView.getModel().get("datosPerfil");
    assertEquals("Av Siempre Viva 742", datosPerfil.getCalle());
    assertEquals(99L, modelAndView.getModel().get("idMascota"));
  }

  @Test
  public void queSiElUsuarioNoEstaLogueadoAlActualizarPerfilRedirijaALogin() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView modelAndView = controlador.actualizarPerfil(new DatosPerfil(), requestMock, null);

    assertEquals("redirect:/login", modelAndView.getViewName());
  }

  @Test
  public void queSiSeActualizaCorrectamenteRedirijaConExito() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    DatosPerfil datosPerfil = new DatosPerfil();

    ModelAndView modelAndView = controlador.actualizarPerfil(datosPerfil, requestMock, null);

    assertEquals("redirect:/perfil?exito=true", modelAndView.getViewName());
    verify(servicioUsuarioMock, times(1)).actualizarPerfil(idUsuario, datosPerfil);
  }

  @Test
  public void queSiSeActualizaCorrectamenteConMascotaRedirijaConIdMascota() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    DatosPerfil datosPerfil = new DatosPerfil();

    ModelAndView modelAndView = controlador.actualizarPerfil(datosPerfil, requestMock, 55L);

    assertEquals("redirect:/perfil?exito=true&idMascota=55", modelAndView.getViewName());
  }

  @Test
  public void queSiOcurreUnErrorAlActualizarVuelvaALaVistaConMensaje() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    DatosPerfil datosPerfil = new DatosPerfil();

    doThrow(new RuntimeException("Error simulado"))
      .when(servicioUsuarioMock)
      .actualizarPerfil(idUsuario, datosPerfil);

    ModelAndView modelAndView = controlador.actualizarPerfil(datosPerfil, requestMock, null);

    assertEquals("perfil", modelAndView.getViewName());
    assertEquals("Error simulado", modelAndView.getModel().get("error"));
  }
}
