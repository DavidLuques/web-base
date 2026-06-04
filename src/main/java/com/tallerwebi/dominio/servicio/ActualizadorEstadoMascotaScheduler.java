package com.tallerwebi.dominio.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Servicio de lógica de negocio.
 */
@Component
public class ActualizadorEstadoMascotaScheduler {

  private static final long UN_MINUTO_EN_MS = 1 * 60 * 1000;
  private static final long QUINCE_SEGUNDOS_EN_MS = 15 * 1000;

  private final OrquestadorService orquestadorService;

  @Autowired
  public ActualizadorEstadoMascotaScheduler(OrquestadorService orquestadorService) {
    this.orquestadorService = orquestadorService;
  }

  @Scheduled(fixedRate = UN_MINUTO_EN_MS)
  public void actualizarEstadosDeMascotas() {
    orquestadorService.procesarTodasLasMascotas();
  }

  @Scheduled(fixedRate = QUINCE_SEGUNDOS_EN_MS)
  public void refrescarLecturaDelCollar() {
    orquestadorService.refrescarTodasLasLecturas();
  }
}
