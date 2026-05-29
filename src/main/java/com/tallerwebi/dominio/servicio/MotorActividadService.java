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

  private static final double PROBABILIDAD_MEDIA = 0.5;

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

    int rangoFreq = rango.getFrecuenciaMaxima() - rango.getFrecuenciaMinima();
    int limiteDormido = rango.getFrecuenciaMinima() + (rangoFreq / 4);
    int limiteReposo = rango.getFrecuenciaMinima() + (rangoFreq / 2);
    int limiteCaminando = rango.getFrecuenciaMinima() + ((rangoFreq * 3) / 4);

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

  private EstadoMascota evaluarEstado(
    int frecuencia,
    double movimiento,
    double rotacion,
    int limiteDormido,
    int limiteReposo,
    int limiteCaminando
  ) {
    if (
      EstadoMascota.DURMIENDO
        .getComportamiento()
        .coincideConLectura(frecuencia, movimiento, rotacion, limiteDormido)
    ) return EstadoMascota.DURMIENDO;
    if (
      EstadoMascota.REPOSO
        .getComportamiento()
        .coincideConLectura(frecuencia, movimiento, rotacion, limiteReposo)
    ) return EstadoMascota.REPOSO;
    if (
      EstadoMascota.CAMINANDO
        .getComportamiento()
        .coincideConLectura(frecuencia, movimiento, rotacion, limiteCaminando)
    ) return EstadoMascota.CAMINANDO;
    return EstadoMascota.CORRIENDO;
  }

  private EstadoMascota aplicarTransicionRealista(Long idMascota, EstadoMascota nuevoEstado) {
    EstadoMascota anterior = memoriaEstados.get(idMascota);

    if (anterior == null) {
      memoriaEstados.put(idMascota, nuevoEstado);
      return nuevoEstado;
    }

    EstadoMascota estadoFinal;

    if (nuevoEstado == anterior) {
      int orden = anterior.getComportamiento().getOrden();
      if (orden == 0) {
        estadoFinal = EstadoMascota.porOrden(1);
      } else if (orden >= EstadoMascota.CORRIENDO.getComportamiento().getOrden()) {
        estadoFinal = EstadoMascota.porOrden(orden - 1);
      } else {
        estadoFinal =
          Math.random() > PROBABILIDAD_MEDIA
            ? EstadoMascota.porOrden(orden + 1)
            : EstadoMascota.porOrden(orden - 1);
      }
    } else {
      estadoFinal = avanzarUnPaso(anterior, nuevoEstado);
    }

    memoriaEstados.put(idMascota, estadoFinal);
    return estadoFinal;
  }

  private EstadoMascota avanzarUnPaso(EstadoMascota anterior, EstadoMascota objetivo) {
    int ordenAnterior = anterior.getComportamiento().getOrden();
    int ordenObjetivo = objetivo.getComportamiento().getOrden();

    if (ordenObjetivo > ordenAnterior) return EstadoMascota.porOrden(ordenAnterior + 1);
    if (ordenObjetivo < ordenAnterior) return EstadoMascota.porOrden(ordenAnterior - 1);
    return anterior;
  }

  private double calcularMagnitud(Double coordenadaX, Double coordenadaY, Double coordenadaZ) {
    return Math.sqrt(
      coordenadaX * coordenadaX + coordenadaY * coordenadaY + coordenadaZ * coordenadaZ
    );
  }

  public void limpiarMemoria() {
    memoriaEstados.clear();
  }
}
