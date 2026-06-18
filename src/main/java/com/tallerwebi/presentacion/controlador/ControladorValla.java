package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.servicio.OrquestadorService;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import com.tallerwebi.dominio.servicio.ServicioVallado;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * / Controlador de la vista.
 */
@Controller
public class ControladorValla {

  private final OrquestadorService orquestadorService;
  private final ServicioMascota servicioMascota;
  private final ServicioVallado servicioVallado;

  @Autowired
  public ControladorValla(
    OrquestadorService orquestadorService,
    ServicioMascota servicioMascota,
    ServicioVallado servicioVallado
  ) {
    this.orquestadorService = orquestadorService;
    this.servicioMascota = servicioMascota;
    this.servicioVallado = servicioVallado;
  }

  @RequestMapping(path = "/analisis/valla/{idMascota}", method = RequestMethod.GET)
  public ModelAndView verVallado(HttpServletRequest request, @PathVariable Long idMascota) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelMap modelo = new ModelMap();
    modelo.put("idMascota", idMascota);
    if (idUsuario != null) {
      modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    }
    Mascota mascota = servicioMascota.obtenerMascotaPorId(idMascota);
    modelo.put("mascotaNombre", mascota != null ? mascota.getNombre() : "Mascota");

    return new ModelAndView("valla-mascota", modelo);
  }

  @GetMapping("/api/mascotas/{id}/ubicacion")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> obtenerUbicacionActual(@PathVariable Long id) {
    Map<String, Object> ubicacion = orquestadorService.obtenerUltimaUbicacion(id);
    return ResponseEntity.ok(ubicacion);
  }

  @GetMapping("/api/mascotas/{id}/vallado")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> obtenerVallado(@PathVariable Long id) {
    Map<String, Object> vallado = orquestadorService.obtenerVallado(id);
    return ResponseEntity.ok(vallado);
  }

  @PostMapping(path = "/analisis/valla/{idMascota}/actualizar")
  @ResponseBody
  public ResponseEntity<String> actualizarRadio(
    @PathVariable Long idMascota,
    @RequestParam("radio") Integer radio
  ) {
    servicioVallado.actualizarRadioValla(idMascota, radio);

    return ResponseEntity.ok("{\"status\":\"success\"}");
  }
}
