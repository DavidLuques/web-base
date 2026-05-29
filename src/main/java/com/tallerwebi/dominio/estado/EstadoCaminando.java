package com.tallerwebi.dominio.estado;

import java.util.Random;

public class EstadoCaminando implements ComportamientoEstado {

  @Override
  public double generarMovimiento(Random random) {
    return random.nextDouble() * 7.0;
  }

  @Override
  public double generarGyro(Random random) {
    return random.nextDouble() * 3.5;
  }

  @Override
  public void actualizarGps(double[] coordenada, Random random) {
    coordenada[0] += (random.nextDouble() - 0.5) * 0.001;
    coordenada[1] += (random.nextDouble() - 0.5) * 0.001;
  }

  @Override
  public double getTemperaturaBase() {
    return 38.5;
  }

  @Override
  public double getFactorFrecuencia() {
    return 0.70;
  }

  @Override
  public double getFactorPresion() {
    return 0.65;
  }

  @Override
  public double getMET() {
    return 3.0;
  }

  @Override
  public double getVelocidadKmH() {
    return 5.0;
  }

  @Override
  public int getOrden() {
    return 2;
  }

  @Override
  public boolean coincideConLectura(
    int frecuencia,
    double movimiento,
    double rotacion,
    int limite
  ) {
    return frecuencia <= limite && movimiento < 9 && rotacion < 4;
  }

  @Override
  public boolean registraActividad() {
    return true;
  }
}
