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

@Controller
@RequestMapping("/analisis/alertas")
public class ControladorAlerta {

  private static final String ATRIBUTO_ID_USUARIO = "ID_USUARIO";

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
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario != null) {
      model.addAttribute("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    }
    return "alertas";
  }

  @GetMapping("/sin-mascota")
  public String verPantallaDeAlertasSinMascota(Model model, HttpServletRequest request) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario != null) {
      model.addAttribute("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    }
    // idMascota no se agrega al modelo, queda null en la vista
    return "alertas";
  }

  @GetMapping("/datos/{idMascota}")
  @ResponseBody
  public List<AlertaDto> obtenerAlertasDeMascota(@PathVariable Long idMascota) {
    return servicioAlerta.obtenerAlertasPorMascota(idMascota);
  }

  @GetMapping("/usuario")
  @ResponseBody
  public List<AlertaDto> obtenerAlertasDeUsuario(HttpServletRequest request) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return java.util.Collections.emptyList();
    }
    return servicioAlerta.obtenerAlertasPorUsuario(idUsuario);
  }

  @PutMapping("/{idAlerta}/leer")
  @ResponseBody
  public ResponseEntity<Void> marcarAlertaComoLeida(@PathVariable Long idAlerta) {
    servicioAlerta.marcarComoLeida(idAlerta);
    return ResponseEntity.ok().build();
  }
}
