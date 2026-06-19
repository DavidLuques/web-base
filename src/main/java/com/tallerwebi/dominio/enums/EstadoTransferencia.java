package com.tallerwebi.dominio.enums;

import com.tallerwebi.dominio.estado.ComportamientoEstadoTransferencia;
import com.tallerwebi.dominio.estado.EstadoTransferenciaCancelada;
import com.tallerwebi.dominio.estado.EstadoTransferenciaCompletada;
import com.tallerwebi.dominio.estado.EstadoTransferenciaPendiente;

public enum EstadoTransferencia {
  PENDIENTE(new EstadoTransferenciaPendiente()),
  COMPLETADA(new EstadoTransferenciaCompletada()),
  CANCELADA(new EstadoTransferenciaCancelada());

  private final ComportamientoEstadoTransferencia comportamiento;

  EstadoTransferencia(ComportamientoEstadoTransferencia comportamiento) {
    this.comportamiento = comportamiento;
  }

  public ComportamientoEstadoTransferencia getComportamiento() {
    return comportamiento;
  }
}
