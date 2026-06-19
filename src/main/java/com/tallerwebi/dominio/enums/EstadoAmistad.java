package com.tallerwebi.dominio.enums;

import com.tallerwebi.dominio.estado.ComportamientoEstadoAmistad;
import com.tallerwebi.dominio.estado.EstadoAmistadAceptada;
import com.tallerwebi.dominio.estado.EstadoAmistadPendiente;
import com.tallerwebi.dominio.estado.EstadoAmistadRechazada;

public enum EstadoAmistad {
  PENDIENTE(new EstadoAmistadPendiente()),
  ACEPTADA(new EstadoAmistadAceptada()),
  RECHAZADA(new EstadoAmistadRechazada());

  private final ComportamientoEstadoAmistad comportamiento;

  EstadoAmistad(ComportamientoEstadoAmistad comportamiento) {
    this.comportamiento = comportamiento;
  }

  public ComportamientoEstadoAmistad getComportamiento() {
    return comportamiento;
  }
}
