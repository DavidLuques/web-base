package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dto.RangosVitalesDto;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import java.util.Map;

public interface OrquestadorService {
  ResultadoSimulacionDto procesarMascota(Long idMascota);

  void procesarTodasLasMascotas();

  ResultadoSimulacionDto obtenerUltimoEstado(Long idMascota);

  ResultadoSimulacionDto refrescarLectura(Long idMascota);

  void refrescarTodasLasLecturas();

  Map<String, Object> obtenerUltimaUbicacion(Long idMascota);

  RangosVitalesDto obtenerRangosVitales(Long idMascota);
}
