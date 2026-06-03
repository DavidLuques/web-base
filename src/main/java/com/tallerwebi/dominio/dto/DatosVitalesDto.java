package com.tallerwebi.dominio.dto;

/**
 *  datos.
 */
public class DatosVitalesDto {

  private Integer frecuenciaCardiaca;
  private Integer presionSistolica;
  private Integer presionDiastolica;
  private Double temperatura;

  public DatosVitalesDto() {}

  public DatosVitalesDto(
    Integer frecuenciaCardiaca,
    Integer presionSistolica,
    Integer presionDiastolica,
    Double temperatura
  ) {
    this.frecuenciaCardiaca = frecuenciaCardiaca;
    this.presionSistolica = presionSistolica;
    this.presionDiastolica = presionDiastolica;
    this.temperatura = temperatura;
  }

  public Integer getFrecuenciaCardiaca() {
    return frecuenciaCardiaca;
  }

  public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
    this.frecuenciaCardiaca = frecuenciaCardiaca;
  }

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
