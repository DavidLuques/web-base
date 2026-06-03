package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioMascota;
import com.tallerwebi.dominio.modelo.Mascota;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioMascota")
public class RepositorioMascotaImpl implements RepositorioMascota {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioMascotaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Mascota buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Mascota.class, id);
  }

  @Override
  public void guardar(Mascota mascota) {
    sessionFactory.getCurrentSession().save(mascota);
  }

  @Override
  public void actualizar(Mascota mascota) {
    sessionFactory.getCurrentSession().update(mascota);
  }

  @Override
  public void eliminar(Mascota mascota) {
    sessionFactory.getCurrentSession().delete(mascota);
  }
}
