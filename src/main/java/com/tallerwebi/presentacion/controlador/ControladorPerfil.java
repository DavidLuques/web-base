package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.Usuario;
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
      return new ModelAndView("redirect:/login");
    }

    Usuario usuario = servicioUsuario.obtenerPerfil(idUsuario);

    DatosPerfil datosPerfil = new DatosPerfil();
    datosPerfil.setNombre(usuario.getNombre());
    datosPerfil.setEmail(usuario.getEmail());
    datosPerfil.setTelefono(usuario.getTelefono());

    if (usuario.getUbicacion() != null) {
      datosPerfil.setCalle(usuario.getUbicacion().getCalle());
      datosPerfil.setCiudad(usuario.getUbicacion().getCiudad());
      datosPerfil.setProvincia(usuario.getUbicacion().getProvincia());
      datosPerfil.setPais(usuario.getUbicacion().getPais());
      datosPerfil.setCodigoPostal(usuario.getUbicacion().getCodigoPostal());
    }

    ModelMap modelo = new ModelMap();
    modelo.put("datosPerfil", datosPerfil);
    modelo.put("idMascota", idMascota);
    modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    return new ModelAndView("perfil", modelo);
  }

  @RequestMapping(path = "/perfil/actualizar", method = RequestMethod.POST)
  public ModelAndView actualizarPerfil(
    @ModelAttribute("datosPerfil") DatosPerfil datosPerfil,
    HttpServletRequest request,
    @RequestParam(required = false) Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }

    try {
      servicioUsuario.actualizarPerfil(idUsuario, datosPerfil);
      String redirectUrl = idMascota != null
        ? "redirect:/perfil?exito=true&idMascota=" + idMascota
        : "redirect:/perfil?exito=true";
      return new ModelAndView(redirectUrl);
    } catch (RuntimeException e) {
      ModelMap modelo = new ModelMap();
      modelo.put("datosPerfil", datosPerfil);
      modelo.put("error", e.getMessage());
      modelo.put("idMascota", idMascota);
      modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
      return new ModelAndView("perfil", modelo);
    }
  }
}
