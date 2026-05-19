package com.tallerwebi.dominio.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ActualizadorEstadoMascotaScheduler {

  private static final long TRES_MINUTOS_EN_MS = 10000;

  private final SimulacionActividadService simulacionActividadService;

  @Autowired
  public ActualizadorEstadoMascotaScheduler(SimulacionActividadService simulacionActividadService) {
    this.simulacionActividadService = simulacionActividadService;
  }

  @Scheduled(fixedRate = TRES_MINUTOS_EN_MS)
  public void actualizarEstadosDeMascotas() {
    simulacionActividadService.simularDetalleParaTodas();
  }
}
