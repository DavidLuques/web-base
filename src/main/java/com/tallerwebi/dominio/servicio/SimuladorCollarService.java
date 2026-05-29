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
  private static final double MAX_VARIACION_FRECUENCIA = 3.0;
  private static final double MAX_VARIACION_PRESION = 2.0;
  private static final double TEMP_MAX_VARIACION = 0.15;

  private Double latitudActual = -34.7222;
  private Double longitudActual = -58.5250;
  private Double temperaturaActual = 38.5;
  private Double sistolicaActual = null;
  private Double diastolicaActual = null;

  private final Map<Long, Double> frecuenciaPorMascota = new HashMap<>();
  private final Random random = new Random();

  public LecturaSensor generarLectura(Integer frecuenciaMinima, Integer frecuenciaMaxima) {
    EstadoMascota estado = sortearEstado();
    LecturaSensor lectura = new LecturaSensor();

    lectura.setFrecuenciaCardiaca(
      generarFrecuenciaSegun(estado, frecuenciaMinima, frecuenciaMaxima)
    );
    lectura.setAccelX(estado.getComportamiento().generarMovimiento(random));
    lectura.setAccelY(estado.getComportamiento().generarMovimiento(random));
    lectura.setAccelZ(estado.getComportamiento().generarMovimiento(random));
    lectura.setGyroX(estado.getComportamiento().generarGyro(random));
    lectura.setGyroY(estado.getComportamiento().generarGyro(random));
    lectura.setGyroZ(estado.getComportamiento().generarGyro(random));
    actualizarGpsSegun(estado);
    lectura.setLatitud(latitudActual);
    lectura.setLongitud(longitudActual);

    return lectura;
  }

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
    EstadoMascota estado = sortearEstado();

    if (sistolicaActual == null) {
      sistolicaActual = (double) generarPresionSegun(estado, sistolicaMinima, sistolicaMaxima);
      diastolicaActual = (double) generarPresionSegun(estado, diastolicaMinima, diastolicaMaxima);
    }

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(
      obtenerFrecuenciaIncremental(idMascota, estado, frecuenciaMinima, frecuenciaMaxima)
    );
    lectura.setAccelX(estado.getComportamiento().generarMovimiento(random));
    lectura.setAccelY(estado.getComportamiento().generarMovimiento(random));
    lectura.setAccelZ(estado.getComportamiento().generarMovimiento(random));
    lectura.setGyroX(estado.getComportamiento().generarGyro(random));
    lectura.setGyroY(estado.getComportamiento().generarGyro(random));
    lectura.setGyroZ(estado.getComportamiento().generarGyro(random));

    actualizarPresionSegun(
      estado,
      sistolicaMinima,
      sistolicaMaxima,
      diastolicaMinima,
      diastolicaMaxima
    );
    lectura.setPresionSistolica(sistolicaActual.intValue());
    lectura.setPresionDiastolica(diastolicaActual.intValue());

    actualizarTemperaturaSegun(estado);
    lectura.setTemperatura(Math.round(temperaturaActual * 10.0) / 10.0);

    actualizarGpsSegun(estado);
    lectura.setLatitud(latitudActual);
    lectura.setLongitud(longitudActual);

    return lectura;
  }

  public LecturaSensor generarLecturaCritica(
    Long idMascota,
    EstadoMascota estadoActual,
    Integer frecuenciaMaxima,
    Integer sistolicaMaxima,
    Integer diastolicaMaxima
  ) {
    if (sistolicaActual == null) sistolicaActual = 120.0;
    if (diastolicaActual == null) diastolicaActual = 80.0;

    EstadoMascota corriendo = EstadoMascota.CORRIENDO;
    LecturaSensor lectura = new LecturaSensor();

    int frecuenciaCritica = frecuenciaMaxima + 40;
    frecuenciaPorMascota.put(idMascota, (double) frecuenciaCritica);
    lectura.setFrecuenciaCardiaca(frecuenciaCritica);

    lectura.setAccelX(corriendo.getComportamiento().generarMovimiento(random) * 2);
    lectura.setAccelY(corriendo.getComportamiento().generarMovimiento(random) * 2);
    lectura.setAccelZ(corriendo.getComportamiento().generarMovimiento(random) * 2);
    lectura.setGyroX(corriendo.getComportamiento().generarGyro(random) * 2);
    lectura.setGyroY(corriendo.getComportamiento().generarGyro(random) * 2);
    lectura.setGyroZ(corriendo.getComportamiento().generarGyro(random) * 2);

    sistolicaActual = sistolicaMaxima + 30.0;
    diastolicaActual = diastolicaMaxima + 20.0;
    lectura.setPresionSistolica(sistolicaActual.intValue());
    lectura.setPresionDiastolica(diastolicaActual.intValue());

    temperaturaActual = 41.5;
    lectura.setTemperatura(temperaturaActual);

    actualizarGpsSegun(corriendo);
    lectura.setLatitud(latitudActual);
    lectura.setLongitud(longitudActual);

    return lectura;
  }

  public int actualizarFrecuencia(Long idMascota, EstadoMascota estado, int min, int max) {
    int nueva = obtenerFrecuenciaIncremental(idMascota, estado, min, max);
    frecuenciaPorMascota.put(idMascota, (double) nueva);
    return nueva;
  }

  // ── privados ──────────────────────────────────────────────

  private EstadoMascota sortearEstado() {
    double probabilidadEstado = random.nextDouble();
    if (probabilidadEstado < LIMITE_DORMIDO) return EstadoMascota.DURMIENDO;
    if (probabilidadEstado < LIMITE_REPOSO) return EstadoMascota.REPOSO;
    if (probabilidadEstado < LIMITE_CAMINANDO) return EstadoMascota.CAMINANDO;
    return EstadoMascota.CORRIENDO;
  }

  private int generarFrecuenciaSegun(EstadoMascota estado, int min, int max) {
    return min + (int) ((max - min) * estado.getComportamiento().getFactorFrecuencia());
  }

  private int generarPresionSegun(EstadoMascota estado, int min, int max) {
    return min + (int) ((max - min) * estado.getComportamiento().getFactorPresion());
  }

  private void actualizarTemperaturaSegun(EstadoMascota estado) {
    double objetivo = estado.getComportamiento().getTemperaturaBase();
    double variacion = (random.nextDouble() - 0.5) * TEMP_MAX_VARIACION;
    temperaturaActual += (objetivo - temperaturaActual) * 0.1 + variacion;
    temperaturaActual = Math.max(37.5, Math.min(39.9, temperaturaActual));
  }

  private void actualizarGpsSegun(EstadoMascota estado) {
    double[] coords = { latitudActual, longitudActual };
    estado.getComportamiento().actualizarGps(coords, random);
    latitudActual = coords[0];
    longitudActual = coords[1];
  }

  private void actualizarPresionSegun(
    EstadoMascota estado,
    int sistolicaMin,
    int sistolicaMax,
    int diastolicaMin,
    int diastolicaMax
  ) {
    double objSis = generarPresionSegun(estado, sistolicaMin, sistolicaMax);
    double objDias = generarPresionSegun(estado, diastolicaMin, diastolicaMax);

    sistolicaActual +=
    (objSis - sistolicaActual) * 0.1 + (random.nextDouble() - 0.5) * MAX_VARIACION_PRESION;
    diastolicaActual +=
    (objDias - diastolicaActual) * 0.1 + (random.nextDouble() - 0.5) * MAX_VARIACION_PRESION;

    sistolicaActual = Math.max(sistolicaMin, Math.min(sistolicaMax, sistolicaActual));
    diastolicaActual = Math.max(diastolicaMin, Math.min(diastolicaMax, diastolicaActual));
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
}
