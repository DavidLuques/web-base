package com.tallerwebi.dominio.servicio;

import com.tallerwebi.presentacion.DatosPerfil;
import org.springframework.ui.ModelMap;

/**
 * Servicio que encapsula la preparación del modelo para las vistas de perfil.
 */
public interface ServicioPerfil {
  ModelMap prepararVerPerfil(Long idUsuario, Long idMascota);
  ModelMap prepararEditarPerfil(Long idUsuario, Long idMascota);
  void actualizarPerfil(Long idUsuario, DatosPerfil datosPerfil);
  ModelMap prepararModeloErrorActualizar(
    Long idUsuario,
    DatosPerfil datosPerfil,
    Long idMascota,
    String mensajeError
  );
}
