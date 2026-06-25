package com.tallerwebi.dominio.modelo.estado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tallerwebi.dominio.estado.EstadoAmistadAceptada;
import com.tallerwebi.dominio.estado.EstadoAmistadPendiente;
import com.tallerwebi.dominio.estado.EstadoAmistadRechazada;
import org.junit.jupiter.api.Test;

public class ComportamientoEstadoAmistadTest {

  @Test
  void pendienteDebePermitirAceptar() {
    assertTrue(new EstadoAmistadPendiente().puedeAceptar());
  }

  @Test
  void pendienteDebePermitirRechazar() {
    assertTrue(new EstadoAmistadPendiente().puedeRechazar());
  }

  @Test
  void pendienteNoDebeConsiderarseAmigos() {
    assertFalse(new EstadoAmistadPendiente().sonAmigos());
  }

  @Test
  void pendienteDebeRetornarElNombreCorrecto() {
    assertEquals("PENDIENTE", new EstadoAmistadPendiente().getNombre());
  }

  @Test
  void aceptadaNoDebePermitirAceptarDeNuevo() {
    assertFalse(new EstadoAmistadAceptada().puedeAceptar());
  }

  @Test
  void aceptadaNoDebePermitirRechazar() {
    assertFalse(new EstadoAmistadAceptada().puedeRechazar());
  }

  @Test
  void aceptadaDebeConsiderarseAmigos() {
    assertTrue(new EstadoAmistadAceptada().sonAmigos());
  }

  @Test
  void aceptadaDebeRetornarElNombreCorrecto() {
    assertEquals("ACEPTADA", new EstadoAmistadAceptada().getNombre());
  }

  @Test
  void rechazadaNoDebePermitirAceptar() {
    assertFalse(new EstadoAmistadRechazada().puedeAceptar());
  }

  @Test
  void rechazadaNoDebePermitirRechazarDeNuevo() {
    assertFalse(new EstadoAmistadRechazada().puedeRechazar());
  }

  @Test
  void rechazadaNoDebeConsiderarseAmigos() {
    assertFalse(new EstadoAmistadRechazada().sonAmigos());
  }

  @Test
  void rechazadaDebeRetornarElNombreCorrecto() {
    assertEquals("RECHAZADA", new EstadoAmistadRechazada().getNombre());
  }

  @Test
  void pendienteDebePermitirCancelar() {
    assertTrue(new EstadoAmistadPendiente().puedeCancelar());
  }

  @Test
  void aceptadaNoDebePermitirCancelar() {
    assertFalse(new EstadoAmistadAceptada().puedeCancelar());
  }

  @Test
  void rechazadaNoDebePermitirCancelar() {
    assertFalse(new EstadoAmistadRechazada().puedeCancelar());
  }

  @Test
  void pendienteNoDebePermitirEliminar() {
    assertFalse(new EstadoAmistadPendiente().puedeEliminar());
  }

  @Test
  void aceptadaDebePermitirEliminar() {
    assertTrue(new EstadoAmistadAceptada().puedeEliminar());
  }

  @Test
  void rechazadaNoDebePermitirEliminar() {
    assertFalse(new EstadoAmistadRechazada().puedeEliminar());
  }
}
