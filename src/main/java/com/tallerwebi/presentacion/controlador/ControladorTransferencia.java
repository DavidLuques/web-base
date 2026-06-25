package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.enums.EstadoTransferencia;
import com.tallerwebi.dominio.excepcion.AccionNoPermitidaEnEsteEstadoException;
import com.tallerwebi.dominio.excepcion.NoSonAmigosException;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.SolicitudTransferencia;
import com.tallerwebi.dominio.servicio.ServicioAmistad;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import com.tallerwebi.dominio.servicio.ServicioTransferenciaMascota;
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
public class ControladorTransferencia {

  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_SIN_MASCOTA = "redirect:/sin-mascota";
  private static final String REDIRECT_TRANSFERENCIAS_EXITO = "redirect:/transferencias?exito=true";
  private static final String REDIRECT_TRANSFERENCIAS_ERROR = "redirect:/transferencias?error=";
  private static final String REDIRECT_DASHBOARD = "redirect:/analisis/dashboard/";
  private static final String PARAMETRO_ID_MASCOTA = "&idMascota=";
  private static final String ATRIBUTO_ID_USUARIO = "ID_USUARIO";

  private final ServicioTransferenciaMascota servicioTransferenciaMascota;
  private final ServicioMascota servicioMascota;
  private final ServicioAmistad servicioAmistad;

  @Autowired
  public ControladorTransferencia(
    ServicioTransferenciaMascota servicioTransferenciaMascota,
    ServicioMascota servicioMascota,
    ServicioAmistad servicioAmistad
  ) {
    this.servicioTransferenciaMascota = servicioTransferenciaMascota;
    this.servicioMascota = servicioMascota;
    this.servicioAmistad = servicioAmistad;
  }

  private String armarSufijoMascota(Long idMascota) {
    return idMascota != null ? PARAMETRO_ID_MASCOTA + idMascota : "";
  }

  private boolean elUsuarioEraElOrigenDeTransferenciaCompletada(
    SolicitudTransferencia solicitud,
    Long idUsuario
  ) {
    return (
      solicitud.getEstado() == EstadoTransferencia.COMPLETADA &&
      solicitud.getUsuarioOrigen() != null &&
      solicitud.getUsuarioOrigen().getId().equals(idUsuario)
    );
  }

  private boolean elUsuarioEsElDestinoDeTransferenciaCompletada(
    SolicitudTransferencia solicitud,
    Long idUsuario
  ) {
    return (
      solicitud.getEstado() == EstadoTransferencia.COMPLETADA &&
      solicitud.getUsuarioDestino() != null &&
      solicitud.getUsuarioDestino().getId().equals(idUsuario)
    );
  }

  // Devuelve el redirect correcto para el origen tras completarse la transferencia,
  // o null si el usuario logueado no era el origen.
  private ModelAndView redirigirTrasCompletarSiEraOrigen(
    SolicitudTransferencia solicitud,
    Long idUsuario
  ) {
    if (!elUsuarioEraElOrigenDeTransferenciaCompletada(solicitud, idUsuario)) {
      return null;
    }
    List<Mascota> restantes = servicioMascota.obtenerMascotasPorUsuario(idUsuario);
    if (restantes == null || restantes.isEmpty()) {
      return new ModelAndView(REDIRECT_SIN_MASCOTA);
    }
    return new ModelAndView(REDIRECT_DASHBOARD + restantes.get(0).getId());
  }

  @RequestMapping(path = "/transferencias", method = RequestMethod.GET)
  public ModelAndView verTransferencias(
    HttpServletRequest request,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    List<Mascota> misMascotas = servicioMascota.obtenerMascotasPorUsuario(idUsuario);
    boolean tieneMascotas = misMascotas != null && !misMascotas.isEmpty();

    if (tieneMascotas && idMascota == null) {
      return new ModelAndView(REDIRECT_DASHBOARD + misMascotas.get(0).getId());
    }

    List<SolicitudTransferencia> pendientes =
      servicioTransferenciaMascota.obtenerPendientesPorUsuario(idUsuario);
    boolean tienePendientes = pendientes != null && !pendientes.isEmpty();

    if (!tieneMascotas && !tienePendientes) {
      return new ModelAndView(REDIRECT_SIN_MASCOTA);
    }

    ModelMap modelo = new ModelMap();
    modelo.put("transferenciasPendientes", pendientes);
    modelo.put("amigos", servicioAmistad.obtenerAmigos(idUsuario));
    modelo.put("idUsuarioActual", idUsuario);
    modelo.put("idMascota", idMascota);
    modelo.put("misMascotas", misMascotas);
    return new ModelAndView("transferencias", modelo);
  }

