package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LectorCollarServiceImpl implements LectorCollarService {

  private static final double VARIACION_FRECUENCIA = 3.0;
  private static final double VARIACION_PRESION = 2.0;
  private static final double VARIACION_TEMPERATURA = 0.15;
  private static final double VARIACION_GPS_LEVE = 0.001;
  private static final double VARIACION_GPS_NORMAL = 0.0005;

  private Double latitudActual = -34.7222;
  private Double longitudActual = -58.5250;
  private Double temperaturaActual = 38.5;
  private Double sistolicaActual = null;
  private Double diastolicaActual = null;
  private final Map<Long, Double> frecuenciaPorMascota = new HashMap<>();

  private final Random random = new Random();
  private final MascotaDao mascotaDao;
  private final RangoVitalDao rangoVitalDao;

  @Autowired
  public LectorCollarServiceImpl(MascotaDao mascotaDao, RangoVitalDao rangoVitalDao) {
    this.mascotaDao = mascotaDao;
    this.rangoVitalDao = rangoVitalDao;
  }

  @Override
  public LecturaSensor obtenerLectura(Long idMascota) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    RangoVitalPorTamano rango = rangoVitalDao.buscarPorTamano(mascota.getTamano());

    inicializarPresionSiEsNecesario(rango);

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
    lectura.setLatitud(siguienteLatitud());
    lectura.setLongitud(siguienteLongitud());

    return lectura;
  }

  @Override
  public LecturaSensor obtenerLecturaCritica(Long idMascota) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    RangoVitalPorTamano rango = rangoVitalDao.buscarPorTamano(mascota.getTamano());

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(rango.getFrecuenciaMaxima() + 40);
    lectura.setTemperatura(41.5);
    lectura.setPresionSistolica(rango.getSistolicaMaxima() + 30);
    lectura.setPresionDiastolica(rango.getDiastolicaMaxima() + 20);
    lectura.setAccelX(siguienteMovimiento() * 2);
    lectura.setAccelY(siguienteMovimiento() * 2);
    lectura.setAccelZ(siguienteMovimiento() * 2);
    lectura.setGyroX(siguienteRotacion() * 2);
    lectura.setGyroY(siguienteRotacion() * 2);
    lectura.setGyroZ(siguienteRotacion() * 2);
    lectura.setLatitud(siguienteLatitud());
    lectura.setLongitud(siguienteLongitud());

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

  private double siguienteLatitud() {
    latitudActual += variacion(VARIACION_GPS_LEVE);
    return latitudActual;
  }

  private double siguienteLongitud() {
    longitudActual += variacion(VARIACION_GPS_NORMAL);
    return longitudActual;
  }

  private double variacion(double magnitud) {
    return (random.nextDouble() - 0.5) * magnitud;
  }

  private double valorMedioEntre(double min, double max) {
    return min + (max - min) / 2.0;
  }
}
