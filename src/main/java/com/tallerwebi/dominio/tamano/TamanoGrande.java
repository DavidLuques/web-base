package com.tallerwebi.dominio.tamano;

/**
 *  datos.
 */
public class TamanoGrande implements ComportamientoTamano {

  @Override
  public int getPasosPorKm() {
    return 1500;
  }

  @Override
  public double getPesoMinimo() {
    return 25.0;
  }

  @Override
  public double getPesoMaximo() {
    return 45.0;
  }
}
