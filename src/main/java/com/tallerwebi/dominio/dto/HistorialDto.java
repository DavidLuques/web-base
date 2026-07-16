package com.tallerwebi.dominio.dto;

import java.util.ArrayList;
import java.util.List;

public class HistorialDto {

  private List<PuntoHistorialDto> puntos = new ArrayList<>();
  private List<NivelHoraDto> nivelesActividad = new ArrayList<>();

  public List<PuntoHistorialDto> getPuntos() {
    return puntos;
  }

  public void setPuntos(List<PuntoHistorialDto> puntos) {
    this.puntos = puntos;
  }

  public List<NivelHoraDto> getNivelesActividad() {
    return nivelesActividad;
  }

  public void setNivelesActividad(List<NivelHoraDto> nivelesActividad) {
    this.nivelesActividad = nivelesActividad;
  }
}
