package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioAlerta")
public class RepositorioAlertaImpl implements RepositorioAlerta {

  private static final String PARAM_ID_MASCOTA = "idMascota";

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioAlertaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void save(Alerta alerta) {
    this.sessionFactory.getCurrentSession().save(alerta);
  }

  @Override
  public List<Alerta> buscarPorMascota(Long idMascota) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT a FROM Alerta a JOIN FETCH a.mascota WHERE a.mascota.id = :idMascota",
        Alerta.class
      )
      .setParameter(PARAM_ID_MASCOTA, idMascota)
      .getResultList();
  }

  @Override
  public List<Alerta> buscarPorUsuario(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT a FROM Alerta a WHERE a.usuario.id = :idUsuario ORDER BY a.fechaYHora DESC",
        Alerta.class
      )
      .setParameter("idUsuario", idUsuario)
      .getResultList();
  }

  @Override
  public Alerta buscarUltimaAlertaDePesoPorMascota(Long idMascota) {
    List<Alerta> alertas = sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT a FROM Alerta a WHERE a.mascota.id = :idMascota " +
        "AND a.mensaje LIKE :prefijo ORDER BY a.fechaYHora DESC",
        Alerta.class
      )
      .setParameter(PARAM_ID_MASCOTA, idMascota)
      .setParameter("prefijo", "Atencion: El peso%")
      .setMaxResults(1)
      .getResultList();
    return alertas.isEmpty() ? null : alertas.get(0);
  }

  @Override
  public Alerta buscarUltimaAlertaDeValladoPorMascota(Long idMascota) {
    List<Alerta> alertas = sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT a FROM Alerta a WHERE a.mascota.id = :idMascota " +
        "AND a.mensaje LIKE :prefijo ORDER BY a.fechaYHora DESC",
        Alerta.class
      )
      .setParameter(PARAM_ID_MASCOTA, idMascota)
      .setParameter("prefijo", "EMERGENCIA: % se alejo %")
      .setMaxResults(1)
      .getResultList();
    return alertas.isEmpty() ? null : alertas.get(0);
  }

  @Override
  public void actualizar(Alerta alerta) {
    sessionFactory.getCurrentSession().update(alerta);
  }

  @Override
  public Alerta buscarPorId(Long idAlerta) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("SELECT a FROM Alerta a WHERE a.id = :id", Alerta.class)
      .setParameter("id", idAlerta)
      .uniqueResult();
  }

  @Override
  public List<Alerta> buscarEmergenciasActivasPorUsuario(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT a FROM Alerta a JOIN FETCH a.mascota m " +
        "WHERE m.usuario.id = :idUsuario " +
        "AND a.tipo = :tipo AND a.leido = false",
        Alerta.class
      )
      .setParameter("idUsuario", idUsuario)
      .setParameter("tipo", TipoAlerta.EMERGENCIA)
      .getResultList();
  }

  @Override
  public void eliminarPorUsuario(Long idUsuario) {
    sessionFactory
      .getCurrentSession()
      .createQuery("DELETE FROM Alerta a WHERE a.usuario.id = :idUsuario")
      .setParameter("idUsuario", idUsuario)
      .executeUpdate();
  }

  @Override
  public void eliminarPorMascota(Long idMascota) {
    sessionFactory
      .getCurrentSession()
      .createQuery("DELETE FROM Alerta a WHERE a.mascota.id = :idMascota")
      .setParameter(PARAM_ID_MASCOTA, idMascota)
      .executeUpdate();
  }
}
