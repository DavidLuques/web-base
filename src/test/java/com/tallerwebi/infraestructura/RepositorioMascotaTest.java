package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.modelo.Mascota;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioMascotaTest {

  private SessionFactory sessionFactoryMock;
  private Session sessionMock;
  private RepositorioMascotaImpl repositorioMascota;

  @BeforeEach
  public void init() {
    sessionFactoryMock = mock(SessionFactory.class);
    sessionMock = mock(Session.class);

    when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

    repositorioMascota = new RepositorioMascotaImpl(sessionFactoryMock);
  }

  @Test
  public void queGuardeUnaMascotaExitosamente() {
    Mascota mascota = new Mascota();

    repositorioMascota.guardar(mascota);

    verify(sessionMock, times(1)).save(mascota);
  }

  @Test
  public void queBusqueUnaMascotaPorId() {
    Mascota mascota = new Mascota();
    mascota.setId(1L);

    when(sessionMock.get(Mascota.class, 1L)).thenReturn(mascota);

    Mascota resultado = repositorioMascota.buscarPorId(1L);

    assertNotNull(resultado);
    verify(sessionMock, times(1)).get(Mascota.class, 1L);
  }

  @Test
  public void queActualiceUnaMascotaExitosamente() {
    Mascota mascota = new Mascota();

    repositorioMascota.actualizar(mascota);

    verify(sessionMock, times(1)).update(mascota);
  }

  @Test
  public void queElimineUnaMascotaExitosamente() {
    Mascota mascota = new Mascota();

    repositorioMascota.eliminar(mascota);

    verify(sessionMock, times(1)).delete(mascota);
  }
}
