package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de lógica de negocio.
 */
@Service
@Transactional
public class AnalizadorDeDatosServiceImpl implements AnalizadorDeDatosService {

  private static final double PROBABILIDAD_MEDIA = 0.5;
  private static final int RADIO_TIERRA_KM = 6371;

  private final RangoVitalDao rangoVitalDao;
  private final Map<Long, EstadoMascota> memoriaEstados = new HashMap<>();

  @Autowired
  public AnalizadorDeDatosServiceImpl(RangoVitalDao rangoVitalDao) {
    this.rangoVitalDao = rangoVitalDao;
  }

  // ── Estado ───────────────────────────────────────────────

  @Override
  public EstadoMascota determinarEstado(Mascota mascota, LecturaSensor lectura) {
    RangoVitalPorTamano rango = rangoVitalDao.buscarPorTamano(mascota.getTamano());

    int frecuencia = lectura.getFrecuenciaCardiaca();
    double movimiento = magnitudVector(
      lectura.getAccelX(),
      lectura.getAccelY(),
      lectura.getAccelZ()
    );
    double rotacion = magnitudVector(lectura.getGyroX(), lectura.getGyroY(), lectura.getGyroZ());

    int rangoFreq = rango.getFrecuenciaMaxima() - rango.getFrecuenciaMinima();
    int limiteDormido = rango.getFrecuenciaMinima() + (rangoFreq / 4);
    int limiteReposo = rango.getFrecuenciaMinima() + (rangoFreq / 2);
    int limiteCaminando = rango.getFrecuenciaMinima() + ((rangoFreq * 3) / 4);

    EstadoMascota estadoDeducido = deducirEstadoDesdeLectura(
      frecuencia,
      movimiento,
      rotacion,
      limiteDormido,
      limiteReposo,
      limiteCaminando
    );

    return aplicarTransicionGradual(mascota.getId(), estadoDeducido);
  }

  private EstadoMascota deducirEstadoDesdeLectura(
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

  //
  private EstadoMascota aplicarTransicionGradual(Long idMascota, EstadoMascota estadoDeducido) {
    EstadoMascota anterior = memoriaEstados.get(idMascota);

    if (anterior == null) {
      memoriaEstados.put(idMascota, estadoDeducido);
      return estadoDeducido;
    }

    EstadoMascota estadoFinal;

    if (estadoDeducido == anterior) {
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
      estadoFinal = avanzarUnPaso(anterior, estadoDeducido);
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

  // ── Geolocalización ──────────────────────────────────────

  @Override
  public double calcularDistanciaEntreUbicaciones(
    Double lat1,
    Double lon1,
    Double lat2,
    Double lon2
  ) {
    double disLat = Math.toRadians(lat1 - lat2);
    double disLon = Math.toRadians(lon1 - lon2);

    double valorHaversine =
      Math.sin(disLat / 2) * Math.sin(disLat / 2) +
      Math.cos(Math.toRadians(lat1)) *
        Math.cos(Math.toRadians(lat2)) *
        Math.sin(disLon / 2) *
        Math.sin(disLon / 2);

    double distanciaAngular =
      2 * Math.atan2(Math.sqrt(valorHaversine), Math.sqrt(1 - valorHaversine));

    return Math.round(RADIO_TIERRA_KM * distanciaAngular * 1000.0) / 1000.0;
  }

  // ── Actividad ────────────────────────────────────────────

  @Override
  public int calcularPasos(Double distanciaEnKm, TamanoMascota tamano) {
    if (distanciaEnKm == null || distanciaEnKm == 0.0 || tamano == null) return 0;
    return (int) Math.round(distanciaEnKm * tamano.getComportamiento().getPasosPorKm());
  }

  @Override
  public double calcularCalorias(Double distanciaEnKm, EstadoMascota estado, Double pesoKg) {
    if (
      distanciaEnKm == null ||
      distanciaEnKm == 0.0 ||
      estado == null ||
      pesoKg == null ||
      pesoKg == 0.0
    ) {
      return 0.0;
    }

    double met = estado.getComportamiento().getMET();
    double velocidadKmH = estado.getComportamiento().getVelocidadKmH();
    double duracionHoras = distanciaEnKm / velocidadKmH;

    return Math.round(met * pesoKg * duracionHoras * 10.0) / 10.0;
  }

  // ── Helpers ──────────────────────────────────────────────

  private double magnitudVector(Double coordenadaX, Double coordenadaY, Double coordenadaZ) {
    return Math.sqrt(
      coordenadaX * coordenadaX + coordenadaY * coordenadaY + coordenadaZ * coordenadaZ
    );
  }

  public void limpiarMemoria() {
    memoriaEstados.clear();
  }
}
