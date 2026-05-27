package com.tallerwebi.dominio;

import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.modelo.Mascota;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    return repositorioUsuario.buscarUsuario(email, password);
  }

  @Override
  public void registrar(Usuario usuario) throws UsuarioExistente {
    Usuario usuarioEncontrado = repositorioUsuario.buscarUsuario(
      usuario.getEmail(),
      usuario.getPassword()
    );
    if (usuarioEncontrado != null) {
      throw new UsuarioExistente();
    }

    usuario.setActivo(true);
    usuario.setRol("USER");
    usuario.setFechaCreacion(java.time.LocalDateTime.now().toString());

    repositorioUsuario.guardar(usuario);
  }

  @Override
  public List<Mascota> buscarMascotasPorUsuario(Long idUsuario) {
    return mascotaDao.buscarPorUsuarioId(idUsuario);
  }
}
