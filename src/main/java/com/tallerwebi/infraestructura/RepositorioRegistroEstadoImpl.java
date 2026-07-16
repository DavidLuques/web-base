package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioRegistroEstado;
import com.tallerwebi.dominio.modelo.RegistroEstado;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioRegistroEstadoImpl implements RepositorioRegistroEstado {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioRegistroEstadoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(RegistroEstado registro) {
    sessionFactory.getCurrentSession().save(registro);
  }

  @Override
  public List<RegistroEstado> buscarPorMascota(Long idMascota) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM RegistroEstado r WHERE r.mascota.id = :idMascota ORDER BY r.fechaYHora ASC",
        RegistroEstado.class
      )
      .setParameter("idMascota", idMascota)
      .getResultList();
  }
}
