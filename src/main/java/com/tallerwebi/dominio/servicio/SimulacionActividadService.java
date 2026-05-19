package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioActividad;
import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SimulacionActividadService {

  private final MascotaDao mascotaDao;
  private final RangoVitalDao rangoVitalDao;
  private final SimuladorCollarService simuladorCollarService;
  private final MotorActividadService motorActividadService;
  private final RepositorioActividad repositorioActividad;
  private final ServicioAnalisis servicioAnalisis;
  private final RepositorioAnalisis repositorioAnalisis;

  @Autowired
  public SimulacionActividadService(
    MascotaDao mascotaDao,
    RangoVitalDao rangoVitalDao,
    SimuladorCollarService simuladorCollarService,
    MotorActividadService motorActividadService,
    RepositorioActividad repositorioActividad,
    ServicioAnalisis servicioAnalisis
    RepositorioAnalisis repositorioAnalisis
  ) {
    this.mascotaDao = mascotaDao;
    this.rangoVitalDao = rangoVitalDao;
    this.simuladorCollarService = simuladorCollarService;
    this.motorActividadService = motorActividadService;
    this.repositorioActividad = repositorioActividad;
    this.servicioAnalisis = servicioAnalisis;
    this.repositorioAnalisis = repositorioAnalisis;
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
    Double distanciaTotal = repositorioActividad.obtenerDistanciaTotalPorMascota(mascotaId);

    return new ResultadoSimulacionDto(
      mascota.getNombre(),
      estado,
      lectura.getFrecuenciaCardiaca(),
      lectura.getPresionSistolica(),
      lectura.getPresionDiastolica(),
      lectura.getTemperatura(),
      distanciaTotal
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
    Double distanciaTotal = repositorioActividad.obtenerDistanciaTotalPorMascota(mascotaId);
      
    if (mascota == null) {
      return new ResultadoSimulacionDto("No encontrada", null);
    }

    Analisis ultimo = repositorioAnalisis.obtenerUltimoAnalisis(mascotaId);
    if (ultimo == null) {
      return new ResultadoSimulacionDto(mascota.getNombre(), mascota.getEstadoActual());
    }

    return new ResultadoSimulacionDto(
      mascota.getNombre(),
      mascota.getEstadoActual(),
      ultimo.getDatos().getFrecuenciaCardiaca(),
      ultimo.getDatos().getPresionSistolica(),
      ultimo.getDatos().getPresionDiastolica(),
      ultimo.getDatos().getTemperatura(),
      distanciaTotal
    );
  }
}
