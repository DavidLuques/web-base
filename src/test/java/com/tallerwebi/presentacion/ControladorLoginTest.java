package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorLoginTest {

  private ControladorLogin controladorLogin;
  private Usuario usuario;
  private DatosLogin datosLoginMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private ServicioLogin servicioLoginMock;

  @BeforeEach
  public void init() {
    datosLoginMock = new DatosLogin("dami@unlam.com", "123");
    usuario = new Usuario();
    usuario.setEmail("dami@unlam.com");
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    servicioLoginMock = mock(ServicioLogin.class);
    controladorLogin = new ControladorLogin(servicioLoginMock);
  }

  @Test
  public void loginConUsuarioYPasswordInorrectosDeberiaLlevarALoginNuevamente() {
    // preparacion
    when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(null);

    // ejecucion
    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Usuario o clave incorrecta")
    );
    verify(sessionMock, times(0)).setAttribute("ROL", "USER");
  }

  @Test
  public void loginConUsuarioYPasswordCorrectosDeberiaLLevarAHome() {
    // preparacion
    Usuario usuarioEncontrado = new Usuario();
    usuarioEncontrado.setId(1L);
    usuarioEncontrado.setRol("USER");

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(servicioLoginMock.consultarUsuario(anyString(), anyString()))
      .thenReturn(usuarioEncontrado);
    when(servicioLoginMock.buscarMascotasPorUsuario(1L))
      .thenReturn(java.util.Collections.emptyList());

    // ejecucion
    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/simulacion/dashboard/1"));
    verify(sessionMock, times(1)).setAttribute("ROL", usuarioEncontrado.getRol());
    verify(sessionMock, times(1)).setAttribute("ID_USUARIO", 1L);
  }

  @Test
  public void registrameSiUsuarioNoExisteDeberiaCrearUsuarioYVolverAlLogin()
    throws UsuarioExistente {
    when(requestMock.getSession()).thenReturn(sessionMock);
    usuario.setId(2L);
    usuario.setRol("USER");

    // ejecucion
    ModelAndView modelAndView = controladorLogin.registrarme(usuario, requestMock);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/simulacion/dashboard/1"));
    verify(servicioLoginMock, times(1)).registrar(usuario);
    verify(sessionMock, times(1)).setAttribute("ROL", "USER");
    verify(sessionMock, times(1)).setAttribute("ID_USUARIO", 2L);
  }

  @Test
  public void registrarmeSiUsuarioExisteDeberiaVolverAFormularioYMostrarError()
    throws UsuarioExistente {
    // preparacion
    doThrow(UsuarioExistente.class).when(servicioLoginMock).registrar(usuario);

    // ejecucion
    ModelAndView modelAndView = controladorLogin.registrarme(usuario, requestMock);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("El usuario ya existe")
    );
  }

  @Test
  public void errorEnRegistrarmeDeberiaVolverAFormularioYMostrarError() throws UsuarioExistente {
    // preparacion
    doThrow(RuntimeException.class).when(servicioLoginMock).registrar(usuario);

    // ejecucion
    ModelAndView modelAndView = controladorLogin.registrarme(usuario, requestMock);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Error al registrar el nuevo usuario")
    );
  }

  @Test
  public void irALoginDeberiaRetornarVistaLoginConDatosLogin() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.irALogin();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
    assertThat(modelAndView.getModel().get("datosLogin"), instanceOf(DatosLogin.class));
  }

  @Test
  public void nuevoUsuarioDeberiaRetornarVistaNuevoUsuarioConUsuarioVacio() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.nuevoUsuario();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    assertThat(modelAndView.getModel().get("usuario"), instanceOf(Usuario.class));
  }

  @Test
  public void irAHomeDeberiaRetornarVistaHome() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.irAHome();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("home"));
  }

  @Test
  public void logoutDeberiaInvalidarSesionYRedirigirALogin() {
    when(requestMock.getSession()).thenReturn(sessionMock);

    ModelAndView modelAndView = controladorLogin.logout(requestMock);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    verify(sessionMock, times(1)).invalidate();
  }

  @Test
  public void inicioDeberiaRedirigirALogin() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.inicio();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }
}
