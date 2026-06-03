package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.Vallado;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ValladoDaoImpl implements ValladoDao {

  private final SessionFactory sessionFactory;

  @Autowired
  public ValladoDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Vallado buscarPorMascota(Long idMascota) {
    List<Vallado> resultados = sessionFactory
      .getCurrentSession()
      .createQuery("SELECT v FROM Vallado v WHERE v.mascota.id = :idMascota", Vallado.class)
      .setParameter("idMascota", idMascota)
      .getResultList();
    return resultados.isEmpty() ? null : resultados.get(0);
  }

  @Override
  public void guardar(Vallado vallado) {
    sessionFactory.getCurrentSession().save(vallado);
  }

  @Override
  public void modificar(Vallado vallado) {
    sessionFactory.getCurrentSession().update(vallado);
  }

  @Override
  public void eliminar(Long idVallado) {
    Vallado vallado = sessionFactory.getCurrentSession().get(Vallado.class, idVallado);
    if (vallado != null) {
      sessionFactory.getCurrentSession().delete(vallado);
    }
  }
}
