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

  @Override
  public Double obtenerDistanciaTotalPorMascota(Long mascotaId) {
    String hql =
      "SELECT SUM(a.distanciaRecorrida) FROM Actividad a WHERE a.mascota.id = :mascotaId";
    Double resultado = (Double) sessionFactory
      .getCurrentSession()
      .createQuery(hql)
      .setParameter("mascotaId", mascotaId)
      .uniqueResult();
    return (resultado != null) ? resultado : 0.0;
  }
}
