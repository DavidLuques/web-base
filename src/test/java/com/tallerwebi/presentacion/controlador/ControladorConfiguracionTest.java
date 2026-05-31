package com.tallerwebi.presentacion.controlador;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorConfiguracionTest {

    private ControladorConfiguracion controlador;
    private ServicioUsuario servicioUsuarioMock;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;

    @BeforeEach
    public void init() {
        servicioUsuarioMock = mock(ServicioUsuario.class);
        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);

        when(requestMock.getSession()).thenReturn(sessionMock);

        controlador = new ControladorConfiguracion(servicioUsuarioMock);
    }

    @Test
    public void cuandoElUsuarioEstaLogueadoRetornaLaVistaConfiguraciones() {
        Long idUsuario = 1L;
        Usuario usuario = new Usuario();

        when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
        when(servicioUsuarioMock.obtenerPerfil(idUsuario)).thenReturn(usuario);

        ModelAndView mav = controlador.irAConfiguraciones(requestMock, null);

        assertThat(mav.getViewName(), equalTo("configuraciones"));
    }

    @Test
    public void cuandoElUsuarioNoEstaLogueadoRedireccionaAlLogin() {
        when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

        ModelAndView mav = controlador.irAConfiguraciones(requestMock, null);

        assertThat(mav.getViewName(), equalTo("redirect:/login"));
    }

    @Test
    public void cuandoElUsuarioEstaLogueadoElModeloContieneElUsuario() {
        Long idUsuario = 1L;
        Usuario usuario = new Usuario();

        when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
        when(servicioUsuarioMock.obtenerPerfil(idUsuario)).thenReturn(usuario);

        ModelAndView mav = controlador.irAConfiguraciones(requestMock, null);

        assertThat(mav.getModel().get("usuario"), equalTo(usuario));
    }

    @Test
    public void cuandoSePassaIdMascotaElModeloLaContiene() {
        Long idUsuario = 1L;
        Long idMascota = 5L;
        Usuario usuario = new Usuario();

        when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
        when(servicioUsuarioMock.obtenerPerfil(idUsuario)).thenReturn(usuario);

        ModelAndView mav = controlador.irAConfiguraciones(requestMock, idMascota);

        assertThat(mav.getModel().get("idMascota"), equalTo(idMascota));
    }

    @Test
    public void cuandoNoSePassaIdMascotaElModeloLaTieneNula() {
        Long idUsuario = 1L;
        Usuario usuario = new Usuario();

        when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
        when(servicioUsuarioMock.obtenerPerfil(idUsuario)).thenReturn(usuario);

        ModelAndView mav = controlador.irAConfiguraciones(requestMock, null);

        assertThat(mav.getModel().get("idMascota"), nullValue());
    }

    @Test
    public void cuandoElUsuarioEstaLogueadoSeLlamaAlServicioUnaVez() {
        Long idUsuario = 1L;
        Usuario usuario = new Usuario();

        when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);
        when(servicioUsuarioMock.obtenerPerfil(idUsuario)).thenReturn(usuario);

        controlador.irAConfiguraciones(requestMock, null);

        verify(servicioUsuarioMock, times(1)).obtenerPerfil(idUsuario);
    }

    @Test
    public void cuandoElUsuarioNoEstaLogueadoNoSeLlamaAlServicio() {
        when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

        controlador.irAConfiguraciones(requestMock, null);

        verify(servicioUsuarioMock, never()).obtenerPerfil(any());
    }

    @Test
    public void cuandoElUsuarioNoEstaLogueadoElModeloEstaVacio() {
        when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

        ModelAndView mav = controlador.irAConfiguraciones(requestMock, null);

        assertThat(mav.getModel().isEmpty(), equalTo(true));
    }
}