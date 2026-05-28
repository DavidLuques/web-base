package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorConfiguracion {

  private ServicioUsuario servicioUsuario;

  @Autowired
  public ControladorConfiguracion(ServicioUsuario servicioUsuario) {
    this.servicioUsuario = servicioUsuario;
  }

  @RequestMapping(path = "/configuraciones", method = RequestMethod.GET)
  public ModelAndView irAConfiguraciones(
    HttpServletRequest request,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }

    Usuario usuario = servicioUsuario.obtenerPerfil(idUsuario);
    ModelMap modelo = new ModelMap();
    modelo.put("usuario", usuario);
    modelo.put("idMascota", idMascota);
    return new ModelAndView("configuraciones", modelo);
  }
}
