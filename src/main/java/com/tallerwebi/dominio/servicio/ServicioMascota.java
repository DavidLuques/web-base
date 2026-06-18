package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.presentacion.DatosAltaMascota;
import java.util.List;

/**
 * Servicio de lógica de negocio.
 */
public interface ServicioMascota {
  Long registrarMascota(DatosAltaMascota datos, Long idUsuario);
  List<Mascota> obtenerMascotasPorUsuario(Long idUsuario);
  Mascota obtenerMascotaPorId(Long id);
  DatosAltaMascota obtenerDatosMascota(Long id);
  void actualizarMascota(Long idMascota, DatosAltaMascota datos);
  void eliminarMascota(Long id);
}
