package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.TurnoVeterinaria;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TurnoVeterinariaDao {
  void guardar(TurnoVeterinaria turno);

  void modificar(TurnoVeterinaria turno);

  TurnoVeterinaria buscarPorId(Long id);

  List<TurnoVeterinaria> buscarProximosPorMascota(Long idMascota, LocalDateTime fechaActual);

  List<TurnoVeterinaria> buscarPasadosPorMascota(Long idMascota, LocalDateTime fechaActual);

  boolean existeTurnoParaMascotaEnFecha(Long idMascota, LocalDateTime fechaYHora);

  boolean existeTurnoEnVeterinariaEnFecha(String nombreVeterinaria, LocalDateTime fechaYHora);
  List<LocalDateTime> obtenerFechasOcupadasEnVeterinaria(String nombreVeterinaria, LocalDate fecha);
}
