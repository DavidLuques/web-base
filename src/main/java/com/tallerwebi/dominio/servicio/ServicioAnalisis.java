package com.tallerwebi.dominio.servicio;

public interface ServicioAnalisis {
  void simularGeolocalizacion();
  Double calcularDistancia(Double lat1, Double lon1, Double lat2, Double lon2);
}
