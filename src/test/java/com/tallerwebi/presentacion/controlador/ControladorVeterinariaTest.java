package com.tallerwebi.presentacion.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.TurnoVeterinaria;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import com.tallerwebi.dominio.servicio.ServicioVeterinaria;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorVeterinariaTest {

  private ControladorVeterinaria controladorVeterinaria;
  private ServicioVeterinaria servicioVeterinariaMock;
  private ServicioMascota servicioMascotaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioVeterinariaMock = mock(ServicioVeterinaria.class);
    servicioMascotaMock = mock(ServicioMascota.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);

    when(requestMock.getSession()).thenReturn(sessionMock);

    controladorVeterinaria =
      new ControladorVeterinaria(servicioVeterinariaMock, servicioMascotaMock);
  }

  @Test
  public void queSiNoHayUsuarioEnSesionRedirijaAlLoginAlVerElPanel() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView mav = controladorVeterinaria.mostrarPanelVeterinaria(requestMock, 1L);

    assertEquals("redirect:/login", mav.getViewName());
  }

  @Test
  public void queAlAgendarTurnoExitosamenteRedirijaConParametroDeExito() {
    Long idMascota = 1L;
    String nombreVet = "Centro Veterinario";
    String direccion = "Calle Falsa 123";
    LocalDateTime fecha = LocalDateTime.now();
    String motivo = "Consulta";

    ModelAndView mav = controladorVeterinaria.agendarTurno(
      idMascota,
      nombreVet,
      direccion,
      fecha,
      motivo
    );

    assertEquals(
      "redirect:/analisis/veterinaria/mascota/1?exito=turno_guardado",
      mav.getViewName()
    );
    verify(servicioVeterinariaMock, times(1))
      .agendarTurno(idMascota, nombreVet, direccion, fecha, motivo);
  }

  @Test
  public void queAlCancelarTurnoExitosamenteRedirijaConParametroDeExito() {
    Long idTurno = 10L;
    Long idMascota = 1L;

    ModelAndView mav = controladorVeterinaria.cancelarTurno(idTurno, idMascota);

    assertEquals(
      "redirect:/analisis/veterinaria/mascota/1?exito=turno_cancelado",
      mav.getViewName()
    );
    verify(servicioVeterinariaMock, times(1)).cancelarTurno(idTurno);
  }

  @Test
  public void queSiOcurreUnErrorAlAgendarRedirijaConParametroDeError() {
    Long idMascota = 1L;
    doThrow(new RuntimeException("Fallo en la base de datos"))
      .when(servicioVeterinariaMock)
      .agendarTurno(anyLong(), anyString(), anyString(), any(), anyString());

    ModelAndView mav = controladorVeterinaria.agendarTurno(
      idMascota,
      "Vet",
      "Dir",
      LocalDateTime.now(),
      "Motivo"
    );

    // Debe ir por el 'catch' y devolver la URL con el error
    assertTrue(mav.getViewName().contains("error=fallo_reserva"));
    assertEquals(
      "No se pudo agendar el turno: Fallo en la base de datos",
      mav.getModel().get("error")
    );
  }

  @Test
  public void queSiIdUsuarioEsNuloRedirijaAlLogin() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    ModelAndView mav = controladorVeterinaria.mostrarPanelVeterinaria(requestMock, 1L);

    assertEquals("redirect:/login", mav.getViewName());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void queAlMostrarElPanelConTurnosSeConviertanCorrectamenteParaJavaScript() {
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(1L);
    when(servicioMascotaMock.obtenerMascotaPorId(1L)).thenReturn(new Mascota());

    TurnoVeterinaria turnoPrueba = new TurnoVeterinaria();
    turnoPrueba.setId(100L);
    turnoPrueba.setNombreVeterinaria("Clínica Central");
    turnoPrueba.setFechaYHora(LocalDateTime.now());
    turnoPrueba.setMotivo("Vacuna");

    java.util.List<TurnoVeterinaria> listaTurnos = new java.util.ArrayList<>();
    listaTurnos.add(turnoPrueba);

    // Simulamos que devuelve turnos para que entre a los bucles 'for'
    when(servicioVeterinariaMock.obtenerTurnosProximos(1L)).thenReturn(listaTurnos);
    when(servicioVeterinariaMock.obtenerTurnosPasados(1L)).thenReturn(listaTurnos);

    ModelAndView mav = controladorVeterinaria.mostrarPanelVeterinaria(requestMock, 1L);

    java.util.List<java.util.Map<String, String>> proximosJS = (java.util.List<
        java.util.Map<String, String>
      >) mav.getModel().get("proximosJS");

    assertEquals(1, proximosJS.size());
    assertEquals("Clínica Central", proximosJS.get(0).get("nombreVeterinaria"));
    assertEquals("100", proximosJS.get(0).get("id"));
  }
}
