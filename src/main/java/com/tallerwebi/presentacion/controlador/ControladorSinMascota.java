package com.tallerwebi.presentacion.controlador;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorSinMascota {

  @GetMapping("/sin-mascota")
  public String sinMascota(Model model, HttpServletRequest request) {
    return "sin-mascota";
  }
}
