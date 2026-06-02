package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LectorCollarServiceImplTest {

  private LectorCollarServiceImpl servicio;
  private MascotaDao mascotaDao;
  private RangoVitalDao rangoVitalDao;
  private RangoVitalPorTamano rango;

  @BeforeEach
  void setUp() {
    mascotaDao = mock(MascotaDao.class);
    rangoVitalDao = mock(RangoVitalDao.class);
    servicio = new LectorCollarServiceImpl(mascotaDao, rangoVitalDao);

    rango = new RangoVitalPorTamano();
    rango.setTamano(TamanoMascota.MEDIANO);
    rango.setFrecuenciaMinima(60);
    rango.setFrecuenciaMaxima(140);
    rango.setTemperaturaMinima(37.5);
    rango.setTemperaturaMaxima(39.5);
    rango.setSistolicaMinima(100);
    rango.setSistolicaMaxima(160);
    rango.setDiastolicaMinima(60);
    rango.setDiastolicaMaxima(100);

    Mascota mascota = new Mascota();
    mascota.setTamano(TamanoMascota.MEDIANO);

    when(mascotaDao.buscarPorId(1L)).thenReturn(mascota);
    when(rangoVitalDao.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);
  }

  // ── obtenerLectura ──────────────────────────────────────────────

  @Test
  void deberiaRetornarFrecuenciaCardiacaDentroDelRango() {
    LecturaSensor lectura = servicio.obtenerLectura(1L);

    assertThat(
      lectura.getFrecuenciaCardiaca(),
      allOf(
        greaterThanOrEqualTo(rango.getFrecuenciaMinima()),
        lessThanOrEqualTo(rango.getFrecuenciaMaxima())
      )
    );
  }

  @Test
  void deberiaRetornarTemperaturaDentroDelRango() {
    LecturaSensor lectura = servicio.obtenerLectura(1L);

    assertThat(
      lectura.getTemperatura(),
      allOf(
        greaterThanOrEqualTo(rango.getTemperaturaMinima()),
        lessThanOrEqualTo(rango.getTemperaturaMaxima())
      )
    );
  }

  @Test
  void deberiaRetornarPresionSistolicaDentroDelRango() {
    LecturaSensor lectura = servicio.obtenerLectura(1L);

    assertThat(
      lectura.getPresionSistolica(),
      allOf(
        greaterThanOrEqualTo(rango.getSistolicaMinima()),
        lessThanOrEqualTo(rango.getSistolicaMaxima())
      )
    );
  }

  @Test
  void deberiaRetornarPresionDiastolicaDentroDelRango() {
    LecturaSensor lectura = servicio.obtenerLectura(1L);

    assertThat(
      lectura.getPresionDiastolica(),
      allOf(
        greaterThanOrEqualTo(rango.getDiastolicaMinima()),
        lessThanOrEqualTo(rango.getDiastolicaMaxima())
      )
    );
  }

  @Test
  void deberiaRetornarValoresDeMovimientoNoNegativos() {
    LecturaSensor lectura = servicio.obtenerLectura(1L);

    assertThat(lectura.getAccelX(), greaterThanOrEqualTo(0.0));
    assertThat(lectura.getAccelY(), greaterThanOrEqualTo(0.0));
    assertThat(lectura.getAccelZ(), greaterThanOrEqualTo(0.0));
  }

  @Test
  void deberiaRetornarValoresDeRotacionNoNegativos() {
    LecturaSensor lectura = servicio.obtenerLectura(1L);

    assertThat(lectura.getGyroX(), greaterThanOrEqualTo(0.0));
    assertThat(lectura.getGyroY(), greaterThanOrEqualTo(0.0));
    assertThat(lectura.getGyroZ(), greaterThanOrEqualTo(0.0));
  }

  @Test
  void deberiaRetornarCoordenadasGPSCercanasAlPuntoInicial() {
    LecturaSensor lectura = servicio.obtenerLectura(1L);

    assertThat(lectura.getLatitud(), closeTo(-34.7222, 0.05));
    assertThat(lectura.getLongitud(), closeTo(-58.5250, 0.05));
  }

  @Test
  void deberiaConsultarMascotaYRangoAlObtenerLectura() {
    servicio.obtenerLectura(1L);

    verify(mascotaDao, times(1)).buscarPorId(1L);
    verify(rangoVitalDao, times(1)).buscarPorTamano(TamanoMascota.MEDIANO);
  }

  // ── estado incremental ──────────────────────────────────────────

  @Test
  void deberiaMantenerseLaFrecuenciaEstableEnLlamadasSucesivas() {
    int primera = servicio.obtenerLectura(1L).getFrecuenciaCardiaca();
    int segunda = servicio.obtenerLectura(1L).getFrecuenciaCardiaca();
    int tercera = servicio.obtenerLectura(1L).getFrecuenciaCardiaca();

    assertThat(Math.abs(segunda - primera), lessThanOrEqualTo(10));
    assertThat(Math.abs(tercera - segunda), lessThanOrEqualTo(10));
  }

  @Test
  void deberiaMantenerseLaFrecuenciaIndependientePorCadaMascota() {
    Mascota otraMascota = new Mascota();
    otraMascota.setTamano(TamanoMascota.MEDIANO);
    when(mascotaDao.buscarPorId(2L)).thenReturn(otraMascota);

    for (int i = 0; i < 5; i++) servicio.obtenerLectura(1L);
    for (int i = 0; i < 5; i++) servicio.obtenerLectura(2L);

    assertThat(
      servicio.obtenerLectura(1L).getFrecuenciaCardiaca(),
      allOf(
        greaterThanOrEqualTo(rango.getFrecuenciaMinima()),
        lessThanOrEqualTo(rango.getFrecuenciaMaxima())
      )
    );
    assertThat(
      servicio.obtenerLectura(2L).getFrecuenciaCardiaca(),
      allOf(
        greaterThanOrEqualTo(rango.getFrecuenciaMinima()),
        lessThanOrEqualTo(rango.getFrecuenciaMaxima())
      )
    );
  }
}
