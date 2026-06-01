package com.tallerwebi.dominio.modelo.estado;

import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.estado.EstadoCaminando;
import com.tallerwebi.dominio.estado.EstadoCorriendo;
import com.tallerwebi.dominio.estado.EstadoDurmiendo;
import com.tallerwebi.dominio.estado.EstadoReposo;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class EstadosComportamientoTest {

  private final Random random = new Random(42);

  @Test
  void caminandoDebeRegistrarActividad() {
    assertTrue(new EstadoCaminando().registraActividad());
  }

  @Test
  void caminandoNoDebeRegistrarSueno() {
    assertFalse(new EstadoCaminando().registraSueno());
  }

  @Test
  void caminandoDebeGenerarMovimientoEnRangoCorrecto() {
    double mov = new EstadoCaminando().generarMovimiento(random);
    assertTrue(mov >= 0 && mov < 7.0);
  }

  @Test
  void caminandoDebeGenerarGyroEnRangoCorrecto() {
    double gyro = new EstadoCaminando().generarGyro(random);
    assertTrue(gyro >= 0 && gyro < 3.5);
  }

  @Test
  void caminandoDebeActualizarGps() {
    double[] coords = { -34.0, -58.0 };
    new EstadoCaminando().actualizarGps(coords, random);
    assertNotEquals(-34.0, coords[0]);
  }

  @Test
  void caminandoDebeRetornarValoresCorrectos() {
    EstadoCaminando e = new EstadoCaminando();
    assertEquals(38.5, e.getTemperaturaBase());
    assertEquals(0.70, e.getFactorFrecuencia());
    assertEquals(0.65, e.getFactorPresion());
    assertEquals(3.0, e.getMET());
    assertEquals(5.0, e.getVelocidadKmH());
    assertEquals(2, e.getOrden());
  }

  @Test
  void caminandoCoincideConLecturaEnRangoNormal() {
    assertTrue(new EstadoCaminando().coincideConLectura(100, 5.0, 2.0, 120));
  }

  @Test
  void caminandoNoCoincideConLecturaConMovimientoAlto() {
    assertFalse(new EstadoCaminando().coincideConLectura(100, 10.0, 2.0, 120));
  }

  @Test
  void corriendoDebeRegistrarActividad() {
    assertTrue(new EstadoCorriendo().registraActividad());
  }

  @Test
  void corriendoNoDebeRegistrarSueno() {
    assertFalse(new EstadoCorriendo().registraSueno());
  }

  @Test
  void corriendoDebeGenerarMovimientoEnRangoCorrecto() {
    double mov = new EstadoCorriendo().generarMovimiento(random);
    assertTrue(mov >= 8.0 && mov < 12.0);
  }

  @Test
  void corriendoDebeGenerarGyroEnRangoCorrecto() {
    double gyro = new EstadoCorriendo().generarGyro(random);
    assertTrue(gyro >= 4.0 && gyro < 6.0);
  }

  @Test
  void corriendoDebeActualizarGps() {
    double[] coords = { -34.0, -58.0 };
    new EstadoCorriendo().actualizarGps(coords, random);
    assertNotEquals(-34.0, coords[0]);
  }

  @Test
  void corriendoDebeRetornarValoresCorrectos() {
    EstadoCorriendo e = new EstadoCorriendo();
    assertEquals(39.0, e.getTemperaturaBase());
    assertEquals(0.90, e.getFactorFrecuencia());
    assertEquals(0.90, e.getFactorPresion());
    assertEquals(6.0, e.getMET());
    assertEquals(15.0, e.getVelocidadKmH());
    assertEquals(3, e.getOrden());
  }

  @Test
  void corriendoSiempreCoincideConLectura() {
    assertTrue(new EstadoCorriendo().coincideConLectura(200, 20.0, 10.0, 120));
  }

  @Test
  void durmiendoDebeRegistrarSueno() {
    assertTrue(new EstadoDurmiendo().registraSueno());
  }

  @Test
  void durmiendoNoDebeRegistrarActividad() {
    assertFalse(new EstadoDurmiendo().registraActividad());
  }

  @Test
  void durmiendoDebeGenerarMovimientoEnRangoCorrecto() {
    double mov = new EstadoDurmiendo().generarMovimiento(random);
    assertTrue(mov >= 0 && mov < 0.8);
  }

  @Test
  void durmiendoDebeGenerarGyroEnRangoCorrecto() {
    double gyro = new EstadoDurmiendo().generarGyro(random);
    assertTrue(gyro >= 0 && gyro < 0.4);
  }

  @Test
  void durmiendoNoDebeActualizarGps() {
    double[] coords = { -34.0, -58.0 };
    new EstadoDurmiendo().actualizarGps(coords, random);
    assertEquals(-34.0, coords[0]);
    assertEquals(-58.0, coords[1]);
  }

  @Test
  void durmiendoDebeRetornarValoresCorrectos() {
    EstadoDurmiendo e = new EstadoDurmiendo();
    assertEquals(38.0, e.getTemperaturaBase());
    assertEquals(0.15, e.getFactorFrecuencia());
    assertEquals(0.15, e.getFactorPresion());
    assertEquals(1.0, e.getMET());
    assertEquals(1.0, e.getVelocidadKmH());
    assertEquals(0, e.getOrden());
  }

  @Test
  void durmiendoCoincideConLecturaEnRangoNormal() {
    assertTrue(new EstadoDurmiendo().coincideConLectura(80, 1.0, 0.5, 120));
  }

  @Test
  void durmiendoNoCoincideConLecturaConMovimientoAlto() {
    assertFalse(new EstadoDurmiendo().coincideConLectura(80, 3.0, 0.5, 120));
  }

  @Test
  void reposoNoDebeRegistrarActividad() {
    assertFalse(new EstadoReposo().registraActividad());
  }

  @Test
  void reposoNoDebeRegistrarSueno() {
    assertFalse(new EstadoReposo().registraSueno());
  }

  @Test
  void reposoDebeGenerarMovimientoEnRangoCorrecto() {
    double mov = new EstadoReposo().generarMovimiento(random);
    assertTrue(mov >= 0 && mov < 3.0);
  }

  @Test
  void reposoDebeGenerarGyroEnRangoCorrecto() {
    double gyro = new EstadoReposo().generarGyro(random);
    assertTrue(gyro >= 0 && gyro < 1.5);
  }

  @Test
  void reposoNoDebeActualizarGps() {
    double[] coords = { -34.0, -58.0 };
    new EstadoReposo().actualizarGps(coords, random);
    assertEquals(-34.0, coords[0]);
    assertEquals(-58.0, coords[1]);
  }

  @Test
  void reposoDebeRetornarValoresCorrectos() {
    EstadoReposo e = new EstadoReposo();
    assertEquals(38.2, e.getTemperaturaBase());
    assertEquals(0.45, e.getFactorFrecuencia());
    assertEquals(0.40, e.getFactorPresion());
    assertEquals(1.5, e.getMET());
    assertEquals(1.0, e.getVelocidadKmH());
    assertEquals(1, e.getOrden());
  }

  @Test
  void reposoCoincideConLecturaEnRangoNormal() {
    assertTrue(new EstadoReposo().coincideConLectura(100, 3.0, 2.0, 120));
  }

  @Test
  void reposoNoCoincideConLecturaConMovimientoAlto() {
    assertFalse(new EstadoReposo().coincideConLectura(100, 6.0, 2.0, 120));
  }
}
