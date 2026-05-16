package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.servicio.SimulacionActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simulacion")
public class ControladorSimulacion {

  private final SimulacionActividadService simulacionActividadService;

  @Autowired
  public ControladorSimulacion(SimulacionActividadService simulacionActividadService) {
    this.simulacionActividadService = simulacionActividadService;
  }

  @GetMapping("/{idMascota}")
  public ResultadoSimulacionDto simular(@PathVariable Long idMascota) {
    return simulacionActividadService.simularDetalle(idMascota);
  }
}
