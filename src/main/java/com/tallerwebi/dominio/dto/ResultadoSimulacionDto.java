package com.tallerwebi.dominio.dto;

import com.tallerwebi.dominio.enums.EstadoMascota;

public class ResultadoSimulacionDto {

  private String nombreMascota;
  private EstadoMascota estado;
  private Double distanciaRecorrida;
  private Integer frecuenciaCardiaca;
  private Integer presionSistolica;
  private Integer presionDiastolica;
  private Double temperatura;

  public ResultadoSimulacionDto() {}

  public ResultadoSimulacionDto(
    String nombreMascota,
    EstadoMascota estado,
    Double distanciaRecorrida
  ) {
    this.nombreMascota = nombreMascota;
    this.estado = estado;
    this.distanciaRecorrida = distanciaRecorrida;
  }

  public ResultadoSimulacionDto(
    String nombreMascota,
    EstadoMascota estado,
    Integer frecuenciaCardiaca,
    Integer presionSistolica,
    Integer presionDiastolica,
    Double temperatura
  ) {
    this.nombreMascota = nombreMascota;
    this.estado = estado;
    this.frecuenciaCardiaca = frecuenciaCardiaca;
    this.presionSistolica = presionSistolica;
    this.presionDiastolica = presionDiastolica;
    this.temperatura = temperatura;
  }

  public String getNombreMascota() {
    return nombreMascota;
  }

  public void setNombreMascota(String nombreMascota) {
    this.nombreMascota = nombreMascota;
  }

  public EstadoMascota getEstado() {
    return estado;
  }

  public void setEstado(EstadoMascota estado) {
    this.estado = estado;
  }

  public Double getDistanciaRecorrida() {
    return this.distanciaRecorrida;
  }

  public void setDistanciaRecorrida(Double distanciaRecorrida) {
    this.distanciaRecorrida = distanciaRecorrida;
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
