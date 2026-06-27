package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.presentacion.DatosPerfil;

/**
 * Servicio de lógica de negocio.
 */
public interface ServicioUsuario {
  Usuario obtenerPerfil(Long id);
  void eliminar(Long id);
  void actualizarPerfil(Long id, DatosPerfil datosPerfil);
  DatosPerfil obtenerDatosPerfil(Long id);
  void eliminarCuenta(Long id);
}
