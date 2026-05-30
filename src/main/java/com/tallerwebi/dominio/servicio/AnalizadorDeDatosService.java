package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;

public interface AnalizadorDeDatosService {
  EstadoMascota determinarEstado(Mascota mascota, LecturaSensor lectura);

  double calcularDistanciaEntreUbicaciones(Double lat1, Double lon1, Double lat2, Double lon2);

  int calcularPasos(Double distanciaEnKm, TamanoMascota tamano);

  double calcularCalorias(Double distanciaEnKm, EstadoMascota estado, Double pesoKg);
}
