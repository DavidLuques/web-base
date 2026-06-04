package com.tallerwebi.presentacion.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

public class ControladorSinMascotaTest {

  private ControladorSinMascota controlador;
  private Model modelMock;
  private HttpServletRequest requestMock;

  @BeforeEach
  public void init() {
    controlador = new ControladorSinMascota();
    modelMock = mock(Model.class);
    requestMock = mock(HttpServletRequest.class);
  }

  @Test
  public void alIngresarASinMascotaRetornaLaVistaCorrecta() {
    String vista = controlador.sinMascota(modelMock, requestMock);

    assertEquals("sin-mascota", vista);
  }
}
