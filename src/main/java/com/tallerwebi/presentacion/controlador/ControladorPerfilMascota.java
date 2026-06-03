package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.servicio.ServicioMascota;
import com.tallerwebi.presentacion.DatosAltaMascota;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * / Controlador de la vista.
 */
@Controller
public class ControladorPerfilMascota {

  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String ATRIBUTO_DATOS_MASCOTA = "datosMascota";
  private static final String ATRIBUTO_ID_USUARIO = "ID_USUARIO";

  private ServicioMascota servicioMascota;

  @Autowired
  public ControladorPerfilMascota(ServicioMascota servicioMascota) {
    this.servicioMascota = servicioMascota;
  }

  @RequestMapping(path = "/configuraciones/mascota/perfil", method = RequestMethod.GET)
  public ModelAndView verPerfilMascota(
    HttpServletRequest request,
    @RequestParam(required = true) Long idMascota
  ) {
    return procesarVistaPerfilMascota(request, idMascota, "ver-perfil-mascota");
  }

  @RequestMapping(path = "/configuraciones/mascota/editar", method = RequestMethod.GET)
  public ModelAndView editarPerfilMascota(
    HttpServletRequest request,
    @RequestParam(required = true) Long idMascota
  ) {
    return procesarVistaPerfilMascota(request, idMascota, "perfil-mascota");
  }

  private ModelAndView procesarVistaPerfilMascota(
    HttpServletRequest request,
    Long idMascota,
    String vista
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    DatosAltaMascota datosMascota = servicioMascota.obtenerDatosMascota(idMascota);
    if (datosMascota == null) {
      return new ModelAndView("redirect:/configuraciones");
    }

    ModelMap modelo = new ModelMap();
    modelo.put(ATRIBUTO_DATOS_MASCOTA, datosMascota);
    modelo.put("idMascota", idMascota);
    modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    return new ModelAndView(vista, modelo);
  }

  @RequestMapping(path = "/configuraciones/mascota/actualizar", method = RequestMethod.POST)
  public ModelAndView actualizarPerfilMascota(
    @ModelAttribute(ATRIBUTO_DATOS_MASCOTA) DatosAltaMascota datosMascota,
    HttpServletRequest request,
    @RequestParam(required = true) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioMascota.actualizarMascota(idMascota, datosMascota);
    return new ModelAndView(
      "redirect:/configuraciones/mascota/perfil?exito=true&idMascota=" + idMascota
    );
  }

  @RequestMapping(path = "/configuraciones/mascota/eliminar", method = RequestMethod.POST)
  public ModelAndView eliminarMascota(
    HttpServletRequest request,
    @RequestParam(required = true) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioMascota.eliminarMascota(idMascota);
    return new ModelAndView("redirect:/configuraciones");
  }
}
