package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.excepcion.AccionNoPermitidaEnEsteEstadoException;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.modelo.SolicitudAmistad;
import com.tallerwebi.dominio.modelo.Usuario;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorAmistad {

  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_AMIGOS_EXITO = "redirect:/amigos?exito=true";
  private static final String REDIRECT_AMIGOS_ERROR = "redirect:/amigos?error=";
  private static final String PARAMETRO_ID_MASCOTA = "&idMascota=";
  private static final String ATRIBUTO_ID_USUARIO = "ID_USUARIO";

  private final ServicioAmistad servicioAmistad;
  private final ServicioMascota servicioMascota;

  @Autowired
  public ControladorAmistad(ServicioAmistad servicioAmistad, ServicioMascota servicioMascota) {
    this.servicioAmistad = servicioAmistad;
    this.servicioMascota = servicioMascota;
  }

  private String armarSufijoMascota(Long idMascota) {
    return idMascota != null ? PARAMETRO_ID_MASCOTA + idMascota : "";
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
    modelo.put("solicitudesEnviadas", servicioAmistad.obtenerSolicitudesEnviadas(idUsuario));
    return new ModelAndView("amigos", modelo);
  }

  @RequestMapping(path = "/amigos/estado", method = RequestMethod.GET)
  @ResponseBody
  public java.util.Map<String, Object> estadoAmigos(HttpServletRequest request) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    java.util.Map<String, Object> respuesta = new java.util.HashMap<>();
    if (idUsuario == null) {
      respuesta.put("error", "no-session");
      return respuesta;
    }

    List<Usuario> amigos = servicioAmistad.obtenerAmigos(idUsuario);
    List<SolicitudAmistad> pendientes = servicioAmistad.obtenerSolicitudesPendientes(idUsuario);
    List<SolicitudAmistad> enviadas = servicioAmistad.obtenerSolicitudesEnviadas(idUsuario);

    String hashAmigos = amigos != null
      ? amigos
        .stream()
        .map(a -> String.valueOf(a.getId()))
        .sorted()
        .collect(java.util.stream.Collectors.joining(","))
      : "";
    String hashPendientes = pendientes != null
      ? pendientes
        .stream()
        .map(s -> String.valueOf(s.getId()))
        .sorted()
        .collect(java.util.stream.Collectors.joining(","))
      : "";
    String hashEnviadas = enviadas != null
      ? enviadas
        .stream()
        .map(s -> String.valueOf(s.getId()))
        .sorted()
        .collect(java.util.stream.Collectors.joining(","))
      : "";

    respuesta.put("hash", hashAmigos + "|" + hashPendientes + "|" + hashEnviadas);
    return respuesta;
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

    String sufijoMascota = armarSufijoMascota(idMascota);
    try {
      servicioAmistad.enviarSolicitudPorEmail(idUsuario, emailReceptor);
      return new ModelAndView(REDIRECT_AMIGOS_EXITO + sufijoMascota);
    } catch (AccionNoPermitidaEnEsteEstadoException | UsuarioNoEncontrado e) {
      return new ModelAndView(REDIRECT_AMIGOS_ERROR + e.getMessage() + sufijoMascota);
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
    return new ModelAndView(REDIRECT_AMIGOS_EXITO + armarSufijoMascota(idMascota));
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
    return new ModelAndView(REDIRECT_AMIGOS_EXITO + armarSufijoMascota(idMascota));
  }

  @RequestMapping(path = "/amigos/cancelar-solicitud", method = RequestMethod.POST)
  public ModelAndView cancelarSolicitud(
    HttpServletRequest request,
    @RequestParam Long idSolicitud,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    String sufijoMascota = armarSufijoMascota(idMascota);
    try {
      servicioAmistad.cancelarSolicitud(idSolicitud);
      return new ModelAndView(REDIRECT_AMIGOS_EXITO + sufijoMascota);
    } catch (AccionNoPermitidaEnEsteEstadoException e) {
      return new ModelAndView(REDIRECT_AMIGOS_ERROR + e.getMessage() + sufijoMascota);
    }
  }

  @RequestMapping(path = "/amigos/eliminar", method = RequestMethod.POST)
  public ModelAndView eliminarAmigo(
    HttpServletRequest request,
    @RequestParam Long idAmigo,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    String sufijoMascota = armarSufijoMascota(idMascota);
    try {
      servicioAmistad.eliminarAmigo(idUsuario, idAmigo);
      return new ModelAndView(REDIRECT_AMIGOS_EXITO + sufijoMascota);
    } catch (AccionNoPermitidaEnEsteEstadoException e) {
      return new ModelAndView(REDIRECT_AMIGOS_ERROR + e.getMessage() + sufijoMascota);
    }
  }
}
