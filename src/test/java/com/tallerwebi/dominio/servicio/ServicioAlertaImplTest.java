package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.Mascota;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioAlertaImplTest {

  private RepositorioAlerta repositorioAlertaMock;
  private ServicioNotificaciones servicioNotificacionesMock;
  private ServicioAlerta servicioAlerta;

  @BeforeEach
  public void init() {
    repositorioAlertaMock = mock(RepositorioAlerta.class);
    servicioNotificacionesMock = mock(ServicioNotificaciones.class);
    servicioAlerta = new ServicioAlertaImpl(repositorioAlertaMock, servicioNotificacionesMock);
  }

  @Test
  void debeCrearAlertaCorrectamente() {
    Mascota mascota = new Mascota();
    mascota.setNombre("Firulais");

    servicioAlerta.crearAlerta(mascota, TipoAlerta.ALERTA, "Mensaje de prueba");

    verify(repositorioAlertaMock, times(1)).save(any(Alerta.class));
    verify(servicioNotificacionesMock, never()).enviarNotificacionEmergencia(any(Alerta.class));
  }

  @Test
  void debeCrearAlertaDeEmergenciaYEnviarNotificacion() {
    Mascota mascota = new Mascota();
    mascota.setNombre("Firulais");

    servicioAlerta.crearAlerta(mascota, TipoAlerta.EMERGENCIA, "Mensaje de emergencia");

    verify(repositorioAlertaMock, times(1)).save(any(Alerta.class));
    verify(servicioNotificacionesMock, times(1)).enviarNotificacionEmergencia(any(Alerta.class));
  }

  @Test
  void debeMarcarAlertaComoLeida() {
    Long idAlerta = 1L;
    Alerta alerta = new Alerta();
    alerta.setId(idAlerta);
    alerta.setLeido(false);

    when(repositorioAlertaMock.buscarPorId(idAlerta)).thenReturn(alerta);

    servicioAlerta.marcarComoLeida(idAlerta);

    assertTrue(alerta.getLeido());
    verify(repositorioAlertaMock).actualizar(alerta);
  }

  @Test
  void debeRetornarAlertasMapeadasComoDto() {
    Alerta alerta1 = new Alerta();
    alerta1.setId(1L);
    alerta1.setTipo(TipoAlerta.ALERTA);
    alerta1.setMensaje("Mensaje 1");
    alerta1.setFechaYHora(LocalDateTime.now());
    alerta1.setLeido(false);

    when(repositorioAlertaMock.buscarPorMascota(1L)).thenReturn(Arrays.asList(alerta1));

    List<AlertaDto> resultado = servicioAlerta.obtenerAlertasPorMascota(1L);

    assertEquals(1, resultado.size());
    assertEquals("Mensaje 1", resultado.get(0).getMensaje());
  }

  @Test
  void debeRetornarListaVaciaSiIdEsNull() {
    List<AlertaDto> resultado = servicioAlerta.obtenerAlertasPorMascota(null);

    assertTrue(resultado.isEmpty());
    verify(repositorioAlertaMock, never()).buscarPorMascota(any());
  }

  @Test
  void debeObtenerUltimaAlertaDePeso() {
    Alerta alertaPeso = new Alerta();
    alertaPeso.setMensaje("Atencion: El peso");

    when(repositorioAlertaMock.buscarUltimaAlertaDePesoPorMascota(1L)).thenReturn(alertaPeso);

    Alerta resultado = servicioAlerta.buscarUltimaAlertaDePeso(1L);

    assertNotNull(resultado);
    assertEquals("Atencion: El peso", resultado.getMensaje());
  }
}
