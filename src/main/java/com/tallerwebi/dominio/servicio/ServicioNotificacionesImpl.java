package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementación de servicio para notificaciones de emergencias.
 */
@Service
public class ServicioNotificacionesImpl implements ServicioNotificaciones {

  private static final Logger logger = Logger.getLogger(ServicioNotificacionesImpl.class.getName());
  private static final String REMITENTE = "pettrackertw1@gmail.com";
  private final JavaMailSender mailSender;

  public ServicioNotificacionesImpl(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public void enviarNotificacionEmergencia(Alerta alerta) {
    if (alerta == null || !TipoAlerta.EMERGENCIA.equals(alerta.getTipo())) {
      return;
    }
    try {
      enviarEmailEmergencia(alerta);
    } catch (Exception e) {
      logger.log(Level.WARNING, "Error al enviar notificacion de emergencia", e);
    }
  }

  private void enviarEmailEmergencia(Alerta alerta) {
    String correoUsuario = obtenerCorreoUsuario(alerta);
    if (correoUsuario == null || correoUsuario.isEmpty()) {
      logger.log(Level.WARNING, "No se pudo obtener el correo del usuario para la alerta");
      return;
    }

    SimpleMailMessage mensaje = new SimpleMailMessage();
    mensaje.setFrom(REMITENTE);
    mensaje.setTo(correoUsuario);
    mensaje.setSubject("EMERGENCIA: Alerta de " + alerta.getMascota().getNombre());
    mensaje.setText(
      "ALERTA DE EMERGENCIA\n\n" +
      "Mascota: " +
      alerta.getMascota().getNombre() +
      "\n" +
      "Tipo: " +
      alerta.getTipo().name() +
      "\n" +
      "Mensaje: " +
      alerta.getMensaje() +
      "\n" +
      "Fecha: " +
      alerta.getFechaYHora() +
      "\n\n" +
      "Por favor, revisa el sistema inmediatamente."
    );

    mailSender.send(mensaje);
  }

  private String obtenerCorreoUsuario(Alerta alerta) {
    if (alerta.getMascota() == null || alerta.getMascota().getUsuario() == null) {
      return null;
    }
    return alerta.getMascota().getUsuario().getEmail();
  }
}
