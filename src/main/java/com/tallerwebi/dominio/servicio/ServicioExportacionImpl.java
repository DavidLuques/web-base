package com.tallerwebi.dominio.servicio;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.Mascota;
import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioExportacion")
@Transactional
public class ServicioExportacionImpl implements ServicioExportacion {

  private final ServicioMascota servicioMascota;
  private final RepositorioAnalisis repositorioAnalisis;
  private final RepositorioAlerta repositorioAlerta;

  @Autowired
  public ServicioExportacionImpl(
    ServicioMascota servicioMascota,
    RepositorioAnalisis repositorioAnalisis,
    RepositorioAlerta repositorioAlerta
  ) {
    this.servicioMascota = servicioMascota;
    this.repositorioAnalisis = repositorioAnalisis;
    this.repositorioAlerta = repositorioAlerta;
  }

  @Override
  public void exportarPdfMascota(Long idMascota, javax.servlet.http.HttpServletResponse response)
    throws Exception {
    Mascota mascota = servicioMascota.obtenerMascotaPorId(idMascota);
    if (mascota == null) {
      throw new IllegalArgumentException("Mascota no encontrada");
    }

    List<Analisis> analisisList = repositorioAnalisis.buscarPorMascota(idMascota);
    List<Alerta> alertas = repositorioAlerta.buscarPorMascota(idMascota);

    List<Analisis> analisisUltimos30Dias = analisisList
      .stream()
      .filter(a ->
        a.getFechaYHora() != null && a.getFechaYHora().isAfter(LocalDateTime.now().minusDays(30))
      )
      .collect(Collectors.toList());

    List<Alerta> alertasUltimos30Dias = alertas
      .stream()
      .filter(a ->
        a.getFechaYHora() != null && a.getFechaYHora().isAfter(LocalDateTime.now().minusDays(30))
      )
      .collect(Collectors.toList());

    response.setContentType("application/pdf");
    response.setHeader(
      "Content-Disposition",
      "attachment; filename=reporte_mascota_" + idMascota + ".pdf"
    );
    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");

    try (Document document = new Document()) {
      PdfWriter.getInstance(document, response.getOutputStream());
      document.open();

      agregarCabecera(document, mascota);
      agregarResumenClinico(document, analisisUltimos30Dias);
      agregarAnomalias(document, alertasUltimos30Dias);

      Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
      fontSubTitle.setSize(14);
      document.add(new Paragraph("Anexo: Historial Detallado (Últimos 30 días)", fontSubTitle));
      document.add(new Paragraph(" "));

      agregarTabla(document, analisisUltimos30Dias);
    } catch (DocumentException ex) {
      throw new IOException("Error al generar el PDF", ex);
    }
  }

  private void agregarCabecera(Document document, Mascota mascota) {
    Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
    fontTitle.setSize(18);
    Paragraph title = new Paragraph("Reporte de Salud: " + mascota.getNombre(), fontTitle);
    title.setAlignment(Paragraph.ALIGN_CENTER);
    document.add(title);

    document.add(new Paragraph(" "));

    Font fontText = FontFactory.getFont(FontFactory.HELVETICA);
    fontText.setSize(12);
    document.add(
      new Paragraph("Tipo: " + (mascota.getTipo() != null ? mascota.getTipo() : "-"), fontText)
    );
    document.add(
      new Paragraph("Raza: " + (mascota.getRaza() != null ? mascota.getRaza() : "-"), fontText)
    );
    document.add(
      new Paragraph(
        "Peso: " + (mascota.getPeso() != null ? mascota.getPeso() + " kg" : "-"),
        fontText
      )
    );
    document.add(
      new Paragraph(
        "Estado actual: " +
        (mascota.getEstadoActual() != null ? mascota.getEstadoActual().toString() : "-"),
        fontText
      )
    );

    document.add(new Paragraph(" "));
  }

  private void agregarResumenClinico(Document document, List<Analisis> analisisList) {
    Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
    fontSubTitle.setSize(14);
    document.add(new Paragraph("Resumen Clínico (Últimos 30 días)", fontSubTitle));
    document.add(new Paragraph(" "));

    if (analisisList.isEmpty()) {
      document.add(new Paragraph("No hay datos de telemetría registrados en los últimos 30 días."));
      document.add(new Paragraph(" "));
      return;
    }

    Font fontText = FontFactory.getFont(FontFactory.HELVETICA);
    fontText.setSize(12);

    agregarEstadisticasFrecuenciaCardiaca(document, analisisList, fontText);
    agregarEstadisticasTemperatura(document, analisisList, fontText);
    agregarEstadisticasRpp(document, analisisList, fontText);

    document.add(new Paragraph(" "));
  }

  private void agregarEstadisticasFrecuenciaCardiaca(
    Document document,
    List<Analisis> analisisList,
    Font font
  ) {
    IntSummaryStatistics stats = analisisList
      .stream()
      .filter(a -> a.getDatos().getFrecuenciaCardiaca() != null)
      .mapToInt(a -> a.getDatos().getFrecuenciaCardiaca())
      .summaryStatistics();

    if (stats.getCount() > 0) {
      document.add(
        new Paragraph(
          String.format(
            "Frecuencia Cardíaca: Promedio %d bpm (Mín: %d, Máx: %d)",
            (int) stats.getAverage(),
            stats.getMin(),
            stats.getMax()
          ),
          font
        )
      );
    }
  }

