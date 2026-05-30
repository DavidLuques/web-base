package com.tallerwebi.dominio.tamano;

public class TamanoMediano implements ComportamientoTamano {

  @Override
  public int getPasosPorKm() {
    return 2100;
  }

  @Override
  public double getPesoMinimo() {
    return 11.0;
  }

  @Override
  public double getPesoMaximo() {
    return 25.0;
  }
}
