package com.tallerwebi.dominio.estado;

public class EstadoAmistadRechazada implements ComportamientoEstadoAmistad {

  @Override
  public boolean puedeAceptar() {
    return false;
  }

  @Override
  public boolean puedeRechazar() {
    return false;
  }

  @Override
  public boolean sonAmigos() {
    return false;
  }

  @Override
  public String getNombre() {
    return "RECHAZADA";
  }
}
