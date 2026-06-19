package com.tallerwebi.dominio.estado;

public class EstadoAmistadPendiente implements ComportamientoEstadoAmistad {

  @Override
  public boolean puedeAceptar() {
    return true;
  }

  @Override
  public boolean puedeRechazar() {
    return true;
  }

  @Override
  public boolean sonAmigos() {
    return false;
  }

  @Override
  public String getNombre() {
    return "PENDIENTE";
  }
}
