package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioAlerta")
public class RepositorioAlertaImpl implements RepositorioAlerta {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioAlertaImpl(SessionFactory sessionFactory) {
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

  @Override
  public Alerta buscarUltimaAlertaDePesoPorMascota(Long idMascota) {
    List<Alerta> alertas = sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT a FROM Alerta a WHERE a.mascota.id = :idMascota " +
        "AND a.mensaje LIKE :prefijo ORDER BY a.fechaYHora DESC",
        Alerta.class
      )
      .setParameter("idMascota", idMascota)
      .setParameter("prefijo", "Atencion: El peso%")
      .setMaxResults(1)
      .getResultList();
    return alertas.isEmpty() ? null : alertas.get(0);
  }

  @Override
  public void actualizar(Alerta alerta) {
    sessionFactory.getCurrentSession().update(alerta);
  }

  @Override
  public Alerta buscarPorId(Long idAlerta) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("SELECT a FROM Alerta a WHERE a.id = :id", Alerta.class)
      .setParameter("id", idAlerta)
      .uniqueResult();
  }
}
