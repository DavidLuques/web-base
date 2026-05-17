package com.tallerwebi.dominio.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DireccionTest {

  @Test
  public void queSePuedaEstablecerLaUbicacionBaseDeLaCasa() {
    Direccion direccion = new Direccion();
    direccion.setCalle("Av. Siempreviva 742");
    direccion.setCiudad("Springfield");
    direccion.setCodigoPostal("1783");
    direccion.setPais("Argentina");
    direccion.setProvincia("Buenos Aires");

    assertEquals("Av. Siempreviva 742", direccion.getCalle());
    assertEquals("Springfield", direccion.getCiudad());
    assertEquals("1783", direccion.getCodigoPostal());
    assertEquals("Argentina", direccion.getPais());
    assertEquals("Buenos Aires", direccion.getProvincia());
  }
}
