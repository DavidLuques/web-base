package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RegistroHistorialDao;
import com.tallerwebi.dominio.dao.TurnoVeterinariaDao;
import com.tallerwebi.dominio.enums.EstadoTurno;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RegistroHistorial;
import com.tallerwebi.dominio.modelo.TurnoVeterinaria;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioVeterinaria")
@Transactional
public class ServicioVeterinariaImpl implements ServicioVeterinaria {

  private final TurnoVeterinariaDao turnoDao;
  private final RegistroHistorialDao historialDao;
  private final MascotaDao mascotaDao;
  private final ServicioAlerta servicioAlerta;

  @Autowired
  public ServicioVeterinariaImpl(
    TurnoVeterinariaDao turnoDao,
    RegistroHistorialDao historialDao,
    MascotaDao mascotaDao,
    ServicioAlerta servicioAlerta
  ) {
    this.turnoDao = turnoDao;
    this.historialDao = historialDao;
    this.mascotaDao = mascotaDao;
    this.servicioAlerta = servicioAlerta;
  }

  @Override
  public void agendarTurno(
    Long idMascota,
    String nombre,
    String direccion,
    LocalDateTime fecha,
    String motivo
  ) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    if (mascota == null) {
      throw new IllegalArgumentException("La mascota no existe");
    }

    TurnoVeterinaria nuevoTurno = new TurnoVeterinaria();
    nuevoTurno.setMascota(mascota);
    nuevoTurno.setNombreVeterinaria(nombre);
    nuevoTurno.setDireccionVeterinaria(direccion);
    nuevoTurno.setFechaYHora(fecha);
    nuevoTurno.setMotivo(motivo);
    nuevoTurno.setEstado(EstadoTurno.PENDIENTE);

    turnoDao.guardar(nuevoTurno);

    // Integración de alerta automática por éxito
    String mensajeAlerta =
      "Turno reservado con éxito en " + nombre + " para el " + fecha.toString();
    TipoAlerta tipoAlerta = TipoAlerta.INFO;

    servicioAlerta.crearAlerta(mascota, tipoAlerta, mensajeAlerta);
  }

  @Override
  public List<TurnoVeterinaria> obtenerTurnosProximos(Long idMascota) {
    return turnoDao.buscarProximosPorMascota(idMascota, LocalDateTime.now());
  }

  @Override
  public List<TurnoVeterinaria> obtenerTurnosPasados(Long idMascota) {
    return turnoDao.buscarPasadosPorMascota(idMascota, LocalDateTime.now());
  }

  @Override
  public void cancelarTurno(Long idTurno) {
    TurnoVeterinaria turno = turnoDao.buscarPorId(idTurno);
    if (turno != null) {
      turno.setEstado(EstadoTurno.CANCELADO);
      turnoDao.modificar(turno);

      // Integración de alerta por cancelación
      String mensajeAlerta = "El turno en " + turno.getNombreVeterinaria() + " ha sido cancelado.";
      TipoAlerta tipoAlerta = TipoAlerta.INFO;
      Mascota mascota = turno.getMascota();
      servicioAlerta.crearAlerta(mascota, tipoAlerta, mensajeAlerta);
    }
  }

  @Override
  public List<RegistroHistorial> obtenerHistorialClinico(Long idMascota) {
    return historialDao.buscarPorMascota(idMascota);
  }
}
