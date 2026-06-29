package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
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
public class ControladorConfiguracion {

  private static final String ATRIBUTO_MIS_MASCOTAS = "misMascotas";
  private static final String ATRIBUTO_DATOS_MASCOTA = "datosMascota";
  private static final String VISTA_NUEVA_MASCOTA = "nueva-mascota";
  private static final String ATRIBUTO_ERROR = "error";
  private static final int MIN_ANIO_NACIMIENTO = 1900;

  private ServicioUsuario servicioUsuario;
  private ServicioMascota servicioMascota;

  @Autowired
  public ControladorConfiguracion(
    ServicioUsuario servicioUsuario,
    ServicioMascota servicioMascota
  ) {
    this.servicioUsuario = servicioUsuario;
    this.servicioMascota = servicioMascota;
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
    modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    return new ModelAndView("configuraciones", modelo);
  }

  @RequestMapping(path = "/configuraciones/mascota/nueva", method = RequestMethod.GET)
  public ModelAndView irAAltaMascota(
    HttpServletRequest request,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelMap modelo = new ModelMap();
    modelo.put(ATRIBUTO_DATOS_MASCOTA, new DatosAltaMascota());
    modelo.put("idMascota", idMascota);
    modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    return new ModelAndView(VISTA_NUEVA_MASCOTA, modelo);
  }

  @RequestMapping(path = "/configuraciones/mascota/nueva", method = RequestMethod.POST)
  public ModelAndView registrarMascota(
    @ModelAttribute(ATRIBUTO_DATOS_MASCOTA) DatosAltaMascota datosMascota,
    HttpServletRequest request
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }

    if (datosMascota.getPeso() == null || datosMascota.getPeso() <= 0) {
      ModelMap modelo = new ModelMap();
      modelo.put(ATRIBUTO_DATOS_MASCOTA, datosMascota);
      modelo.put(ATRIBUTO_ERROR, "El peso debe ser mayor a 0.");
      modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView(VISTA_NUEVA_MASCOTA, modelo);
    }

    String fechaStr = datosMascota.getFechaNacimiento();
    if (fechaStr == null || fechaStr.trim().isEmpty()) {
      ModelMap modelo = new ModelMap();
      modelo.put(ATRIBUTO_DATOS_MASCOTA, datosMascota);
      modelo.put(ATRIBUTO_ERROR, "La fecha de nacimiento es obligatoria.");
      modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView(VISTA_NUEVA_MASCOTA, modelo);
    }

    try {
      java.time.LocalDate fecha = java.time.LocalDate.parse(fechaStr);
      if (fecha.getYear() <= MIN_ANIO_NACIMIENTO) {
        ModelMap modelo = new ModelMap();
        modelo.put(ATRIBUTO_DATOS_MASCOTA, datosMascota);
        modelo.put(ATRIBUTO_ERROR, "El año de nacimiento debe ser mayor a 1900.");
        modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
        return new ModelAndView(VISTA_NUEVA_MASCOTA, modelo);
      }
    } catch (java.time.format.DateTimeParseException e) {
      ModelMap modelo = new ModelMap();
      modelo.put(ATRIBUTO_DATOS_MASCOTA, datosMascota);
      modelo.put(ATRIBUTO_ERROR, "La fecha ingresada no es válida.");
      modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView(VISTA_NUEVA_MASCOTA, modelo);
    }

    try {
      Long nuevaMascotaId = servicioMascota.registrarMascota(datosMascota, idUsuario);
      return new ModelAndView("redirect:/configuraciones?idMascota=" + nuevaMascotaId);
    } catch (Exception e) {
      ModelMap modelo = new ModelMap();
      modelo.put(ATRIBUTO_DATOS_MASCOTA, datosMascota);
      modelo.put(ATRIBUTO_ERROR, "Ocurrió un error al registrar la mascota: " + e.getMessage());
      modelo.put(ATRIBUTO_MIS_MASCOTAS, servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView(VISTA_NUEVA_MASCOTA, modelo);
    }
  }
}
