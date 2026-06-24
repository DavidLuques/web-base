package com.tallerwebi.presentacion.controlador;

import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.servicio.ServicioExportacion;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ControladorExportacionTest {

  private ControladorExportacion controladorExportacion;
  private ServicioExportacion servicioExportacion;
  private HttpServletRequest requestMock;
  private HttpServletResponse responseMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void setUp() {
    servicioExportacion = mock(ServicioExportacion.class);
    controladorExportacion = new ControladorExportacion(servicioExportacion);
    requestMock = mock(HttpServletRequest.class);
    responseMock = mock(HttpServletResponse.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(requestMock.getContextPath()).thenReturn("/spring");
  }

  @Test
  public void exportarPdf_UsuarioLogueadoYMascotaExistente_LlamaAServicio() throws Exception {
    // Arrange
    Long idMascota = 1L;
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);

    // Act
    controladorExportacion.exportarPdfMascota(requestMock, responseMock, idMascota);

    // Assert
    verify(servicioExportacion, times(1)).exportarPdfMascota(idMascota, responseMock);
  }

  @Test
  public void exportarPdf_UsuarioNoLogueado_RedirigeALogin() throws Exception {
    // Arrange
    Long idMascota = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(null);

    // Act
    controladorExportacion.exportarPdfMascota(requestMock, responseMock, idMascota);

    // Assert
    verify(responseMock, times(1)).sendRedirect("/spring/login");
    verify(servicioExportacion, never()).exportarPdfMascota(anyLong(), any());
  }

  @Test
  public void exportarPdf_MascotaInexistente_RetornaErrorNotFound() throws Exception {
    // Arrange
    Long idMascota = 99L;
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("ID_USUARIO")).thenReturn(idUsuario);

    doThrow(new IllegalArgumentException("Mascota no encontrada"))
      .when(servicioExportacion)
      .exportarPdfMascota(idMascota, responseMock);

    // Act
    controladorExportacion.exportarPdfMascota(requestMock, responseMock, idMascota);

    // Assert
    verify(responseMock, times(1))
      .sendError(HttpServletResponse.SC_NOT_FOUND, "Mascota no encontrada");
  }
}
