package com.tallerwebi.dominio.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ActualizadorEstadoMascotaScheduler {

  private static final long UN_MINUTO_EN_MS = 1 * 60 * 1000;
  private static final long DIEZ_SEGUNDOS_EN_MS = 10 * 1000;

  private final SimulacionActividadService simulacionActividadService;

  @Autowired
  public ActualizadorEstadoMascotaScheduler(SimulacionActividadService simulacionActividadService) {
    this.simulacionActividadService = simulacionActividadService;
  }

  @Scheduled(fixedRate = UN_MINUTO_EN_MS)
  public void actualizarEstadosDeMascotas() {
    simulacionActividadService.simularDetalleParaTodas();
  }

  @Scheduled(fixedRate = DIEZ_SEGUNDOS_EN_MS)
  public void actualizarFrecuenciaCardiaca() {
    simulacionActividadService.actualizarFrecuenciaParaTodas();
  }
}
