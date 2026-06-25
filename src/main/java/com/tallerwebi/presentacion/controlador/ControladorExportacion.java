package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.servicio.ServicioExportacion;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControladorExportacion {

  private static final String ATRIBUTO_ID_USUARIO = "ID_USUARIO";
  private final ServicioExportacion servicioExportacion;

  @Autowired
  public ControladorExportacion(ServicioExportacion servicioExportacion) {
    this.servicioExportacion = servicioExportacion;
  }

  @RequestMapping(path = "/mascota/exportar/pdf", method = RequestMethod.GET)
  public void exportarPdfMascota(
    HttpServletRequest request,
    javax.servlet.http.HttpServletResponse response,
    @RequestParam(required = true) Long idMascota
  ) throws Exception {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      response.sendRedirect(request.getContextPath() + "/login");
      return;
    }

    try {
      servicioExportacion.exportarPdfMascota(idMascota, response);
    } catch (IllegalArgumentException e) {
      response.sendError(
        javax.servlet.http.HttpServletResponse.SC_NOT_FOUND,
        "Mascota no encontrada"
      );
    }
  }
}
