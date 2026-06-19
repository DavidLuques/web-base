package com.tallerwebi.dominio.estado;

public interface ComportamientoEstadoTransferencia {
  boolean puedeConfirmarOrigen();
  boolean puedeConfirmarDestino();
  boolean puedeCancelar();
  boolean estaCompleta();
  String getNombre();
}
