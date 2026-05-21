package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.modelo.RegistroSueno;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioSuenoTest {

  private SessionFactory sessionFactoryMock;
  private Session sessionMock;
  private Query queryMock;

  private RepositorioSuenoImpl repositorioSueno;

  @BeforeEach
  public void init() {
    sessionFactoryMock = mock(SessionFactory.class);
    sessionMock = mock(Session.class);
    queryMock = mock(Query.class);

    when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

    repositorioSueno = new RepositorioSuenoImpl(sessionFactoryMock);
  }

  @Test
  public void queGuardeRegistroDeSuenoExitosamente() {
    RegistroSueno registro = new RegistroSueno();

    repositorioSueno.guardar(registro);

    verify(sessionMock, times(1)).save(registro);
  }

  @Test
  public void queObtengaTotalMinutosDormidosPorMascota() {
    String hql =
      "SELECT SUM(r.minutosDormido) FROM RegistroSueno r WHERE r.mascota.id = :mascotaId";

    when(sessionMock.createQuery(hql)).thenReturn(queryMock);

    when(queryMock.setParameter("mascotaId", 1L)).thenReturn(queryMock);

    when(queryMock.uniqueResult()).thenReturn(10L);

    Integer resultado = repositorioSueno.obtenerTotalMinutosDormidosPorMascota(1L);

    assertEquals(10, resultado);

    verify(sessionMock, times(1)).createQuery(hql);
    verify(queryMock, times(1)).setParameter("mascotaId", 1L);
    verify(queryMock, times(1)).uniqueResult();
  }

  @Test
  public void queDevuelvaCeroCuandoNoHayMinutosDormidos() {
    String hql =
      "SELECT SUM(r.minutosDormido) FROM RegistroSueno r WHERE r.mascota.id = :mascotaId";

    when(sessionMock.createQuery(hql)).thenReturn(queryMock);

    when(queryMock.setParameter("mascotaId", 1L)).thenReturn(queryMock);

    when(queryMock.uniqueResult()).thenReturn(null);

    Integer resultado = repositorioSueno.obtenerTotalMinutosDormidosPorMascota(1L);

    assertEquals(0, resultado);
  }

  @Test
  public void queMinutosDormidosSeIncrementanCadaVezQueSeGuardaUnRegistro() {
    String hql =
      "SELECT SUM(r.minutosDormido) FROM RegistroSueno r WHERE r.mascota.id = :mascotaId";

    when(sessionMock.createQuery(hql)).thenReturn(queryMock);
    when(queryMock.setParameter("mascotaId", 1L)).thenReturn(queryMock);
    when(queryMock.uniqueResult()).thenReturn(null);

    Integer primerResultado = repositorioSueno.obtenerTotalMinutosDormidosPorMascota(1L);
    assertEquals(0, primerResultado);

    RegistroSueno registro = new RegistroSueno();
    repositorioSueno.guardar(registro);
    verify(sessionMock, atLeastOnce()).save(registro);

    when(queryMock.uniqueResult()).thenReturn(2L);

    Integer segundoResultado = repositorioSueno.obtenerTotalMinutosDormidosPorMascota(1L);
    assertEquals(2, segundoResultado);
  }
}
