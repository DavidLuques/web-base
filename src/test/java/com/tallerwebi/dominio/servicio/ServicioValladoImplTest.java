package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.ValladoDao;
import com.tallerwebi.dominio.modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServicioValladoImplTest {

  @Mock
  private ValladoDao valladoDao;

  @Mock
  private MascotaDao mascotaDao;

  @Mock
  private RepositorioAnalisis repositorioAnalisis;

  @InjectMocks
  private ServicioValladoImpl servicio;

  private Mascota mascotaMock;
  private final Long ID_MASCOTA = 1L;
  private final Double LATITUD = -34.0;
  private final Double LONGITUD = -58.0;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mascotaMock = new Mascota();
    mascotaMock.setId(ID_MASCOTA);
  }

  @Test
  void siNoExisteValladoCreaUnoNuevoYClonaAnalisisCompleto() {
    // No hay vallado, pero la mascota tiene un historial completo de sensores y vitales
    when(valladoDao.buscarPorMascota(ID_MASCOTA)).thenReturn(null);
    when(mascotaDao.buscarPorId(ID_MASCOTA)).thenReturn(mascotaMock);

    Analisis ultimoAnalisis = new Analisis();
    DatosAnalisis datos = new DatosAnalisis();
    datos.setSensor(new DatosSensor()); // Tiene sensores viejos
    datos.setVitalesYVitales(new DatosVitalesYUbicacion()); // Tiene vitales viejos
    ultimoAnalisis.setDatos(datos);

    when(repositorioAnalisis.obtenerUltimoAnalisis(ID_MASCOTA)).thenReturn(ultimoAnalisis);

    servicio.actualizarCentroVallado(ID_MASCOTA, LATITUD, LONGITUD);

    // Entró al if (vallado == null) y usó los datos viejos
    verify(valladoDao, times(1)).guardar(any(Vallado.class));
    verify(valladoDao, never()).modificar(any());
    verify(repositorioAnalisis, times(1)).guardar(any(Analisis.class));
  }

  @Test
  void siYaExisteValladoLoModificaYAplicaValoresPorDefectoAlAnalisisSiEsNuevo() {
    // El vallado existe, pero la mascota NUNCA tuvo un análisis (ultimo = null)
    Vallado valladoExistente = new Vallado();
    when(valladoDao.buscarPorMascota(ID_MASCOTA)).thenReturn(valladoExistente);
    when(mascotaDao.buscarPorId(ID_MASCOTA)).thenReturn(mascotaMock);

    
    when(repositorioAnalisis.obtenerUltimoAnalisis(ID_MASCOTA)).thenReturn(null);

    servicio.actualizarCentroVallado(ID_MASCOTA, LATITUD, LONGITUD);

    verify(valladoDao, never()).guardar(any(Vallado.class));
    verify(valladoDao, times(1)).modificar(valladoExistente);
    verify(repositorioAnalisis, times(1)).guardar(any(Analisis.class));
  }

  @Test
  void siAnalisisPrevioTieneDatosParcialesAplicaLosDefectosFaltantes() {
    // Tiene análisis previo, pero el sensor es null
    when(valladoDao.buscarPorMascota(ID_MASCOTA)).thenReturn(new Vallado());
    when(mascotaDao.buscarPorId(ID_MASCOTA)).thenReturn(mascotaMock);

    Analisis ultimoAnalisisParcial = new Analisis();
    DatosAnalisis datosParciales = new DatosAnalisis();
    datosParciales.setSensor(null); // Forzamos el branch "tieneSensoresViejos = false"
    datosParciales.setVitalesYVitales(new DatosVitalesYUbicacion()); // Mantenemos los vitales
    ultimoAnalisisParcial.setDatos(datosParciales);

    when(repositorioAnalisis.obtenerUltimoAnalisis(ID_MASCOTA)).thenReturn(ultimoAnalisisParcial);

    servicio.actualizarCentroVallado(ID_MASCOTA, LATITUD, LONGITUD);

    verify(repositorioAnalisis, times(1)).guardar(any(Analisis.class));
  }

  @Test
  void siLaMascotaNoExisteEnBaseDeDatosNoIntentaGuardarElAnalisis() {
    when(valladoDao.buscarPorMascota(ID_MASCOTA)).thenReturn(null);
    when(mascotaDao.buscarPorId(ID_MASCOTA)).thenReturn(null); // Forzamos que "mascota != null" sea falso

    servicio.actualizarCentroVallado(ID_MASCOTA, LATITUD, LONGITUD);

    // Se guarda el vallado, pero se omite la creación del Análisis
    verify(valladoDao, times(1)).guardar(any(Vallado.class));
    verify(repositorioAnalisis, never()).guardar(any(Analisis.class));
  }

  @Test
  void siOcurreUnErrorEnBaseDeDatosSeLanzaLaExcepcion() {
    when(valladoDao.buscarPorMascota(ID_MASCOTA))
      .thenThrow(new RuntimeException("Simulando error SQL"));

    // esperamos que esto tire una excepción
    assertThrows(
      RuntimeException.class,
      () -> {
        servicio.actualizarCentroVallado(ID_MASCOTA, LATITUD, LONGITUD);
      }
    );

    // Verificación adicional nos aseguramos de que no se haya guardado nada en la BD al fallar
    verify(valladoDao, never()).guardar(any());
    verify(valladoDao, never()).modificar(any());
    verify(repositorioAnalisis, never()).guardar(any());
  }
}
