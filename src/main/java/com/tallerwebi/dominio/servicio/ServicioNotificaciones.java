package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.Alerta;

/**
 * Interfaz de servicio para notificaciones de emergencias.
 */
public interface ServicioNotificaciones {
  void enviarNotificacionEmergencia(Alerta alerta);
}
