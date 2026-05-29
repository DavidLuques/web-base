package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.enums.EstadoMascota;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class ComportamientoEstadoTest {

  private final Random random = new Random();

  @Test
  void dadoEstadoDurmiendoCuandoGeneraMovimientoEntoncesEsMenorAlMaximo() {
    double movimiento = EstadoMascota.DURMIENDO.getComportamiento().generarMovimiento(random);
    assertThat(movimiento, lessThan(0.8));
  }

  @Test
  void dadoEstadoDurmiendoCuandoGeneraGiroEntoncesEsMenorAlMaximo() {
    double gyro = EstadoMascota.DURMIENDO.getComportamiento().generarGyro(random);
    assertThat(gyro, lessThan(0.4));
  }

  @Test
  void dadoEstadoDurmiendoCuandoActualizaGpsEntoncesLasCoordenadasNoCambian() {
    double[] coords = { -34.7222, -58.5250 };
    double latAntes = coords[0];
    double lonAntes = coords[1];

    EstadoMascota.DURMIENDO.getComportamiento().actualizarGps(coords, random);

    assertThat(coords[0], equalTo(latAntes));
    assertThat(coords[1], equalTo(lonAntes));
  }

  @Test
  void dadoEstadoDurmiendoEntoncesNoRegistraActividad() {
    assertThat(EstadoMascota.DURMIENDO.getComportamiento().registraActividad(), is(false));
  }

  @Test
  void dadoEstadoDurmiendoEntoncesCoincideConLecturaDePocoMovimiento() {
    boolean resultado = EstadoMascota.DURMIENDO
      .getComportamiento()
      .coincideConLectura(60, 0.5, 0.3, 80);
    assertThat(resultado, is(true));
  }

  @Test
  void dadoEstadoDurmiendoEntoncesNoCoincideConLecturaDeAltoMovimiento() {
    boolean resultado = EstadoMascota.DURMIENDO
      .getComportamiento()
      .coincideConLectura(120, 10.0, 6.0, 80);
    assertThat(resultado, is(false));
  }

  @Test
  void dadoEstadoDurmiendoEntoncesOrdenEsCero() {
    assertThat(EstadoMascota.DURMIENDO.getComportamiento().getOrden(), equalTo(0));
  }

  @Test
  void dadoEstadoRepososCuandoGeneraMovimientoEntoncesEsMenorAlMaximo() {
    double movimiento = EstadoMascota.REPOSO.getComportamiento().generarMovimiento(random);
    assertThat(movimiento, lessThan(3.0));
  }

  @Test
  void dadoEstadoReposoEntoncesNoRegistraActividad() {
    assertThat(EstadoMascota.REPOSO.getComportamiento().registraActividad(), is(false));
  }

  @Test
  void dadoEstadoRepososCuandoActualizaGpsEntoncesLasCoordenadasNoCambian() {
    double[] coords = { -34.7222, -58.5250 };
    double latAntes = coords[0];
    double lonAntes = coords[1];

    EstadoMascota.REPOSO.getComportamiento().actualizarGps(coords, random);

    assertThat(coords[0], equalTo(latAntes));
    assertThat(coords[1], equalTo(lonAntes));
  }

  @Test
  void dadoEstadoReposoEntoncesOrdenEsUno() {
    assertThat(EstadoMascota.REPOSO.getComportamiento().getOrden(), equalTo(1));
  }

  @Test
  void dadoEstadoCaminandoCuandoGeneraMovimientoEntoncesEsMenorAlMaximo() {
    double movimiento = EstadoMascota.CAMINANDO.getComportamiento().generarMovimiento(random);
    assertThat(movimiento, lessThan(7.0));
  }

  @Test
  void dadoEstadoCaminandoEntoncesRegistraActividad() {
    assertThat(EstadoMascota.CAMINANDO.getComportamiento().registraActividad(), is(true));
  }

  @Test
  void dadoEstadoCaminandoCuandoActualizaGpsEntoncesCambiaLasCoordenadas() {
    double[] coords = { -34.7222, -58.5250 };
    double latAntes = coords[0];
    double lonAntes = coords[1];

    boolean cambio = false;
    for (int i = 0; i < 20; i++) {
      double[] c = { latAntes, lonAntes };
      EstadoMascota.CAMINANDO.getComportamiento().actualizarGps(c, random);
      if (c[0] != latAntes || c[1] != lonAntes) {
        cambio = true;
        break;
      }
    }
    assertThat(cambio, is(true));
  }

  @Test
  void dadoEstadoCaminandoEntoncesOrdenEsDos() {
    assertThat(EstadoMascota.CAMINANDO.getComportamiento().getOrden(), equalTo(2));
  }

  @Test
  void dadoEstadoCorriendoCuandoGeneraMovimientoEntoncesEsMayorQueOcho() {
    double movimiento = EstadoMascota.CORRIENDO.getComportamiento().generarMovimiento(random);
    assertThat(movimiento, greaterThanOrEqualTo(8.0));
  }

  @Test
  void dadoEstadoCorriendoCuandoGeneraGiroEntoncesEsMayorQueCuatro() {
    double gyro = EstadoMascota.CORRIENDO.getComportamiento().generarGyro(random);
    assertThat(gyro, greaterThanOrEqualTo(4.0));
  }

  @Test
  void dadoEstadoCorriendoEntoncesRegistraActividad() {
    assertThat(EstadoMascota.CORRIENDO.getComportamiento().registraActividad(), is(true));
  }

  @Test
  void dadoEstadoCorriendoEntoncesOrdenEsTres() {
    assertThat(EstadoMascota.CORRIENDO.getComportamiento().getOrden(), equalTo(3));
  }

  @Test
  void losEstadosDeberianTenerOrdenCreciente() {
    assertThat(
      EstadoMascota.DURMIENDO.getComportamiento().getOrden(),
      lessThan(EstadoMascota.REPOSO.getComportamiento().getOrden())
    );
    assertThat(
      EstadoMascota.REPOSO.getComportamiento().getOrden(),
      lessThan(EstadoMascota.CAMINANDO.getComportamiento().getOrden())
    );
    assertThat(
      EstadoMascota.CAMINANDO.getComportamiento().getOrden(),
      lessThan(EstadoMascota.CORRIENDO.getComportamiento().getOrden())
    );
  }

  @Test
  void cadaEstadoDeberiaDevolver_getMET_Mayor_queElAnterior() {
    assertThat(
      EstadoMascota.REPOSO.getComportamiento().getMET(),
      greaterThan(EstadoMascota.DURMIENDO.getComportamiento().getMET())
    );
    assertThat(
      EstadoMascota.CAMINANDO.getComportamiento().getMET(),
      greaterThan(EstadoMascota.REPOSO.getComportamiento().getMET())
    );
    assertThat(
      EstadoMascota.CORRIENDO.getComportamiento().getMET(),
      greaterThan(EstadoMascota.CAMINANDO.getComportamiento().getMET())
    );
  }

  @Test
  void cadaEstadoDeberiaDevolver_getVelocidadKmH_MayorOIgualQueElAnterior() {
    assertThat(
      EstadoMascota.CAMINANDO.getComportamiento().getVelocidadKmH(),
      greaterThan(EstadoMascota.REPOSO.getComportamiento().getVelocidadKmH())
    );
    assertThat(
      EstadoMascota.CORRIENDO.getComportamiento().getVelocidadKmH(),
      greaterThan(EstadoMascota.CAMINANDO.getComportamiento().getVelocidadKmH())
    );
  }
}
