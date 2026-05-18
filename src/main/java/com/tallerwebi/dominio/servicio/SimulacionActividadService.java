package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
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

  @Autowired
  public SimulacionActividadService(
    MascotaDao mascotaDao,
    RangoVitalDao rangoVitalDao,
    SimuladorCollarService simuladorCollarService,
    MotorActividadService motorActividadService
  ) {
    this.mascotaDao = mascotaDao;
    this.rangoVitalDao = rangoVitalDao;
    this.simuladorCollarService = simuladorCollarService;
    this.motorActividadService = motorActividadService;
  }

  public ResultadoSimulacionDto simularDetalle(Long mascotaId) {
    Mascota mascota = mascotaDao.buscarPorId(mascotaId);

    RangoVitalPorTamano rango = rangoVitalDao.buscarPorTamano(mascota.getTamano());

    LecturaSensor lectura = simuladorCollarService.generarLectura(
      rango.getFrecuenciaMinima(),
      rango.getFrecuenciaMaxima()
    );

    EstadoMascota estado = motorActividadService.analizar(mascota, lectura);

    mascota.setEstadoActual(estado);
    mascotaDao.modificar(mascota);

    return new ResultadoSimulacionDto(mascota.getNombre(), estado);
  }

  public void simularDetalleParaTodas() {
    List<Mascota> mascotas = mascotaDao.buscarTodas();
    for (Mascota mascota : mascotas) {
      simularDetalle(mascota.getId());
    }
  }

  public ResultadoSimulacionDto obtenerEstadoActual(Long mascotaId) {
    Mascota mascota = mascotaDao.buscarPorId(mascotaId);
    return new ResultadoSimulacionDto(mascota.getNombre(), mascota.getEstadoActual());
  }
}
