package com.tallerwebi.dominio.modelo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RangoFrecuenciaCardiaca {

  @Column(name = "frecuenciaMinima")
  private Integer minima;

  @Column(name = "frecuenciaMaxima")
  private Integer maxima;

  public RangoFrecuenciaCardiaca() {}

  public Integer getMinima() {
    return minima;
  }

  public void setMinima(Integer minima) {
    this.minima = minima;
  }

  public Integer getMaxima() {
    return maxima;
  }

  public void setMaxima(Integer maxima) {
    this.maxima = maxima;
  }
}
