package com.tallerwebi.dominio.modelo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RangoPresionArterial {

  @Column(name = "sistolicaMinima")
  private Integer sistolicaMinima;

  @Column(name = "sistolicaMaxima")
  private Integer sistolicaMaxima;

  @Column(name = "diastolicaMinima")
  private Integer diastolicaMinima;

  @Column(name = "diastolicaMaxima")
  private Integer diastolicaMaxima;

  public RangoPresionArterial() {}

  public Integer getSistolicaMinima() {
    return sistolicaMinima;
  }

  public void setSistolicaMinima(Integer sistolicaMinima) {
    this.sistolicaMinima = sistolicaMinima;
  }

  public Integer getSistolicaMaxima() {
    return sistolicaMaxima;
  }

  public void setSistolicaMaxima(Integer sistolicaMaxima) {
    this.sistolicaMaxima = sistolicaMaxima;
  }

  public Integer getDiastolicaMinima() {
    return diastolicaMinima;
  }

  public void setDiastolicaMinima(Integer diastolicaMinima) {
    this.diastolicaMinima = diastolicaMinima;
  }

  public Integer getDiastolicaMaxima() {
    return diastolicaMaxima;
  }

  public void setDiastolicaMaxima(Integer diastolicaMaxima) {
    this.diastolicaMaxima = diastolicaMaxima;
  }
}
