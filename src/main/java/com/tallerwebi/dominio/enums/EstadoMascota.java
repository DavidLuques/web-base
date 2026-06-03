package com.tallerwebi.dominio.enums;

import com.tallerwebi.dominio.estado.ComportamientoEstado;
import com.tallerwebi.dominio.estado.EstadoCaminando;
import com.tallerwebi.dominio.estado.EstadoCorriendo;
import com.tallerwebi.dominio.estado.EstadoDurmiendo;
import com.tallerwebi.dominio.estado.EstadoReposo;

/**
 *  datos.
 */
public enum EstadoMascota {
  DURMIENDO(new EstadoDurmiendo()),
  REPOSO(new EstadoReposo()),
  CAMINANDO(new EstadoCaminando()),
  CORRIENDO(new EstadoCorriendo());

  private final ComportamientoEstado comportamiento;

  EstadoMascota(ComportamientoEstado comportamiento) {
    this.comportamiento = comportamiento;
  }

  public ComportamientoEstado getComportamiento() {
    return comportamiento;
  }

  public static EstadoMascota porOrden(int orden) {
    for (EstadoMascota e : values()) {
      if (e.getComportamiento().getOrden() == orden) return e;
    }
    return CORRIENDO;
  }
}
