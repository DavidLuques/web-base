package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioActividad;
import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.RepositorioSueno;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.Actividad;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import com.tallerwebi.dominio.modelo.RegistroSueno;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SimulacionActividadService {

  private static final int MINUTOS_POR_TICK = 2;

  private final MascotaDao mascotaDao;
  private final RangoVitalDao rangoVitalDao;
  private final SimuladorCollarService simuladorCollarService;
  private final MotorActividadService motorActividadService;
  private final RepositorioActividad repositorioActividad;
  private final ServicioAnalisis servicioAnalisis;
  private final RepositorioAnalisis repositorioAnalisis;
  private final RepositorioSueno repositorioSueno;

  private static final double MET_DURMIENDO = 1.0;
  private static final double MET_REPOSO = 1.5;
  private static final double MET_CAMINANDO = 3.0;
  private static final double MET_CORRIENDO = 6.0;
  private static final double VEL_CAMINANDO = 5.0;
  private static final double VEL_CORRIENDO = 15.0;

  @Autowired
  public SimulacionActividadService(
          MascotaDao mascotaDao,
          RangoVitalDao rangoVitalDao,
          SimuladorCollarService simuladorCollarService,
          MotorActividadService motorActividadService,
          RepositorioActividad repositorioActividad,
          ServicioAnalisis servicioAnalisis,
          RepositorioAnalisis repositorioAnalisis,
          RepositorioSueno repositorioSueno
  ) {
    this.mascotaDao = mascotaDao;
    this.rangoVitalDao = rangoVitalDao;
    this.simuladorCollarService = simuladorCollarService;
    this.motorActividadService = motorActividadService;
    this.repositorioActividad = repositorioActividad;
    this.servicioAnalisis = servicioAnalisis;
    this.repositorioAnalisis = repositorioAnalisis;
    this.repositorioSueno = repositorioSueno;
  }

  private void registrarActividadSegunEstado(Mascota mascota, EstadoMascota estado) {
    double velocidadKmH;
    if (estado == EstadoMascota.CAMINANDO) {
      velocidadKmH = VEL_CAMINANDO;
    } else if (estado == EstadoMascota.CORRIENDO) {
      velocidadKmH = VEL_CORRIENDO;
    } else {
      return;
    }

    double distanciaEnKm = velocidadKmH * (MINUTOS_POR_TICK / 60.0);

    Actividad actividad = new Actividad();
    actividad.setDistanciaRecorrida(distanciaEnKm);
    actividad.setFechaYHora(LocalDateTime.now());
    actividad.setMascota(mascota);
    repositorioActividad.guardar(actividad);
  }

  public ResultadoSimulacionDto simularDetalle(Long mascotaId) {
    Mascota mascota = mascotaDao.buscarPorId(mascotaId);
    RangoVitalPorTamano rango = rangoVitalDao.buscarPorTamano(mascota.getTamano());

    LecturaSensor lectura = simuladorCollarService.generarLectura(
            mascota.getId(),
            mascota.getEstadoActual(),
            rango.getFrecuenciaMinima(),
            rango.getFrecuenciaMaxima(),
            rango.getSistolicaMinima(),
            rango.getSistolicaMaxima(),
            rango.getDiastolicaMinima(),
            rango.getDiastolicaMaxima()
    );

    EstadoMascota estado = motorActividadService.analizar(mascota, lectura);
    mascota.setEstadoActual(estado);
    mascotaDao.modificar(mascota);

    if (estado == EstadoMascota.DURMIENDO) {
      RegistroSueno registro = new RegistroSueno();
      registro.setMinutosDormido(MINUTOS_POR_TICK);
      registro.setFechaYHora(LocalDateTime.now());
      registro.setMascota(mascota);
      repositorioSueno.guardar(registro);
    }

    registrarActividadSegunEstado(mascota, estado);

    Double distanciaTotal = repositorioActividad.obtenerDistanciaTotalPorMascota(mascotaId);
    Integer pasosCalculados = calcularPasos(distanciaTotal, mascota.getTamano());
    Double calorias = calcularCalorias(distanciaTotal, estado, mascota.getPeso());
    Integer minutosDormidos = repositorioSueno.obtenerTotalMinutosDormidosPorMascota(mascotaId);

    return new ResultadoSimulacionDto(
            mascota.getNombre(),
            estado,
            lectura.getFrecuenciaCardiaca(),
            lectura.getPresionSistolica(),
            lectura.getPresionDiastolica(),
            lectura.getTemperatura(),
            distanciaTotal,
            pasosCalculados,
            calorias,
            minutosDormidos
    );
  }

  public void simularDetalleParaTodas() {
    List<Mascota> mascotas = mascotaDao.buscarTodas();
    for (Mascota mascota : mascotas) {
      simularDetalle(mascota.getId());
      servicioAnalisis.simularGeolocalizacion(mascota.getId());
    }
  }

  public void actualizarFrecuenciaParaTodas() {
    List<Mascota> mascotas = mascotaDao.buscarTodas();
    for (Mascota mascota : mascotas) {
      RangoVitalPorTamano rango = rangoVitalDao.buscarPorTamano(mascota.getTamano());
      simuladorCollarService.actualizarFrecuencia(
              mascota.getId(),
              mascota.getEstadoActual(),
              rango.getFrecuenciaMinima(),
              rango.getFrecuenciaMaxima()
      );
    }
  }

  public ResultadoSimulacionDto obtenerEstadoActual(Long mascotaId) {
    Mascota mascota = mascotaDao.buscarPorId(mascotaId);

    if (mascota == null) {
      return new ResultadoSimulacionDto("No encontrada", null, null, null);
    }

    Double distanciaTotal = repositorioActividad.obtenerDistanciaTotalPorMascota(mascotaId);
    Integer pasosCalculados = calcularPasos(distanciaTotal, mascota.getTamano());
    Integer minutosDormidos = repositorioSueno.obtenerTotalMinutosDormidosPorMascota(mascotaId);

    Analisis ultimo = repositorioAnalisis.obtenerUltimoAnalisis(mascotaId);

    if (ultimo == null) {
      return new ResultadoSimulacionDto(
              mascota.getNombre(),
              mascota.getEstadoActual(),
              distanciaTotal,
              pasosCalculados,
              0.0,
              minutosDormidos
      );
    }

    Double calorias = calcularCalorias(
            distanciaTotal,
            mascota.getEstadoActual(),
            mascota.getPeso()
    );

    return new ResultadoSimulacionDto(
            mascota.getNombre(),
            mascota.getEstadoActual(),
            ultimo.getDatos().getFrecuenciaCardiaca(),
            ultimo.getDatos().getPresionSistolica(),
            ultimo.getDatos().getPresionDiastolica(),
            ultimo.getDatos().getTemperatura(),
            distanciaTotal,
            pasosCalculados,
            calorias,
            minutosDormidos
    );
  }

  private Double calcularCalorias(Double distanciaEnKm, EstadoMascota estado, Double pesoKg) {
    if (
            distanciaEnKm == null ||
                    distanciaEnKm == 0.0 ||
                    estado == null ||
                    pesoKg == null ||
                    pesoKg == 0.0
    ) {
      return 0.0;
    }

    double met;
    double velocidadKmH;

    switch (estado) {
      case DURMIENDO:
        met = MET_DURMIENDO;
        velocidadKmH = 1.0;
        break;
      case REPOSO:
        met = MET_REPOSO;
        velocidadKmH = 1.0;
        break;
      case CAMINANDO:
        met = MET_CAMINANDO;
        velocidadKmH = VEL_CAMINANDO;
        break;
      default:
        met = MET_CORRIENDO;
        velocidadKmH = VEL_CORRIENDO;
        break;
    }

    double duracionHoras = distanciaEnKm / velocidadKmH;
    return Math.round(met * pesoKg * duracionHoras * 10.0) / 10.0;
  }

  private Integer calcularPasos(Double distanciaEnKm, TamanoMascota tamano) {
    if (distanciaEnKm == null || distanciaEnKm == 0.0 || tamano == null) {
      return 0;
    }

    int pasosPorKm;
    switch (tamano) {
      case PEQUENO:
        pasosPorKm = 3200;
        break;
      case MEDIANO:
        pasosPorKm = 2100;
        break;
      case GRANDE:
        pasosPorKm = 1500;
        break;
      default:
        pasosPorKm = 2100;
        break;
    }

    return (int) Math.round(distanciaEnKm * pasosPorKm);
  }
}