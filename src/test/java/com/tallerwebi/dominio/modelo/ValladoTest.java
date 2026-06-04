package com.tallerwebi.dominio.modelo;

import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.enums.TamanoMascota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ValladoTest {

  private Vallado vallado;
  private Mascota mascota;

  @BeforeEach
  void setUp() {
    mascota = new Mascota();
    mascota.setId(1L);
    mascota.setNombre("Firulais");
    mascota.setTamano(TamanoMascota.MEDIANO);

    vallado = new Vallado(mascota, -34.7222, -58.5250, 150.0);
  }

  @Test
  void debeCrearValladoConConstructor() {
    assertNotNull(vallado);
    assertEquals(mascota, vallado.getMascota());
    assertEquals(-34.7222, vallado.getLatitudCentro());
    assertEquals(-58.5250, vallado.getLongitudCentro());
    assertEquals(150.0, vallado.getRadioMetros());
  }

  @Test
  void debeSetearYObtenerLatitudCentro() {
    vallado.setLatitudCentro(-34.8000);
    assertEquals(-34.8000, vallado.getLatitudCentro());
  }

  @Test
  void debeSetearYObtenerLongitudCentro() {
    vallado.setLongitudCentro(-58.4000);
    assertEquals(-58.4000, vallado.getLongitudCentro());
  }

  @Test
  void debeSetearYObtenerRadioMetros() {
    vallado.setRadioMetros(200.0);
    assertEquals(200.0, vallado.getRadioMetros());
  }

  @Test
  void debeSetearYObtenerMascota() {
    Mascota otraMascota = new Mascota();
    otraMascota.setId(2L);
    vallado.setMascota(otraMascota);
    assertEquals(otraMascota, vallado.getMascota());
  }

  @Test
  void debeSetearYObtenerID() {
    vallado.setId(5L);
    assertEquals(5L, vallado.getId());
  }

  @Test
  void debeCrearValladoConConstructorVacio() {
    Vallado valladoVacio = new Vallado();
    assertNull(valladoVacio.getId());
    assertNull(valladoVacio.getMascota());
  }
}
