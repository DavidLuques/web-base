package com.tallerwebi.dominio.estado;

import java.util.Random;

public class EstadoDurmiendo implements ComportamientoEstado {

  @Override
  public double generarMovimiento(Random random) {
    return random.nextDouble() * 0.8;
  }

  @Override
  public double generarGyro(Random random) {
    return random.nextDouble() * 0.4;
  }

  @Override
  public boolean registraSueno() {
    return true;
  }

  @Override
  public void actualizarGps(double[] coordenada, Random random) {}

  @Override
  public double getTemperaturaBase() {
    return 38.0;
  }

  @Override
  public double getFactorFrecuencia() {
    return 0.15;
  }

  @Override
  public double getFactorPresion() {
    return 0.15;
  }

  @Override
  public double getMET() {
    return 1.0;
  }

  @Override
  public double getVelocidadKmH() {
    return 1.0;
  }

  @Override
  public int getOrden() {
    return 0;
  }

  @Override
  public boolean coincideConLectura(
    int frecuencia,
    double movimiento,
    double rotacion,
    int limite
  ) {
    return frecuencia <= limite && movimiento < 2 && rotacion < 1;
  }

  @Override
  public boolean registraActividad() {
    return false;
  }
}
