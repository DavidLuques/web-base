package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dao.TurnoVeterinariaDao;
import com.tallerwebi.dominio.dao.TurnoVeterinariaDaoImpl;
import com.tallerwebi.dominio.enums.EstadoTurno;
import com.tallerwebi.dominio.modelo.RegistroHistorial;
import java.time.LocalDateTime;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegistroHistorialDaoTest {

  private TurnoVeterinariaDao turnoDao;
  private SessionFactory sessionFactoryMock;
  private Session sessionMock;
  private Query queryMock;

  @BeforeEach
  public void init() {
    sessionFactoryMock = mock(SessionFactory.class);
    sessionMock = mock(Session.class);
    queryMock = mock(Query.class);

    when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

    turnoDao = new TurnoVeterinariaDaoImpl(sessionFactoryMock);
  }

  @Test
  public void queFuncionenLosGettersYSettersDeRegistroHistorial() {
    RegistroHistorial registro = new RegistroHistorial();
    registro.setDiagnostico("Gripe");
    registro.setTratamiento("Antibióticos");

    assertEquals("Gripe", registro.getDiagnostico());
    assertEquals("Antibióticos", registro.getTratamiento());
  }

  @Test
  public void quePuedaBuscarTurnosProximos() {
    when(sessionMock.createQuery(anyString())).thenReturn(queryMock);
    LocalDateTime fechaPrueba = LocalDateTime.now();

    turnoDao.buscarProximosPorMascota(1L, fechaPrueba);

    verify(queryMock, times(1)).setParameter("idMascota", 1L);
    verify(queryMock, times(1)).setParameter("fechaActual", fechaPrueba);
    verify(queryMock, times(1)).setParameter(eq("estadoCancelado"), eq(EstadoTurno.CANCELADO));

    verify(queryMock, times(1)).getResultList();
  }
}
