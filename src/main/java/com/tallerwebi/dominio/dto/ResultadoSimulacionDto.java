package com.tallerwebi.dominio.dto;

import com.tallerwebi.dominio.enums.EstadoMascota;

public class ResultadoSimulacionDto {

  private String nombreMascota;
  private EstadoMascota estado;

  public ResultadoSimulacionDto() {}

  public ResultadoSimulacionDto(String nombreMascota, EstadoMascota estado) {
    this.nombreMascota = nombreMascota;
    this.estado = estado;
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
}
