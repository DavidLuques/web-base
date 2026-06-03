package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dao.MascotaDaoImpl;
import com.tallerwebi.dominio.modelo.Mascota;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MascotaDaoImplTest {

  private SessionFactory sessionFactoryMock;
  private Session sessionMock;
  private MascotaDaoImpl mascotaDao;

  @BeforeEach
  public void init() {
    sessionFactoryMock = mock(SessionFactory.class);
    sessionMock = mock(Session.class);

    when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

    mascotaDao = new MascotaDaoImpl(sessionFactoryMock);
  }

  @Test
  public void queElimineUnaMascotaExitosamente() {
    Mascota mascota = new Mascota();

    mascotaDao.eliminar(mascota);

    verify(sessionMock, times(1)).delete(mascota);
  }
}
