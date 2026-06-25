package com.tallerwebi.presentacion.controlador;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.AccionNoPermitidaEnEsteEstadoException;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.SolicitudAmistad;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioAmistad;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorAmistadTest {

  private static final String ATRIBUTO_ID_USUARIO = "ID_USUARIO";

  private ControladorAmistad controlador;
  private ServicioAmistad servicioAmistadMock;
  private ServicioMascota servicioMascotaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioAmistadMock = mock(ServicioAmistad.class);
    servicioMascotaMock = mock(ServicioMascota.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);

    controlador = new ControladorAmistad(servicioAmistadMock, servicioMascotaMock);
  }

  private void simularUsuarioLogueado(Long idUsuario) {
    when(sessionMock.getAttribute(ATRIBUTO_ID_USUARIO)).thenReturn(idUsuario);
  }

  @Test
  public void siNoHayUsuarioLogueadoVerAmigosDebeRedirigirALogin() {
    ModelAndView mav = controlador.verAmigos(requestMock, null);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siHayUsuarioLogueadoVerAmigosDebeMostrarLaVistaConElModeloCompleto() {
    simularUsuarioLogueado(1L);
    List<Usuario> amigos = List.of(mock(Usuario.class));
    List<SolicitudAmistad> pendientes = List.of(new SolicitudAmistad());
    List<Mascota> misMascotas = List.of(new Mascota());
    when(servicioAmistadMock.obtenerAmigos(1L)).thenReturn(amigos);
    when(servicioAmistadMock.obtenerSolicitudesPendientes(1L)).thenReturn(pendientes);
    when(servicioMascotaMock.obtenerMascotasPorUsuario(1L)).thenReturn(misMascotas);

    ModelAndView mav = controlador.verAmigos(requestMock, 5L);

    assertThat(mav.getViewName(), equalTo("amigos"));
    assertThat(mav.getModel().get("amigos"), equalTo(amigos));
    assertThat(mav.getModel().get("solicitudesPendientes"), equalTo(pendientes));
    assertThat(mav.getModel().get("misMascotas"), equalTo(misMascotas));
    assertThat(mav.getModel().get("idMascota"), equalTo(5L));
  }

  @Test
  public void siNoHayUsuarioLogueadoEnviarSolicitudDebeRedirigirALogin() {
    ModelAndView mav = controlador.enviarSolicitud(requestMock, "amigo@mail.com", null);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siLaSolicitudSeEnviaCorrectamenteDebeRedirigirAExito() {
    simularUsuarioLogueado(1L);

    ModelAndView mav = controlador.enviarSolicitud(requestMock, "amigo@mail.com", 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/amigos?exito=true&idMascota=5"));
  }

  @Test
  public void siElEmailNoCorrespondeAUnUsuarioDebeRedirigirConError() {
    simularUsuarioLogueado(1L);
    when(servicioAmistadMock.enviarSolicitudPorEmail(1L, "noexiste@mail.com"))
      .thenThrow(new UsuarioNoEncontrado("No existe un usuario con ese email"));

    ModelAndView mav = controlador.enviarSolicitud(requestMock, "noexiste@mail.com", 5L);

    assertThat(
      mav.getViewName(),
      equalTo("redirect:/amigos?error=No existe un usuario con ese email&idMascota=5")
    );
  }

  @Test
  public void siLaAccionNoEstaPermitidaAlEnviarSolicitudDebeRedirigirConError() {
    simularUsuarioLogueado(1L);
    when(servicioAmistadMock.enviarSolicitudPorEmail(1L, "yomismo@mail.com"))
      .thenThrow(new AccionNoPermitidaEnEsteEstadoException("No podés agregarte a vos mismo"));

    ModelAndView mav = controlador.enviarSolicitud(requestMock, "yomismo@mail.com", null);

    assertThat(mav.getViewName(), equalTo("redirect:/amigos?error=No podés agregarte a vos mismo"));
  }

  @Test
  public void siNoHayUsuarioLogueadoAceptarSolicitudDebeRedirigirALogin() {
    ModelAndView mav = controlador.aceptarSolicitud(requestMock, 10L, null);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siSeAceptaLaSolicitudDebeRedirigirAExitoYLlamarAlServicio() {
    simularUsuarioLogueado(1L);

    ModelAndView mav = controlador.aceptarSolicitud(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/amigos?exito=true&idMascota=5"));
    verify(servicioAmistadMock, times(1)).aceptarSolicitud(10L);
  }

  @Test
  public void siSeAceptaLaSolicitudSinIdMascotaDebeRedirigirAExitoSinSufijo() {
    simularUsuarioLogueado(1L);

    ModelAndView mav = controlador.aceptarSolicitud(requestMock, 10L, null);

    assertThat(mav.getViewName(), equalTo("redirect:/amigos?exito=true"));
  }

  @Test
  public void siNoHayUsuarioLogueadoRechazarSolicitudDebeRedirigirALogin() {
    ModelAndView mav = controlador.rechazarSolicitud(requestMock, 10L, null);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siSeRechazaLaSolicitudDebeRedirigirAExitoYLlamarAlServicio() {
    simularUsuarioLogueado(1L);

    ModelAndView mav = controlador.rechazarSolicitud(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/amigos?exito=true&idMascota=5"));
    verify(servicioAmistadMock, times(1)).rechazarSolicitud(10L);
  }

  @Test
  public void siSeRechazaLaSolicitudSinIdMascotaDebeRedirigirAExitoSinSufijo() {
    simularUsuarioLogueado(1L);

    ModelAndView mav = controlador.rechazarSolicitud(requestMock, 10L, null);

    assertThat(mav.getViewName(), equalTo("redirect:/amigos?exito=true"));
  }
  
  @Test
  public void siNoHayUsuarioLogueadoCancelarSolicitudDebeRedirigirALogin() {
      ModelAndView mav = controlador.cancelarSolicitud(requestMock, 10L, null);

      assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siSeCancelaSolicitudDebeRedirigirAExitoYLlamarAlServicio() {
      simularUsuarioLogueado(1L);

      ModelAndView mav = controlador.cancelarSolicitud(requestMock, 10L, 5L);

      assertThat(mav.getViewName(), equalTo("redirect:/amigos?exito=true&idMascota=5"));
      verify(servicioAmistadMock, times(1)).cancelarSolicitud(10L);
  }

  @Test
  public void siSeCancelaSolicitudSinIdMascotaDebeRedirigirAExitoSinSufijo() {
      simularUsuarioLogueado(1L);

      ModelAndView mav = controlador.cancelarSolicitud(requestMock, 10L, null);

      assertThat(mav.getViewName(), equalTo("redirect:/amigos?exito=true"));
  }
  
  @Test
  public void siNoHayUsuarioLogueadoEliminarAmigoDebeRedirigirALogin() {
      ModelAndView mav = controlador.eliminarAmigo(requestMock, 2L, null);

      assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siSeEliminaAmigoDebeRedirigirAExitoYLlamarAlServicio() {
      simularUsuarioLogueado(1L);

      ModelAndView mav = controlador.eliminarAmigo(requestMock, 2L, 5L);

      assertThat(mav.getViewName(), equalTo("redirect:/amigos?exito=true&idMascota=5"));
      verify(servicioAmistadMock, times(1)).eliminarAmigo(1L, 2L);
  }

  @Test
  public void siSeEliminaAmigoSinIdMascotaDebeRedirigirAExitoSinSufijo() {
      simularUsuarioLogueado(1L);

      ModelAndView mav = controlador.eliminarAmigo(requestMock, 2L, null);

      assertThat(mav.getViewName(), equalTo("redirect:/amigos?exito=true"));
  }
  
  @Test
  public void siLaAccionNoEstaPermitidaAlCancelarSolicitudDebeRedirigirConError() {
      simularUsuarioLogueado(1L);
      doThrow(new AccionNoPermitidaEnEsteEstadoException("No se puede cancelar una solicitud en estado ACEPTADA"))
        .when(servicioAmistadMock).cancelarSolicitud(10L);

      ModelAndView mav = controlador.cancelarSolicitud(requestMock, 10L, 5L);

      assertThat(
        mav.getViewName(),
        equalTo("redirect:/amigos?error=No se puede cancelar una solicitud en estado ACEPTADA&idMascota=5")
      );
  }

  @Test
  public void siLaAccionNoEstaPermitidaAlEliminarAmigoDebeRedirigirConError() {
      simularUsuarioLogueado(1L);
      doThrow(new AccionNoPermitidaEnEsteEstadoException("No se puede eliminar esta amistad"))
        .when(servicioAmistadMock).eliminarAmigo(1L, 2L);

      ModelAndView mav = controlador.eliminarAmigo(requestMock, 2L, 5L);

      assertThat(
        mav.getViewName(),
        equalTo("redirect:/amigos?error=No se puede eliminar esta amistad&idMascota=5")
      );
  }


}
