package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * / Controlador de la vista.
 */
@RestController
@RequestMapping("/perfil/usuario")
public class ControladorUsuario {

  private ServicioUsuario servicioUsuario;

  @Autowired
  public ControladorUsuario(ServicioUsuario servicioUsuario) {
    this.servicioUsuario = servicioUsuario;
  }

  @GetMapping("/{id}")
  public Usuario verPerfil(@PathVariable Long id) {
    return servicioUsuario.obtenerPerfil(id);
  }

  @DeleteMapping("/{id}")
  public String eliminarCuenta(@PathVariable Long id) {
    servicioUsuario.eliminar(id);
    return "La cuenta ha sido desactivada exitosamente";
  }

  @PutMapping("/notificaciones-mail")
  public ResponseEntity<Void> toggleNotificacionesMail(HttpServletRequest request) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return ResponseEntity.status(401).build();
    }
    servicioUsuario.toggleNotificacionesMail(idUsuario);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/notificaciones-mail")
  public ResponseEntity<Boolean> getNotificacionesMail(HttpServletRequest request) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return ResponseEntity.status(401).build();
    }
    Usuario usuario = servicioUsuario.obtenerPerfil(idUsuario);
    return ResponseEntity.ok(usuario.getNotificacionesMailActivas());
  }
}
