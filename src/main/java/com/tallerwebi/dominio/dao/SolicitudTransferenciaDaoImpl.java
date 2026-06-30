package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.modelo.SolicitudTransferencia;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SolicitudTransferenciaDaoImpl implements SolicitudTransferenciaDao {

  private final SessionFactory sessionFactory;

  @Autowired
  public SolicitudTransferenciaDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(SolicitudTransferencia solicitud) {
    sessionFactory.getCurrentSession().save(solicitud);
  }

  @Override
  public void modificar(SolicitudTransferencia solicitud) {
    sessionFactory.getCurrentSession().update(solicitud);
  }

  @Override
  public SolicitudTransferencia buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(SolicitudTransferencia.class, id);
  }

  @Override
  public List<SolicitudTransferencia> buscarPendientesPorUsuario(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM SolicitudTransferencia s WHERE " +
        "(s.usuarioOrigen.id = :idUsuario OR s.usuarioDestino.id = :idUsuario) " +
        "AND s.estado = 'PENDIENTE'",
        SolicitudTransferencia.class
      )
      .setParameter("idUsuario", idUsuario)
      .list();
  }

  @Override
  public void eliminarPorUsuario(Long idUsuario) {
    sessionFactory
      .getCurrentSession()
      .createQuery(
        "DELETE FROM SolicitudTransferencia s WHERE s.usuarioOrigen.id = :id OR s.usuarioDestino.id = :id"
      )
      .setParameter("id", idUsuario)
      .executeUpdate();
  }

  @Override
  public List<SolicitudTransferencia> buscarHistorialPorUsuario(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM SolicitudTransferencia s WHERE " +
        "(s.usuarioOrigen.id = :idUsuario OR s.usuarioDestino.id = :idUsuario) " +
        "AND s.estado != 'PENDIENTE' " +
        "ORDER BY s.fechaCreacion DESC",
        SolicitudTransferencia.class
      )
      .setParameter("idUsuario", idUsuario)
      .list();
  }
}
