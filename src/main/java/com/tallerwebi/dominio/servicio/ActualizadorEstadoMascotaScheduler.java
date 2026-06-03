package com.tallerwebi.dominio.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Servicio de lógica de negocio.
 */
@Component
public class ActualizadorEstadoMascotaScheduler {

  private static final long DOS_MINUTOS_EN_MS = 2 * 60 * 1000;
  private static final long DIEZ_SEGUNDOS_EN_MS = 10 * 1000;

  private final OrquestadorService orquestadorService;

  @Autowired
  public ActualizadorEstadoMascotaScheduler(OrquestadorService orquestadorService) {
    this.orquestadorService = orquestadorService;
  }

  @Scheduled(fixedRate = DOS_MINUTOS_EN_MS)
  public void actualizarEstadosDeMascotas() {
    orquestadorService.procesarTodasLasMascotas();
  }

  @Scheduled(fixedRate = DIEZ_SEGUNDOS_EN_MS)
  public void refrescarLecturaDelCollar() {
    orquestadorService.refrescarTodasLasLecturas();
  }
}
