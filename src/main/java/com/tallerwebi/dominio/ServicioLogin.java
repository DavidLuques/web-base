package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.modelo.Mascota;
import java.util.List;

/**
 * Repositorio de acceso a datos.
 */
public interface ServicioLogin {
  Usuario consultarUsuario(String email, String password);
  void registrar(Usuario usuario) throws UsuarioExistente;
  List<Mascota> buscarMascotasPorUsuario(Long idUsuario);
}
