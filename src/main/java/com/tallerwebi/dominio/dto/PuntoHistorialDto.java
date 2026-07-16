package com.tallerwebi.dominio.dto;

public class PuntoHistorialDto {

  private String fechaYHora;
  private Integer frecuenciaCardiaca;
  private Integer presionSistolica;
  private Integer presionDiastolica;
  private Double temperatura;
  private Double distanciaAcumulada;
  private Double caloriasAcumuladas;
  private Integer minutosDormidosAcumulados;
  private Integer pasosAcumulados;

  public String getFechaYHora() {
    return fechaYHora;
  }

  public void setFechaYHora(String fechaYHora) {
    this.fechaYHora = fechaYHora;
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

  public Double getDistanciaAcumulada() {
    return distanciaAcumulada;
  }

  public void setDistanciaAcumulada(Double distanciaAcumulada) {
    this.distanciaAcumulada = distanciaAcumulada;
  }

  public Double getCaloriasAcumuladas() {
    return caloriasAcumuladas;
  }

  public void setCaloriasAcumuladas(Double caloriasAcumuladas) {
    this.caloriasAcumuladas = caloriasAcumuladas;
  }

  public Integer getMinutosDormidosAcumulados() {
    return minutosDormidosAcumulados;
  }

  public void setMinutosDormidosAcumulados(Integer minutosDormidosAcumulados) {
    this.minutosDormidosAcumulados = minutosDormidosAcumulados;
  }

  public Integer getPasosAcumulados() {
    return pasosAcumulados;
  }

  public void setPasosAcumulados(Integer pasosAcumulados) {
    this.pasosAcumulados = pasosAcumulados;
  }
}
