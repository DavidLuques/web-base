package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.modelo.SolicitudAmistad;
import java.util.List;

public interface ServicioAmistad {
  SolicitudAmistad enviarSolicitud(Long idEmisor, Long idReceptor);
  void aceptarSolicitud(Long idSolicitud);
  void rechazarSolicitud(Long idSolicitud);
  boolean sonAmigos(Long idUsuario1, Long idUsuario2);
  List<Usuario> obtenerAmigos(Long idUsuario);
  List<SolicitudAmistad> obtenerSolicitudesPendientes(Long idUsuario);
  SolicitudAmistad enviarSolicitudPorEmail(Long idEmisor, String emailReceptor);
  List<SolicitudAmistad> obtenerSolicitudesEnviadas(Long idUsuario);
  void cancelarSolicitud(Long idSolicitud);
  void eliminarAmigo(Long idUsuario, Long idAmigo);
}
