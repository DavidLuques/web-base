package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.modelo.Mascota;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ActualizadorEstadoMascotaScheduler {

  private static final long TRES_MINUTOS_EN_MS = 3 * 60 * 1000;

  private final MascotaDao mascotaDao;
  private final SimulacionActividadService simulacionActividadService;

  @Autowired
  public ActualizadorEstadoMascotaScheduler(
    MascotaDao mascotaDao,
    SimulacionActividadService simulacionActividadService
  ) {
    this.mascotaDao = mascotaDao;
    this.simulacionActividadService = simulacionActividadService;
  }

  @Scheduled(fixedRate = TRES_MINUTOS_EN_MS)
  @Transactional
  public void actualizarEstadosDeMascotas() {
    List<Mascota> mascotas = mascotaDao.buscarTodas();
    for (Mascota mascota : mascotas) {
      simulacionActividadService.simularDetalle(mascota.getId());
    }
  }
}
