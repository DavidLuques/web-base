package com.tallerwebi.dominio.modelo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RangoTemperatura {

  @Column(name = "temperaturaMinima")
  private Double minima;

  @Column(name = "temperaturaMaxima")
  private Double maxima;

  public RangoTemperatura() {}

  public Double getMinima() {
    return minima;
  }

  public void setMinima(Double minima) {
    this.minima = minima;
  }

  public Double getMaxima() {
    return maxima;
  }

  public void setMaxima(Double maxima) {
    this.maxima = maxima;
  }
}
