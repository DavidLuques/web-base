package com.tallerwebi.presentacion.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import com.tallerwebi.presentacion.DatosAltaMascota;
import java.util.List;
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
  public void alEliminarMascotaYNoQuedarMascotasRedirigeASinMascota() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(1L);
    when(servicioMascotaMock.obtenerMascotasPorUsuario(1L))
      .thenReturn(java.util.Collections.emptyList());

    ModelAndView mav = controlador.eliminarMascota(requestMock, 1L);

    verify(servicioMascotaMock).eliminarMascota(1L);
    verify(servicioMascotaMock).obtenerMascotasPorUsuario(1L);

    assertEquals("redirect:/sin-mascota", mav.getViewName());
  }

  @Test
  public void alEliminarMascotaYQuedarMascotasRedirigeAlDashboardDeLaPrimera() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(1L);

    Mascota mascota = mock(Mascota.class);
    when(mascota.getId()).thenReturn(5L);

    when(servicioMascotaMock.obtenerMascotasPorUsuario(1L)).thenReturn(List.of(mascota));

    ModelAndView mav = controlador.eliminarMascota(requestMock, 1L);

    verify(servicioMascotaMock).eliminarMascota(1L);
    verify(servicioMascotaMock).obtenerMascotasPorUsuario(1L);

    assertEquals("redirect:/analisis/dashboard/5", mav.getViewName());
  }

  @Test
  public void siLaMascotaNoExisteAlVerPerfilRedirigeAConfiguraciones() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(1L);
    when(servicioMascotaMock.obtenerDatosMascota(1L)).thenReturn(null);

    ModelAndView mav = controlador.verPerfilMascota(requestMock, 1L);

    assertEquals("redirect:/configuraciones", mav.getViewName());
  }

  @Test
  public void siLaMascotaNoExisteAlEditarPerfilRedirigeAConfiguraciones() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(1L);
    when(servicioMascotaMock.obtenerDatosMascota(1L)).thenReturn(null);

    ModelAndView mav = controlador.editarPerfilMascota(requestMock, 1L);

    assertEquals("redirect:/configuraciones", mav.getViewName());
  }

  @Test
  public void alVerPerfilAgregaMisMascotasAlModelo() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(1L);

    DatosAltaMascota datosMascota = new DatosAltaMascota();
    List<Mascota> mascotas = List.of(mock(Mascota.class));

    when(servicioMascotaMock.obtenerDatosMascota(1L)).thenReturn(datosMascota);

    when(servicioMascotaMock.obtenerMascotasPorUsuario(1L)).thenReturn(mascotas);

    ModelAndView mav = controlador.verPerfilMascota(requestMock, 1L);

    assertEquals(mascotas, mav.getModel().get("misMascotas"));
  }
}
