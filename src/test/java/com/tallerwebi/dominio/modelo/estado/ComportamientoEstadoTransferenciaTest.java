package com.tallerwebi.dominio.modelo.estado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tallerwebi.dominio.estado.EstadoTransferenciaCancelada;
import com.tallerwebi.dominio.estado.EstadoTransferenciaCompletada;
import com.tallerwebi.dominio.estado.EstadoTransferenciaPendiente;
import org.junit.jupiter.api.Test;

public class ComportamientoEstadoTransferenciaTest {

  @Test
  void pendienteDebePermitirConfirmarPorOrigen() {
    assertTrue(new EstadoTransferenciaPendiente().puedeConfirmarOrigen());
  }

  @Test
  void pendienteDebePermitirConfirmarPorDestino() {
    assertTrue(new EstadoTransferenciaPendiente().puedeConfirmarDestino());
  }

  @Test
  void pendienteDebePermitirCancelar() {
    assertTrue(new EstadoTransferenciaPendiente().puedeCancelar());
  }

  @Test
  void pendienteNoDebeEstarCompleta() {
    assertFalse(new EstadoTransferenciaPendiente().estaCompleta());
  }

  @Test
  void pendienteDebeRetornarElNombreCorrecto() {
    assertEquals("PENDIENTE", new EstadoTransferenciaPendiente().getNombre());
  }

  @Test
  void completadaNoDebePermitirConfirmarPorOrigen() {
    assertFalse(new EstadoTransferenciaCompletada().puedeConfirmarOrigen());
  }

  @Test
  void completadaNoDebePermitirConfirmarPorDestino() {
    assertFalse(new EstadoTransferenciaCompletada().puedeConfirmarDestino());
  }

  @Test
  void completadaNoDebePermitirCancelar() {
    assertFalse(new EstadoTransferenciaCompletada().puedeCancelar());
  }

  @Test
  void completadaDebeEstarCompleta() {
    assertTrue(new EstadoTransferenciaCompletada().estaCompleta());
  }

  @Test
  void completadaDebeRetornarElNombreCorrecto() {
    assertEquals("COMPLETADA", new EstadoTransferenciaCompletada().getNombre());
  }

  @Test
  void canceladaNoDebePermitirConfirmarPorOrigen() {
    assertFalse(new EstadoTransferenciaCancelada().puedeConfirmarOrigen());
  }

  @Test
  void canceladaNoDebePermitirConfirmarPorDestino() {
    assertFalse(new EstadoTransferenciaCancelada().puedeConfirmarDestino());
  }

  @Test
  void canceladaNoDebePermitirCancelarDeNuevo() {
    assertFalse(new EstadoTransferenciaCancelada().puedeCancelar());
  }

  @Test
  void canceladaNoDebeEstarCompleta() {
    assertFalse(new EstadoTransferenciaCancelada().estaCompleta());
  }

  @Test
  void canceladaDebeRetornarElNombreCorrecto() {
    assertEquals("CANCELADA", new EstadoTransferenciaCancelada().getNombre());
  }
}
