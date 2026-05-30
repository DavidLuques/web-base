package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;

public interface OrquestadorService {
  ResultadoSimulacionDto procesarMascota(Long idMascota);

  void procesarTodasLasMascotas();

  ResultadoSimulacionDto obtenerUltimoEstado(Long idMascota);
}
