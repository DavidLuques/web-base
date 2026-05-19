package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioActividad;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.modelo.Actividad;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.DatosAnalisis;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import com.tallerwebi.infraestructura.RepositorioAnalisisImpl;
import com.tallerwebi.infraestructura.RepositorioMascotaImpl;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioAnalisisImpl implements ServicioAnalisis {

  private final RepositorioAnalisisImpl repositorioAnalisis;
  private final RepositorioMascotaImpl repositorioMascota;
  private final SimuladorCollarService simuladorCollar;
  private final RepositorioActividad repositorioActividad;
  private final RangoVitalDao rangoVitalDao;

  @Autowired
  public ServicioAnalisisImpl(
    RepositorioAnalisisImpl repositorioAnalisis,
    RepositorioMascotaImpl repositorioMascota,
    SimuladorCollarService simuladorCollar,
    RepositorioActividad repositorioActividad,
    RangoVitalDao rangoVitalDao
  ) {
    this.repositorioAnalisis = repositorioAnalisis;
    this.repositorioMascota = repositorioMascota;
    this.simuladorCollar = simuladorCollar;
    this.repositorioActividad = repositorioActividad;
    this.rangoVitalDao = rangoVitalDao;
  }

  @Scheduled(fixedRate = 10000)
  @Override
  public void simularGeolocalizacion() {
    Mascota perro = repositorioMascota.buscarPorId(1L);

    if (perro != null) {
      RangoVitalPorTamano rango = rangoVitalDao.buscarPorTamano(perro.getTamano());

      LecturaSensor lecturaCruda = simuladorCollar.generarLectura(
        perro.getId(),
        perro.getEstadoActual(),
        rango.getFrecuenciaMinima(),
        rango.getFrecuenciaMaxima(),
        rango.getSistolicaMinima(),
        rango.getSistolicaMaxima(),
        rango.getDiastolicaMinima(),
        rango.getDiastolicaMaxima()
      );

      Analisis ultimoAnalisis = repositorioAnalisis.obtenerUltimoAnalisis(perro.getId());
      Double distancia = (ultimoAnalisis != null)
        ? calcularDistancia(
          ultimoAnalisis.getLatitud(),
          ultimoAnalisis.getLongitud(),
          lecturaCruda.getLatitud(),
          lecturaCruda.getLongitud()
        )
        : 0.0;

      Analisis nuevoAnalisis = armarEntidad(lecturaCruda, perro);
      repositorioAnalisis.guardar(nuevoAnalisis);

      if (distancia > 0) {
        Actividad actividad = new Actividad();
        actividad.setDistanciaRecorrida(distancia);
        actividad.setFechaYHora(LocalDateTime.now());
        actividad.setMascota(perro);
        repositorioActividad.guardar(actividad);
      }
    }
  }

  private Analisis armarEntidad(LecturaSensor lectura, Mascota perro) {
    Analisis analisis = new Analisis();
    analisis.setLatitud(lectura.getLatitud());
    analisis.setLongitud(lectura.getLongitud());
    analisis.setFechaYHora(LocalDateTime.now());
    analisis.setMascota(perro);

    DatosAnalisis datos = new DatosAnalisis();
    datos.setFrecuenciaCardiaca(lectura.getFrecuenciaCardiaca());
    datos.setAccelX(lectura.getAccelX());
    datos.setAccelY(lectura.getAccelY());
    datos.setAccelZ(lectura.getAccelZ());
    datos.setGyroX(lectura.getGyroX());
    datos.setGyroY(lectura.getGyroY());
    datos.setGyroZ(lectura.getGyroZ());
    datos.setPresionSistolica(lectura.getPresionSistolica());
    datos.setPresionDiastolica(lectura.getPresionDiastolica());
    datos.setTemperatura(lectura.getTemperatura());

    analisis.setDatos(datos);
    return analisis;
  }

  public Double calcularDistancia(Double lat1, Double lon1, Double lat2, Double lon2) {
    final int RADIO_TIERRA = 6371;
    double disLat = Math.toRadians(lat1 - lat2);
    double disLon = Math.toRadians(lon1 - lon2);

    double ald =
      Math.sin(disLat / 2) * Math.sin(disLat / 2) +
      Math.cos(Math.toRadians(lat1)) *
        Math.cos(Math.toRadians(lat2)) *
        Math.sin(disLon / 2) *
        Math.sin(disLon / 2);

    double car = 2 * Math.atan2(Math.sqrt(ald), Math.sqrt(1 - ald));

    return Math.round(RADIO_TIERRA * car * 1000.0) / 1000.0;
  }
}
