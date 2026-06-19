package com.tallerwebi.dominio.estado;

public interface ComportamientoEstadoAmistad {
  boolean puedeAceptar();
  boolean puedeRechazar();
  boolean sonAmigos();
  String getNombre();
}
