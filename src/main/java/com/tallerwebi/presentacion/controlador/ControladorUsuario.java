package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfil/usuario")
public class ControladorUsuario {

  private ServicioUsuario servicioUsuario;

  @Autowired
  public ControladorUsuario(ServicioUsuario servicioUsuario) {
    this.servicioUsuario = servicioUsuario;
  }

  // Al entrar a /api/usuarios/1, se ejecuta esto
  @GetMapping("/{id}")
  public Usuario verPerfil(@PathVariable Long id) {
    return servicioUsuario.obtenerPerfil(id);
  }
}