  private void agregarEstadisticasTemperatura(
    Document document,
    List<Analisis> analisisList,
    Font font
  ) {
    DoubleSummaryStatistics stats = analisisList
      .stream()
      .filter(a -> a.getDatos().getTemperatura() != null)
      .mapToDouble(a -> a.getDatos().getTemperatura())
      .summaryStatistics();

    if (stats.getCount() > 0) {
      document.add(
        new Paragraph(
          String.format(Locale.US, "Temperatura: Promedio %.1f °C", stats.getAverage()),
          font
        )
      );
    }
  }

  private void agregarEstadisticasRpp(Document document, List<Analisis> analisisList, Font font) {
    DoubleSummaryStatistics stats = analisisList
      .stream()
      .filter(a ->
        a.getDatos().getFrecuenciaCardiaca() != null && a.getDatos().getPresionSistolica() != null
      )
      .mapToDouble(a ->
        (a.getDatos().getFrecuenciaCardiaca() * a.getDatos().getPresionSistolica()) / 100.0
      )
      .summaryStatistics();

    if (stats.getCount() > 0) {
      document.add(
        new Paragraph(
          String.format(
            Locale.US,
            "Índice de Trabajo Cardíaco: Promedio %.1f (Pico Máximo: %.1f)",
            stats.getAverage(),
            stats.getMax()
          ),
          font
        )
      );
    }
  }

  private void agregarAnomalias(Document document, List<Alerta> alertas) {
    Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
    fontSubTitle.setSize(14);
    document.add(new Paragraph("Reporte de Anomalías (Últimos 30 días)", fontSubTitle));
    document.add(new Paragraph(" "));

    Font fontText = FontFactory.getFont(FontFactory.HELVETICA);
    fontText.setSize(12);
    if (alertas.isEmpty()) {
      document.add(
        new Paragraph(
          "No se registraron anomalías clínicas ni alertas en el período analizado. El paciente se encuentra estable.",
          fontText
        )
      );
    } else {
      for (Alerta alerta : alertas) {
        String fecha = alerta.getFechaYHora() != null
          ? alerta.getFechaYHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
          : "-";
        document.add(
          new Paragraph(
            "- [" + fecha + "] " + alerta.obtenerTipoFormato() + ": " + alerta.getMensaje(),
            fontText
          )
        );
      }
    }
    document.add(new Paragraph(" "));
  }

  private void agregarTabla(Document document, List<Analisis> analisisList) {
    PdfPTable table = new PdfPTable(5);
    table.setWidthPercentage(100f);
    table.setWidths(new float[] { 2.5f, 1.5f, 1.5f, 1.5f, 1.5f });

    String[] headers = {
      "Fecha",
      "Frec. Cardíaca",
      "Presión (S/D)",
      "Temp.",
      "Índ. Trabajo Cardíaco",
    };
    Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
    fontHeader.setColor(Color.WHITE);

    for (String headerTitle : headers) {
      PdfPCell cell = new PdfPCell();
      cell.setBackgroundColor(Color.DARK_GRAY);
      cell.setPadding(5);
      cell.setPhrase(new Phrase(headerTitle, fontHeader));
      table.addCell(cell);
    }

    for (Analisis analisis : analisisList) {
      agregarFilaTabla(table, analisis);
    }

    document.add(table);
  }

  private void agregarFilaTabla(PdfPTable table, Analisis analisis) {
    String fecha = analisis.getFechaYHora() != null
      ? analisis.getFechaYHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
      : "-";
    table.addCell(fecha);

    String fc = analisis.getDatos().getFrecuenciaCardiaca() != null
      ? String.valueOf(analisis.getDatos().getFrecuenciaCardiaca())
      : "-";
    table.addCell(fc);

    final String presion;
    if (
      analisis.getDatos().getPresionSistolica() != null &&
      analisis.getDatos().getPresionDiastolica() != null
    ) {
      presion =
        analisis.getDatos().getPresionSistolica() +
        "/" +
        analisis.getDatos().getPresionDiastolica();
    } else {
      presion = "-";
    }
    table.addCell(presion);

    String temp = analisis.getDatos().getTemperatura() != null
      ? String.valueOf(analisis.getDatos().getTemperatura())
      : "-";
    table.addCell(temp);

    final String indiceTrabajoCardiaco;
    if (
      analisis.getDatos().getFrecuenciaCardiaca() != null &&
      analisis.getDatos().getPresionSistolica() != null
    ) {
      double rpp =
        (analisis.getDatos().getFrecuenciaCardiaca() * analisis.getDatos().getPresionSistolica()) /
        100.0;
      indiceTrabajoCardiaco = String.format(Locale.US, "%.1f", rpp);
    } else {
      indiceTrabajoCardiaco = "-";
    }
    table.addCell(indiceTrabajoCardiaco);
  }
}
