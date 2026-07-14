package com.tallerwebi.dominio.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ImpactoDatosDtoTest {

  @Test
  public void gettersAndSetters() {
    ImpactoDatosDto dto = new ImpactoDatosDto();
    dto.setPeso(12.5);
    dto.setTamano("MEDIANO");
    dto.setPasosPorKm(1000);
    dto.setPesoMinimoTamano(5.0);
    dto.setPesoMaximoTamano(20.0);
    dto.setEstadoActual("REPOSO");
    dto.setMetActual(1.2);
    dto.setVelocidadActualKmH(3.5);

    assertEquals(12.5, dto.getPeso());
    assertEquals("MEDIANO", dto.getTamano());
    assertEquals(1000, dto.getPasosPorKm());
    assertEquals(5.0, dto.getPesoMinimoTamano());
    assertEquals(20.0, dto.getPesoMaximoTamano());
    assertEquals("REPOSO", dto.getEstadoActual());
    assertEquals(1.2, dto.getMetActual());
    assertEquals(3.5, dto.getVelocidadActualKmH());
  }
}
