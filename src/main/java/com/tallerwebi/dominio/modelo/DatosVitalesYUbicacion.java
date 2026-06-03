package com.tallerwebi.dominio.modelo;

import javax.persistence.Embeddable;

/**
 * Entidad del sistema
 */
@Embeddable
public class DatosVitalesYUbicacion {

  private Integer presionSistolica;
  private Integer presionDiastolica;
  private Double temperatura;
  private Double oxigenacion;
  private Integer horasSueno;

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

  public Double getOxigenacion() {
    return oxigenacion;
  }

  public void setOxigenacion(Double oxigenacion) {
    this.oxigenacion = oxigenacion;
  }

  public Integer getHorasSueno() {
    return horasSueno;
  }

  public void setHorasSueno(Integer horasSueno) {
    this.horasSueno = horasSueno;
  }
}
