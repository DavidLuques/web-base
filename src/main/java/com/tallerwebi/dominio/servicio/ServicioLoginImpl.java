package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.Usuario;
import java.util.List;
import javax.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Repositorio de acceso a datos.
 */
@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

  private RepositorioUsuario repositorioUsuario;
  private MascotaDao mascotaDao;

  @Autowired
  public ServicioLoginImpl(RepositorioUsuario repositorioUsuario, MascotaDao mascotaDao) {
    this.repositorioUsuario = repositorioUsuario;
    this.mascotaDao = mascotaDao;
  }

  @Override
  public Usuario consultarUsuario(String email, String password) {
    Usuario usuario = repositorioUsuario.buscar(email);
    if (usuario != null && BCrypt.checkpw(password, usuario.getPassword())) {
      return usuario;
    }
    return null;
  }

  @Override
  public void registrar(Usuario usuario) throws UsuarioExistente {
    Usuario usuarioEncontrado = repositorioUsuario.buscar(usuario.getEmail());
    if (usuarioEncontrado != null) {
      throw new UsuarioExistente();
    }

    usuario.setActivo(true);
    usuario.setRol("USER");
    usuario.setFechaCreacion(java.time.LocalDateTime.now().toString());

    String hashedPassword = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
    usuario.setPassword(hashedPassword);

    repositorioUsuario.guardar(usuario);
  }

  @Override
  public List<Mascota> buscarMascotasPorUsuario(Long idUsuario) {
    return mascotaDao.buscarPorUsuarioId(idUsuario);
  }
}
