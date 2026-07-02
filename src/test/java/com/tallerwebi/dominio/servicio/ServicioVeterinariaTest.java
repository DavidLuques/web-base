package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RegistroHistorialDao;
import com.tallerwebi.dominio.dao.TurnoVeterinariaDao;
import com.tallerwebi.dominio.enums.EstadoTurno;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.TurnoVeterinaria;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioVeterinariaTest {

  private ServicioVeterinaria servicioVeterinaria;
  private TurnoVeterinariaDao turnoDaoMock;
  private RegistroHistorialDao historialDaoMock;
  private MascotaDao mascotaDaoMock;
  private ServicioAlerta servicioAlertaMock;

  @BeforeEach
  public void init() {
    turnoDaoMock = mock(TurnoVeterinariaDao.class);
    historialDaoMock = mock(RegistroHistorialDao.class);
    mascotaDaoMock = mock(MascotaDao.class);
    servicioAlertaMock = mock(ServicioAlerta.class);

    servicioVeterinaria =
      new ServicioVeterinariaImpl(
        turnoDaoMock,
        historialDaoMock,
        mascotaDaoMock,
        servicioAlertaMock
      );
  }

  @Test
  public void queSePuedaAgendarUnTurnoYSeGenereLaAlerta() {
    Long idMascota = 1L;
    Mascota mascotaMock = new Mascota();
    mascotaMock.setId(idMascota);
    when(mascotaDaoMock.buscarPorId(idMascota)).thenReturn(mascotaMock);

    LocalDateTime fecha = LocalDateTime.now().plusDays(2);

    servicioVeterinaria.agendarTurno(idMascota, "Vet Centro", "Av. San Martin 123", fecha, "Control");

    verify(turnoDaoMock, times(1)).guardar(any(TurnoVeterinaria.class));
    verify(servicioAlertaMock, times(1))
            .crearAlertaUsuario(any(), eq(TipoAlerta.INFO), anyString());
  }

  @Test
  public void queLanceExcepcionSiSeIntentaAgendarParaUnaMascotaInexistente() {
    Long idMascota = 99L;
    when(mascotaDaoMock.buscarPorId(idMascota)).thenReturn(null);

    assertThrows(
      IllegalArgumentException.class,
      () -> {
        servicioVeterinaria.agendarTurno(
          idMascota,
          "Vet Centro",
          "Direccion",
          LocalDateTime.now(),
          "Motivo"
        );
      }
    );

    verify(turnoDaoMock, never()).guardar(any(TurnoVeterinaria.class));
  }

  @Test
  public void queAlCancelarUnTurnoCambieSuEstadoYGenereLaAlerta() {
    Long idTurno = 1L;
    TurnoVeterinaria turnoMock = new TurnoVeterinaria();
    turnoMock.setId(idTurno);
    turnoMock.setEstado(EstadoTurno.PENDIENTE);

    Mascota mascota = new Mascota();
    mascota.setId(1L);
    turnoMock.setMascota(mascota);
    turnoMock.setNombreVeterinaria("Vet Sur");

    when(turnoDaoMock.buscarPorId(idTurno)).thenReturn(turnoMock);

    servicioVeterinaria.cancelarTurno(idTurno);

    assertEquals(EstadoTurno.CANCELADO, turnoMock.getEstado());
    verify(turnoDaoMock, times(1)).modificar(turnoMock);
    verify(servicioAlertaMock, times(1))
            .crearAlertaUsuario(any(), eq(TipoAlerta.INFO), anyString());
  }
}
