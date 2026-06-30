package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.enums.EstadoTurno;
import com.tallerwebi.dominio.modelo.TurnoVeterinaria;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("turnoVeterinariaDao")
public class TurnoVeterinariaDaoImpl implements TurnoVeterinariaDao {

  private final SessionFactory sessionFactory;

  @Autowired
  public TurnoVeterinariaDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(TurnoVeterinaria turno) {
    sessionFactory.getCurrentSession().save(turno);
  }

  @Override
  public void modificar(TurnoVeterinaria turno) {
    sessionFactory.getCurrentSession().update(turno);
  }

  @Override
  public TurnoVeterinaria buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(TurnoVeterinaria.class, id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<TurnoVeterinaria> buscarProximosPorMascota(
    Long idMascota,
    LocalDateTime fechaActual
  ) {
    Query<TurnoVeterinaria> query = sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM TurnoVeterinaria t WHERE t.mascota.id = :idMascota AND t.fechaYHora >= :fechaActual AND t.estado != :estadoCancelado ORDER BY t.fechaYHora ASC"
      );
    query.setParameter("idMascota", idMascota);
    query.setParameter("fechaActual", fechaActual);

    // Acá usamos directamente el Enum gracias al import
    query.setParameter("estadoCancelado", EstadoTurno.CANCELADO);
    return query.getResultList();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<TurnoVeterinaria> buscarPasadosPorMascota(Long idMascota, LocalDateTime fechaActual) {
    Query<TurnoVeterinaria> query = sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM TurnoVeterinaria t WHERE t.mascota.id = :idMascota AND t.fechaYHora < :fechaActual AND t.estado != :estadoCancelado ORDER BY t.fechaYHora DESC"
      );
    query.setParameter("idMascota", idMascota);
    query.setParameter("fechaActual", fechaActual);

    query.setParameter("estadoCancelado", EstadoTurno.CANCELADO);
    return query.getResultList();
  }
}
