package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.modelo.Analisis;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioAnalisis")
public class RepositorioAnalisisImpl implements RepositorioAnalisis {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioAnalisisImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Analisis nuevoAnalisis) {
    sessionFactory.getCurrentSession().save(nuevoAnalisis);
  }

  @Override
  public Analisis obtenerUltimoAnalisis(Long id) {
    return (Analisis) sessionFactory
      .getCurrentSession()
      .createQuery("FROM Analisis a WHERE a.mascota.id = :id ORDER BY a.fechaYHora DESC")
      .setParameter("id", id)
      .setMaxResults(1)
      .uniqueResult();
  }
}
