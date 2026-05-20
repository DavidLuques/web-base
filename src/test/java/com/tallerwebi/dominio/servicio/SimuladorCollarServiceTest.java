package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimuladorCollarServiceTest {

  private SimuladorCollarService simuladorCollarService;

  private static final int FRECUENCIA_MINIMA = 80;
  private static final int FRECUENCIA_MAXIMA = 120;
  private static final int SISTOLICA_MINIMA = 115;
  private static final int SISTOLICA_MAXIMA = 140;
  private static final int DIASTOLICA_MINIMA = 75;
  private static final int DIASTOLICA_MAXIMA = 90;

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
      assertThat(lectura.getGyroX(), lessThan(6.0));
      assertThat(lectura.getGyroY(), lessThan(6.0));
      assertThat(lectura.getGyroZ(), lessThan(6.0));
    }
  }

  @Test
  public void dadoUnRangoCompletoConPresionCuandoGeneroLecturaEntoncesPresionNoEsNula() {
    LecturaSensor lectura = simuladorCollarService.generarLectura(
      1L,
      EstadoMascota.REPOSO,
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA,
      SISTOLICA_MINIMA,
      SISTOLICA_MAXIMA,
      DIASTOLICA_MINIMA,
      DIASTOLICA_MAXIMA
    );

    assertThat(lectura.getPresionSistolica(), notNullValue());
    assertThat(lectura.getPresionDiastolica(), notNullValue());
  }

  @Test
  public void dadoUnRangoCompletoConPresionCuandoGeneroLecturaEntoncesPresionEstaEnRango() {
    LecturaSensor lectura = simuladorCollarService.generarLectura(
      1L,
      EstadoMascota.REPOSO,
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA,
      SISTOLICA_MINIMA,
      SISTOLICA_MAXIMA,
      DIASTOLICA_MINIMA,
      DIASTOLICA_MAXIMA
    );

    assertThat(lectura.getPresionSistolica(), greaterThanOrEqualTo(SISTOLICA_MINIMA));
    assertThat(lectura.getPresionSistolica(), lessThanOrEqualTo(SISTOLICA_MAXIMA));
    assertThat(lectura.getPresionDiastolica(), greaterThanOrEqualTo(DIASTOLICA_MINIMA));
    assertThat(lectura.getPresionDiastolica(), lessThanOrEqualTo(DIASTOLICA_MAXIMA));
  }

  @Test
  public void dadoUnRangoCompletoConTemperaturaCuandoGeneroLecturaEntoncesTemperaturaNoEsNula() {
    LecturaSensor lectura = simuladorCollarService.generarLectura(
      1L,
      EstadoMascota.REPOSO,
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA,
      SISTOLICA_MINIMA,
      SISTOLICA_MAXIMA,
      DIASTOLICA_MINIMA,
      DIASTOLICA_MAXIMA
    );

    assertThat(lectura.getTemperatura(), notNullValue());
  }

  @Test
  public void dadoUnRangoCompletoConTemperaturaCuandoGeneroLecturaEntoncesTemperaturaEstaEnRangoNormalDePerro() {
    LecturaSensor lectura = simuladorCollarService.generarLectura(
      1L,
      EstadoMascota.REPOSO,
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA,
      SISTOLICA_MINIMA,
      SISTOLICA_MAXIMA,
      DIASTOLICA_MINIMA,
      DIASTOLICA_MAXIMA
    );

    assertThat(lectura.getTemperatura(), greaterThanOrEqualTo(37.5));
    assertThat(lectura.getTemperatura(), lessThanOrEqualTo(39.9));
  }

  @Test
  public void dadoUnaMascotaCuandoGeneroMultiplesLecturasEntoncesPresionVarlaDeAPoco() {
    Long idMascota = 1L;

    LecturaSensor primera = simuladorCollarService.generarLectura(
      idMascota,
      EstadoMascota.REPOSO,
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA,
      SISTOLICA_MINIMA,
      SISTOLICA_MAXIMA,
      DIASTOLICA_MINIMA,
      DIASTOLICA_MAXIMA
    );

    LecturaSensor segunda = simuladorCollarService.generarLectura(
      idMascota,
      EstadoMascota.REPOSO,
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA,
      SISTOLICA_MINIMA,
      SISTOLICA_MAXIMA,
      DIASTOLICA_MINIMA,
      DIASTOLICA_MAXIMA
    );

    int diferenciaSistolica = Math.abs(
      primera.getPresionSistolica() - segunda.getPresionSistolica()
    );
    assertThat(diferenciaSistolica, lessThanOrEqualTo(10));
  }

  @Test
  public void dadoUnaMascotaCuandoGeneroMultiplesLecturasEntoncesTemperaturaVarlaDeAPoco() {
    Long idMascota = 1L;

    LecturaSensor primera = simuladorCollarService.generarLectura(
      idMascota,
      EstadoMascota.REPOSO,
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA,
      SISTOLICA_MINIMA,
      SISTOLICA_MAXIMA,
      DIASTOLICA_MINIMA,
      DIASTOLICA_MAXIMA
    );

    LecturaSensor segunda = simuladorCollarService.generarLectura(
      idMascota,
      EstadoMascota.REPOSO,
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA,
      SISTOLICA_MINIMA,
      SISTOLICA_MAXIMA,
      DIASTOLICA_MINIMA,
      DIASTOLICA_MAXIMA
    );

    double diferencia = Math.abs(primera.getTemperatura() - segunda.getTemperatura());
    assertThat(diferencia, lessThan(1.0));
  }

  @Test
  public void dadoUnaMascotaCuandoActualizoFrecuenciaEntoncesFrecuenciaEstaEnRango() {
    int frecuencia = simuladorCollarService.actualizarFrecuencia(
      1L,
      EstadoMascota.CAMINANDO,
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA
    );

    assertThat(frecuencia, greaterThanOrEqualTo(FRECUENCIA_MINIMA));
    assertThat(frecuencia, lessThanOrEqualTo(FRECUENCIA_MAXIMA));
  }

  @Test
  public void dadoUnaMascotaCuandoActualizoFrecuenciaVariasVecesEntoncesFrecuenciaVarlaDeAPoco() {
    Long idMascota = 1L;

    int primera = simuladorCollarService.actualizarFrecuencia(
      idMascota,
      EstadoMascota.REPOSO,
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA
    );
    int segunda = simuladorCollarService.actualizarFrecuencia(
      idMascota,
      EstadoMascota.REPOSO,
      FRECUENCIA_MINIMA,
      FRECUENCIA_MAXIMA
    );

    int diferencia = Math.abs(primera - segunda);
    assertThat(diferencia, lessThanOrEqualTo(10));
  }
}
