package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.SolicitudTransferencia;
import java.util.List;

public interface SolicitudTransferenciaDao {
  void guardar(SolicitudTransferencia solicitud);
  void modificar(SolicitudTransferencia solicitud);
  SolicitudTransferencia buscarPorId(Long id);
  List<SolicitudTransferencia> buscarPendientesPorUsuario(Long idUsuario);
}
