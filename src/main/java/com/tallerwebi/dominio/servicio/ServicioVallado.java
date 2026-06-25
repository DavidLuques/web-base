package com.tallerwebi.dominio.servicio;

/*
    interfaz
*/

public interface ServicioVallado {
  void actualizarRadioValla(Long idMascota, Integer radioValla);
  void actualizarCentroVallado(Long idMascota, Double latitud, Double longitud);
}
