package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class SimuladorCollarService {

  private static final double LIMITE_DORMIDO = 0.25;
  private static final double LIMITE_REPOSO = 0.50;
  private static final double LIMITE_CAMINANDO = 0.75;

  private static final double FACTOR_FRECUENCIA_DORMIDO = 0.15;
  private static final double FACTOR_FRECUENCIA_REPOSO = 0.45;
  private static final double FACTOR_FRECUENCIA_CAMINANDO = 0.70;
  private static final double FACTOR_FRECUENCIA_CORRIENDO = 0.90;

  private static final double MAX_MOVIMIENTO_DORMIDO = 0.8;
  private static final double MAX_MOVIMIENTO_REPOSO = 3.0;
  private static final double MAX_MOVIMIENTO_CAMINANDO = 7.0;
  private static final double BASE_MOVIMIENTO_CORRIENDO = 8.0;
  private static final double RANGO_MOVIMIENTO_CORRIENDO = 4.0;

  private static final double MAX_GYRO_DORMIDO = 0.4;
  private static final double MAX_GYRO_REPOSO = 1.5;
  private static final double MAX_GYRO_CAMINANDO = 3.5;
  private static final double BASE_GYRO_CORRIENDO = 4.0;
  private static final double RANGO_GYRO_CORRIENDO = 2.0;

  private final Random random = new Random();

  public LecturaSensor generarLectura(Integer frecuenciaMinima, Integer frecuenciaMaxima) {
    EstadoMascota estadoSimulado = sortearEstado();

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(
      generarFrecuenciaSegun(estadoSimulado, frecuenciaMinima, frecuenciaMaxima)
    );
    lectura.setAccelX(generarMovimientoSegun(estadoSimulado));
    lectura.setAccelY(generarMovimientoSegun(estadoSimulado));
    lectura.setAccelZ(generarMovimientoSegun(estadoSimulado));
    lectura.setGyroX(generarGyroSegun(estadoSimulado));
    lectura.setGyroY(generarGyroSegun(estadoSimulado));
    lectura.setGyroZ(generarGyroSegun(estadoSimulado));

    return lectura;
  }

  private EstadoMascota sortearEstado() {
    double probabilidad = random.nextDouble();

    if (probabilidad < LIMITE_DORMIDO) {
      return EstadoMascota.DURMIENDO;
    } else if (probabilidad < LIMITE_REPOSO) {
      return EstadoMascota.REPOSO;
    } else if (probabilidad < LIMITE_CAMINANDO) {
      return EstadoMascota.CAMINANDO;
    } else {
      return EstadoMascota.CORRIENDO;
    }
  }

  private int generarFrecuenciaSegun(EstadoMascota estado, int min, int max) {
    int rango = max - min;

    switch (estado) {
      case DURMIENDO:
        return min + (int) (rango * FACTOR_FRECUENCIA_DORMIDO);
      case REPOSO:
        return min + (int) (rango * FACTOR_FRECUENCIA_REPOSO);
      case CAMINANDO:
        return min + (int) (rango * FACTOR_FRECUENCIA_CAMINANDO);
      default:
        return min + (int) (rango * FACTOR_FRECUENCIA_CORRIENDO);
    }
  }

  private Double generarMovimientoSegun(EstadoMascota estado) {
    switch (estado) {
      case DURMIENDO:
        return random.nextDouble() * MAX_MOVIMIENTO_DORMIDO;
      case REPOSO:
        return random.nextDouble() * MAX_MOVIMIENTO_REPOSO;
      case CAMINANDO:
        return random.nextDouble() * MAX_MOVIMIENTO_CAMINANDO;
      default:
        return BASE_MOVIMIENTO_CORRIENDO + random.nextDouble() * RANGO_MOVIMIENTO_CORRIENDO;
    }
  }

  private Double generarGyroSegun(EstadoMascota estado) {
    switch (estado) {
      case DURMIENDO:
        return random.nextDouble() * MAX_GYRO_DORMIDO;
      case REPOSO:
        return random.nextDouble() * MAX_GYRO_REPOSO;
      case CAMINANDO:
        return random.nextDouble() * MAX_GYRO_CAMINANDO;
      default:
        return BASE_GYRO_CORRIENDO + random.nextDouble() * RANGO_GYRO_CORRIENDO;
    }
  }
}
