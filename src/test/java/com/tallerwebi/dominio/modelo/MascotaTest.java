package com.tallerwebi.dominio.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class MascotaTest {

  @Test
  public void queSePuedaRegistrarElPerfilCompletoDeUnaMascota() {
    Mascota mascota = new Mascota();
    LocalDate fechaNacimiento = LocalDate.of(2020, 1, 1);

    mascota.setId(1L);
    mascota.setNombre("Firulais");
    mascota.setEsteril(true);
    mascota.setFechaNacimiento(fechaNacimiento);
    mascota.setPeso(10.5);
    mascota.setRaza("Labrador");
    mascota.setGenero("Macho");
    mascota.setTipo("Perro");
    mascota.setImagenMascota("url_imagen.jpg");

    assertNotNull(mascota, "La instancia de mascota no debe ser nula");
    assertEquals(1L, mascota.getId());
    assertEquals("Firulais", mascota.getNombre());
    assertTrue(mascota.getEsteril(), "La mascota debería estar registrada como estéril");
    assertEquals(fechaNacimiento, mascota.getFechaNacimiento());
    assertEquals(10.5, mascota.getPeso());
    assertEquals("Labrador", mascota.getRaza());
    assertEquals("Macho", mascota.getGenero());
    assertEquals("Perro", mascota.getTipo());
    assertEquals("url_imagen.jpg", mascota.getImagenMascota());
  }
}
