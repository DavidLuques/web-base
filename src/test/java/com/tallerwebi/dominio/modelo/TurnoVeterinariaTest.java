package com.tallerwebi.dominio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tallerwebi.dominio.enums.EstadoTurno;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class TurnoVeterinariaTest {

  @Test
  public void queFuncionenLosGettersYSettersDeTurnoVeterinaria() {
    TurnoVeterinaria turno = new TurnoVeterinaria();
    Mascota mascotaMock = new Mascota();
    LocalDateTime fecha = LocalDateTime.now();

    turno.setId(15L);
    turno.setMascota(mascotaMock);
    turno.setNombreVeterinaria("Clínica Veterinaria San Roque");
    turno.setDireccionVeterinaria("Av. de Mayo 1500");
    turno.setFechaYHora(fecha);
    turno.setMotivo("Vacunación anual y desparasitación");
    turno.setEstado(EstadoTurno.COMPLETADO);

    assertNotNull(turno);
    assertEquals(15L, turno.getId());
    assertEquals(mascotaMock, turno.getMascota());
    assertEquals("Clínica Veterinaria San Roque", turno.getNombreVeterinaria());
    assertEquals("Av. de Mayo 1500", turno.getDireccionVeterinaria());
    assertEquals(fecha, turno.getFechaYHora());
    assertEquals("Vacunación anual y desparasitación", turno.getMotivo());
    assertEquals(EstadoTurno.COMPLETADO, turno.getEstado());
  }
}
