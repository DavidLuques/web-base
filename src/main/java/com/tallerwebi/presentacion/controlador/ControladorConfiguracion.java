package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.Usuario;
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
    modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
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
    modelo.put("datosMascota", new DatosAltaMascota());
    modelo.put("idMascota", idMascota);
    modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    return new ModelAndView("nueva-mascota", modelo);
  }

  @RequestMapping(path = "/configuraciones/mascota/nueva", method = RequestMethod.POST)
  public ModelAndView registrarMascota(
    @ModelAttribute("datosMascota") DatosAltaMascota datosMascota,
    HttpServletRequest request
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }

    try {
      Long nuevaMascotaId = servicioMascota.registrarMascota(datosMascota, idUsuario);
      return new ModelAndView("redirect:/configuraciones?idMascota=" + nuevaMascotaId);
    } catch (Exception e) {
      ModelMap modelo = new ModelMap();
      modelo.put("datosMascota", datosMascota);
      modelo.put("error", "Ocurrió un error al registrar la mascota: " + e.getMessage());
      modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView("nueva-mascota", modelo);
    }
  }
}
