package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.servicio.AlertaService;
import com.tallerwebi.dominio.servicio.SimulacionActividadService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/simulacion")
public class ControladorSimulacion {

  private final SimulacionActividadService simulacionActividadService;
  private final AlertaService alertaService;

  @Autowired
  public ControladorSimulacion(
    SimulacionActividadService simulacionActividadService,
    AlertaService alertaService
  ) {
    this.simulacionActividadService = simulacionActividadService;
    this.alertaService = alertaService;
  }

  @GetMapping("/{idMascota}")
  @ResponseBody
  public ResultadoSimulacionDto simular(@PathVariable Long idMascota) {
    return simulacionActividadService.simularDetalle(idMascota);
  }

  @GetMapping("/alertas/{idMascota}")
  public String verPantallaDeAlertas(@PathVariable Long idMascota, Model model) {
    model.addAttribute("idMascota", idMascota);
    return "alertas";
  }

  @GetMapping("/alertas/datos/{idMascota}")
  @ResponseBody
  public List<AlertaDto> obtenerAlertasDeMascota(@PathVariable Long idMascota) {
    return alertaService.obtenerAlertasPorMascota(idMascota);
  }

  @GetMapping("/vista/{idMascota}")
  public String vista(@PathVariable Long idMascota, Model model) {
    model.addAttribute("idMascota", idMascota);
    return "simulacion";
  }

  @GetMapping(value = "/estado/{idMascota}", produces = "application/json;charset=UTF-8")
  @ResponseBody
  public ResultadoSimulacionDto obtenerEstado(@PathVariable Long idMascota) {
    return simulacionActividadService.obtenerEstadoActual(idMascota);
  }

  @GetMapping("/dashboard/{idMascota}")
  public String vistaDashboardFigma(@PathVariable Long idMascota, Model model) {
    model.addAttribute("idMascota", idMascota);
    return "dashboard";
  }
}
