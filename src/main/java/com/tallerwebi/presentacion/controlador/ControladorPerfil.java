package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import com.tallerwebi.presentacion.DatosPerfil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPerfil {

  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String ATRIBUTO_DATOS_PERFIL = "datosPerfil";

  private ServicioUsuario servicioUsuario;
  private ServicioMascota servicioMascota;

  @Autowired
  public ControladorPerfil(ServicioUsuario servicioUsuario, ServicioMascota servicioMascota) {
    this.servicioUsuario = servicioUsuario;
    this.servicioMascota = servicioMascota;
  }

  @RequestMapping(path = "/perfil", method = RequestMethod.GET)
  public ModelAndView verPerfil(
    HttpServletRequest request,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      DatosPerfil datosPerfil = servicioUsuario.obtenerDatosPerfil(idUsuario);
      ModelMap modelo = new ModelMap();
      modelo.put(ATRIBUTO_DATOS_PERFIL, datosPerfil);
      modelo.put("idMascota", idMascota);
      modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView("ver-perfil", modelo);
    } catch (UsuarioNoEncontrado e) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
  }

  @RequestMapping(path = "/perfil/editar", method = RequestMethod.GET)
  public ModelAndView editarPerfil(
    HttpServletRequest request,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      DatosPerfil datosPerfil = servicioUsuario.obtenerDatosPerfil(idUsuario);
      ModelMap modelo = new ModelMap();
      modelo.put(ATRIBUTO_DATOS_PERFIL, datosPerfil);
      modelo.put("idMascota", idMascota);
      modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView("perfil", modelo);
    } catch (UsuarioNoEncontrado e) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
  }

  @RequestMapping(path = "/perfil/actualizar", method = RequestMethod.POST)
  public ModelAndView actualizarPerfil(
    @ModelAttribute(ATRIBUTO_DATOS_PERFIL) DatosPerfil datosPerfil,
    HttpServletRequest request,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioUsuario.actualizarPerfil(idUsuario, datosPerfil);
      String redirectUrl = idMascota != null
        ? "redirect:/perfil?exito=true&idMascota=" + idMascota
        : "redirect:/perfil?exito=true";
      return new ModelAndView(redirectUrl);
    } catch (UsuarioNoEncontrado e) {
      ModelMap modelo = new ModelMap();
      modelo.put(ATRIBUTO_DATOS_PERFIL, datosPerfil);
      modelo.put("error", e.getMessage());
      modelo.put("idMascota", idMascota);
      modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView("perfil", modelo);
    }
  }
}
