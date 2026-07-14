package com.tallerwebi.dominio.excepcion;

/**
 * ExcepcionTurnoInvalido
 */
public class ExcepcionTurnoInvalido extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ExcepcionTurnoInvalido(String mensaje) {
    super(mensaje);
  }
}
