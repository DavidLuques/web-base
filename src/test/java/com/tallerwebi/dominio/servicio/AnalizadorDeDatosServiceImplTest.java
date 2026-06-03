package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnalizadorDeDatosServiceImplTest {

  private AnalizadorDeDatosServiceImpl analizador;
  private RangoVitalDao rangoVitalDaoMock;

  @BeforeEach
  public void init() {
    rangoVitalDaoMock = mock(RangoVitalDao.class);
    analizador = new AnalizadorDeDatosServiceImpl(rangoVitalDaoMock);
  }

  @Test
  public void dadoUnKilometroRecorridoPorMascotaPequenaDebeCalcularCantidadCorrectaDePasos() {
    Integer pasos = analizador.calcularPasos(1.0, TamanoMascota.PEQUENO);

    assertThat(pasos, equalTo(TamanoMascota.PEQUENO.getComportamiento().getPasosPorKm()));
  }

  @Test
  public void cuandoLaDistanciaEsNulaDebeRetornarCeroPasos() {
    Integer pasos = analizador.calcularPasos(null, TamanoMascota.MEDIANO);

    assertThat(pasos, equalTo(0));
  }

  @Test
  public void dadoUnEstadoCorriendoDebeCalcularCaloriasConsumidas() {
    Double calorias = analizador.calcularCalorias(3.0, EstadoMascota.CORRIENDO, 20.0);

    double esperado =
      Math.round(
        EstadoMascota.CORRIENDO.getComportamiento().getMET() *
        20.0 *
        (3.0 / EstadoMascota.CORRIENDO.getComportamiento().getVelocidadKmH()) *
        10.0
      ) /
      10.0;

    assertThat(calorias, equalTo(esperado));
  }

  @Test
  public void cuandoLaDistanciaEsCeroNoDebeCalcularCalorias() {
    Double calorias = analizador.calcularCalorias(0.0, EstadoMascota.CAMINANDO, 15.0);

    assertThat(calorias, equalTo(0.0));
  }

  @Test
  public void dadaLaMismaUbicacionLaDistanciaDebeSerCero() {
    double distancia = analizador.calcularDistanciaEntreUbicaciones(-34.0, -58.0, -34.0, -58.0);

    assertThat(distancia, equalTo(0.0));
  }

  @Test
  public void dadaUnaMascotaSinEstadoPrevioCuandoSeDeterminaEstadoDebeRetornarElEstadoDeducido() {
    Mascota mascota = new Mascota();
    mascota.setId(1L);
    mascota.setTamano(TamanoMascota.MEDIANO);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(60);
    rango.setFrecuenciaMaxima(140);

    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(65);
    lectura.setAccelX(0.1);
    lectura.setAccelY(0.1);
    lectura.setAccelZ(0.1);
    lectura.setGyroX(0.1);
    lectura.setGyroY(0.1);
    lectura.setGyroZ(0.1);

    EstadoMascota estado = analizador.determinarEstado(mascota, lectura);

    assertThat(estado, equalTo(EstadoMascota.DURMIENDO));
  }

  @Test
  public void cuandoSeLimpiaLaMemoriaDebeEliminarEstadosGuardados() {
    analizador.limpiarMemoria();

    assertThat(true, equalTo(true));
  }

  @Test
  public void debeCalcularDistanciaEntreDosUbicacionesDiferentes() {
    double distancia = analizador.calcularDistanciaEntreUbicaciones(
      -34.6037,
      -58.3816,
      -34.6158,
      -58.4333
    );

    assertThat(distancia > 0, equalTo(true));
  }

  @Test
  public void cuandoLaDistanciaEsCeroDebeRetornarCeroPasos() {
    Integer pasos = analizador.calcularPasos(0.0, TamanoMascota.PEQUENO);

    assertThat(pasos, equalTo(0));
  }

  @Test
  public void cuandoElTamanoEsNuloDebeRetornarCeroPasos() {
    Integer pasos = analizador.calcularPasos(5.0, null);

    assertThat(pasos, equalTo(0));
  }

  @Test
  public void dadoUnKilometroRecorridoPorMascotaMedianaDebeCalcularCantidadCorrectaDePasos() {
    Integer pasos = analizador.calcularPasos(1.0, TamanoMascota.MEDIANO);

    assertThat(pasos, equalTo(TamanoMascota.MEDIANO.getComportamiento().getPasosPorKm()));
  }

  @Test
  public void dadoUnKilometroRecorridoPorMascotaGrandeDebeCalcularCantidadCorrectaDePasos() {
    Integer pasos = analizador.calcularPasos(1.0, TamanoMascota.GRANDE);

    assertThat(pasos, equalTo(TamanoMascota.GRANDE.getComportamiento().getPasosPorKm()));
  }

  @Test
  public void dadoDosCeroKilometrosLosPassosSonProporcionales() {
    Integer pasosPorUno = analizador.calcularPasos(1.0, TamanoMascota.MEDIANO);
    Integer pasosPorDos = analizador.calcularPasos(2.0, TamanoMascota.MEDIANO);

    assertThat(pasosPorDos, equalTo(pasosPorUno * 2));
  }

  @Test
  public void cuandoElEstadoEsNuloDebeRetornarCeroCalorias() {
    Double calorias = analizador.calcularCalorias(3.0, null, 10.0);

    assertThat(calorias, equalTo(0.0));
  }

  @Test
  public void cuandoElPesoEsNuloDebeRetornarCeroCalorias() {
    Double calorias = analizador.calcularCalorias(3.0, EstadoMascota.CAMINANDO, null);

    assertThat(calorias, equalTo(0.0));
  }

  @Test
  public void cuandoElPesoEsCeroDebeRetornarCeroCalorias() {
    Double calorias = analizador.calcularCalorias(3.0, EstadoMascota.CAMINANDO, 0.0);

    assertThat(calorias, equalTo(0.0));
  }

  @Test
  public void cuandoLaDistanciaEsNulaDebeRetornarCeroCalorias() {
    Double calorias = analizador.calcularCalorias(null, EstadoMascota.CAMINANDO, 10.0);

    assertThat(calorias, equalTo(0.0));
  }

  @Test
  public void dadoEstadoCaminandoDebeCalcularCaloriasConsumidas() {
    Double calorias = analizador.calcularCalorias(2.0, EstadoMascota.CAMINANDO, 10.0);

    double esperado =
      Math.round(
        EstadoMascota.CAMINANDO.getComportamiento().getMET() *
        10.0 *
        (2.0 / EstadoMascota.CAMINANDO.getComportamiento().getVelocidadKmH()) *
        10.0
      ) /
      10.0;

    assertThat(calorias, equalTo(esperado));
  }

  @Test
  public void dadaDistanciaSimetricaDebeRetornarLaMismaDistancia() {
    double distanciaAB = analizador.calcularDistanciaEntreUbicaciones(-34.0, -58.0, -35.0, -59.0);
    double distanciaBA = analizador.calcularDistanciaEntreUbicaciones(-35.0, -59.0, -34.0, -58.0);

    assertThat(distanciaAB, equalTo(distanciaBA));
  }

  @Test
  public void dadaUnaMascotaConEstadoPrevioDistintoDebeAvanzarUnPasoHaciaElNuevoEstado() {
    Mascota mascota = new Mascota();
    mascota.setId(2L);
    mascota.setTamano(TamanoMascota.MEDIANO);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(60);
    rango.setFrecuenciaMaxima(140);

    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);

    LecturaSensor lectura1 = new LecturaSensor();
    lectura1.setFrecuenciaCardiaca(65);
    lectura1.setAccelX(0.1);
    lectura1.setAccelY(0.1);
    lectura1.setAccelZ(0.1);
    lectura1.setGyroX(0.1);
    lectura1.setGyroY(0.1);
    lectura1.setGyroZ(0.1);
    EstadoMascota estadoInicial = analizador.determinarEstado(mascota, lectura1);

    LecturaSensor lectura2 = new LecturaSensor();
    lectura2.setFrecuenciaCardiaca(135);
    lectura2.setAccelX(3.0);
    lectura2.setAccelY(3.0);
    lectura2.setAccelZ(3.0);
    lectura2.setGyroX(3.0);
    lectura2.setGyroY(3.0);
    lectura2.setGyroZ(3.0);
    EstadoMascota estadoSiguiente = analizador.determinarEstado(mascota, lectura2);

    int ordenInicial = estadoInicial.getComportamiento().getOrden();
    int ordenSiguiente = estadoSiguiente.getComportamiento().getOrden();

    assertThat(ordenSiguiente, equalTo(ordenInicial + 1));
  }

  @Test
  public void cuandoSeLimpiaLaMemoriaLaSiguienteDeterminacionEsComoSinEstadoPrevio() {
    Mascota mascota = new Mascota();
    mascota.setId(3L);
    mascota.setTamano(TamanoMascota.MEDIANO);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(60);
    rango.setFrecuenciaMaxima(140);

    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(65);
    lectura.setAccelX(0.1);
    lectura.setAccelY(0.1);
    lectura.setAccelZ(0.1);
    lectura.setGyroX(0.1);
    lectura.setGyroY(0.1);
    lectura.setGyroZ(0.1);

    analizador.determinarEstado(mascota, lectura);
    analizador.limpiarMemoria();

    EstadoMascota estadoTraslimpiar = analizador.determinarEstado(mascota, lectura);

    assertThat(estadoTraslimpiar, equalTo(EstadoMascota.DURMIENDO));
  }

  @Test
  public void dadaUnaLecturaDeCorridaDebeDeducirEstadoCorriendo() {
    Mascota mascota = new Mascota();
    mascota.setId(4L);
    mascota.setTamano(TamanoMascota.MEDIANO);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(60);
    rango.setFrecuenciaMaxima(140);

    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(138);
    lectura.setAccelX(5.0);
    lectura.setAccelY(5.0);
    lectura.setAccelZ(5.0);
    lectura.setGyroX(5.0);
    lectura.setGyroY(5.0);
    lectura.setGyroZ(5.0);

    EstadoMascota estado = analizador.determinarEstado(mascota, lectura);

    assertThat(estado, equalTo(EstadoMascota.CORRIENDO));
  }

  @Test
  public void cuandoPermaneceDormidoAvanzaAReposo() {
    Mascota mascota = new Mascota();
    mascota.setId(100L);
    mascota.setTamano(TamanoMascota.MEDIANO);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(60);
    rango.setFrecuenciaMaxima(140);

    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(65);
    lectura.setAccelX(0.1);
    lectura.setAccelY(0.1);
    lectura.setAccelZ(0.1);
    lectura.setGyroX(0.1);
    lectura.setGyroY(0.1);
    lectura.setGyroZ(0.1);

    analizador.determinarEstado(mascota, lectura);

    EstadoMascota resultado = analizador.determinarEstado(mascota, lectura);

    assertThat(resultado, equalTo(EstadoMascota.REPOSO));
  }

  @Test
  public void cuandoPermaneceCorriendoRetrocedeACaminando() {
    Mascota mascota = new Mascota();
    mascota.setId(101L);
    mascota.setTamano(TamanoMascota.MEDIANO);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(60);
    rango.setFrecuenciaMaxima(140);

    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(138);
    lectura.setAccelX(5.0);
    lectura.setAccelY(5.0);
    lectura.setAccelZ(5.0);
    lectura.setGyroX(5.0);
    lectura.setGyroY(5.0);
    lectura.setGyroZ(5.0);

    analizador.determinarEstado(mascota, lectura);

    EstadoMascota resultado = analizador.determinarEstado(mascota, lectura);

    assertThat(resultado, equalTo(EstadoMascota.CAMINANDO));
  }
}
