package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RegistroHistorialDao;
import com.tallerwebi.dominio.dao.TurnoVeterinariaDao;
import com.tallerwebi.dominio.enums.EstadoTurno;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.excepcion.ExcepcionTurnoInvalido;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RegistroHistorial;
import com.tallerwebi.dominio.modelo.TurnoVeterinaria;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
  private static final DateTimeFormatter FORMATTER_HORA = DateTimeFormatter.ofPattern("HH:mm");

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
    String tipoTurno,
    String motivo
  ) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);

    // Centralizamos las validaciones en métodos privados
    validarMascota(mascota);
    validarDisponibilidadDeTurno(idMascota, nombre, fecha);

    TurnoVeterinaria nuevoTurno = new TurnoVeterinaria();
    nuevoTurno.setMascota(mascota);
    nuevoTurno.setNombreVeterinaria(nombre);
    nuevoTurno.setDireccionVeterinaria(direccion);
    nuevoTurno.setFechaYHora(fecha);
    nuevoTurno.setTipoTurno(tipoTurno);
    nuevoTurno.setMotivo(motivo);
    nuevoTurno.setEstado(EstadoTurno.PENDIENTE);

    turnoDao.guardar(nuevoTurno);

    String mensajeAlerta =
      "Turno reservado con éxito en " + nombre + " para el " + fecha.toString();
    servicioAlerta.crearAlertaUsuario(mascota.getUsuario(), TipoAlerta.INFO, mensajeAlerta);
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
      servicioAlerta.crearAlertaUsuario(mascota.getUsuario(), tipoAlerta, mensajeAlerta);
    }
  }

  @Override
  public List<RegistroHistorial> obtenerHistorialClinico(Long idMascota) {
    return historialDao.buscarPorMascota(idMascota);
  }

  @Override
  public List<String> obtenerHorariosOcupados(String veterinaria, LocalDate fecha) {
    // Buscamos las fechas ocupadas en la base de datos
    List<LocalDateTime> fechasOcupadas = turnoDao.obtenerFechasOcupadasEnVeterinaria(
      veterinaria,
      fecha
    );

    List<String> horariosOcupados = new ArrayList<>();

    // Convertimos cada LocalDateTime a texto usando la constante global
    for (LocalDateTime dt : fechasOcupadas) {
      horariosOcupados.add(dt.format(FORMATTER_HORA));
    }

    return horariosOcupados;
  }

  // --- MÉTODOS PRIVADOS DE VALIDACIÓN ---

  private void validarMascota(Mascota mascota) {
    if (mascota == null) {
      throw new IllegalArgumentException("La mascota no existe");
    }
  }

  private void validarDisponibilidadDeTurno(Long idMascota, String nombre, LocalDateTime fecha) {
    int minutos = fecha.getMinute();
    if (minutos != 0 && minutos != 30) {
      throw new ExcepcionTurnoInvalido(
        "Los turnos deben solicitarse en intervalos de 30 minutos exactos."
      );
    }

    if (turnoDao.existeTurnoParaMascotaEnFecha(idMascota, fecha)) {
      throw new ExcepcionTurnoInvalido(
        "Ya tenés un turno agendado para esta mascota en ese horario."
      );
    }

    if (turnoDao.existeTurnoEnVeterinariaEnFecha(nombre, fecha)) {
      throw new ExcepcionTurnoInvalido(
        "Este horario ya fue reservado en esta veterinaria. Por favor, elegí otro."
      );
    }
  }
}
