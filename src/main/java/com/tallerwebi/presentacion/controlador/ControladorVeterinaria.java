package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RegistroHistorial;
import com.tallerwebi.dominio.modelo.TurnoVeterinaria;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import com.tallerwebi.dominio.servicio.ServicioVeterinaria;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/analisis/veterinaria")
public class ControladorVeterinaria {

  private final ServicioVeterinaria servicioVeterinaria;
  private final ServicioMascota servicioMascota;

  @Autowired
  public ControladorVeterinaria(
    ServicioVeterinaria servicioVeterinaria,
    ServicioMascota servicioMascota
  ) {
    this.servicioVeterinaria = servicioVeterinaria;
    this.servicioMascota = servicioMascota;
  }

  @GetMapping("/mascota/{idMascota}")
  public ModelAndView mostrarPanelVeterinaria(
    HttpServletRequest request,
    @PathVariable Long idMascota
  ) {
    Long idUsuario = (Long) request.getSession().getAttribute("ID_USUARIO");
    if (idUsuario == null) {
      return new ModelAndView("redirect:/login");
    }
    ModelMap modelo = new ModelMap();
    if (idUsuario != null) {
      modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    }
    Mascota mascota = servicioMascota.obtenerMascotaPorId(idMascota);
    modelo.put("mascotaNombre", mascota != null ? mascota.getNombre() : "Mascota");

    List<TurnoVeterinaria> proximos = servicioVeterinaria.obtenerTurnosProximos(idMascota);
    List<TurnoVeterinaria> pasados = servicioVeterinaria.obtenerTurnosPasados(idMascota);
    List<RegistroHistorial> historial = servicioVeterinaria.obtenerHistorialClinico(idMascota);

    List<java.util.Map<String, String>> proximosParaJS = new java.util.ArrayList<>();
    for (TurnoVeterinaria t : proximos) {
      java.util.Map<String, String> mapa = new java.util.HashMap<>();
      mapa.put("id", t.getId().toString());
      mapa.put("nombreVeterinaria", t.getNombreVeterinaria());
      mapa.put("fechaYHora", t.getFechaYHora().toString());
      mapa.put("motivo", t.getMotivo());
      proximosParaJS.add(mapa);
    }

    List<java.util.Map<String, String>> pasadosParaJS = new java.util.ArrayList<>();
    for (TurnoVeterinaria t : pasados) {
      java.util.Map<String, String> mapa = new java.util.HashMap<>();
      mapa.put("nombreVeterinaria", t.getNombreVeterinaria());
      mapa.put("fechaYHora", t.getFechaYHora().toString());
      mapa.put("motivo", t.getMotivo());
      pasadosParaJS.add(mapa);
    }

    modelo.put("idMascota", idMascota);
    modelo.put("turnosProximos", proximos);
    modelo.put("turnosPasados", pasados);
    modelo.put("historialClinico", historial);

    modelo.put("proximosJS", proximosParaJS);
    modelo.put("pasadosJS", pasadosParaJS);

    return new ModelAndView("veterinaria", modelo);
  }

  // Procesa el formulario de agendamiento enviado
  @PostMapping("/agendar")
  public ModelAndView agendarTurno(
    @RequestParam("idMascota") Long idMascota,
    @RequestParam("nombreVeterinaria") String nombre,
    @RequestParam("direccionVeterinaria") String direccion,
    @RequestParam("fechaYHora") @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime fecha,
    @RequestParam(value = "motivo", required = false) String motivo
  ) {
    try {
      servicioVeterinaria.agendarTurno(idMascota, nombre, direccion, fecha, motivo);
      return new ModelAndView(
        "redirect:/analisis/veterinaria/mascota/" + idMascota + "?exito=turno_guardado"
      );
    } catch (Exception e) {
      ModelMap modelo = new ModelMap();
      modelo.put("error", "No se pudo agendar el turno: " + e.getMessage());
      return new ModelAndView(
        "redirect:/analisis/veterinaria/mascota/" + idMascota + "?error=fallo_reserva",
        modelo
      );
    }
  }

  // Procesa la cancelación de un turno pendiente
  @PostMapping("/turno/{idTurno}/cancelar")
  public ModelAndView cancelarTurno(
    @PathVariable Long idTurno,
    @RequestParam("idMascota") Long idMascota
  ) {
    servicioVeterinaria.cancelarTurno(idTurno);
    return new ModelAndView(
      "redirect:/analisis/veterinaria/mascota/" + idMascota + "?exito=turno_cancelado"
    );
  }
}
