package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.Mascota;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos.
 */
@Repository
public class MascotaDaoImpl implements MascotaDao {

  private final SessionFactory sessionFactory;

  @Autowired
  public MascotaDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Mascota buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Mascota.class, id);
  }

  @Override
  public void modificar(Mascota mascota) {
    sessionFactory.getCurrentSession().update(mascota);
  }

  @Override
  public List<Mascota> buscarTodas() {
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM Mascota m WHERE m.activo = true", Mascota.class)
      .list();
  }

  @Override
  public List<Mascota> buscarPorUsuarioId(Long usuarioId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM Mascota m WHERE m.usuario.id = :usuarioId AND m.activo = true",
        Mascota.class
      )
      .setParameter("usuarioId", usuarioId)
      .list();
  }

  @Override
  public void guardar(Mascota mascota) {
    sessionFactory.getCurrentSession().save(mascota);
  }

  @Override
  public void eliminar(Mascota mascota) {
    sessionFactory.getCurrentSession().delete(mascota);
  }
}
