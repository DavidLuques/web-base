package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.servicio.OrquestadorService;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorValla {

  private final OrquestadorService orquestadorService;

  @Autowired
  public ControladorValla(OrquestadorService orquestadorService) {
    this.orquestadorService = orquestadorService;
  }

  @RequestMapping(path = "/analisis/valla/{idMascota}", method = RequestMethod.GET)
  public ModelAndView verVallado(HttpServletRequest request, @PathVariable Long idMascota) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelMap modelo = new ModelMap();
    modelo.put("idMascota", idMascota);
    return new ModelAndView("valla-mascota", modelo);
  }

  @GetMapping("/api/mascotas/{id}/ubicacion")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> obtenerUbicacionActual(@PathVariable Long id) {
    Map<String, Object> ubicacion = orquestadorService.obtenerUltimaUbicacion(id);
    return ResponseEntity.ok(ubicacion);
  }
}
