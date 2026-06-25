package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
