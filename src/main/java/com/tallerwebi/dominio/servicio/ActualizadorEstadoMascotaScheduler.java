package com.tallerwebi.dominio.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ActualizadorEstadoMascotaScheduler {

  private static final long DOS_MINUTOS_EN_MS = 2 * 60 * 1000;
  private static final long DIEZ_SEGUNDOS_EN_MS = 10 * 1000;

  private final SimulacionActividadService simulacionActividadService;

  @Autowired
  public ActualizadorEstadoMascotaScheduler(SimulacionActividadService simulacionActividadService) {
    this.simulacionActividadService = simulacionActividadService;
  }

  /**
   * Este método se ejecuta cada 2 minutos para simular una actualización completa del estado
   * de todas las mascotas, incluyendo la generación de una nueva lectura de sensor y el análisis
   * de su estado de actividad.
   */
  @Scheduled(fixedRate = DOS_MINUTOS_EN_MS)
  public void actualizarEstadosDeMascotas() {
    simulacionActividadService.simularDetalleParaTodas();
  }


  @Scheduled(fixedRate = DIEZ_SEGUNDOS_EN_MS)
  public void actualizarFrecuenciaYGeolocalizacion() {
    // Llama a los métodos existentes que actualizan la frecuencia y simulan la geolocalización.
    // El método simularDetalleParaTodas ya se encarga de llamar a la simulación de geolocalización.
    simulacionActividadService.simularDetalleParaTodas();
  }
}
