package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Usuario;

public interface ServicioUsuario {
  Usuario obtenerPerfil(Long id);
  void eliminar(Long id);
  void actualizarPerfil(Long id, com.tallerwebi.presentacion.DatosPerfil datosPerfil);
}
