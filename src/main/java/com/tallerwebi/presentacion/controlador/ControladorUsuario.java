package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
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
}
