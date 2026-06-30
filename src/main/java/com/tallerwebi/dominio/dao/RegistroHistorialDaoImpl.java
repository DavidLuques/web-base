package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.RegistroHistorial;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("registroHistorialDao")
public class RegistroHistorialDaoImpl implements RegistroHistorialDao {

  private final SessionFactory sessionFactory;

  @Autowired
  public RegistroHistorialDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(RegistroHistorial registro) {
    sessionFactory.getCurrentSession().save(registro);
  }

  @Override
  public RegistroHistorial buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(RegistroHistorial.class, id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<RegistroHistorial> buscarPorMascota(Long idMascota) {
    Query<RegistroHistorial> query = sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM RegistroHistorial r WHERE r.mascota.id = :idMascota ORDER BY r.fechaVisita DESC"
      );
    query.setParameter("idMascota", idMascota);
    return query.getResultList();
  }
}
