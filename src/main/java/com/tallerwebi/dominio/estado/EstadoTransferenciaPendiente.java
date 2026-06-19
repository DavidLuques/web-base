package com.tallerwebi.dominio.estado;

public class EstadoTransferenciaPendiente implements ComportamientoEstadoTransferencia {

  @Override
  public boolean puedeConfirmarOrigen() {
    return true;
  }

  @Override
  public boolean puedeConfirmarDestino() {
    return true;
  }

  @Override
  public boolean puedeCancelar() {
    return true;
  }

  @Override
  public boolean estaCompleta() {
    return false;
  }

  @Override
  public String getNombre() {
    return "PENDIENTE";
  }
}
