package com.tallerwebi.dominio.dto;

import com.tallerwebi.dominio.enums.EstadoMascota;

public class ResultadoSimulacionDto {

  private String nombreMascota;
  private EstadoMascota estado;
  private Double distanciaRecorrida;

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
}
