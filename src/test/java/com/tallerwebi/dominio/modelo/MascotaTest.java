package com.tallerwebi.dominio.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class MascotaTest {

  @Test
  public void queSePuedaRegistrarElPerfilCompletoDeUnaMascota() {
    Mascota mascota = new Mascota();
    LocalDate fechaNacimiento = LocalDate.of(2020, 1, 1);

    // Creo el objeto Raza
    Raza razaLabrador = new Raza();
    razaLabrador.setId(1L);
    razaLabrador.setNombre("Labrador");
    razaLabrador.setTipo("Grande");

    mascota.setId(1L);
    mascota.setNombre("Firulais");
    mascota.setEsteril(true);
    mascota.setFechaNacimiento(fechaNacimiento);
    mascota.setPeso(new BigDecimal("10.5")); // Cambio
    mascota.setRaza(razaLabrador); // Recibe el objeto Raza
    mascota.setGenero("Macho");
    mascota.setTipo("Perro");
    mascota.setImagenMascota("url_imagen.jpg");

    assertNotNull(mascota, "La instancia de mascota no debe ser nula");
    assertEquals(1L, mascota.getId());
    assertEquals("Firulais", mascota.getNombre());
    assertTrue(mascota.getEsteril(), "La mascota debería estar registrada como estéril");
    assertEquals(fechaNacimiento, mascota.getFechaNacimiento());

    assertEquals(0, new BigDecimal("10.5").compareTo(mascota.getPeso()));

    //Verifico que el objeto Raza y sus datos coincidan
    assertNotNull(mascota.getRaza());
    assertEquals("Labrador", mascota.getRaza().getNombre());

    assertEquals("Macho", mascota.getGenero());
    assertEquals("Perro", mascota.getTipo());
    assertEquals("url_imagen.jpg", mascota.getImagenMascota());
  }
}
