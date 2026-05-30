package com.tallerwebi.dominio.tamano;

public class TamanoPequeno implements ComportamientoTamano {

  @Override
  public int getPasosPorKm() {
    return 3200;
  }

  @Override
  public double getPesoMinimo() {
    return 2.0;
  }

  @Override
  public double getPesoMaximo() {
    return 10.0;
  }
}
