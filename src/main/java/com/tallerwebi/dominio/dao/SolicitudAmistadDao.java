package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.SolicitudAmistad;
import java.util.List;

public interface SolicitudAmistadDao {
  void guardar(SolicitudAmistad solicitud);
  void modificar(SolicitudAmistad solicitud);
  SolicitudAmistad buscarPorId(Long id);
  List<SolicitudAmistad> buscarPendientesPorReceptor(Long idUsuario);
  List<SolicitudAmistad> buscarAceptadasPorUsuario(Long idUsuario);
  SolicitudAmistad buscarEntreUsuarios(Long idUsuario1, Long idUsuario2);
}
