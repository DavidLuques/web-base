package com.tallerwebi.dominio.excepcion;

public class AccionNoPermitidaEnEsteEstadoException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public AccionNoPermitidaEnEsteEstadoException(String mensaje) {
    super(mensaje);
  }
}
