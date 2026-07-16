package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.modelo.Analisis;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos.
 */
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

  @Override
  public List<Analisis> buscarPorMascota(Long idMascota) {
    return sessionFactory
      .getCurrentSession()
      // Traigo los análisis ordenados por ID descendente para tener los más recientes primero
      .createQuery("FROM Analisis WHERE mascota.id = :idMascota ORDER BY id DESC", Analisis.class)
      .setParameter("idMascota", idMascota)
      .getResultList();
  }

  @Override
  public List<Analisis> buscarPorMascotaAsc(Long idMascota) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM Analisis a WHERE a.mascota.id = :idMascota ORDER BY a.fechaYHora ASC",
        Analisis.class
      )
      .setParameter("idMascota", idMascota)
      .getResultList();
  }
}
