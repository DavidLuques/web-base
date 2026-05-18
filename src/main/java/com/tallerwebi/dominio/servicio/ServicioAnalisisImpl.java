package com.tallerwebi.dominio.servicio;

// import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.DatosAnalisis;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.infraestructura.RepositorioAnalisisImpl;
import com.tallerwebi.infraestructura.RepositorioMascotaImpl;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioAnalisisImpl implements ServicioAnalisis {

  private static final Logger logger = LoggerFactory.getLogger(ServicioAnalisisImpl.class);
  private final RepositorioAnalisisImpl repositorioAnalisis;
  private final RepositorioMascotaImpl repositorioMascota;
  private final SimuladorCollarService simuladorCollar;

  //   private final MotorActividadService motorActividad;

  @Autowired
  public ServicioAnalisisImpl(
    RepositorioAnalisisImpl repositorioAnalisis,
    RepositorioMascotaImpl repositorioMascota,
    SimuladorCollarService simuladorCollar
  ) {
    this.repositorioAnalisis = repositorioAnalisis;
    this.repositorioMascota = repositorioMascota;
    this.simuladorCollar = simuladorCollar;
    // this.motorActividad = motorActividad;
  }

  @Scheduled(fixedRate = 60000) // 1 minuto
  @Override
  public void simularGeolocalizacion() {
    logger.info("1. Ejecutando el motor de simulación...");
    Mascota perro = repositorioMascota.buscarPorId(1L);

    if (perro != null) {
      logger.info("2. Mascota encontrada. Generando datos...");
      // 1. Pedimos al hardware simulado que escupa una nueva lectura (asumiendo rangos de 60-120 lpm)
      LecturaSensor lecturaCruda = simuladorCollar.generarLectura(60, 120);

      // 2. Le preguntamos al motor qué significa esa lectura
      // EstadoMascota estadoActual = motorActividad.analizar(perro, lecturaCruda);

      // 3. Calculamos la distancia usando tu fórmula (mantenemos tu método calcularDistancia)
      // Analisis ultimoAnalisis = repositorioAnalisis.obtenerUltimoAnalisis(perro.getId());
      // Double distancia = 0.0;
      // if (ultimoAnalisis != null) {
      //     distancia = calcularDistancia(
      //         ultimoAnalisis.getLatitud(), ultimoAnalisis.getLongitud(),
      //         lecturaCruda.getLatitud(), lecturaCruda.getLongitud()
      //     );
      // }

      // 4. Mapeamos el DTO a tu Entidad de base de datos
      Analisis nuevoAnalisis = armarEntidad(lecturaCruda, perro);
      repositorioAnalisis.guardar(nuevoAnalisis);
      logger.info("3. Datos guardados en MySQL exitosamente.");
    } else {
      logger.warn("ERROR SILENCIOSO: No existe ninguna mascota con ID 1 en la base de datos.");
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

    return RADIO_TIERRA * car;
  }
}
