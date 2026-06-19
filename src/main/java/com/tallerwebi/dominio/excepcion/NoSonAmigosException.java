package com.tallerwebi.dominio.excepcion;

public class NoSonAmigosException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public NoSonAmigosException(String mensaje) {
    super(mensaje);
  }
}
