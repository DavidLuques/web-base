package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.RegistroHistorial;
import com.tallerwebi.dominio.modelo.TurnoVeterinaria;
import java.time.LocalDateTime;
import java.util.List;

public interface ServicioVeterinaria {
  void agendarTurno(
    Long idMascota,
    String nombre,
    String direccion,
    LocalDateTime fecha,
    String motivo
  );
  List<TurnoVeterinaria> obtenerTurnosProximos(Long idMascota);
  List<TurnoVeterinaria> obtenerTurnosPasados(Long idMascota);
  void cancelarTurno(Long idTurno);
  List<RegistroHistorial> obtenerHistorialClinico(Long idMascota);
}
