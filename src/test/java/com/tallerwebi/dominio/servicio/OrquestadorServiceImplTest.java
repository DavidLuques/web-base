package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioActividad;
import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.RepositorioSueno;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.dao.ValladoDao;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.enums.TipoMascota;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.DatosAnalisis;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrquestadorServiceImplTest {

  private OrquestadorServiceImpl servicio;

  private MascotaDao mascotaDao;
  private ValladoDao valladoDao;
  private LectorCollarService lectorCollarService;
  private AnalizadorDeDatosService analizadorDeDatosService;
  private ServicioEvaluadorAlerta servicioEvaluadorAlerta;
  private RepositorioActividad repositorioActividad;
  private RepositorioSueno repositorioSueno;
  private RepositorioAnalisis repositorioAnalisis;
  private RangoVitalDao rangoVitalDao;
  private RangoVitalPorTamano rango;

  private Mascota mascota;
  private LecturaSensor lectura;

  @BeforeEach
  void setUp() {
    mascotaDao = mock(MascotaDao.class);
    lectorCollarService = mock(LectorCollarService.class);
    analizadorDeDatosService = mock(AnalizadorDeDatosService.class);
    servicioEvaluadorAlerta = mock(ServicioEvaluadorAlerta.class);
    repositorioActividad = mock(RepositorioActividad.class);
    repositorioSueno = mock(RepositorioSueno.class);
    repositorioAnalisis = mock(RepositorioAnalisis.class);
    rangoVitalDao = mock(RangoVitalDao.class);
    valladoDao = mock(ValladoDao.class);

    servicio =
      new OrquestadorServiceImpl(
        mascotaDao,
        lectorCollarService,
        analizadorDeDatosService,
        servicioEvaluadorAlerta,
        repositorioActividad,
        repositorioSueno,
        repositorioAnalisis,
        rangoVitalDao,
        valladoDao
      );

    mascota = new Mascota();
    mascota.setId(1L);
    mascota.setNombre("Firulais");
    mascota.setTipo(TipoMascota.PERRO);
    mascota.setTamano(TamanoMascota.MEDIANO);
    mascota.setPeso(10.5);
    mascota.setEstadoActual(EstadoMascota.CAMINANDO);

    lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(90);
    lectura.setPresionSistolica(120);
    lectura.setPresionDiastolica(80);
    lectura.setTemperatura(38.5);

    rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(80);
    rango.setFrecuenciaMaxima(120);
    rango.setSistolicaMinima(115);
    rango.setSistolicaMaxima(140);
    rango.setDiastolicaMinima(75);
    rango.setDiastolicaMaxima(90);
    rango.setTemperaturaMinima(37.8);
    rango.setTemperaturaMaxima(39.2);

    when(mascotaDao.buscarPorId(1L)).thenReturn(mascota);
    when(lectorCollarService.obtenerLectura(1L)).thenReturn(lectura);
    when(analizadorDeDatosService.determinarEstado(mascota, lectura))
      .thenReturn(EstadoMascota.CAMINANDO);
    when(repositorioActividad.obtenerDistanciaTotalPorMascota(1L)).thenReturn(1.5);
    when(analizadorDeDatosService.calcularPasos(1.5, mascota.getTamano())).thenReturn(2000);
    when(analizadorDeDatosService.calcularCalorias(1.5, EstadoMascota.CAMINANDO, 10.5))
      .thenReturn(50.0);
    when(repositorioSueno.obtenerTotalMinutosDormidosPorMascota(1L)).thenReturn(30);
    when(rangoVitalDao.buscarPorTipoYTamano(TipoMascota.PERRO, TamanoMascota.MEDIANO))
      .thenReturn(rango);
  }

  // ── procesarMascota ─────────────────────────────────────────────

  @Test
  void procesarMascotaDeberiaRetornarDtoConNombreDeLaMascota() {
    ResultadoSimulacionDto resultado = servicio.procesarMascota(1L);

    assertThat(resultado.getNombreMascota(), equalTo("Firulais"));
  }

  @Test
  void procesarMascotaDeberiaRetornarDtoConElEstadoDeterminado() {
    ResultadoSimulacionDto resultado = servicio.procesarMascota(1L);

    assertThat(resultado.getEstado(), equalTo(EstadoMascota.CAMINANDO));
  }

  @Test
  void procesarMascotaDeberiaRetornarDtoConLosDatosVitalesDeLaLectura() {
    ResultadoSimulacionDto resultado = servicio.procesarMascota(1L);

    assertThat(resultado.getFrecuenciaCardiaca(), equalTo(90));
    assertThat(resultado.getPresionSistolica(), equalTo(120));
    assertThat(resultado.getPresionDiastolica(), equalTo(80));
    assertThat(resultado.getTemperatura(), equalTo(38.5));
  }

  @Test
  void procesarMascotaDeberiaRetornarDtoConDistanciaYPasos() {
    ResultadoSimulacionDto resultado = servicio.procesarMascota(1L);

    assertThat(resultado.getDistanciaRecorrida(), equalTo(1.5));
    assertThat(resultado.getPasos(), equalTo(2000));
  }

  @Test
  void procesarMascotaDeberiaRetornarDtoConCaloriasYMinutosDormidos() {
    ResultadoSimulacionDto resultado = servicio.procesarMascota(1L);

    assertThat(resultado.getCalorias(), equalTo(50.0));
    assertThat(resultado.getMinutosDormidos(), equalTo(30));
  }

  @Test
  void procesarMascotaDeberiaActualizarElEstadoDeLaMascota() {
    servicio.procesarMascota(1L);

    verify(mascotaDao, times(1)).modificar(mascota);
    assertThat(mascota.getEstadoActual(), equalTo(EstadoMascota.CAMINANDO));
  }

  @Test
  void procesarMascotaDeberiaConsultarElLectorDeCollar() {
    servicio.procesarMascota(1L);

    verify(lectorCollarService, times(1)).obtenerLectura(1L);
  }

  // ── procesarTodasLasMascotas ────────────────────────────────────

  @Test
  void procesarTodasLasMascotasDeberiaLlamarProcesarPorCadaMascota() {
    Mascota otraMascota = new Mascota();
    otraMascota.setId(2L);
    otraMascota.setNombre("Rex");
    otraMascota.setTipo(TipoMascota.PERRO);
    otraMascota.setPeso(25.0);
    otraMascota.setEstadoActual(EstadoMascota.REPOSO);

    when(mascotaDao.buscarTodas()).thenReturn(List.of(mascota, otraMascota));
    when(mascotaDao.buscarPorId(2L)).thenReturn(otraMascota);
    when(lectorCollarService.obtenerLectura(2L)).thenReturn(lectura);
    when(analizadorDeDatosService.determinarEstado(otraMascota, lectura))
      .thenReturn(EstadoMascota.REPOSO);
    when(repositorioActividad.obtenerDistanciaTotalPorMascota(2L)).thenReturn(0.0);
    when(analizadorDeDatosService.calcularPasos(0.0, otraMascota.getTamano())).thenReturn(0);
    when(analizadorDeDatosService.calcularCalorias(0.0, EstadoMascota.REPOSO, 25.0))
      .thenReturn(10.0);
    when(repositorioSueno.obtenerTotalMinutosDormidosPorMascota(2L)).thenReturn(0);

    servicio.procesarTodasLasMascotas();

    verify(lectorCollarService, times(1)).obtenerLectura(1L);
    verify(lectorCollarService, times(1)).obtenerLectura(2L);
  }

  // ── obtenerUltimoEstado ─────────────────────────────────────────

  @Test
  void obtenerUltimoEstadoDeberiaRetornarNombreNoEncontradoSiLaMascotaNoExiste() {
    when(mascotaDao.buscarPorId(99L)).thenReturn(null);

    ResultadoSimulacionDto resultado = servicio.obtenerUltimoEstado(99L);

    assertThat(resultado.getNombreMascota(), equalTo("No encontrada"));
  }

  @Test
  void obtenerUltimoEstadoDeberiaRetornarDtoConCeroCaloriasYSinAnalisisPrevio() {
    when(repositorioAnalisis.obtenerUltimoAnalisis(1L)).thenReturn(null);

    ResultadoSimulacionDto resultado = servicio.obtenerUltimoEstado(1L);

    assertThat(resultado.getNombreMascota(), equalTo("Firulais"));
    assertThat(resultado.getCalorias(), equalTo(0.0));
  }

  @Test
  void obtenerUltimoEstadoDeberiaRetornarDatosVitalesDelUltimoAnalisis() {
    DatosAnalisis datos = new DatosAnalisis();
    datos.setFrecuenciaCardiaca(95);
    datos.setPresionSistolica(125);
    datos.setPresionDiastolica(82);
    datos.setTemperatura(38.7);

    Analisis ultimoAnalisis = new Analisis();
    ultimoAnalisis.setDatos(datos);

    when(repositorioAnalisis.obtenerUltimoAnalisis(1L)).thenReturn(ultimoAnalisis);
    when(analizadorDeDatosService.calcularCalorias(1.5, EstadoMascota.CAMINANDO, 10.5))
      .thenReturn(50.0);

    ResultadoSimulacionDto resultado = servicio.obtenerUltimoEstado(1L);

    assertThat(resultado.getFrecuenciaCardiaca(), equalTo(95));
    assertThat(resultado.getPresionSistolica(), equalTo(125));
    assertThat(resultado.getPresionDiastolica(), equalTo(82));
    assertThat(resultado.getTemperatura(), equalTo(38.7));
  }

  @Test
  public void deberiaDevolverLaUltimaUbicacionRegistradaConTimestamp() {
    Long idMascota = 1L;
    LocalDateTime fechaAnalisis = LocalDateTime.of(2026, 6, 15, 14, 30, 45);

    Analisis analisisMock = new Analisis();
    analisisMock.setLatitud(-34.5000);
    analisisMock.setLongitud(-58.4000);
    analisisMock.setFechaYHora(fechaAnalisis);

    when(repositorioAnalisis.obtenerUltimoAnalisis(idMascota)).thenReturn(analisisMock);

    Map<String, Object> resultado = servicio.obtenerUltimaUbicacion(idMascota);

    assertEquals(-34.5000, resultado.get("latitud"));
    assertEquals(-58.4000, resultado.get("longitud"));
    assertEquals(fechaAnalisis.toString(), resultado.get("timestamp"));
    verify(repositorioAnalisis, times(1)).obtenerUltimoAnalisis(idMascota);
  }

  @Test
  public void deberiaDevolverUbicacionPorDefectoSiNoHayLecturasPrevias() {
    Long idMascota = 1L;
    when(repositorioAnalisis.obtenerUltimoAnalisis(idMascota)).thenReturn(null);

    Map<String, Object> resultado = servicio.obtenerUltimaUbicacion(idMascota);

    assertEquals(-34.7222, resultado.get("latitud"));
    assertEquals(-58.5250, resultado.get("longitud"));
    verify(repositorioAnalisis, times(1)).obtenerUltimoAnalisis(idMascota);
  }

  @Test
  void obtenerRangosVitalesDeberiaBuscarMascotaYRetornarDto() {
    var resultado = servicio.obtenerRangosVitales(1L);

    assertThat(resultado, notNullValue());

    verify(mascotaDao).buscarPorId(1L);
    verify(rangoVitalDao).buscarPorTipoYTamano(TipoMascota.PERRO, TamanoMascota.MEDIANO);
  }

  @Test
  void refrescarTodasLasLecturasDeberiaProcesarCadaMascota() {
    Mascota mascota2 = new Mascota();
    mascota2.setId(2L);
    mascota2.setTipo(TipoMascota.PERRO);

    when(mascotaDao.buscarTodas()).thenReturn(List.of(mascota, mascota2));

    when(mascotaDao.buscarPorId(2L)).thenReturn(mascota2);

    when(lectorCollarService.obtenerLectura(2L)).thenReturn(lectura);

    when(repositorioActividad.obtenerDistanciaTotalPorMascota(2L)).thenReturn(0.0);

    when(repositorioSueno.obtenerTotalMinutosDormidosPorMascota(2L)).thenReturn(0);

    when(analizadorDeDatosService.calcularPasos(anyDouble(), any())).thenReturn(0);

    when(analizadorDeDatosService.calcularCalorias(anyDouble(), any(), anyDouble()))
      .thenReturn(0.0);

    servicio.refrescarTodasLasLecturas();

    verify(lectorCollarService).obtenerLectura(1L);
    verify(lectorCollarService).obtenerLectura(2L);
  }

  @Test
  void procesarMascotaDeberiaGuardarActividadCuandoHayMovimiento() {
    Analisis analisisPrevio = new Analisis();
    analisisPrevio.setLatitud(-34.0);
    analisisPrevio.setLongitud(-58.0);

    lectura.setLatitud(-34.1);
    lectura.setLongitud(-58.1);

    when(repositorioAnalisis.obtenerUltimoAnalisis(1L)).thenReturn(analisisPrevio);

    when(
      analizadorDeDatosService.calcularDistanciaEntreUbicaciones(
        anyDouble(),
        anyDouble(),
        anyDouble(),
        anyDouble()
      )
    )
      .thenReturn(1.2);

    servicio.procesarMascota(1L);

    verify(repositorioActividad).guardar(any());
  }

  @Test
  void procesarMascotaNoDeberiaGuardarActividadSiLaDistanciaEsCero() {
    Analisis analisisPrevio = new Analisis();

    when(repositorioAnalisis.obtenerUltimoAnalisis(1L)).thenReturn(analisisPrevio);

    when(
      analizadorDeDatosService.calcularDistanciaEntreUbicaciones(
        anyDouble(),
        anyDouble(),
        anyDouble(),
        anyDouble()
      )
    )
      .thenReturn(0.0);

    servicio.procesarMascota(1L);

    verify(repositorioActividad, never()).guardar(any());
  }

  @Test
  void procesarMascotaNoDeberiaGuardarActividadSinAnalisisPrevio() {
    when(repositorioAnalisis.obtenerUltimoAnalisis(1L)).thenReturn(null);

    servicio.procesarMascota(1L);

    verify(repositorioActividad, never()).guardar(any());
  }
}
