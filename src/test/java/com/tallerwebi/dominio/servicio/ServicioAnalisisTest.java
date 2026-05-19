package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioActividad;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.Actividad;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import com.tallerwebi.infraestructura.RepositorioAnalisisImpl;
import com.tallerwebi.infraestructura.RepositorioMascotaImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioAnalisisTest {

  private RepositorioAnalisisImpl repositorioAnalisisMock;
  private RepositorioMascotaImpl repositorioMascotaMock;
  private SimuladorCollarService simuladorCollarMock;
  private MotorActividadService motorActividadMock;
  private RepositorioActividad repositorioActividadMock;
  private ServicioAnalisisImpl servicioAnalisis;
  private RangoVitalDao rangoVitalDaoMock;

  @BeforeEach
  public void init() {
    repositorioAnalisisMock = mock(RepositorioAnalisisImpl.class);
    repositorioMascotaMock = mock(RepositorioMascotaImpl.class);
    simuladorCollarMock = mock(SimuladorCollarService.class);
    motorActividadMock = mock(MotorActividadService.class);
    repositorioActividadMock = mock(RepositorioActividad.class);
    rangoVitalDaoMock = mock(RangoVitalDao.class);

    servicioAnalisis =
      new ServicioAnalisisImpl(
        repositorioAnalisisMock,
        repositorioMascotaMock,
        simuladorCollarMock,
        repositorioActividadMock,
        rangoVitalDaoMock
      );
  }

  @Test
  public void queCalculeLaDistanciaCorrectamenteEntreDosPuntos() {
    Double lat1 = -34.7222;
    Double lon1 = -58.5250;
    Double lat2 = -34.7700;
    Double lon2 = -58.5000;

    Double distancia = servicioAnalisis.calcularDistancia(lat1, lon1, lat2, lon2);

    assertEquals(5.774, distancia, 0.05);
  }

  @Test
  public void queSimuleGeolocalizacionYGuardeActividadSiHayMovimiento() {
    Mascota mascotaReal = new Mascota();
    mascotaReal.setId(1L);
    mascotaReal.setTamano(TamanoMascota.MEDIANO);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(80);
    rango.setFrecuenciaMaxima(120);
    rango.setSistolicaMinima(115);
    rango.setSistolicaMaxima(140);
    rango.setDiastolicaMinima(75);
    rango.setDiastolicaMaxima(90);

    LecturaSensor lecturaFalsa = new LecturaSensor();
    lecturaFalsa.setLatitud(-34.7230);
    lecturaFalsa.setLongitud(-58.5260);

    Analisis analisisAnterior = new Analisis();
    analisisAnterior.setLatitud(-34.7222);
    analisisAnterior.setLongitud(-58.5250);

    when(repositorioMascotaMock.buscarPorId(1L)).thenReturn(mascotaReal);
    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);
    when(
      simuladorCollarMock.generarLectura(
        anyLong(),
        any(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt()
      )
    )
      .thenReturn(lecturaFalsa);
    when(repositorioAnalisisMock.obtenerUltimoAnalisis(1L)).thenReturn(analisisAnterior);

    servicioAnalisis.simularGeolocalizacion();

    verify(repositorioAnalisisMock, times(1)).guardar(any());
    verify(repositorioActividadMock, times(1)).guardar(any());
  }

  @Test
  public void queNoHagaNadaSiLaMascotaNoExiste() {
    when(repositorioMascotaMock.buscarPorId(1L)).thenReturn(null);

    servicioAnalisis.simularGeolocalizacion();

    verify(simuladorCollarMock, never()).generarLectura(anyInt(), anyInt());
    verify(repositorioAnalisisMock, never()).guardar(any());
    verify(repositorioActividadMock, never()).guardar(any());
  }

  @Test
  public void queGuardeAnalisisPeroNoActividadSiEsElPrimerRegistro() {
    Mascota mascotaReal = new Mascota();
    mascotaReal.setId(1L);
    mascotaReal.setTamano(TamanoMascota.MEDIANO);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(80);
    rango.setFrecuenciaMaxima(120);
    rango.setSistolicaMinima(115);
    rango.setSistolicaMaxima(140);
    rango.setDiastolicaMinima(75);
    rango.setDiastolicaMaxima(90);

    LecturaSensor lecturaFalsa = new LecturaSensor();
    lecturaFalsa.setLatitud(-34.7230);
    lecturaFalsa.setLongitud(-58.5260);

    when(repositorioMascotaMock.buscarPorId(1L)).thenReturn(mascotaReal);
    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);
    when(
      simuladorCollarMock.generarLectura(
        anyLong(),
        any(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt()
      )
    )
      .thenReturn(lecturaFalsa);
    when(repositorioAnalisisMock.obtenerUltimoAnalisis(1L)).thenReturn(null);

    servicioAnalisis.simularGeolocalizacion();

    verify(repositorioAnalisisMock, times(1)).guardar(any(Analisis.class));
    verify(repositorioActividadMock, never()).guardar(any(Actividad.class));
  }

  @Test
  public void queGuardeAnalisisPeroNoActividadSiElPerroNoSeMovio() {
    Mascota mascotaReal = new Mascota();
    mascotaReal.setId(1L);
    mascotaReal.setTamano(TamanoMascota.MEDIANO);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(80);
    rango.setFrecuenciaMaxima(120);
    rango.setSistolicaMinima(115);
    rango.setSistolicaMaxima(140);
    rango.setDiastolicaMinima(75);
    rango.setDiastolicaMaxima(90);

    LecturaSensor lecturaFalsa = new LecturaSensor();
    lecturaFalsa.setLatitud(-34.7222);
    lecturaFalsa.setLongitud(-58.5250);

    Analisis analisisAnterior = new Analisis();
    analisisAnterior.setLatitud(-34.7222);
    analisisAnterior.setLongitud(-58.5250);

    when(repositorioMascotaMock.buscarPorId(1L)).thenReturn(mascotaReal);
    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);
    when(
      simuladorCollarMock.generarLectura(
        anyLong(),
        any(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt(),
        anyInt()
      )
    )
      .thenReturn(lecturaFalsa);
    when(repositorioAnalisisMock.obtenerUltimoAnalisis(1L)).thenReturn(analisisAnterior);

    servicioAnalisis.simularGeolocalizacion();

    verify(repositorioAnalisisMock, times(1)).guardar(any(Analisis.class));
    verify(repositorioActividadMock, never()).guardar(any(Actividad.class));
  }
}
