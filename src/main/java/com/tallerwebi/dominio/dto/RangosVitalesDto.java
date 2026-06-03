package com.tallerwebi.dominio.dto;

import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;

public class RangosVitalesDto {

  private Integer frecuenciaMinima;
  private Integer frecuenciaMaxima;
  private Double temperaturaMinima;
  private Double temperaturaMaxima;
  private Integer sistolicaMinima;
  private Integer sistolicaMaxima;
  private Integer diastolicaMinima;
  private Integer diastolicaMaxima;

  public RangosVitalesDto(RangoVitalPorTamano rango) {
    if (rango == null) return;
    this.frecuenciaMinima = rango.getFrecuenciaMinima();
    this.frecuenciaMaxima = rango.getFrecuenciaMaxima();
    this.temperaturaMinima = rango.getTemperaturaMinima();
    this.temperaturaMaxima = rango.getTemperaturaMaxima();
    this.sistolicaMinima = rango.getSistolicaMinima();
    this.sistolicaMaxima = rango.getSistolicaMaxima();
    this.diastolicaMinima = rango.getDiastolicaMinima();
    this.diastolicaMaxima = rango.getDiastolicaMaxima();
  }

  public Integer getFrecuenciaMinima() {
    return frecuenciaMinima;
  }

  public Integer getFrecuenciaMaxima() {
    return frecuenciaMaxima;
  }

  public Double getTemperaturaMinima() {
    return temperaturaMinima;
  }

  public Double getTemperaturaMaxima() {
    return temperaturaMaxima;
  }

  public Integer getSistolicaMinima() {
    return sistolicaMinima;
  }

  public Integer getSistolicaMaxima() {
    return sistolicaMaxima;
  }

  public Integer getDiastolicaMinima() {
    return diastolicaMinima;
  }

  public Integer getDiastolicaMaxima() {
    return diastolicaMaxima;
  }
}
