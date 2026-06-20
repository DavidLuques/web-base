package com.tallerwebi.presentacion.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicio.ServicioMascota;
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
  private ServicioMascota servicioMascotaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioUsuarioMock = mock(ServicioUsuario.class);
    servicioMascotaMock = mock(ServicioMascota.class);
    // por simplicidad el controlador del test usa la impl del servicio de perfil
    // que delega a los mocks anteriores
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    controlador =
      new ControladorPerfil(
        new com.tallerwebi.dominio.servicio.impl.ServicioPerfilImpl(
          servicioUsuarioMock,
          servicioMascotaMock
        )
      );
    controladorConfiguracion =
      new ControladorConfiguracion(servicioUsuarioMock, servicioMascotaMock);
  }

  @Test
  public void queLosCamposDeDatosPerfilSeCargueCorrectamente() {
    DatosPerfil datos = new DatosPerfil();
    datos.setNombre("Juan");
    datos.setEmail("juan@mail.com");
    datos.setPassword("1234");
    datos.setTelefono(1112345678L);
    datos.setCalle("Av Corrientes 123");
    datos.setCiudad("Buenos Aires");
    datos.setProvincia("CABA");
    datos.setPais("Argentina");
    datos.setCodigoPostal("1043");

    assertEquals("Juan", datos.getNombre());
    assertEquals("juan@mail.com", datos.getEmail());
    assertEquals("1234", datos.getPassword());
    assertEquals(1112345678L, datos.getTelefono());
    assertEquals("Av Corrientes 123", datos.getCalle());
    assertEquals("Buenos Aires", datos.getCiudad());
    assertEquals("CABA", datos.getProvincia());
    assertEquals("Argentina", datos.getPais());
    assertEquals("1043", datos.getCodigoPostal());
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
    DatosPerfil datosPerfilSimulado = new DatosPerfil();
    datosPerfilSimulado.setNombre("Test");
    when(servicioUsuarioMock.obtenerDatosPerfil(idUsuario)).thenReturn(datosPerfilSimulado);

    ModelAndView modelAndView = controlador.verPerfil(requestMock, null);

    assertEquals("ver-perfil", modelAndView.getViewName());
    DatosPerfil datosPerfil = (DatosPerfil) modelAndView.getModel().get("datosPerfil");
    assertEquals("Test", datosPerfil.getNombre());
  }

  @Test
  public void queAlVerPerfilConUbicacionSeCarguenLosDatosCorrectamente() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    DatosPerfil datosPerfilSimulado = new DatosPerfil();
    datosPerfilSimulado.setNombre("Test");
    datosPerfilSimulado.setCalle("Av Siempre Viva 742");
    when(servicioUsuarioMock.obtenerDatosPerfil(idUsuario)).thenReturn(datosPerfilSimulado);

    ModelAndView modelAndView = controlador.verPerfil(requestMock, 99L);

    assertEquals("ver-perfil", modelAndView.getViewName());
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

    doThrow(new UsuarioNoEncontrado("Usuario no encontrado"))
      .when(servicioUsuarioMock)
      .actualizarPerfil(idUsuario, datosPerfil);

    ModelAndView modelAndView = controlador.actualizarPerfil(datosPerfil, requestMock, null);

    assertEquals("perfil", modelAndView.getViewName());
    assertEquals("Usuario no encontrado", modelAndView.getModel().get("error"));
  }

  @Test
  public void queAlVerPerfilSinUbicacionLosCamposDeDireccionSeanNulos() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    DatosPerfil datosPerfilSimulado = new DatosPerfil();
    datosPerfilSimulado.setNombre("Test");
    when(servicioUsuarioMock.obtenerDatosPerfil(idUsuario)).thenReturn(datosPerfilSimulado);

    ModelAndView mav = controlador.verPerfil(requestMock, null);

    DatosPerfil datosPerfil = (DatosPerfil) mav.getModel().get("datosPerfil");
    assertEquals(null, datosPerfil.getCalle());
    assertEquals(null, datosPerfil.getCiudad());
    assertEquals(null, datosPerfil.getPais());
  }

  @Test
  public void queAlVerPerfilSeCarguenEmailYTelefono() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    DatosPerfil datosPerfilSimulado = new DatosPerfil();
    datosPerfilSimulado.setNombre("Ana");
    datosPerfilSimulado.setEmail("ana@mail.com");
    datosPerfilSimulado.setTelefono(1198765432L);
    when(servicioUsuarioMock.obtenerDatosPerfil(idUsuario)).thenReturn(datosPerfilSimulado);

    ModelAndView mav = controlador.verPerfil(requestMock, null);

    DatosPerfil datosPerfil = (DatosPerfil) mav.getModel().get("datosPerfil");
    assertEquals("ana@mail.com", datosPerfil.getEmail());
    assertEquals(1198765432L, datosPerfil.getTelefono());
  }

  @Test
  public void queAlVerPerfilElModeloContieneIdMascotaNuloSiNoSePasa() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    when(servicioUsuarioMock.obtenerDatosPerfil(idUsuario)).thenReturn(new DatosPerfil());

    ModelAndView mav = controlador.verPerfil(requestMock, null);

    assertEquals(null, mav.getModel().get("idMascota"));
  }

  @Test
  public void queAlVerPerfilSeLlamaAlServicioUnaVez() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    when(servicioUsuarioMock.obtenerDatosPerfil(idUsuario)).thenReturn(new DatosPerfil());

    controlador.verPerfil(requestMock, null);

    verify(servicioUsuarioMock, times(1)).obtenerDatosPerfil(idUsuario);
  }

  @Test
  public void queAlActualizarPerfilElModeloContieneLosDatosPerfilEnCasoDeError() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    DatosPerfil datosPerfil = new DatosPerfil();
    datosPerfil.setNombre("Carlos");

    doThrow(new UsuarioNoEncontrado("Fallo al guardar"))
      .when(servicioUsuarioMock)
      .actualizarPerfil(idUsuario, datosPerfil);

    ModelAndView mav = controlador.actualizarPerfil(datosPerfil, requestMock, null);

    assertEquals(datosPerfil, mav.getModel().get("datosPerfil"));
  }

  @Test
  public void queAlActualizarPerfilConErrorElModeloContieneIdMascota() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    DatosPerfil datosPerfil = new DatosPerfil();

    doThrow(new UsuarioNoEncontrado("Error"))
      .when(servicioUsuarioMock)
      .actualizarPerfil(idUsuario, datosPerfil);

    ModelAndView mav = controlador.actualizarPerfil(datosPerfil, requestMock, 10L);

    assertEquals("perfil", mav.getViewName());
    assertEquals(10L, mav.getModel().get("idMascota"));
  }

  @Test
  public void queAlActualizarPerfilSinMascotaNoSeIncluyelIdMascotaEnElRedirect() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);

    ModelAndView mav = controlador.actualizarPerfil(new DatosPerfil(), requestMock, null);

    assertEquals("redirect:/perfil?exito=true", mav.getViewName());
  }

  @Test
  public void queAlActualizarPerfilNoLogueadoNoSeLlamaAlServicio() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    controlador.actualizarPerfil(new DatosPerfil(), requestMock, null);

    verify(servicioUsuarioMock, never()).actualizarPerfil(any(), any());
  }

  @Test
  public void queAlVerPerfilConTodasLasDireccionesSeCarganCorrectamente() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    DatosPerfil datosPerfilSimulado = new DatosPerfil();
    datosPerfilSimulado.setCalle("Calle Falsa 123");
    datosPerfilSimulado.setCiudad("Rosario");
    datosPerfilSimulado.setProvincia("Santa Fe");
    datosPerfilSimulado.setPais("Argentina");
    datosPerfilSimulado.setCodigoPostal("2000");
    when(servicioUsuarioMock.obtenerDatosPerfil(idUsuario)).thenReturn(datosPerfilSimulado);

    ModelAndView mav = controlador.verPerfil(requestMock, null);

    DatosPerfil datosPerfil = (DatosPerfil) mav.getModel().get("datosPerfil");
    assertEquals("Rosario", datosPerfil.getCiudad());
    assertEquals("Santa Fe", datosPerfil.getProvincia());
    assertEquals("2000", datosPerfil.getCodigoPostal());
  }

  @Test
  public void queSiElUsuarioNoEstaLogueadoAlEditarPerfilRedirijaALogin() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView modelAndView = controlador.editarPerfil(requestMock, null);

    assertEquals("redirect:/login", modelAndView.getViewName());
  }

  @Test
  public void queSiElUsuarioEstaLogueadoPuedaEditarSuPerfil() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
    DatosPerfil datosPerfilSimulado = new DatosPerfil();
    datosPerfilSimulado.setNombre("Test");
    when(servicioUsuarioMock.obtenerDatosPerfil(idUsuario)).thenReturn(datosPerfilSimulado);

    ModelAndView modelAndView = controlador.editarPerfil(requestMock, null);

    assertEquals("perfil", modelAndView.getViewName());
    DatosPerfil datosPerfil = (DatosPerfil) modelAndView.getModel().get("datosPerfil");
    assertEquals("Test", datosPerfil.getNombre());
  }
}
