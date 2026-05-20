package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Alerta;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioAlerta")
public class AlertaImpl implements RepositorioAlerta {

  private final SessionFactory sessionFactory;

  @Autowired
  public AlertaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void save(Alerta alerta) {
    this.sessionFactory.getCurrentSession().save(alerta);
  }
}
