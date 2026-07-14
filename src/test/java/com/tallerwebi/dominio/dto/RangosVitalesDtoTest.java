package com.tallerwebi.dominio.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import org.junit.jupiter.api.Test;

public class RangosVitalesDtoTest {

  @Test
  public void constructorFromRango() {
    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(50);
    rango.setFrecuenciaMaxima(100);
    rango.setTemperaturaMinima(36.0);
    rango.setTemperaturaMaxima(39.0);
    rango.setSistolicaMinima(110);
    rango.setSistolicaMaxima(140);
    rango.setDiastolicaMinima(70);
    rango.setDiastolicaMaxima(90);

    RangosVitalesDto dto = new RangosVitalesDto(rango);
    assertEquals(50, dto.getFrecuenciaMinima());
    assertEquals(100, dto.getFrecuenciaMaxima());
    assertEquals(36.0, dto.getTemperaturaMinima());
    assertEquals(39.0, dto.getTemperaturaMaxima());
    assertEquals(110, dto.getSistolicaMinima());
    assertEquals(140, dto.getSistolicaMaxima());
    assertEquals(70, dto.getDiastolicaMinima());
    assertEquals(90, dto.getDiastolicaMaxima());
  }
}
