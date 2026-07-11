package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.enums.TipoMascota;
import com.tallerwebi.dominio.modelo.Alerta;
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
  private RepositorioAlerta repositorioAlerta;
  private HttpServletResponse responseMock;

  @BeforeEach
  public void setUp() {
    servicioMascota = mock(ServicioMascota.class);
    repositorioAnalisis = mock(RepositorioAnalisis.class);
    repositorioAlerta = mock(RepositorioAlerta.class);
    servicioExportacion =
      new ServicioExportacionImpl(servicioMascota, repositorioAnalisis, repositorioAlerta);
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
    mascota.setTipo(TipoMascota.PERRO);
    mascota.setRaza("Golden");
    mascota.setPeso(20.5);

    Analisis analisis = new Analisis();
    analisis.setFechaYHora(LocalDateTime.now().minusDays(5));
    DatosAnalisis datos = new DatosAnalisis();
    datos.setFrecuenciaCardiaca(80);
    datos.setTemperatura(38.5);
    datos.setPresionSistolica(120);
    datos.setPresionDiastolica(80);
    analisis.setDatos(datos);

    List<Analisis> historial = new ArrayList<>();
    historial.add(analisis);

    Alerta alerta = new Alerta();
    alerta.setTipo(TipoAlerta.EMERGENCIA);
    alerta.setMensaje("Pulsaciones altas");
    alerta.setFechaYHora(LocalDateTime.now().minusDays(2));
    List<Alerta> alertas = new ArrayList<>();
    alertas.add(alerta);

    when(servicioMascota.obtenerMascotaPorId(idMascota)).thenReturn(mascota);
    when(repositorioAnalisis.buscarPorMascota(idMascota)).thenReturn(historial);
    when(repositorioAlerta.buscarPorMascota(idMascota)).thenReturn(alertas);

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
    verify(repositorioAlerta, times(1)).buscarPorMascota(idMascota);
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
    when(repositorioAlerta.buscarPorMascota(idMascota)).thenReturn(new ArrayList<>());

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

  @Test
  public void exportarPdf_MascotaConAnalisisValoresNulos_NoFalla() throws Exception {
    // Arrange
    Long idMascota = 1L;
    Mascota mascota = new Mascota();
    mascota.setId(idMascota);

    Analisis analisis = new Analisis();
    analisis.setFechaYHora(null);
    DatosAnalisis datos = new DatosAnalisis();
    datos.setFrecuenciaCardiaca(null);
    datos.setPresionSistolica(null);
    datos.setTemperatura(null);
    datos.setPresionDiastolica(null);
    analisis.setDatos(datos);

    List<Analisis> historial = new ArrayList<>();
    historial.add(analisis);

    when(servicioMascota.obtenerMascotaPorId(idMascota)).thenReturn(mascota);
    when(repositorioAnalisis.buscarPorMascota(idMascota)).thenReturn(historial);
    when(repositorioAlerta.buscarPorMascota(idMascota)).thenReturn(new ArrayList<>());

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
    assert (out.size() > 0);
  }
}
