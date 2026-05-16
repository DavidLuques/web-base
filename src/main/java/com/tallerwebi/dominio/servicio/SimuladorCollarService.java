package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.LecturaSensor;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class SimuladorCollarService {

  private static final double LIMITE_DORMIDO_FRECUENCIA = 0.25;
  private static final double LIMITE_REPOSO_FRECUENCIA = 0.60;
  private static final double LIMITE_CAMINANDO_FRECUENCIA = 0.85;

  private static final double FACTOR_FRECUENCIA_DORMIDO = 0.15;
  private static final double FACTOR_FRECUENCIA_REPOSO = 0.45;
  private static final double FACTOR_FRECUENCIA_CAMINANDO = 0.70;
  private static final double FACTOR_FRECUENCIA_CORRIENDO = 0.90;

  private static final double LIMITE_QUIETO_MOVIMIENTO = 0.70;
  private static final double LIMITE_REPOSO_MOVIMIENTO = 0.90;
  private static final double BASE_MOVIMIENTO_REPOSO = 2.0;
  private static final double BASE_MOVIMIENTO_ACTIVO = 5.0;
  private static final double RANGO_MOVIMIENTO_REPOSO = 3.0;
  private static final double RANGO_MOVIMIENTO_ACTIVO = 3.0;

  private static final double LIMITE_QUIETO_GYRO = 0.65;
  private static final double LIMITE_REPOSO_GYRO = 0.90;
  private static final double BASE_GYRO_QUIETO = 1.5;
  private static final double BASE_GYRO_REPOSO = 1.5;
  private static final double BASE_GYRO_ACTIVO = 3.0;
  private static final double RANGO_GYRO_REPOSO = 2.0;
  private static final double RANGO_GYRO_ACTIVO = 2.0;

  private final Random random = new Random();

  public LecturaSensor generarLectura(Integer frecuenciaMinima, Integer frecuenciaMaxima) {
    LecturaSensor lectura = new LecturaSensor();

    lectura.setFrecuenciaCardiaca(generarFrecuencia(frecuenciaMinima, frecuenciaMaxima));

    lectura.setAccelX(generarMovimiento());
    lectura.setAccelY(generarMovimiento());
    lectura.setAccelZ(generarMovimiento());

    lectura.setGyroX(generarGyro());
    lectura.setGyroY(generarGyro());
    lectura.setGyroZ(generarGyro());

    return lectura;
  }

  private int generarFrecuencia(int min, int max) {
    double probabilidad = random.nextDouble();
    int rango = max - min;

    if (probabilidad < LIMITE_DORMIDO_FRECUENCIA) {
      return min + (int) (rango * FACTOR_FRECUENCIA_DORMIDO);
    } else if (probabilidad < LIMITE_REPOSO_FRECUENCIA) {
      return min + (int) (rango * FACTOR_FRECUENCIA_REPOSO);
    } else if (probabilidad < LIMITE_CAMINANDO_FRECUENCIA) {
      return min + (int) (rango * FACTOR_FRECUENCIA_CAMINANDO);
    } else {
      return min + (int) (rango * FACTOR_FRECUENCIA_CORRIENDO);
    }
  }

  private Double generarMovimiento() {
    double probabilidad = random.nextDouble();

    if (probabilidad < LIMITE_QUIETO_MOVIMIENTO) {
      return random.nextDouble() * BASE_MOVIMIENTO_REPOSO;
    } else if (probabilidad < LIMITE_REPOSO_MOVIMIENTO) {
      return BASE_MOVIMIENTO_REPOSO + random.nextDouble() * RANGO_MOVIMIENTO_REPOSO;
    } else {
      return BASE_MOVIMIENTO_ACTIVO + random.nextDouble() * RANGO_MOVIMIENTO_ACTIVO;
    }
  }

  private Double generarGyro() {
    double probabilidad = random.nextDouble();

    if (probabilidad < LIMITE_QUIETO_GYRO) {
      return random.nextDouble() * BASE_GYRO_QUIETO;
    } else if (probabilidad < LIMITE_REPOSO_GYRO) {
      return BASE_GYRO_REPOSO + random.nextDouble() * RANGO_GYRO_REPOSO;
    } else {
      return BASE_GYRO_ACTIVO + random.nextDouble() * RANGO_GYRO_ACTIVO;
    }
  }
}
