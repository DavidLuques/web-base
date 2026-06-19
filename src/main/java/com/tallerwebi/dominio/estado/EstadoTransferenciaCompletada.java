package com.tallerwebi.dominio.estado;

public class EstadoTransferenciaCompletada implements ComportamientoEstadoTransferencia {

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
    return true;
  }

  @Override
  public String getNombre() {
    return "COMPLETADA";
  }
}
