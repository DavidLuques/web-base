package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.SolicitudAmistad;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SolicitudAmistadDaoImpl implements SolicitudAmistadDao {

  private final SessionFactory sessionFactory;

  @Autowired
  public SolicitudAmistadDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(SolicitudAmistad solicitud) {
    sessionFactory.getCurrentSession().save(solicitud);
  }

  @Override
  public void modificar(SolicitudAmistad solicitud) {
    sessionFactory.getCurrentSession().update(solicitud);
  }

  @Override
  public SolicitudAmistad buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(SolicitudAmistad.class, id);
  }

  @Override
  public List<SolicitudAmistad> buscarPendientesPorReceptor(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM SolicitudAmistad s WHERE s.receptor.id = :idUsuario AND s.estado = 'PENDIENTE'",
        SolicitudAmistad.class
      )
      .setParameter("idUsuario", idUsuario)
      .list();
  }

  @Override
  public List<SolicitudAmistad> buscarAceptadasPorUsuario(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM SolicitudAmistad s WHERE (s.emisor.id = :idUsuario OR s.receptor.id = :idUsuario) " +
        "AND s.estado = 'ACEPTADA'",
        SolicitudAmistad.class
      )
      .setParameter("idUsuario", idUsuario)
      .list();
  }

  @Override
  public SolicitudAmistad buscarEntreUsuarios(Long idUsuario1, Long idUsuario2) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM SolicitudAmistad s WHERE " +
        "((s.emisor.id = :id1 AND s.receptor.id = :id2) OR (s.emisor.id = :id2 AND s.receptor.id = :id1)) " +
        "AND s.estado != 'RECHAZADA'",
        SolicitudAmistad.class
      )
      .setParameter("id1", idUsuario1)
      .setParameter("id2", idUsuario2)
      .setMaxResults(1)
      .uniqueResultOptional()
      .orElse(null);
  }
}
