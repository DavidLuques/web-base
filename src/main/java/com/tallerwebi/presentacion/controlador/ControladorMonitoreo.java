package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.servicio.AlertaService;
import com.tallerwebi.dominio.servicio.OrquestadorService;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/analisis")
public class ControladorMonitoreo {

  private final OrquestadorService orquestadorService;
  private final AlertaService alertaService;
  private final ServicioMascota servicioMascota;

  @Autowired
  public ControladorMonitoreo(
    OrquestadorService orquestadorService,
    AlertaService alertaService,
    ServicioMascota servicioMascota
  ) {
    this.orquestadorService = orquestadorService;
    this.alertaService = alertaService;
    this.servicioMascota = servicioMascota;
  }

  @GetMapping("/{idMascota}")
  @ResponseBody
  public ResultadoSimulacionDto procesarMascota(@PathVariable Long idMascota) {
    return orquestadorService.procesarMascota(idMascota);
  }

  @GetMapping("/alertas/{idMascota}")
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

  @GetMapping("/alertas/datos/{idMascota}")
  @ResponseBody
  public List<AlertaDto> obtenerAlertasDeMascota(@PathVariable Long idMascota) {
    return alertaService.obtenerAlertasPorMascota(idMascota);
  }

  @GetMapping("/vista/{idMascota}")
  public String vista(@PathVariable Long idMascota, Model model, HttpServletRequest request) {
    model.addAttribute("idMascota", idMascota);
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario != null) {
      model.addAttribute("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    }
    return "simulacion";
  }

  @GetMapping(value = "/estado/{idMascota}", produces = "application/json;charset=UTF-8")
  @ResponseBody
  public ResultadoSimulacionDto obtenerEstado(@PathVariable Long idMascota) {
    return orquestadorService.obtenerUltimoEstado(idMascota);
  }

  @GetMapping("/dashboard/{idMascota}")
  public String vistaDashboard(
    @PathVariable Long idMascota,
    Model model,
    HttpServletRequest request
  ) {
    model.addAttribute("idMascota", idMascota);
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario != null) {
      model.addAttribute("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    }
    ResultadoSimulacionDto estado = orquestadorService.obtenerUltimoEstado(idMascota);
    model.addAttribute("mascotaNombre", estado != null ? estado.getNombreMascota() : "Mascota");
    return "dashboard";
  }
}
