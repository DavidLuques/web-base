package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.enums.EstadoTurno;
import com.tallerwebi.dominio.modelo.TurnoVeterinaria;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("turnoVeterinariaDao")
public class TurnoVeterinariaDaoImpl implements TurnoVeterinariaDao {

  private static final String PARAM_ESTADO_CANCELADO = "estadoCancelado";
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

    query.setParameter(PARAM_ESTADO_CANCELADO, EstadoTurno.CANCELADO);
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

    query.setParameter(PARAM_ESTADO_CANCELADO, EstadoTurno.CANCELADO);
    return query.getResultList();
  }

  @Override
  public boolean existeTurnoParaMascotaEnFecha(Long idMascota, LocalDateTime fecha) {
    String hql =
      "SELECT count(t) FROM TurnoVeterinaria t WHERE t.mascota.id = :idMascota AND t.fechaYHora = :fecha AND t.estado != :estadoCancelado";
    Long count = (Long) sessionFactory
      .getCurrentSession()
      .createQuery(hql)
      .setParameter("idMascota", idMascota)
      .setParameter("fecha", fecha)
      .setParameter(PARAM_ESTADO_CANCELADO, EstadoTurno.CANCELADO)
      .uniqueResult();
    return count > 0;
  }

  @Override
  public boolean existeTurnoEnVeterinariaEnFecha(String nombreVeterinaria, LocalDateTime fecha) {
    String hql =
      "SELECT count(t) FROM TurnoVeterinaria t WHERE t.nombreVeterinaria = :nombreVeterinaria AND t.fechaYHora = :fecha AND t.estado != :estadoCancelado";
    Long count = (Long) sessionFactory
      .getCurrentSession()
      .createQuery(hql)
      .setParameter("nombreVeterinaria", nombreVeterinaria)
      .setParameter("fecha", fecha)
      .setParameter(PARAM_ESTADO_CANCELADO, EstadoTurno.CANCELADO)
      .uniqueResult();
    return count > 0;
  }

  @Override
  public List<LocalDateTime> obtenerFechasOcupadasEnVeterinaria(
    String nombreVeterinaria,
    LocalDate fecha
  ) {
    // Establecemos el rango del día completo
    LocalDateTime inicioDia = fecha.atStartOfDay();
    LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();

    String hql =
      "SELECT t.fechaYHora FROM TurnoVeterinaria t " +
      "WHERE t.nombreVeterinaria = :nombreVeterinaria " +
      "AND t.fechaYHora >= :inicioDia " +
      "AND t.fechaYHora < :finDia " +
      "AND t.estado != :estadoCancelado";

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql, LocalDateTime.class)
      .setParameter("nombreVeterinaria", nombreVeterinaria)
      .setParameter("inicioDia", inicioDia)
      .setParameter("finDia", finDia)
      .setParameter(PARAM_ESTADO_CANCELADO, EstadoTurno.CANCELADO)
      .getResultList();
  }
}