  @RequestMapping(path = "/transferencias/iniciar", method = RequestMethod.POST)
  public ModelAndView iniciarTransferencia(
    HttpServletRequest request,
    @RequestParam Long idMascota,
    @RequestParam Long idDestino
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioTransferenciaMascota.iniciarTransferencia(idMascota, idUsuario, idDestino);
      return new ModelAndView(REDIRECT_TRANSFERENCIAS_EXITO + armarSufijoMascota(idMascota));
    } catch (NoSonAmigosException e) {
      return new ModelAndView(
        REDIRECT_TRANSFERENCIAS_ERROR + e.getMessage() + armarSufijoMascota(idMascota)
      );
    }
  }

  @RequestMapping(path = "/transferencias/confirmar-origen", method = RequestMethod.POST)
  public ModelAndView confirmarPorOrigen(
    HttpServletRequest request,
    @RequestParam Long idSolicitud,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      SolicitudTransferencia solicitud = servicioTransferenciaMascota.confirmarPorOrigen(
        idSolicitud
      );

      if (elUsuarioEsElDestinoDeTransferenciaCompletada(solicitud, idUsuario)) {
        return new ModelAndView(REDIRECT_DASHBOARD + solicitud.getMascota().getId());
      }
      ModelAndView mavOrigen = redirigirTrasCompletarSiEraOrigen(solicitud, idUsuario);
      if (mavOrigen != null) {
        return mavOrigen;
      }
      return new ModelAndView(REDIRECT_TRANSFERENCIAS_EXITO + armarSufijoMascota(idMascota));
    } catch (AccionNoPermitidaEnEsteEstadoException e) {
      return new ModelAndView(
        REDIRECT_TRANSFERENCIAS_ERROR + e.getMessage() + armarSufijoMascota(idMascota)
      );
    }
  }

  @RequestMapping(path = "/transferencias/confirmar-destino", method = RequestMethod.POST)
  public ModelAndView confirmarPorDestino(
    HttpServletRequest request,
    @RequestParam Long idSolicitud,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      SolicitudTransferencia solicitud = servicioTransferenciaMascota.confirmarPorDestino(
        idSolicitud
      );

      if (elUsuarioEsElDestinoDeTransferenciaCompletada(solicitud, idUsuario)) {
        return new ModelAndView(REDIRECT_DASHBOARD + solicitud.getMascota().getId());
      }
      ModelAndView mavOrigen = redirigirTrasCompletarSiEraOrigen(solicitud, idUsuario);
      if (mavOrigen != null) {
        return mavOrigen;
      }
      return new ModelAndView(REDIRECT_TRANSFERENCIAS_EXITO + armarSufijoMascota(idMascota));
    } catch (AccionNoPermitidaEnEsteEstadoException e) {
      return new ModelAndView(
        REDIRECT_TRANSFERENCIAS_ERROR + e.getMessage() + armarSufijoMascota(idMascota)
      );
    }
  }

  @RequestMapping(path = "/transferencias/estado", method = RequestMethod.GET)
  @org.springframework.web.bind.annotation.ResponseBody
  public java.util.Map<String, Object> estadoTransferencias(HttpServletRequest request) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    java.util.Map<String, Object> respuesta = new java.util.HashMap<>();
    if (idUsuario == null) {
      respuesta.put("error", "no-session");
      return respuesta;
    }
    List<SolicitudTransferencia> pendientes =
      servicioTransferenciaMascota.obtenerPendientesPorUsuario(idUsuario);
    List<Mascota> mascotas = servicioMascota.obtenerMascotasPorUsuario(idUsuario);

    String hashPendientes = pendientes != null
      ? pendientes
        .stream()
        .map(s -> s.getId() + ":" + s.getEstado())
        .sorted()
        .collect(java.util.stream.Collectors.joining(","))
      : "";
    String hashMascotas = mascotas != null
      ? mascotas
        .stream()
        .map(m -> String.valueOf(m.getId()))
        .sorted()
        .collect(java.util.stream.Collectors.joining(","))
      : "";

    respuesta.put("hash", hashPendientes + "|" + hashMascotas);
    return respuesta;
  }

  @RequestMapping(path = "/transferencias/cancelar", method = RequestMethod.POST)
  public ModelAndView cancelarTransferencia(
    HttpServletRequest request,
    @RequestParam Long idSolicitud,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute(ATRIBUTO_ID_USUARIO);
    if (idUsuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioTransferenciaMascota.cancelarTransferencia(idSolicitud);
    return new ModelAndView(REDIRECT_TRANSFERENCIAS_EXITO + armarSufijoMascota(idMascota));
  }
}
