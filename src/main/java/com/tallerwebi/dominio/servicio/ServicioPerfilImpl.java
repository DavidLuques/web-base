package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.presentacion.DatosPerfil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class ServicioPerfilImpl implements ServicioPerfil {

  private final ServicioUsuario servicioUsuario;
  private final ServicioMascota servicioMascota;

  @Autowired
  public ServicioPerfilImpl(ServicioUsuario servicioUsuario, ServicioMascota servicioMascota) {
    this.servicioUsuario = servicioUsuario;
    this.servicioMascota = servicioMascota;
  }

  @Override
  public ModelMap prepararVerPerfil(Long idUsuario, Long idMascota) {
    DatosPerfil datosPerfil = servicioUsuario.obtenerDatosPerfil(idUsuario);
    ModelMap modelo = new ModelMap();
    modelo.put("datosPerfil", datosPerfil);
    modelo.put("idMascota", idMascota);
    modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    return modelo;
  }

  @Override
  public ModelMap prepararEditarPerfil(Long idUsuario, Long idMascota) {
    return prepararVerPerfil(idUsuario, idMascota);
  }

  @Override
  public void actualizarPerfil(Long idUsuario, DatosPerfil datosPerfil) {
    servicioUsuario.actualizarPerfil(idUsuario, datosPerfil);
  }

  @Override
  public ModelMap prepararModeloErrorActualizar(
    Long idUsuario,
    DatosPerfil datosPerfil,
    Long idMascota,
    String mensajeError
  ) {
    ModelMap modelo = new ModelMap();
    modelo.put("datosPerfil", datosPerfil);
    modelo.put("error", mensajeError);
    modelo.put("idMascota", idMascota);
    try {
      modelo.put("misMascotas", servicioMascota.obtenerMascotasPorUsuario(idUsuario));
    } catch (UsuarioNoEncontrado e) {
      // si no existe el usuario, dejamos misMascotas nulo
      modelo.put("misMascotas", null);
    }
    return modelo;
  }

  @Override
  public void eliminarCuenta(Long idUsuario) {
    servicioUsuario.eliminarCuenta(idUsuario);
  }
}
