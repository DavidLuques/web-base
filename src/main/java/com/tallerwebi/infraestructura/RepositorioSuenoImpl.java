package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioSueno;
import com.tallerwebi.dominio.modelo.RegistroSueno;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos.
 */
@Repository
public class RepositorioSuenoImpl implements RepositorioSueno {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioSuenoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(RegistroSueno registro) {
    sessionFactory.getCurrentSession().save(registro);
  }

  @Override
  public Integer obtenerTotalMinutosDormidosPorMascota(Long mascotaId) {
    String hql =
      "SELECT SUM(r.minutosDormido) FROM RegistroSueno r WHERE r.mascota.id = :mascotaId";
    Long resultado = (Long) sessionFactory
      .getCurrentSession()
      .createQuery(hql)
      .setParameter("mascotaId", mascotaId)
      .uniqueResult();
    return (resultado != null) ? resultado.intValue() : 0;
  }
}
