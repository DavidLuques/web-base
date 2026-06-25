package com.tallerwebi.dominio.servicio;

import javax.servlet.http.HttpServletResponse;

public interface ServicioExportacion {
  void exportarPdfMascota(Long idMascota, HttpServletResponse response) throws Exception;
}
