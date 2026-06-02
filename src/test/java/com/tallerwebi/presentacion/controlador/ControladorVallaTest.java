package com.tallerwebi.presentacion.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.servicio.OrquestadorService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ControladorVallaTest {

  private OrquestadorService orquestadorServiceMock;
  private ControladorValla controladorValla;

  @BeforeEach
  public void inicializar() {
    orquestadorServiceMock = mock(OrquestadorService.class);
    controladorValla = new ControladorValla(orquestadorServiceMock);
  }

  @Test
  public void deberiaResponderConEstado200YLasCoordenadasDeLaMascota() {
    Long idMascota = 1L;
    Map<String, Object> ubicacionSimulada = new HashMap<>();
    ubicacionSimulada.put("latitud", -34.7222);
    ubicacionSimulada.put("longitud", -58.5250);

    when(orquestadorServiceMock.obtenerUltimaUbicacion(idMascota)).thenReturn(ubicacionSimulada);

    ResponseEntity<Map<String, Object>> respuesta = controladorValla.obtenerUbicacionActual(
      idMascota
    );

    assertEquals(
      HttpStatus.OK,
      respuesta.getStatusCode(),
      "El código de respuesta debe ser 200 OK"
    );
    assertEquals(-34.7222, respuesta.getBody().get("latitud"));
    assertEquals(-58.5250, respuesta.getBody().get("longitud"));

    verify(orquestadorServiceMock, times(1)).obtenerUltimaUbicacion(idMascota);
  }
}
