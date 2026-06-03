package com.tallerwebi.dominio.dto;

import com.tallerwebi.dominio.enums.EstadoMascota;

/**
 *  datos.
 */
public class ResultadoSimulacionDto {

  private String nombreMascota;
  private EstadoMascota estado;
  private Double distanciaRecorrida;
  private Integer frecuenciaCardiaca;
  private Integer presionSistolica;
  private Integer presionDiastolica;
  private Double temperatura;
  private Integer pasos;
  private Double calorias;
  private Integer minutosDormidos;

  public ResultadoSimulacionDto() {}

  public ResultadoSimulacionDto(
    String nombreMascota,
    EstadoMascota estado,
    Double distanciaRecorrida,
    Integer pasos
  ) {
    this.nombreMascota = nombreMascota;
    this.estado = estado;
    this.distanciaRecorrida = distanciaRecorrida;
    this.pasos = pasos;
    this.calorias = 0.0;
    this.minutosDormidos = 0;
  }

  public ResultadoSimulacionDto(
    String nombreMascota,
    EstadoMascota estado,
    Double distanciaRecorrida,
    Integer pasos,
    Double calorias,
    Integer minutosDormidos
  ) {
    this.nombreMascota = nombreMascota;
    this.estado = estado;
    this.distanciaRecorrida = distanciaRecorrida;
    this.pasos = pasos;
    this.calorias = calorias;
    this.minutosDormidos = minutosDormidos;
  }

  public ResultadoSimulacionDto(
    String nombreMascota,
    EstadoMascota estado,
    Integer frecuenciaCardiaca,
    Integer presionSistolica,
    Integer presionDiastolica,
    Double temperatura,
    Double distanciaRecorrida,
    Integer pasos,
    Double calorias,
    Integer minutosDormidos
  ) {
    this.nombreMascota = nombreMascota;
    this.estado = estado;
    this.frecuenciaCardiaca = frecuenciaCardiaca;
    this.presionSistolica = presionSistolica;
    this.presionDiastolica = presionDiastolica;
    this.temperatura = temperatura;
    this.distanciaRecorrida = distanciaRecorrida;
    this.pasos = pasos;
    this.calorias = calorias;
    this.minutosDormidos = minutosDormidos;
  }

  public Integer getMinutosDormidos() {
    return minutosDormidos;
  }

  public void setMinutosDormidos(Integer minutosDormidos) {
    this.minutosDormidos = minutosDormidos;
  }

  public Double getCalorias() {
    return calorias;
  }

  public void setCalorias(Double calorias) {
    this.calorias = calorias;
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

  public Integer getPasos() {
    return this.pasos;
  }

  public void setPasos(Integer pasos) {
    this.pasos = pasos;
  }
}
