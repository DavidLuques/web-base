package com.tallerwebi.dominio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class RegistroHistorialTest {

  @Test
  public void queFuncionenLosGettersYSettersDeRegistroHistorial() {
    RegistroHistorial historial = new RegistroHistorial();
    Mascota mascotaMock = new Mascota();
    TurnoVeterinaria turnoMock = new TurnoVeterinaria();
    LocalDate fecha = LocalDate.now();

    historial.setId(1L);
    historial.setMascota(mascotaMock);
    historial.setTurnoOriginal(turnoMock);
    historial.setFechaVisita(fecha);
    historial.setDiagnostico("Otitis leve");
    historial.setTratamiento("Gotas cada 8 horas por 5 días");

    assertNotNull(historial);
    assertEquals(1L, historial.getId());
    assertEquals(mascotaMock, historial.getMascota());
    assertEquals(turnoMock, historial.getTurnoOriginal());
    assertEquals(fecha, historial.getFechaVisita());
    assertEquals("Otitis leve", historial.getDiagnostico());
    assertEquals("Gotas cada 8 horas por 5 días", historial.getTratamiento());
  }
}
