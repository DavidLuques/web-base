package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.Mascota;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    return sessionFactory.getCurrentSession().createQuery("FROM Mascota", Mascota.class).list();
  }
}
