package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.modelo.Alerta;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioAlertaTest {

  private SessionFactory sessionFactoryMock;
  private Session sessionMock;
  private Query queryMock;

  private RepositorioAlertaImpl repositorioAlerta;

  @BeforeEach
  public void init() {
    sessionFactoryMock = mock(SessionFactory.class);
    sessionMock = mock(Session.class);
    queryMock = mock(Query.class);

    when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

    repositorioAlerta = new RepositorioAlertaImpl(sessionFactoryMock);
  }

  @Test
  public void queGuardeUnaAlertaExitosamente() {
    Alerta alerta = new Alerta();

    repositorioAlerta.save(alerta);

    verify(sessionMock, times(1)).save(alerta);
  }

  @Test
  public void queBusqueAlertasPorMascotaCorrectamente() {
    String hql = "SELECT a FROM Alerta a JOIN FETCH a.mascota WHERE a.mascota.id = :idMascota";
    List<Alerta> alertasEsperadas = new ArrayList<>();
    alertasEsperadas.add(new Alerta());
    alertasEsperadas.add(new Alerta());

    when(sessionMock.createQuery(hql, Alerta.class)).thenReturn(queryMock);
    when(queryMock.setParameter("idMascota", 1L)).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(alertasEsperadas);

    List<Alerta> resultado = repositorioAlerta.buscarPorMascota(1L);

    assertEquals(2, resultado.size());
    verify(sessionMock, times(1)).createQuery(hql, Alerta.class);
    verify(queryMock, times(1)).setParameter("idMascota", 1L);
    verify(queryMock, times(1)).getResultList();
  }

  @Test
  public void queBusqueLaUltimaAlertaDePesoPorMascotaCuandoExiste() {
    String hql =
      "SELECT a FROM Alerta a WHERE a.mascota.id = :idMascota AND a.mensaje LIKE :prefijo ORDER BY a.fechaYHora DESC";
    Alerta alertaEsperada = new Alerta();
    List<Alerta> listaConAlerta = new ArrayList<>();
    listaConAlerta.add(alertaEsperada);

    when(sessionMock.createQuery(hql, Alerta.class)).thenReturn(queryMock);
    when(queryMock.setParameter("idMascota", 1L)).thenReturn(queryMock);
    when(queryMock.setParameter("prefijo", "Atencion: El peso%")).thenReturn(queryMock);
    when(queryMock.setMaxResults(1)).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(listaConAlerta);

    Alerta resultado = repositorioAlerta.buscarUltimaAlertaDePesoPorMascota(1L);

    assertNotNull(resultado);
    assertEquals(alertaEsperada, resultado);
    verify(sessionMock, times(1)).createQuery(hql, Alerta.class);
    verify(queryMock, times(1)).setMaxResults(1);
  }

  @Test
  public void queDevuelvaNullAlBuscarUltimaAlertaDePesoSiNoExistenAlertas() {
    String hql =
      "SELECT a FROM Alerta a WHERE a.mascota.id = :idMascota AND a.mensaje LIKE :prefijo ORDER BY a.fechaYHora DESC";
    List<Alerta> listaVacia = new ArrayList<>();

    when(sessionMock.createQuery(hql, Alerta.class)).thenReturn(queryMock);
    when(queryMock.setParameter("idMascota", 1L)).thenReturn(queryMock);
    when(queryMock.setParameter("prefijo", "Atencion: El peso%")).thenReturn(queryMock);
    when(queryMock.setMaxResults(1)).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(listaVacia);

    Alerta resultado = repositorioAlerta.buscarUltimaAlertaDePesoPorMascota(1L);

    assertNull(resultado);
  }

  @Test
  public void queActualiceUnaAlertaExitosamente() {
    Alerta alerta = new Alerta();

    repositorioAlerta.actualizar(alerta);

    verify(sessionMock, times(1)).update(alerta);
  }

  @Test
  public void queBusqueUnaAlertaPorIdCorrectamente() {
    String hql = "SELECT a FROM Alerta a WHERE a.id = :id";
    Alerta alertaEsperada = new Alerta();

    when(sessionMock.createQuery(hql, Alerta.class)).thenReturn(queryMock);
    when(queryMock.setParameter("id", 10L)).thenReturn(queryMock);
    when(queryMock.uniqueResult()).thenReturn(alertaEsperada);

    Alerta resultado = repositorioAlerta.buscarPorId(10L);

    assertNotNull(resultado);
    assertEquals(alertaEsperada, resultado);
    verify(sessionMock, times(1)).createQuery(hql, Alerta.class);
    verify(queryMock, times(1)).setParameter("id", 10L);
    verify(queryMock, times(1)).uniqueResult();
  }
}
