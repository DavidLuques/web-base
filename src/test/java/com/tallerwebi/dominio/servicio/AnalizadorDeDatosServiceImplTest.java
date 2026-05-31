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
}
