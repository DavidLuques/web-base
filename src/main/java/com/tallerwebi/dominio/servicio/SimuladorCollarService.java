package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import java.util.HashMap;
import java.util.Map;
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
  private static final double MAX_VARIACION_FRECUENCIA = 3.0;

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

  private static final double TEMP_BASE_DORMIDO = 38.0;
  private static final double TEMP_BASE_REPOSO = 38.2;
  private static final double TEMP_BASE_CAMINANDO = 38.5;
  private static final double TEMP_BASE_CORRIENDO = 39.0;
  private static final double TEMP_MAX_VARIACION = 0.15;

  private static final double FACTOR_PRESION_DORMIDO = 0.15;
  private static final double FACTOR_PRESION_REPOSO = 0.40;
  private static final double FACTOR_PRESION_CAMINANDO = 0.65;
  private static final double FACTOR_PRESION_CORRIENDO = 0.90;
  private static final double MAX_VARIACION_PRESION = 2.0;

  private Double latitudActual = -34.7222;
  private Double longitudActual = -58.5250;
  private Double temperaturaActual = 38.5;
  private Double sistolicaActual = null;
  private Double diastolicaActual = null;

  private final Map<Long, Double> frecuenciaPorMascota = new HashMap<>();

  private final Random random = new Random();

  // Sobrecarga sin presión ni frecuencia incremental (usada por ServicioAnalisisImpl)
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

    actualizarGpsSegun(estadoSimulado);
    lectura.setLatitud(latitudActual);
    lectura.setLongitud(longitudActual);

    return lectura;
  }

  // Método completo con frecuencia incremental por mascota, presión y temperatura
  public LecturaSensor generarLectura(
    Long idMascota,
    EstadoMascota estadoActual,
    Integer frecuenciaMinima,
    Integer frecuenciaMaxima,
    Integer sistolicaMinima,
    Integer sistolicaMaxima,
    Integer diastolicaMinima,
    Integer diastolicaMaxima
  ) {
    EstadoMascota estadoSimulado = sortearEstado();

    if (sistolicaActual == null) {
      sistolicaActual =
        (double) generarPresionSegun(estadoSimulado, sistolicaMinima, sistolicaMaxima);
      diastolicaActual =
        (double) generarPresionSegun(estadoSimulado, diastolicaMinima, diastolicaMaxima);
    }

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(
      obtenerFrecuenciaIncremental(idMascota, estadoSimulado, frecuenciaMinima, frecuenciaMaxima)
    );
    lectura.setAccelX(generarMovimientoSegun(estadoSimulado));
    lectura.setAccelY(generarMovimientoSegun(estadoSimulado));
    lectura.setAccelZ(generarMovimientoSegun(estadoSimulado));
    lectura.setGyroX(generarGyroSegun(estadoSimulado));
    lectura.setGyroY(generarGyroSegun(estadoSimulado));
    lectura.setGyroZ(generarGyroSegun(estadoSimulado));

    actualizarPresionSegun(
      estadoSimulado,
      sistolicaMinima,
      sistolicaMaxima,
      diastolicaMinima,
      diastolicaMaxima
    );
    lectura.setPresionSistolica(sistolicaActual.intValue());
    lectura.setPresionDiastolica(diastolicaActual.intValue());

    actualizarTemperaturaSegun(estadoSimulado);
    lectura.setTemperatura(Math.round(temperaturaActual * 10.0) / 10.0);

    actualizarGpsSegun(estadoSimulado);
    lectura.setLatitud(latitudActual);
    lectura.setLongitud(longitudActual);

    return lectura;
  }

  public int actualizarFrecuencia(Long idMascota, EstadoMascota estadoActual, int min, int max) {
    int nueva = obtenerFrecuenciaIncremental(idMascota, estadoActual, min, max);
    frecuenciaPorMascota.put(idMascota, (double) nueva);
    return nueva;
  }

  private int obtenerFrecuenciaIncremental(Long idMascota, EstadoMascota estado, int min, int max) {
    double objetivo = generarFrecuenciaSegun(estado, min, max);

    if (!frecuenciaPorMascota.containsKey(idMascota)) {
      frecuenciaPorMascota.put(idMascota, objetivo);
      return (int) objetivo;
    }

    double actual = frecuenciaPorMascota.get(idMascota);
    double variacion = (random.nextDouble() - 0.5) * MAX_VARIACION_FRECUENCIA;
    double nueva = actual + (objetivo - actual) * 0.1 + variacion;

    nueva = Math.max(min, Math.min(max, nueva));
    frecuenciaPorMascota.put(idMascota, nueva);
    return (int) nueva;
  }

  private EstadoMascota sortearEstado() {
    double probabilidad = random.nextDouble();
    if (probabilidad < LIMITE_DORMIDO) return EstadoMascota.DURMIENDO; else if (
      probabilidad < LIMITE_REPOSO
    ) return EstadoMascota.REPOSO; else if (
      probabilidad < LIMITE_CAMINANDO
    ) return EstadoMascota.CAMINANDO; else return EstadoMascota.CORRIENDO;
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

  private int generarPresionSegun(EstadoMascota estado, int min, int max) {
    int rango = max - min;
    switch (estado) {
      case DURMIENDO:
        return min + (int) (rango * FACTOR_PRESION_DORMIDO);
      case REPOSO:
        return min + (int) (rango * FACTOR_PRESION_REPOSO);
      case CAMINANDO:
        return min + (int) (rango * FACTOR_PRESION_CAMINANDO);
      default:
        return min + (int) (rango * FACTOR_PRESION_CORRIENDO);
    }
  }

  private void actualizarPresionSegun(
    EstadoMascota estado,
    int sistolicaMin,
    int sistolicaMax,
    int diastolicaMin,
    int diastolicaMax
  ) {
    double objetivoSistolica = generarPresionSegun(estado, sistolicaMin, sistolicaMax);
    double objetivoDiastolica = generarPresionSegun(estado, diastolicaMin, diastolicaMax);

    double variacionSistolica = (random.nextDouble() - 0.5) * MAX_VARIACION_PRESION;
    double variacionDiastolica = (random.nextDouble() - 0.5) * MAX_VARIACION_PRESION;

    sistolicaActual += (objetivoSistolica - sistolicaActual) * 0.1 + variacionSistolica;
    diastolicaActual += (objetivoDiastolica - diastolicaActual) * 0.1 + variacionDiastolica;

    sistolicaActual = Math.max(sistolicaMin, Math.min(sistolicaMax, sistolicaActual));
    diastolicaActual = Math.max(diastolicaMin, Math.min(diastolicaMax, diastolicaActual));
  }

  private void actualizarTemperaturaSegun(EstadoMascota estado) {
    double objetivo;
    switch (estado) {
      case DURMIENDO:
        objetivo = TEMP_BASE_DORMIDO;
        break;
      case REPOSO:
        objetivo = TEMP_BASE_REPOSO;
        break;
      case CAMINANDO:
        objetivo = TEMP_BASE_CAMINANDO;
        break;
      default:
        objetivo = TEMP_BASE_CORRIENDO;
        break;
    }
    double variacion = (random.nextDouble() - 0.5) * TEMP_MAX_VARIACION;
    temperaturaActual += (objetivo - temperaturaActual) * 0.1 + variacion;
    temperaturaActual = Math.max(37.5, Math.min(39.9, temperaturaActual));
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

  private void actualizarGpsSegun(EstadoMascota estado) {
    if (estado == EstadoMascota.CAMINANDO || estado == EstadoMascota.CORRIENDO) {
      double factor = (estado == EstadoMascota.CORRIENDO) ? 0.002 : 0.001;
      latitudActual += (random.nextDouble() - 0.5) * factor;
      longitudActual += (random.nextDouble() - 0.5) * factor;
    }
  }
}
