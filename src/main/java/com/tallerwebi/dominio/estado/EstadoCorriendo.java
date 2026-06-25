package com.tallerwebi.dominio.estado;

import java.util.Random;


public class EstadoCorriendo implements ComportamientoEstado {

  @Override
  public double generarMovimiento(Random random) {
    return 8.0 + random.nextDouble() * 4.0;
  }

  @Override
  public double generarGyro(Random random) {
    return 4.0 + random.nextDouble() * 2.0;
  }

  @Override
  public boolean registraSueno() {
    return false;
  }

  @Override
  public void actualizarGps(double[] coordenada, Random random) {
    coordenada[0] += (random.nextDouble() - 0.5) * 0.002;
    coordenada[1] += (random.nextDouble() - 0.5) * 0.002;
  }

  @Override
  public double getTemperaturaBase() {
    return 39.0;
  }

  @Override
  public double getFactorFrecuencia() {
    return 0.90;
  }

  @Override
  public double getFactorPresion() {
    return 0.90;
  }

  @Override
  public double getMET() {
    return 6.0;
  }

  @Override
  public double getVelocidadKmH() {
    return 15.0;
  }

  @Override
  public int getOrden() {
    return 3;
  }

  @Override
  public boolean coincideConLectura(
    int frecuencia,
    double movimiento,
    double rotacion,
    int limite
  ) {
    return true;
  }

  @Override
  public boolean registraActividad() {
    return true;
  }
}
