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

  private static final int ORDEN_DURMIENDO = 0;
  private static final int ORDEN_REPOSO = 1;
  private static final int ORDEN_CAMINANDO = 2;
  private static final int ORDEN_CORRIENDO = 3;

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

    int limiteDormido = min + (rangoFreq / 4);
    int limiteReposo = min + (rangoFreq / 2);
    int limiteCaminando = min + ((rangoFreq * 3) / 4);

    EstadoMascota nuevoEstado = evaluarEstado(
      frecuencia,
      movimiento,
      rotacion,
      limiteDormido,
      limiteReposo,
      limiteCaminando
    );

    return aplicarTransicionRealista(mascota.getId(), nuevoEstado);
  }

  private EstadoMascota aplicarTransicionRealista(Long idMascota, EstadoMascota nuevoEstado) {
    EstadoMascota anterior = memoriaEstados.get(idMascota);

    if (anterior == null) {
      memoriaEstados.put(idMascota, nuevoEstado);
      return nuevoEstado;
    }

    EstadoMascota estadoFinal;
    if (nuevoEstado == anterior) {
      int orden = ordenDe(anterior);
      if (orden < ORDEN_CORRIENDO) {
        estadoFinal = estadoConOrden(orden + 1);
      } else {
        estadoFinal = estadoConOrden(orden - 1);
      }
    } else {
      estadoFinal = avanzarUnPaso(anterior, nuevoEstado);
    }

    memoriaEstados.put(idMascota, estadoFinal);
    return estadoFinal;
  }

  private EstadoMascota avanzarUnPaso(EstadoMascota anterior, EstadoMascota objetivo) {
    int ordenAnterior = ordenDe(anterior);
    int ordenObjetivo = ordenDe(objetivo);

    if (ordenObjetivo > ordenAnterior) {
      return estadoConOrden(ordenAnterior + 1);
    } else if (ordenObjetivo < ordenAnterior) {
      return estadoConOrden(ordenAnterior - 1);
    } else {
      return anterior;
    }
  }

  private int ordenDe(EstadoMascota estado) {
    switch (estado) {
      case DURMIENDO:
        return ORDEN_DURMIENDO;
      case REPOSO:
        return ORDEN_REPOSO;
      case CAMINANDO:
        return ORDEN_CAMINANDO;
      default:
        return ORDEN_CORRIENDO;
    }
  }

  private EstadoMascota estadoConOrden(int orden) {
    switch (orden) {
      case ORDEN_DURMIENDO:
        return EstadoMascota.DURMIENDO;
      case ORDEN_REPOSO:
        return EstadoMascota.REPOSO;
      case ORDEN_CAMINANDO:
        return EstadoMascota.CAMINANDO;
      default:
        return EstadoMascota.CORRIENDO;
    }
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
