package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.enums.TipoMascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
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
  public RangoVitalPorTamano buscarPorTipoYTamano(TipoMascota tipo, TamanoMascota tamano) {
    String hql = "FROM RangoVitalPorTamano WHERE tipoMascota = :tipo AND tamano = :tamano";

    Query<RangoVitalPorTamano> query = sessionFactory
      .getCurrentSession()
      .createQuery(hql, RangoVitalPorTamano.class)
      .setParameter("tipo", tipo)
      .setParameter("tamano", tamano);

    return query.uniqueResult();
  }
}
