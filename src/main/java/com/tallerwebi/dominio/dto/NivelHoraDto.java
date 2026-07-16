package com.tallerwebi.dominio.dto;

public class NivelHoraDto {

  private String hora;
  private Integer intenso;
  private Integer moderado;
  private Integer liviano;

  public String getHora() {
    return hora;
  }

  public void setHora(String hora) {
    this.hora = hora;
  }

  public Integer getIntenso() {
    return intenso;
  }

  public void setIntenso(Integer intenso) {
    this.intenso = intenso;
  }

  public Integer getModerado() {
    return moderado;
  }

  public void setModerado(Integer moderado) {
    this.moderado = moderado;
  }

  public Integer getLiviano() {
    return liviano;
  }

  public void setLiviano(Integer liviano) {
    this.liviano = liviano;
  }
}
