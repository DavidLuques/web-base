package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio de lógica de negocio.
 */
@Service
public class LectorCollarServiceImpl implements LectorCollarService {

  private static final double VARIACION_FRECUENCIA = 3.0;
  private static final double VARIACION_PRESION = 2.0;
  private static final double VARIACION_TEMPERATURA = 0.15;
  private static final double VARIACION_GPS_LEVE = 0.001;
  private static final double VARIACION_GPS_NORMAL = 0.0005;
  private final RepositorioAnalisis repositorioAnalisis;

  private Double temperaturaActual = 38.5;
  private Double sistolicaActual = null;
  private Double diastolicaActual = null;
  private final Map<Long, Double> frecuenciaPorMascota = new HashMap<>();

  private final Random random = new Random();
  private final MascotaDao mascotaDao;
  private final RangoVitalDao rangoVitalDao;

  @Autowired
  public LectorCollarServiceImpl(
    MascotaDao mascotaDao,
    RangoVitalDao rangoVitalDao,
    RepositorioAnalisis repositorioAnalisis
  ) {
    this.mascotaDao = mascotaDao;
    this.rangoVitalDao = rangoVitalDao;
    this.repositorioAnalisis = repositorioAnalisis;
  }

  @Override
  public LecturaSensor obtenerLectura(Long idMascota) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    RangoVitalPorTamano rango = rangoVitalDao.buscarPorTipoYTamano(
      mascota.getTipo(),
      mascota.getTamano()
    );

    inicializarPresionSiEsNecesario(rango);

    Analisis ultimoAnalisis = repositorioAnalisis.obtenerUltimoAnalisis(idMascota);
    double latBase = (ultimoAnalisis != null && ultimoAnalisis.getLatitud() != null)
      ? ultimoAnalisis.getLatitud()
      : -34.7222;
    double lonBase = (ultimoAnalisis != null && ultimoAnalisis.getLongitud() != null)
      ? ultimoAnalisis.getLongitud()
      : -58.5250;

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(siguienteFrecuencia(idMascota, rango));
    lectura.setTemperatura(siguienteTemperatura(rango));
    lectura.setPresionSistolica(siguientePresionSistolica(rango));
    lectura.setPresionDiastolica(siguientePresionDiastolica(rango));
    lectura.setAccelX(siguienteMovimiento());
    lectura.setAccelY(siguienteMovimiento());
    lectura.setAccelZ(siguienteMovimiento());
    lectura.setGyroX(siguienteRotacion());
    lectura.setGyroY(siguienteRotacion());
    lectura.setGyroZ(siguienteRotacion());
    lectura.setLatitud(siguienteLatitud(latBase));
    lectura.setLongitud(siguienteLongitud(lonBase));

    return lectura;
  }

  private void inicializarPresionSiEsNecesario(RangoVitalPorTamano rango) {
    if (sistolicaActual == null) sistolicaActual =
      valorMedioEntre(rango.getSistolicaMinima(), rango.getSistolicaMaxima());
    if (diastolicaActual == null) diastolicaActual =
      valorMedioEntre(rango.getDiastolicaMinima(), rango.getDiastolicaMaxima());
  }

  private int siguienteFrecuencia(Long idMascota, RangoVitalPorTamano rango) {
    double objetivo = valorMedioEntre(rango.getFrecuenciaMinima(), rango.getFrecuenciaMaxima());
    double actual = frecuenciaPorMascota.getOrDefault(idMascota, objetivo);
    double nueva = actual + (objetivo - actual) * 0.1 + variacion(VARIACION_FRECUENCIA);
    nueva = Math.max(rango.getFrecuenciaMinima(), Math.min(rango.getFrecuenciaMaxima(), nueva));
    frecuenciaPorMascota.put(idMascota, nueva);
    return (int) nueva;
  }

  private double siguienteTemperatura(RangoVitalPorTamano rango) {
    double objetivo = valorMedioEntre(rango.getTemperaturaMinima(), rango.getTemperaturaMaxima());
    temperaturaActual += (objetivo - temperaturaActual) * 0.1 + variacion(VARIACION_TEMPERATURA);
    temperaturaActual =
      Math.max(
        rango.getTemperaturaMinima(),
        Math.min(rango.getTemperaturaMaxima(), temperaturaActual)
      );
    return Math.round(temperaturaActual * 10.0) / 10.0;
  }

  private int siguientePresionSistolica(RangoVitalPorTamano rango) {
    double objetivo = valorMedioEntre(rango.getSistolicaMinima(), rango.getSistolicaMaxima());
    sistolicaActual += (objetivo - sistolicaActual) * 0.1 + variacion(VARIACION_PRESION);
    sistolicaActual =
      Math.max(rango.getSistolicaMinima(), Math.min(rango.getSistolicaMaxima(), sistolicaActual));
    return sistolicaActual.intValue();
  }

  private int siguientePresionDiastolica(RangoVitalPorTamano rango) {
    double objetivo = valorMedioEntre(rango.getDiastolicaMinima(), rango.getDiastolicaMaxima());
    diastolicaActual += (objetivo - diastolicaActual) * 0.1 + variacion(VARIACION_PRESION);
    diastolicaActual =
      Math.max(
        rango.getDiastolicaMinima(),
        Math.min(rango.getDiastolicaMaxima(), diastolicaActual)
      );
    return diastolicaActual.intValue();
  }

  private double siguienteMovimiento() {
    return Math.abs(variacion(10.0));
  }

  private double siguienteRotacion() {
    return Math.abs(variacion(5.0));
  }

  private double siguienteLatitud(double latitudBase) {
    return latitudBase + variacion(VARIACION_GPS_LEVE);
  }

  private double siguienteLongitud(double longitudBase) {
    return longitudBase + variacion(VARIACION_GPS_NORMAL);
  }

  private double variacion(double magnitud) {
    return (random.nextDouble() - 0.5) * magnitud;
  }

  private double valorMedioEntre(double min, double max) {
    return min + (max - min) / 2.0;
  }
}
