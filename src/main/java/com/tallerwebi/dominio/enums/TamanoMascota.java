package com.tallerwebi.dominio.enums;

import com.tallerwebi.dominio.tamano.ComportamientoTamano;
import com.tallerwebi.dominio.tamano.TamanoGrande;
import com.tallerwebi.dominio.tamano.TamanoMediano;
import com.tallerwebi.dominio.tamano.TamanoPequeno;

/**
 *  datos.
 */
public enum TamanoMascota {
  PEQUENO(new TamanoPequeno()),
  MEDIANO(new TamanoMediano()),
  GRANDE(new TamanoGrande());

  private final ComportamientoTamano comportamiento;

  TamanoMascota(ComportamientoTamano comportamiento) {
    this.comportamiento = comportamiento;
  }

  public ComportamientoTamano getComportamiento() {
    return comportamiento;
  }
}
