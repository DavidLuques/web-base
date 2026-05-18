package com.tallerwebi.dominio.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AnalisisTest {

  @Test
  public void queSeAsigneYRetenganLosValoresCorrectamente() {
    DatosAnalisis datos = new DatosAnalisis();
    datos.setFrecuenciaCardiaca(85);
    datos.setAccelX(1.5);
    datos.setAccelY(0.5);
    datos.setAccelZ(9.8);
    datos.setGyroX(0.1);
    datos.setGyroY(0.2);
    datos.setGyroZ(0.0);

    assertEquals(85, datos.getFrecuenciaCardiaca());
    assertEquals(1.5, datos.getAccelX());
    assertEquals(0.5, datos.getAccelY());
    assertEquals(9.8, datos.getAccelZ());
    assertEquals(0.1, datos.getGyroX());
    assertEquals(0.2, datos.getGyroY());
    assertEquals(0.0, datos.getGyroZ());
  }
}
