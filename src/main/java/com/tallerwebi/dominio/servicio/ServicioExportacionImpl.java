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
import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.Mascota;
import java.awt.Color;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioExportacion")
@Transactional
public class ServicioExportacionImpl implements ServicioExportacion {

  private final ServicioMascota servicioMascota;
  private final RepositorioAnalisis repositorioAnalisis;

  @Autowired
  public ServicioExportacionImpl(
    ServicioMascota servicioMascota,
    RepositorioAnalisis repositorioAnalisis
  ) {
    this.servicioMascota = servicioMascota;
    this.repositorioAnalisis = repositorioAnalisis;
  }

  @Override
  public void exportarPdfMascota(Long idMascota, javax.servlet.http.HttpServletResponse response)
    throws Exception {
    Mascota mascota = servicioMascota.obtenerMascotaPorId(idMascota);
    if (mascota == null) {
      throw new IllegalArgumentException("Mascota no encontrada");
    }

    List<Analisis> analisisList = repositorioAnalisis.buscarPorMascota(idMascota);

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
      agregarTabla(document, analisisList);
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

    Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
    fontSubTitle.setSize(14);
    Paragraph subTitle = new Paragraph("Historial de Constantes Vitales", fontSubTitle);
    document.add(subTitle);
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
      indiceTrabajoCardiaco = String.format(java.util.Locale.US, "%.1f", rpp);
    } else {
      indiceTrabajoCardiaco = "-";
    }
    table.addCell(indiceTrabajoCardiaco);
  }
}
