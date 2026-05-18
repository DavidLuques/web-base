package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioActividad;
import com.tallerwebi.dominio.modelo.Actividad;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioActividadImpl implements RepositorioActividad {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioActividadImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Actividad actividad) {
    sessionFactory.getCurrentSession().save(actividad);
  }
}
