package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.servicio.ServicioAlerta;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador de alertas.
 */
@Controller
@RequestMapping("/analisis/alertas")
public class ControladorAlerta {

  private final ServicioAlerta servicioAlerta;
  private final ServicioMascota servicioMascota;

  @Autowired
  public ControladorAlerta(ServicioAlerta servicioAlerta, ServicioMascota servicioMascota) {
    this.servicioAlerta = servicioAlerta;
    this.servicioMascota = servicioMascota;
  }

  @GetMapping("/{idMascota}")
  public String verPantallaDeAlertas(
    @PathVariable Long idMascota,
    Model model,
    HttpServletRequest request
  ) {
    model.addAttribute("idMascota", idMascota);
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario != null) {
      model.addAttribute("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    }
    return "alertas";
  }

  @GetMapping("/datos/{idMascota}")
  @ResponseBody
  public List<AlertaDto> obtenerAlertasDeMascota(@PathVariable Long idMascota) {
    return servicioAlerta.obtenerAlertasPorMascota(idMascota);
  }

  @PutMapping("/{idAlerta}/leer")
  @ResponseBody
  public ResponseEntity<Void> marcarAlertaComoLeida(@PathVariable Long idAlerta) {
    servicioAlerta.marcarComoLeida(idAlerta);
    return ResponseEntity.ok().build();
  }
}
