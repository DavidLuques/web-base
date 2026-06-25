package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.ValladoDao;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.DatosAnalisis;
import com.tallerwebi.dominio.modelo.DatosSensor;
import com.tallerwebi.dominio.modelo.DatosVitalesYUbicacion;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.Vallado;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
    servicio
*/

@Service
@Transactional
public class ServicioValladoImpl implements ServicioVallado {

  @PersistenceContext
  private EntityManager entityManager;

  private final ValladoDao valladoDao;
  private RepositorioAnalisis repositorioAnalisis;
  private MascotaDao mascotaDao;
  private static final Logger LOGGER = Logger.getLogger(ServicioValladoImpl.class.getName());

  @Autowired
  public ServicioValladoImpl(
    ValladoDao valladoDao,
    MascotaDao mascotaDao,
    RepositorioAnalisis repositorioAnalisis
  ) {
    this.valladoDao = valladoDao;
    this.mascotaDao = mascotaDao;
    this.repositorioAnalisis = repositorioAnalisis;
  }

  @Override
  public void actualizarRadioValla(Long idMascota, Integer radioValla) {
    Vallado vallado = valladoDao.buscarPorMascota(idMascota);
    if (vallado != null) {
      vallado.setRadioMetros(radioValla);
      valladoDao.modificar(vallado);
    }
  }

  @Override
  public void actualizarCentroVallado(Long idMascota, Double latitud, Double longitud) {
    try {
      Vallado vallado = valladoDao.buscarPorMascota(idMascota);
      Mascota mascota = mascotaDao.buscarPorId(idMascota);

      if (vallado == null) {
        vallado = new Vallado();
        vallado.setMascota(mascota);
        vallado.setLatitudCentro(latitud);
        vallado.setLongitudCentro(longitud);
        valladoDao.guardar(vallado);
      } else {
        vallado.setLatitudCentro(latitud);
        vallado.setLongitudCentro(longitud);
        valladoDao.modificar(vallado);
      }

      if (mascota != null) {
        // Buscamos el último registro completo que ya existe en la base de datos
        Analisis ultimo = repositorioAnalisis.obtenerUltimoAnalisis(idMascota);

        // Creamos el nuevo análisis copiando la telemetría existente para evitar el error de columnas NULL
        Analisis analisisBase = crearAnalisisConTelemetria(mascota, latitud, longitud, ultimo);

        repositorioAnalisis.guardar(analisisBase);
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error al actualizar el centro y reiniciar la posición", e);
      throw e;
    }
  }

 
  private Analisis crearAnalisisConTelemetria(
    Mascota mascota,
    Double latitud,
    Double longitud,
    Analisis ultimo
  ) {
    Analisis nuevo = new Analisis();
    nuevo.setMascota(mascota);
    nuevo.setFechaYHora(LocalDateTime.now());

    DatosAnalisis nuevosDatos = new DatosAnalisis();
    DatosSensor nuevosSensores = new DatosSensor();
    DatosVitalesYUbicacion nuevosVitalesUbicacion = new DatosVitalesYUbicacion();

    // Validamos de forma independiente si existen los datos viejos
    boolean tieneSensoresViejos =
      (ultimo != null && ultimo.getDatos() != null && ultimo.getDatos().getSensor() != null);
    boolean tieneVitalesViejos =
      (ultimo != null &&
        ultimo.getDatos() != null &&
        ultimo.getDatos().getVitalesYVitales() != null);

    // Sensores
    if (tieneSensoresViejos) {
      DatosSensor sensoresUltimo = ultimo.getDatos().getSensor();
      nuevosSensores.setAccelX(sensoresUltimo.getAccelX());
      nuevosSensores.setAccelY(sensoresUltimo.getAccelY());
      nuevosSensores.setAccelZ(sensoresUltimo.getAccelZ());
      nuevosSensores.setGyroX(sensoresUltimo.getGyroX());
      nuevosSensores.setGyroY(sensoresUltimo.getGyroY());
      nuevosSensores.setGyroZ(sensoresUltimo.getGyroZ());
      nuevosSensores.setFrecuenciaCardiaca(sensoresUltimo.getFrecuenciaCardiaca());
    } else {
      nuevosSensores.setFrecuenciaCardiaca(80);
      nuevosSensores.setAccelX(0.0);
      nuevosSensores.setAccelY(0.0);
      nuevosSensores.setAccelZ(0.0);
      nuevosSensores.setGyroX(0.0);
      nuevosSensores.setGyroY(0.0);
      nuevosSensores.setGyroZ(0.0);
    }

    // Signos Vitales
    if (tieneVitalesViejos) {
      DatosVitalesYUbicacion vitalesUltimo = ultimo.getDatos().getVitalesYVitales();
      nuevosVitalesUbicacion.setPresionSistolica(vitalesUltimo.getPresionSistolica());
      nuevosVitalesUbicacion.setPresionDiastolica(vitalesUltimo.getPresionDiastolica());
      nuevosVitalesUbicacion.setHorasSueno(vitalesUltimo.getHorasSueno());
      nuevosVitalesUbicacion.setOxigenacion(vitalesUltimo.getOxigenacion());
      nuevosVitalesUbicacion.setTemperatura(vitalesUltimo.getTemperatura());
    } else {
      nuevosVitalesUbicacion.setPresionSistolica(120);
      nuevosVitalesUbicacion.setPresionDiastolica(80);
      nuevosVitalesUbicacion.setHorasSueno(8);
      nuevosVitalesUbicacion.setOxigenacion(98.0);
      nuevosVitalesUbicacion.setTemperatura(37.0);
    }

    // Asignación de las Coordenadas
    nuevo.setLatitud(latitud);
    nuevo.setLongitud(longitud);

    nuevosDatos.setSensor(nuevosSensores);
    nuevosDatos.setVitalesYVitales(nuevosVitalesUbicacion);
    nuevo.setDatos(nuevosDatos);

    return nuevo;
  }
}
