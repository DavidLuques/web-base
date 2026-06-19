package com.tallerwebi.dominio.estado;

public class EstadoTransferenciaCancelada implements ComportamientoEstadoTransferencia {

  @Override
  public boolean puedeConfirmarOrigen() {
    return false;
  }

  @Override
  public boolean puedeConfirmarDestino() {
    return false;
  }

  @Override
  public boolean puedeCancelar() {
    return false;
  }

  @Override
  public boolean estaCompleta() {
    return false;
  }

  @Override
  public String getNombre() {
    return "CANCELADA";
  }
}
