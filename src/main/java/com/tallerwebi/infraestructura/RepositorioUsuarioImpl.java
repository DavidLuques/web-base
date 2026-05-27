package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioUsuario")
public class RepositorioUsuarioImpl implements RepositorioUsuario {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioUsuarioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Usuario usuario) {
    sessionFactory.getCurrentSession().save(usuario);
  }

  @SuppressWarnings("deprecation")
  @Override
  public Usuario buscar(String email) {
    return (Usuario) sessionFactory
      .getCurrentSession()
      .createCriteria(Usuario.class)
      .add(Restrictions.eq("email", email))
      .uniqueResult();
  }

  @Override
  public void modificar(Usuario usuario) {
    sessionFactory.getCurrentSession().update(usuario);
  }

  @Override
  public Usuario buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Usuario.class, id);
  }
}
