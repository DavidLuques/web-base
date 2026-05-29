package com.tallerwebi.dominio.estado;

import java.util.Random;

public class EstadoReposo implements ComportamientoEstado {

  @Override
  public double generarMovimiento(Random random) {
    return random.nextDouble() * 3.0;
  }

  @Override
  public double generarGyro(Random random) {
    return random.nextDouble() * 1.5;
  }

  @Override
  public void actualizarGps(double[] coordenada, Random random) {
    /* no se mueve */
  }

  @Override
  public double getTemperaturaBase() {
    return 38.2;
  }

  @Override
  public double getFactorFrecuencia() {
    return 0.45;
  }

  @Override
  public double getFactorPresion() {
    return 0.40;
  }

  @Override
  public double getMET() {
    return 1.5;
  }

  @Override
  public double getVelocidadKmH() {
    return 1.0;
  }

  @Override
  public int getOrden() {
    return 1;
  }

  @Override
  public boolean coincideConLectura(
    int frecuencia,
    double movimiento,
    double rotacion,
    int limite
  ) {
    return frecuencia <= limite && movimiento < 5 && rotacion < 2.5;
  }

  @Override
  public boolean registraActividad() {
    return false;
  }
}
