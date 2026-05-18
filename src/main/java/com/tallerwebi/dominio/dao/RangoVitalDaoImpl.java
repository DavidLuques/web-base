package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RangoVitalDaoImpl implements RangoVitalDao {

  private SessionFactory sessionFactory;

  @Autowired
  public RangoVitalDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public RangoVitalPorTamano buscarPorTamano(TamanoMascota tamano) {
    String hql = "FROM RangoVitalPorTamano WHERE tamano = :tamano";

    return (RangoVitalPorTamano) sessionFactory
      .getCurrentSession()
      .createQuery(hql)
      .setParameter("tamano", tamano)
      .uniqueResult();
  }
}
