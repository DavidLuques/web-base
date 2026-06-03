package com.tallerwebi.presentacion.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.servicio.ServicioMascota;
import com.tallerwebi.presentacion.DatosAltaMascota;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPerfilMascotaTest {

  private ControladorPerfilMascota controlador;
  private ServicioMascota servicioMascotaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioMascotaMock = mock(ServicioMascota.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);

    when(requestMock.getSession()).thenReturn(sessionMock);

    controlador = new ControladorPerfilMascota(servicioMascotaMock);
  }

  @Test
  public void siNoHayUsuarioLogueadoAlVerPerfilRedirigeALogin() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView mav = controlador.verPerfilMascota(requestMock, 1L);

    assertEquals("redirect:/login", mav.getViewName());
  }

  @Test
  public void siHayUsuarioLogueadoYDatosExistenMuestraElPerfilMascota() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(1L);
    DatosAltaMascota datosMascota = new DatosAltaMascota();
    when(servicioMascotaMock.obtenerDatosMascota(1L)).thenReturn(datosMascota);

    ModelAndView mav = controlador.verPerfilMascota(requestMock, 1L);

    assertEquals("ver-perfil-mascota", mav.getViewName());
    assertEquals(datosMascota, mav.getModel().get("datosMascota"));
    assertEquals(1L, mav.getModel().get("idMascota"));
  }

  @Test
  public void siNoHayUsuarioLogueadoAlEditarPerfilRedirigeALogin() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView mav = controlador.editarPerfilMascota(requestMock, 1L);

    assertEquals("redirect:/login", mav.getViewName());
  }

  @Test
  public void siHayUsuarioLogueadoYDatosExistenMuestraFormularioDeEdicion() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(1L);
    DatosAltaMascota datosMascota = new DatosAltaMascota();
    when(servicioMascotaMock.obtenerDatosMascota(1L)).thenReturn(datosMascota);

    ModelAndView mav = controlador.editarPerfilMascota(requestMock, 1L);

    assertEquals("perfil-mascota", mav.getViewName());
    assertEquals(datosMascota, mav.getModel().get("datosMascota"));
  }

  @Test
  public void alActualizarPerfilLlamaAlServicioYRedirigeAlPerfil() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(1L);
    DatosAltaMascota datosMascota = new DatosAltaMascota();

    ModelAndView mav = controlador.actualizarPerfilMascota(datosMascota, requestMock, 1L);

    verify(servicioMascotaMock).actualizarMascota(1L, datosMascota);
    assertEquals(
      "redirect:/configuraciones/mascota/perfil?exito=true&idMascota=1",
      mav.getViewName()
    );
  }

  @Test
  public void alEliminarMascotaLlamaAlServicioYRedirigeAConfiguraciones() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(1L);

    ModelAndView mav = controlador.eliminarMascota(requestMock, 1L);

    verify(servicioMascotaMock).eliminarMascota(1L);
    assertEquals("redirect:/configuraciones", mav.getViewName());
  }
}
