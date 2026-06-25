package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.DatosAnalisis;
import com.tallerwebi.dominio.modelo.Mascota;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioExportacionImplTest {

  private ServicioExportacionImpl servicioExportacion;
  private ServicioMascota servicioMascota;
  private RepositorioAnalisis repositorioAnalisis;
  private HttpServletResponse responseMock;

  @BeforeEach
  public void setUp() {
    servicioMascota = mock(ServicioMascota.class);
    repositorioAnalisis = mock(RepositorioAnalisis.class);
    servicioExportacion = new ServicioExportacionImpl(servicioMascota, repositorioAnalisis);
    responseMock = mock(HttpServletResponse.class);
  }

  @Test
  public void exportarPdf_MascotaExistenteConHistorial_ConfiguraHeadersYEscribePdf()
    throws Exception {
    // Arrange
    Long idMascota = 1L;
    Mascota mascota = new Mascota();
    mascota.setId(idMascota);
    mascota.setNombre("Firulais");
    mascota.setTipo("Perro");
    mascota.setRaza("Golden");
    mascota.setPeso(20.5);

    Analisis analisis = new Analisis();
    analisis.setFechaYHora(LocalDateTime.now());
    DatosAnalisis datos = new DatosAnalisis();
    datos.setFrecuenciaCardiaca(80);
    datos.setTemperatura(38.5);
    datos.setPresionSistolica(120);
    datos.setPresionDiastolica(80);
    datos.setOxigenacion(98.0);
    analisis.setDatos(datos);

    List<Analisis> historial = new ArrayList<>();
    historial.add(analisis);

    when(servicioMascota.obtenerMascotaPorId(idMascota)).thenReturn(mascota);
    when(repositorioAnalisis.buscarPorMascota(idMascota)).thenReturn(historial);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ServletOutputStream servletOutputStream = new ServletOutputStream() {
      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setWriteListener(WriteListener writeListener) {}

      @Override
      public void write(int b) throws IOException {
        out.write(b);
      }
    };

    when(responseMock.getOutputStream()).thenReturn(servletOutputStream);

    // Act
    servicioExportacion.exportarPdfMascota(idMascota, responseMock);

    // Assert
    verify(responseMock).setContentType("application/pdf");
    verify(responseMock)
      .setHeader(
        "Content-Disposition",
        "attachment; filename=reporte_mascota_" + idMascota + ".pdf"
      );
    verify(responseMock).setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    verify(servicioMascota, times(1)).obtenerMascotaPorId(idMascota);
    verify(repositorioAnalisis, times(1)).buscarPorMascota(idMascota);
    assert (out.size() > 0);
  }

  @Test
  public void exportarPdf_MascotaExistenteSinHistorial_ConfiguraHeadersYEscribePdf()
    throws Exception {
    // Arrange
    Long idMascota = 1L;
    Mascota mascota = new Mascota();
    mascota.setId(idMascota);
    mascota.setNombre("Firulais");

    when(servicioMascota.obtenerMascotaPorId(idMascota)).thenReturn(mascota);
    when(repositorioAnalisis.buscarPorMascota(idMascota)).thenReturn(new ArrayList<>());

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ServletOutputStream servletOutputStream = new ServletOutputStream() {
      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setWriteListener(WriteListener writeListener) {}

      @Override
      public void write(int b) throws IOException {
        out.write(b);
      }
    };

    when(responseMock.getOutputStream()).thenReturn(servletOutputStream);

    // Act
    servicioExportacion.exportarPdfMascota(idMascota, responseMock);

    // Assert
    verify(responseMock).setContentType("application/pdf");
    assert (out.size() > 0);
  }

  @Test
  public void exportarPdf_MascotaInexistente_LanzaIllegalArgumentException() {
    // Arrange
    Long idMascota = 99L;
    when(servicioMascota.obtenerMascotaPorId(idMascota)).thenReturn(null);

    // Act & Assert
    assertThrows(
      IllegalArgumentException.class,
      () -> {
        servicioExportacion.exportarPdfMascota(idMascota, responseMock);
      }
    );
  }
}
