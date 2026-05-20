package com.tallerwebi.dominio.modelo;

import javax.persistence.Embeddable;

@Embeddable
public class DatosVitalesYUbicacion {

  private Integer presionSistolica;
  private Integer presionDiastolica;
  private Double temperatura;

  public DatosVitalesYUbicacion() {}

  public Integer getPresionSistolica() {
    return presionSistolica;
  }

  public void setPresionSistolica(Integer presionSistolica) {
    this.presionSistolica = presionSistolica;
  }

  public Integer getPresionDiastolica() {
    return presionDiastolica;
  }

  public void setPresionDiastolica(Integer presionDiastolica) {
    this.presionDiastolica = presionDiastolica;
  }

  public Double getTemperatura() {
    return temperatura;
  }

  public void setTemperatura(Double temperatura) {
    this.temperatura = temperatura;
  }
}
