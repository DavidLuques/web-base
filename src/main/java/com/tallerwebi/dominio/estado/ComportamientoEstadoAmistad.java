package com.tallerwebi.dominio.estado;

public interface ComportamientoEstadoAmistad {
  boolean puedeAceptar();
  boolean puedeRechazar();
  boolean puedeCancelar();
  boolean puedeEliminar();
  boolean sonAmigos();
  String getNombre();
}
