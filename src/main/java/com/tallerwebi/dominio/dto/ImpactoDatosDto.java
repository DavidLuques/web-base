package com.tallerwebi.dominio.dto;

public class ImpactoDatosDto {

  private Double peso;
  private String tamano;
  private Integer pasosPorKm;
  private Double pesoMinimoTamano;
  private Double pesoMaximoTamano;
  private String estadoActual;
  private Double metActual;
  private Double velocidadActualKmH;

  public ImpactoDatosDto() {}

  public Double getPeso() {
    return peso;
  }

  public void setPeso(Double peso) {
    this.peso = peso;
  }

  public String getTamano() {
    return tamano;
  }

  public void setTamano(String tamano) {
    this.tamano = tamano;
  }

  public Integer getPasosPorKm() {
    return pasosPorKm;
  }

  public void setPasosPorKm(Integer pasosPorKm) {
    this.pasosPorKm = pasosPorKm;
  }

  public Double getPesoMinimoTamano() {
    return pesoMinimoTamano;
  }

  public void setPesoMinimoTamano(Double pesoMinimoTamano) {
    this.pesoMinimoTamano = pesoMinimoTamano;
  }

  public Double getPesoMaximoTamano() {
    return pesoMaximoTamano;
  }

  public void setPesoMaximoTamano(Double pesoMaximoTamano) {
    this.pesoMaximoTamano = pesoMaximoTamano;
  }

  public String getEstadoActual() {
    return estadoActual;
  }

  public void setEstadoActual(String estadoActual) {
    this.estadoActual = estadoActual;
  }

  public Double getMetActual() {
    return metActual;
  }

  public void setMetActual(Double metActual) {
    this.metActual = metActual;
  }

  public Double getVelocidadActualKmH() {
    return velocidadActualKmH;
  }

  public void setVelocidadActualKmH(Double velocidadActualKmH) {
    this.velocidadActualKmH = velocidadActualKmH;
  }
}
