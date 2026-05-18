package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.modelo.LecturaSensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimuladorCollarServiceTest {

  private SimuladorCollarService simuladorCollarService;

  private static final int FRECUENCIA_MINIMA = 80;
  private static final int FRECUENCIA_MAXIMA = 120;

  @BeforeEach
  public void init() {
    simuladorCollarService = new SimuladorCollarService();
  }

  @Test
  public void dadoUnRangoValidoCuandoGeneroUnaLecturaEntoncesNoEsNula() {
    LecturaSensor lectura = simuladorCollarService.generarLectura(
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA
    );

    assertThat(lectura, notNullValue());
  }

  @Test
  public void dadoUnRangoValidoCuandoGeneroUnaLecturaEntoncesTieneFrencuenciaCardiacaDentroDelRangoEsperado() {
    LecturaSensor lectura = simuladorCollarService.generarLectura(
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA
    );

    assertThat(lectura.getFrecuenciaCardiaca(), greaterThanOrEqualTo(FRECUENCIA_MINIMA));
    assertThat(lectura.getFrecuenciaCardiaca(), lessThanOrEqualTo(FRECUENCIA_MAXIMA));
  }

  @Test
  public void dadoUnRangoValidoCuandoGeneroUnaLecturaEntoncesLosCamposDeAcelerometroNoSonNulos() {
    LecturaSensor lectura = simuladorCollarService.generarLectura(
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA
    );

    assertThat(lectura.getAccelX(), notNullValue());
    assertThat(lectura.getAccelY(), notNullValue());
    assertThat(lectura.getAccelZ(), notNullValue());
  }

  @Test
  public void dadoUnRangoValidoCuandoGeneroUnaLecturaEntoncesLosCamposDeGiroscopioNoSonNulos() {
    LecturaSensor lectura = simuladorCollarService.generarLectura(
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA
    );

    assertThat(lectura.getGyroX(), notNullValue());
    assertThat(lectura.getGyroY(), notNullValue());
    assertThat(lectura.getGyroZ(), notNullValue());
  }

  @Test
  public void dadoUnRangoValidoCuandoGeneroUnaLecturaEntoncesLosValoresDeAcelerometroSonPositivos() {
    LecturaSensor lectura = simuladorCollarService.generarLectura(
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA
    );

    assertThat(lectura.getAccelX(), greaterThanOrEqualTo(0.0));
    assertThat(lectura.getAccelY(), greaterThanOrEqualTo(0.0));
    assertThat(lectura.getAccelZ(), greaterThanOrEqualTo(0.0));
  }

  @Test
  public void dadoUnRangoValidoCuandoGeneroUnaLecturaEntoncesLosValoresDeGiroscopioSonPositivos() {
    LecturaSensor lectura = simuladorCollarService.generarLectura(
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA
    );

    assertThat(lectura.getGyroX(), greaterThanOrEqualTo(0.0));
    assertThat(lectura.getGyroY(), greaterThanOrEqualTo(0.0));
    assertThat(lectura.getGyroZ(), greaterThanOrEqualTo(0.0));
  }

  @Test
  public void dadoUnRangoValidoCuandoGeneroMultiplesLecturasEntoncesLaFrecuenciaVariaEntreLlamadas() {
    boolean hayVariacion = false;
    int primeraFrecuencia = simuladorCollarService
      .generarLectura(FRECUENCIA_MINIMA, FRECUENCIA_MAXIMA)
      .getFrecuenciaCardiaca();

    for (int i = 0; i < 20; i++) {
      int otraFrecuencia = simuladorCollarService
        .generarLectura(FRECUENCIA_MINIMA, FRECUENCIA_MAXIMA)
        .getFrecuenciaCardiaca();
      if (otraFrecuencia != primeraFrecuencia) {
        hayVariacion = true;
        break;
      }
    }

    assertThat(hayVariacion, equalTo(true));
  }

  @Test
  public void dadoUnRangoValido_cuandoGeneroMuchasLecturas_entoncesLosValoresDeAcelerometroNuncaSuperanElMaximoEsperado() {
    for (int i = 0; i < 50; i++) {
      LecturaSensor lectura = simuladorCollarService.generarLectura(
        FRECUENCIA_MINIMA,
        FRECUENCIA_MAXIMA
      );

      // BASE_MOVIMIENTO_CORRIENDO(8) + RANGO_MOVIMIENTO_CORRIENDO(4) = 12 max por eje
      assertThat(lectura.getAccelX(), lessThan(12.0));
      assertThat(lectura.getAccelY(), lessThan(12.0));
      assertThat(lectura.getAccelZ(), lessThan(12.0));
    }
  }

  @Test
  public void dadoUnRangoValido_cuandoGeneroMuchasLecturas_entoncesLosValoresDeGiroscopioNuncaSuperanElMaximoEsperado() {
    for (int i = 0; i < 50; i++) {
      LecturaSensor lectura = simuladorCollarService.generarLectura(
        FRECUENCIA_MINIMA,
        FRECUENCIA_MAXIMA
      );

      // BASE_GYRO_CORRIENDO(4) + RANGO_GYRO_CORRIENDO(2) = 6 max por eje
      assertThat(lectura.getGyroX(), lessThan(6.0));
      assertThat(lectura.getGyroY(), lessThan(6.0));
      assertThat(lectura.getGyroZ(), lessThan(6.0));
    }
  }
}
