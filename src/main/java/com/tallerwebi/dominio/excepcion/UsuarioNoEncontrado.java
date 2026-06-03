package com.tallerwebi.dominio.excepcion;

/**
 *  excepcion.
 */
public class UsuarioNoEncontrado extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public UsuarioNoEncontrado(String mensaje) {
    super(mensaje);
  }
}
