package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.SolicitudTransferenciaDao;
import com.tallerwebi.dominio.enums.EstadoTransferencia;
import com.tallerwebi.dominio.excepcion.AccionNoPermitidaEnEsteEstadoException;
import com.tallerwebi.dominio.excepcion.NoSonAmigosException;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.SolicitudTransferencia;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioTransferenciaMascotaImplTest {

  private ServicioTransferenciaMascotaImpl servicio;
  private SolicitudTransferenciaDao solicitudTransferenciaDaoMock;
  private MascotaDao mascotaDaoMock;
  private RepositorioUsuario repositorioUsuarioMock;
  private ServicioAmistad servicioAmistadMock;

  @BeforeEach
  public void init() {
    solicitudTransferenciaDaoMock = mock(SolicitudTransferenciaDao.class);
    mascotaDaoMock = mock(MascotaDao.class);
    repositorioUsuarioMock = mock(RepositorioUsuario.class);
    servicioAmistadMock = mock(ServicioAmistad.class);
    servicio =
      new ServicioTransferenciaMascotaImpl(
        solicitudTransferenciaDaoMock,
        mascotaDaoMock,
        repositorioUsuarioMock,
        servicioAmistadMock
      );
  }

  // ── iniciarTransferencia ─────────────────────────────────────────

  @Test
  public void dadosDosAmigosDebeIniciarLaTransferenciaEnEstadoPendiente() {
    Mascota mascota = new Mascota();
    mascota.setId(5L);
    Usuario origen = mock(Usuario.class);
    Usuario destino = mock(Usuario.class);

    when(servicioAmistadMock.sonAmigos(1L, 2L)).thenReturn(true);
    when(mascotaDaoMock.buscarPorId(5L)).thenReturn(mascota);
    when(repositorioUsuarioMock.buscarPorId(1L)).thenReturn(origen);
    when(repositorioUsuarioMock.buscarPorId(2L)).thenReturn(destino);

    SolicitudTransferencia solicitud = servicio.iniciarTransferencia(5L, 1L, 2L);

    assertThat(solicitud.getMascota(), equalTo(mascota));
    assertThat(solicitud.getUsuarioOrigen(), equalTo(origen));
    assertThat(solicitud.getUsuarioDestino(), equalTo(destino));
    assertThat(solicitud.getEstado(), equalTo(EstadoTransferencia.PENDIENTE));
    verify(solicitudTransferenciaDaoMock, times(1)).guardar(solicitud);
  }

  @Test
  public void siLosUsuariosNoSonAmigosDebeLanzarExcepcionYNoGuardarNada() {
    when(servicioAmistadMock.sonAmigos(1L, 2L)).thenReturn(false);

    assertThrows(NoSonAmigosException.class, () -> servicio.iniciarTransferencia(5L, 1L, 2L));
    verify(solicitudTransferenciaDaoMock, never()).guardar(org.mockito.ArgumentMatchers.any());
  }

  // ── confirmarPorOrigen ───────────────────────────────────────────

  @Test
  public void dadaUnaSolicitudPendienteAlConfirmarPorOrigenDebeQuedarMarcadaSinCompletarse() {
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    when(solicitudTransferenciaDaoMock.buscarPorId(20L)).thenReturn(solicitud);

    servicio.confirmarPorOrigen(20L);

    assertThat(solicitud.getConfirmadaPorOrigen(), equalTo(true));
    assertThat(solicitud.getEstado(), equalTo(EstadoTransferencia.PENDIENTE));
    verify(solicitudTransferenciaDaoMock, times(1)).modificar(solicitud);
    verify(mascotaDaoMock, never()).modificar(org.mockito.ArgumentMatchers.any());
  }

  @Test
  public void siElDestinoYaHabiaConfirmadoAlConfirmarPorOrigenDebeCompletarLaTransferencia() {
    Mascota mascota = new Mascota();
    Usuario destino = mock(Usuario.class);

    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    solicitud.setMascota(mascota);
    solicitud.setUsuarioDestino(destino);
    solicitud.setConfirmadaPorDestino(true);
    when(solicitudTransferenciaDaoMock.buscarPorId(20L)).thenReturn(solicitud);

    servicio.confirmarPorOrigen(20L);

    assertThat(solicitud.getEstado(), equalTo(EstadoTransferencia.COMPLETADA));
    assertThat(mascota.getUsuario(), equalTo(destino));
    verify(mascotaDaoMock, times(1)).modificar(mascota);
    verify(solicitudTransferenciaDaoMock, times(1)).modificar(solicitud);
  }

  @Test
  public void dadaUnaSolicitudYaCompletadaAlConfirmarPorOrigenDebeLanzarExcepcion() {
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    solicitud.setEstado(EstadoTransferencia.COMPLETADA);
    when(solicitudTransferenciaDaoMock.buscarPorId(20L)).thenReturn(solicitud);

    assertThrows(
      AccionNoPermitidaEnEsteEstadoException.class,
      () -> servicio.confirmarPorOrigen(20L)
    );
  }

  // ── confirmarPorDestino ──────────────────────────────────────────

  @Test
  public void dadaUnaSolicitudPendienteAlConfirmarPorDestinoDebeQuedarMarcadaSinCompletarse() {
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    when(solicitudTransferenciaDaoMock.buscarPorId(21L)).thenReturn(solicitud);

    servicio.confirmarPorDestino(21L);

    assertThat(solicitud.getConfirmadaPorDestino(), equalTo(true));
    assertThat(solicitud.getEstado(), equalTo(EstadoTransferencia.PENDIENTE));
    verify(solicitudTransferenciaDaoMock, times(1)).modificar(solicitud);
    verify(mascotaDaoMock, never()).modificar(org.mockito.ArgumentMatchers.any());
  }

  @Test
  public void siElOrigenYaHabiaConfirmadoAlConfirmarPorDestinoDebeCompletarLaTransferencia() {
    Mascota mascota = new Mascota();
    Usuario destino = mock(Usuario.class);

    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    solicitud.setMascota(mascota);
    solicitud.setUsuarioDestino(destino);
    solicitud.setConfirmadaPorOrigen(true);
    when(solicitudTransferenciaDaoMock.buscarPorId(21L)).thenReturn(solicitud);

    servicio.confirmarPorDestino(21L);

    assertThat(solicitud.getEstado(), equalTo(EstadoTransferencia.COMPLETADA));
    assertThat(mascota.getUsuario(), equalTo(destino));
    verify(mascotaDaoMock, times(1)).modificar(mascota);
    verify(solicitudTransferenciaDaoMock, times(1)).modificar(solicitud);
  }

  @Test
  public void dadaUnaSolicitudCanceladaAlConfirmarPorDestinoDebeLanzarExcepcion() {
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    solicitud.setEstado(EstadoTransferencia.CANCELADA);
    when(solicitudTransferenciaDaoMock.buscarPorId(21L)).thenReturn(solicitud);

    assertThrows(
      AccionNoPermitidaEnEsteEstadoException.class,
      () -> servicio.confirmarPorDestino(21L)
    );
  }

  // ── cancelarTransferencia ────────────────────────────────────────

  @Test
  public void dadaUnaSolicitudPendienteAlCancelarDebeQuedarCancelada() {
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    when(solicitudTransferenciaDaoMock.buscarPorId(30L)).thenReturn(solicitud);

    servicio.cancelarTransferencia(30L);

    assertThat(solicitud.getEstado(), equalTo(EstadoTransferencia.CANCELADA));
    verify(solicitudTransferenciaDaoMock, times(1)).modificar(solicitud);
  }

  @Test
  public void dadaUnaSolicitudYaCompletadaAlIntentarCancelarDebeLanzarExcepcion() {
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    solicitud.setEstado(EstadoTransferencia.COMPLETADA);
    when(solicitudTransferenciaDaoMock.buscarPorId(30L)).thenReturn(solicitud);

    assertThrows(
      AccionNoPermitidaEnEsteEstadoException.class,
      () -> servicio.cancelarTransferencia(30L)
    );
  }

  // ── obtenerPendientesPorUsuario ──────────────────────────────────

  @Test
  public void obtenerPendientesPorUsuarioDebeDelegarEnElDao() {
    SolicitudTransferencia pendiente = new SolicitudTransferencia();
    when(solicitudTransferenciaDaoMock.buscarPendientesPorUsuario(1L))
      .thenReturn(List.of(pendiente));

    List<SolicitudTransferencia> pendientes = servicio.obtenerPendientesPorUsuario(1L);

    assertThat(pendientes, contains(pendiente));
  }
}
