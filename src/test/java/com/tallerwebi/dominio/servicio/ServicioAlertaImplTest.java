package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.Usuario;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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

  // ── crearAlerta ──────────────────────────────────────────────────

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
  void debeCrearAlertaConLosDatosCorrectos() {
    Mascota mascota = new Mascota();
    mascota.setNombre("Firulais");

    servicioAlerta.crearAlerta(mascota, TipoAlerta.EMERGENCIA, "Mensaje de prueba");

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    Alerta guardada = captor.getValue();
    assertEquals(TipoAlerta.EMERGENCIA, guardada.getTipo());
    assertEquals("Mensaje de prueba", guardada.getMensaje());
    assertEquals(mascota, guardada.getMascota());
    assertEquals(false, guardada.getLeido());
    assertNotNull(guardada.getFechaYHora());
  }

  // ── crearAlertaUsuario ───────────────────────────────────────────

  @Test
  void debeCrearAlertaDeUsuarioCorrectamente() {
    Usuario usuario = mock(Usuario.class);

    servicioAlerta.crearAlertaUsuario(usuario, TipoAlerta.INFO, "Notificación de prueba");

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    Alerta guardada = captor.getValue();
    assertEquals(TipoAlerta.INFO, guardada.getTipo());
    assertEquals("Notificación de prueba", guardada.getMensaje());
    assertEquals(usuario, guardada.getUsuario());
    assertEquals(false, guardada.getLeido());
  }

  // ── marcarComoLeida ──────────────────────────────────────────────

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
  void cuandoLaAlertaNoExisteMarcarComoLeidaNoActualiza() {
    when(repositorioAlertaMock.buscarPorId(99L)).thenReturn(null);

    servicioAlerta.marcarComoLeida(99L);

    verify(repositorioAlertaMock, never()).actualizar(any());
  }

  // ── obtenerAlertasPorMascota ─────────────────────────────────────

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
    assertEquals(TipoAlerta.ALERTA, resultado.get(0).getTipo());
    assertEquals(false, resultado.get(0).getLeido());
  }

  @Test
  void debeRetornarListaVaciaSiIdEsNull() {
    List<AlertaDto> resultado = servicioAlerta.obtenerAlertasPorMascota(null);

    assertTrue(resultado.isEmpty());
    verify(repositorioAlertaMock, never()).buscarPorMascota(any());
  }

  // ── obtenerAlertasPorUsuario ─────────────────────────────────────

  @Test
  void debeRetornarAlertasDeUsuarioMapeadasComoDto() {
    Alerta alerta = new Alerta();
    alerta.setId(3L);
    alerta.setTipo(TipoAlerta.INFO);
    alerta.setMensaje("Alerta de usuario");
    alerta.setFechaYHora(LocalDateTime.now());
    alerta.setLeido(false);

    when(repositorioAlertaMock.buscarPorUsuario(2L)).thenReturn(Arrays.asList(alerta));

    List<AlertaDto> resultado = servicioAlerta.obtenerAlertasPorUsuario(2L);

    assertEquals(1, resultado.size());
    assertEquals("Alerta de usuario", resultado.get(0).getMensaje());
    assertEquals(TipoAlerta.INFO, resultado.get(0).getTipo());
  }

  @Test
  void debeRetornarListaVaciaSiIdUsuarioEsNull() {
    List<AlertaDto> resultado = servicioAlerta.obtenerAlertasPorUsuario(null);

    assertTrue(resultado.isEmpty());
    verify(repositorioAlertaMock, never()).buscarPorUsuario(any());
  }

  // ── buscarUltimaAlertaDePeso ─────────────────────────────────────

  @Test
  void debeObtenerUltimaAlertaDePeso() {
    Alerta alertaPeso = new Alerta();
    alertaPeso.setMensaje("Atencion: El peso");

    when(repositorioAlertaMock.buscarUltimaAlertaDePesoPorMascota(1L)).thenReturn(alertaPeso);

    Alerta resultado = servicioAlerta.buscarUltimaAlertaDePeso(1L);

    assertNotNull(resultado);
    assertEquals("Atencion: El peso", resultado.getMensaje());
  }

  @Test
  void cuandoNoHayAlertaDePesoDebeRetornarNull() {
    when(repositorioAlertaMock.buscarUltimaAlertaDePesoPorMascota(1L)).thenReturn(null);

    Alerta resultado = servicioAlerta.buscarUltimaAlertaDePeso(1L);

    assertNull(resultado);
  }

  @Test
  void debeMarcarTodasLasAlertasDeUnaMascotaComoLeidas() {
    Long idMascota = 1L;

    servicioAlerta.marcarTodasComoLeidas(idMascota);

    verify(repositorioAlertaMock, times(1)).marcarTodasComoLeidasPorMascota(idMascota);
  }

  @Test
  void debeMarcarTodasLasAlertasDeUnUsuarioComoLeidas() {
    Long idUsuario = 2L;

    servicioAlerta.marcarTodasComoLeidasUsuario(idUsuario);

    verify(repositorioAlertaMock, times(1)).marcarTodasComoLeidasPorUsuario(idUsuario);
  }

  @Test
  void debeEliminarAlertasPorIds() {
    List<Long> ids = Arrays.asList(1L, 2L, 3L);

    servicioAlerta.eliminarAlertas(ids);

    verify(repositorioAlertaMock, times(1)).eliminarPorIds(ids);
  }

  @Test
  void debeEliminarAlertasConListaVacia() {
    List<Long> ids = java.util.Collections.emptyList();

    servicioAlerta.eliminarAlertas(ids);

    verify(repositorioAlertaMock, times(1)).eliminarPorIds(ids);
  }

  @Test
  void debeObtenerEmergenciasActivasPorUsuario() {
    Mascota mascota = new Mascota();
    mascota.setNombre("Firulais");

    Alerta emergencia = new Alerta();
    emergencia.setId(1L);
    emergencia.setTipo(TipoAlerta.EMERGENCIA);
    emergencia.setMensaje("Emergencia activa");
    emergencia.setMascota(mascota);
    emergencia.setLeido(false);
    emergencia.setFechaYHora(LocalDateTime.now());

    when(repositorioAlertaMock.buscarEmergenciasActivasPorUsuario(1L))
            .thenReturn(Arrays.asList(emergencia));

    List<java.util.Map<String, Object>> resultado =
            servicioAlerta.obtenerEmergenciasActivasPorUsuario(1L);

    assertEquals(1, resultado.size());
    assertEquals(1L, resultado.get(0).get("id"));
    assertEquals("Emergencia activa", resultado.get(0).get("mensaje"));
    assertEquals("Firulais", resultado.get(0).get("nombreMascota"));
  }

  @Test
  void debeRetornarListaVaciaSiNoHayEmergenciasActivas() {
    when(repositorioAlertaMock.buscarEmergenciasActivasPorUsuario(1L))
            .thenReturn(java.util.Collections.emptyList());

    List<java.util.Map<String, Object>> resultado =
            servicioAlerta.obtenerEmergenciasActivasPorUsuario(1L);

    assertTrue(resultado.isEmpty());
  }
}
