package com.tallerwebi.dominio.estado;

public class EstadoAmistadAceptada implements ComportamientoEstadoAmistad {

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
    return true;
  }

  @Override
  public String getNombre() {
    return "ACEPTADA";
  }

  @Override
  public boolean puedeCancelar() {
    return false;
  }

  @Override
  public boolean puedeEliminar() {
    return true;
  }
}
