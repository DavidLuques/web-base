package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.SolicitudTransferencia;
import java.util.List;

public interface ServicioTransferenciaMascota {
  SolicitudTransferencia iniciarTransferencia(Long idMascota, Long idOrigen, Long idDestino);
  SolicitudTransferencia confirmarPorOrigen(Long idSolicitud);
  SolicitudTransferencia confirmarPorDestino(Long idSolicitud);
  void cancelarTransferencia(Long idSolicitud);
  List<SolicitudTransferencia> obtenerPendientesPorUsuario(Long idUsuario);
  List<SolicitudTransferencia> obtenerHistorialPorUsuario(Long idUsuario);
}
