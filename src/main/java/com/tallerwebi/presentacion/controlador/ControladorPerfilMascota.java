package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.servicio.ServicioMascota;
import com.tallerwebi.presentacion.DatosAltaMascota;
import java.util.List;
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
  private static final String VISTA_PERFIL_MASCOTA = "perfil-mascota";
  private static final String ATRIBUTO_ID_MASCOTA = "idMascota";
  private static final String ATRIBUTO_MIS_MASCOTAS = "misMascotas";
  private static final String ATRIBUTO_ERROR = "error";
  private static final int MIN_ANIO_NACIMIENTO = 1900;

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
    return procesarVistaPerfilMascota(request, idMascota, VISTA_PERFIL_MASCOTA);
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
    modelo.put(ATRIBUTO_ID_MASCOTA, idMascota);
    modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
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

    if (datosMascota.getPeso() == null || datosMascota.getPeso() <= 0) {
      ModelMap modelo = new ModelMap();
      modelo.put(ATRIBUTO_DATOS_MASCOTA, datosMascota);
      modelo.put(ATRIBUTO_ID_MASCOTA, idMascota);
      modelo.put(ATRIBUTO_ERROR, "El peso debe ser mayor a 0.");
      modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView(VISTA_PERFIL_MASCOTA, modelo);
    }

    String fechaStr = datosMascota.getFechaNacimiento();
    if (fechaStr == null || fechaStr.trim().isEmpty()) {
      ModelMap modelo = new ModelMap();
      modelo.put(ATRIBUTO_DATOS_MASCOTA, datosMascota);
      modelo.put(ATRIBUTO_ID_MASCOTA, idMascota);
      modelo.put(ATRIBUTO_ERROR, "La fecha de nacimiento es obligatoria.");
      modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView(VISTA_PERFIL_MASCOTA, modelo);
    }

    try {
      java.time.LocalDate fecha = java.time.LocalDate.parse(fechaStr);
      if (fecha.getYear() <= MIN_ANIO_NACIMIENTO) {
        ModelMap modelo = new ModelMap();
        modelo.put(ATRIBUTO_DATOS_MASCOTA, datosMascota);
        modelo.put(ATRIBUTO_ID_MASCOTA, idMascota);
        modelo.put(ATRIBUTO_ERROR, "El año de nacimiento debe ser mayor a 1900.");
        modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
        return new ModelAndView(VISTA_PERFIL_MASCOTA, modelo);
      }
    } catch (java.time.format.DateTimeParseException e) {
      ModelMap modelo = new ModelMap();
      modelo.put(ATRIBUTO_DATOS_MASCOTA, datosMascota);
      modelo.put(ATRIBUTO_ID_MASCOTA, idMascota);
      modelo.put(ATRIBUTO_ERROR, "La fecha ingresada no es válida.");
      modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView(VISTA_PERFIL_MASCOTA, modelo);
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
    if (idUsuario == null) return new ModelAndView(REDIRECT_LOGIN);

    servicioMascota.eliminarMascota(idMascota);

    List<com.tallerwebi.dominio.modelo.Mascota> restantes =
      servicioMascota.obtenerMascotasPorUsuario(idUsuario);

    if (restantes == null || restantes.isEmpty()) {
      return new ModelAndView("redirect:/sin-mascota");
    }
    return new ModelAndView("redirect:/analisis/dashboard/" + restantes.get(0).getId());
  }
}
