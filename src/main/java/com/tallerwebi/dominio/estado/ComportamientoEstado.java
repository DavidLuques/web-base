package com.tallerwebi.dominio.estado;

import java.util.Random;

/**
 *  datos.
 */
public interface ComportamientoEstado {
  double generarMovimiento(Random random);
  double generarGyro(Random random);
  void actualizarGps(double[] coordenadas, Random random);
  double getTemperaturaBase();
  boolean coincideConLectura(int frecuencia, double movimiento, double rotacion, int limite);
  boolean registraActividad();
  double getFactorFrecuencia();
  double getFactorPresion();
  double getMET();
  double getVelocidadKmH();
  int getOrden();
  boolean registraSueno();
}
