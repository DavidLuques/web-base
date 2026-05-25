package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Alerta;
import java.util.List;
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

  @Override
  public List<Alerta> buscarPorMascota(Long idMascota) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT a FROM Alerta a JOIN FETCH a.mascota WHERE a.mascota.id = :idMascota",
        Alerta.class
      )
      .setParameter("idMascota", idMascota)
      .getResultList();
  }
}
