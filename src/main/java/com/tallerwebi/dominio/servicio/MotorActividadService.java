package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MotorActividadService {

  private final RangoVitalDao rangoVitalDao;

  private final Map<Long, EstadoMascota> memoriaEstados = new HashMap<>();

  @Autowired
  public MotorActividadService(RangoVitalDao rangoVitalDao) {
    this.rangoVitalDao = rangoVitalDao;
  }

  public EstadoMascota analizar(Mascota mascota, LecturaSensor lectura) {
    RangoVitalPorTamano rango = rangoVitalDao.buscarPorTamano(mascota.getTamano());

    int frecuencia = lectura.getFrecuenciaCardiaca();

    double movimiento = calcularMagnitud(
      lectura.getAccelX(),
      lectura.getAccelY(),
      lectura.getAccelZ()
    );

    double rotacion = calcularMagnitud(lectura.getGyroX(), lectura.getGyroY(), lectura.getGyroZ());

    int min = rango.getFrecuenciaMinima();
    int max = rango.getFrecuenciaMaxima();

    int rangoFreq = max - min;

    int dormido = min + (rangoFreq / 4);
    int reposo = min + (rangoFreq / 2);
    int caminando = min + ((rangoFreq * 3) / 4);

    EstadoMascota nuevoEstado = evaluarEstado(
      frecuencia,
      movimiento,
      rotacion,
      dormido,
      reposo,
      caminando
    );

    return aplicarTransicionRealista(mascota.getId(), nuevoEstado);
  }

  private EstadoMascota aplicarTransicionRealista(Long idMascota, EstadoMascota nuevoEstado) {
    EstadoMascota anterior = memoriaEstados.get(idMascota);

    if (anterior == null) {
      memoriaEstados.put(idMascota, nuevoEstado);
      return nuevoEstado;
    }

    if (esSaltoBrusco(anterior, nuevoEstado)) {
      return anterior;
    }

    memoriaEstados.put(idMascota, nuevoEstado);
    return nuevoEstado;
  }

  private boolean esSaltoBrusco(EstadoMascota anterior, EstadoMascota nuevo) {
    return (
      (anterior == EstadoMascota.CORRIENDO && nuevo == EstadoMascota.DURMIENDO) ||
      (anterior == EstadoMascota.DURMIENDO && nuevo == EstadoMascota.CORRIENDO)
    );
  }

  private EstadoMascota evaluarEstado(
    int frecuencia,
    double movimiento,
    double rotacion,
    int limiteDormido,
    int limiteReposo,
    int limiteCaminando
  ) {
    if (esDurmiendo(frecuencia, movimiento, rotacion, limiteDormido)) {
      return EstadoMascota.DURMIENDO;
    }
    if (esEnReposo(frecuencia, movimiento, rotacion, limiteReposo)) {
      return EstadoMascota.REPOSO;
    }
    if (esCaminando(frecuencia, movimiento, rotacion, limiteCaminando)) {
      return EstadoMascota.CAMINANDO;
    }
    return EstadoMascota.CORRIENDO;
  }

  private boolean esDurmiendo(int frecuencia, double movimiento, double rotacion, int limite) {
    return frecuencia <= limite && movimiento < 2 && rotacion < 1;
  }

  private boolean esEnReposo(int frecuencia, double movimiento, double rotacion, int limite) {
    return frecuencia <= limite && movimiento < 5 && rotacion < 2.5;
  }

  private boolean esCaminando(int frecuencia, double movimiento, double rotacion, int limite) {
    return frecuencia <= limite && movimiento < 9 && rotacion < 4;
  }

  private double calcularMagnitud(Double coordX, Double coordY, Double coordZ) {
    return Math.sqrt(coordX * coordX + coordY * coordY + coordZ * coordZ);
  }

  public void limpiarMemoria() {
    memoriaEstados.clear();
  }
}
