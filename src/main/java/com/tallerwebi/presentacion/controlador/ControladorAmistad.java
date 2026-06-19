package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AccionNoPermitidaEnEsteEstadoException;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.modelo.SolicitudAmistad;
import com.tallerwebi.dominio.servicio.ServicioAmistad;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorAmistad {

  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String ATRIBUTO_ID_USUARIO = "ID_USUARIO";

  private final ServicioAmistad servicioAmistad;
  private final ServicioMascota servicioMascota;

  @Autowired
  public ControladorAmistad(ServicioAmistad servicioAmistad, ServicioMascota servicioMascota) {
    this.servicioAmistad = servicioAmistad;
    this.servicioMascota = servicioMascota;
  }

  @RequestMapping(path = "/amigos", method = RequestMethod.GET)
  public ModelAndView verAmigos(
    HttpServletRequest request,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    List<Usuario> amigos = servicioAmistad.obtenerAmigos(idUsuario);
    List<SolicitudAmistad> pendientes = servicioAmistad.obtenerSolicitudesPendientes(idUsuario);

    ModelMap modelo = new ModelMap();
    modelo.put("amigos", amigos);
    modelo.put("solicitudesPendientes", pendientes);
    modelo.put("idMascota", idMascota);
    modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    return new ModelAndView("amigos", modelo);
  }

  @RequestMapping(path = "/amigos/enviar-solicitud", method = RequestMethod.POST)
  public ModelAndView enviarSolicitud(
    HttpServletRequest request,
    @RequestParam String emailReceptor,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    String sufijoMascota = idMascota != null ? "&idMascota=" + idMascota : "";

    try {
      servicioAmistad.enviarSolicitudPorEmail(idUsuario, emailReceptor);
      return new ModelAndView("redirect:/amigos?exito=true" + sufijoMascota);
    } catch (AccionNoPermitidaEnEsteEstadoException | UsuarioNoEncontrado e) {
      return new ModelAndView("redirect:/amigos?error=" + e.getMessage() + sufijoMascota);
    }
  }

  @RequestMapping(path = "/amigos/aceptar", method = RequestMethod.POST)
  public ModelAndView aceptarSolicitud(
    HttpServletRequest request,
    @RequestParam Long idSolicitud,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioAmistad.aceptarSolicitud(idSolicitud);
    String sufijoMascota = idMascota != null ? "&idMascota=" + idMascota : "";
    return new ModelAndView("redirect:/amigos?exito=true" + sufijoMascota);
  }

  @RequestMapping(path = "/amigos/rechazar", method = RequestMethod.POST)
  public ModelAndView rechazarSolicitud(
    HttpServletRequest request,
    @RequestParam Long idSolicitud,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioAmistad.rechazarSolicitud(idSolicitud);
    String sufijoMascota = idMascota != null ? "&idMascota=" + idMascota : "";
    return new ModelAndView("redirect:/amigos?exito=true" + sufijoMascota);
  }
}
