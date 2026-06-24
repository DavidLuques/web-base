package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dao.SolicitudAmistadDao;
import com.tallerwebi.dominio.enums.EstadoAmistad;
import com.tallerwebi.dominio.excepcion.AccionNoPermitidaEnEsteEstadoException;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.modelo.SolicitudAmistad;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioAmistadImplTest {

  private ServicioAmistadImpl servicio;
  private SolicitudAmistadDao solicitudAmistadDaoMock;
  private RepositorioUsuario repositorioUsuarioMock;
  private ServicioAlerta servicioAlertaMock;

  @BeforeEach
  public void init() {
    solicitudAmistadDaoMock = mock(SolicitudAmistadDao.class);
    repositorioUsuarioMock = mock(RepositorioUsuario.class);
    servicioAlertaMock = mock(ServicioAlerta.class);

    servicio =
      new ServicioAmistadImpl(solicitudAmistadDaoMock, repositorioUsuarioMock, servicioAlertaMock);
  }

  // ── enviarSolicitud ──────────────────────────────────────────────

  @Test
  public void dadosDosUsuariosSinSolicitudPreviaDebeCrearSolicitudPendiente() {
    Usuario emisor = mock(Usuario.class);
    Usuario receptor = mock(Usuario.class);
    when(solicitudAmistadDaoMock.buscarEntreUsuarios(1L, 2L)).thenReturn(null);
    when(repositorioUsuarioMock.buscarPorId(1L)).thenReturn(emisor);
    when(repositorioUsuarioMock.buscarPorId(2L)).thenReturn(receptor);

    SolicitudAmistad solicitud = servicio.enviarSolicitud(1L, 2L);

    assertThat(solicitud.getEmisor(), equalTo(emisor));
    assertThat(solicitud.getReceptor(), equalTo(receptor));
    assertThat(solicitud.getEstado(), equalTo(EstadoAmistad.PENDIENTE));
    verify(solicitudAmistadDaoMock, times(1)).guardar(solicitud);
  }

  @Test
  public void siYaExisteUnaSolicitudEntreLosUsuariosDebeLanzarExcepcion() {
    SolicitudAmistad existente = new SolicitudAmistad();
    when(solicitudAmistadDaoMock.buscarEntreUsuarios(1L, 2L)).thenReturn(existente);

    assertThrows(
      AccionNoPermitidaEnEsteEstadoException.class,
      () -> servicio.enviarSolicitud(1L, 2L)
    );
  }

  // ── aceptarSolicitud ─────────────────────────────────────────────

  @Test
  public void dadaUnaSolicitudPendienteAlAceptarDebeQuedarAceptada() {
    Usuario emisor = mock(Usuario.class);
    Usuario receptor = mock(Usuario.class);
    when(receptor.getNombre()).thenReturn("Ana");

    SolicitudAmistad solicitud = new SolicitudAmistad();
    solicitud.setEmisor(emisor);
    solicitud.setReceptor(receptor);
    when(solicitudAmistadDaoMock.buscarPorId(10L)).thenReturn(solicitud);

    servicio.aceptarSolicitud(10L);

    assertThat(solicitud.getEstado(), equalTo(EstadoAmistad.ACEPTADA));
    verify(solicitudAmistadDaoMock, times(1)).modificar(solicitud);
  }

  @Test
  public void dadaUnaSolicitudYaAceptadaAlIntentarAceptarDebeLanzarExcepcion() {
    SolicitudAmistad solicitud = new SolicitudAmistad();
    solicitud.setEstado(EstadoAmistad.ACEPTADA);
    when(solicitudAmistadDaoMock.buscarPorId(10L)).thenReturn(solicitud);

    assertThrows(
      AccionNoPermitidaEnEsteEstadoException.class,
      () -> servicio.aceptarSolicitud(10L)
    );
  }

  // ── rechazarSolicitud ────────────────────────────────────────────

  @Test
  public void dadaUnaSolicitudPendienteAlRechazarDebeQuedarRechazada() {
    Usuario emisor = mock(Usuario.class);
    Usuario receptor = mock(Usuario.class);
    when(receptor.getNombre()).thenReturn("Ana");

    SolicitudAmistad solicitud = new SolicitudAmistad();
    solicitud.setEmisor(emisor);
    solicitud.setReceptor(receptor);
    when(solicitudAmistadDaoMock.buscarPorId(11L)).thenReturn(solicitud);

    servicio.rechazarSolicitud(11L);

    assertThat(solicitud.getEstado(), equalTo(EstadoAmistad.RECHAZADA));
    verify(solicitudAmistadDaoMock, times(1)).modificar(solicitud);
  }

  @Test
  public void dadaUnaSolicitudYaRechazadaAlIntentarRechazarDebeLanzarExcepcion() {
    SolicitudAmistad solicitud = new SolicitudAmistad();
    solicitud.setEstado(EstadoAmistad.RECHAZADA);
    when(solicitudAmistadDaoMock.buscarPorId(11L)).thenReturn(solicitud);

    assertThrows(
      AccionNoPermitidaEnEsteEstadoException.class,
      () -> servicio.rechazarSolicitud(11L)
    );
  }

  // ── sonAmigos ────────────────────────────────────────────────────

  @Test
  public void dadaUnaSolicitudAceptadaSonAmigosDebeRetornarTrue() {
    SolicitudAmistad solicitud = new SolicitudAmistad();
    solicitud.setEstado(EstadoAmistad.ACEPTADA);
    when(solicitudAmistadDaoMock.buscarEntreUsuarios(1L, 2L)).thenReturn(solicitud);

    boolean sonAmigos = servicio.sonAmigos(1L, 2L);

    assertThat(sonAmigos, equalTo(true));
  }

  @Test
  public void dadaUnaSolicitudPendienteSonAmigosDebeRetornarFalse() {
    SolicitudAmistad solicitud = new SolicitudAmistad();
    when(solicitudAmistadDaoMock.buscarEntreUsuarios(1L, 2L)).thenReturn(solicitud);

    boolean sonAmigos = servicio.sonAmigos(1L, 2L);

    assertThat(sonAmigos, equalTo(false));
  }

  @Test
  public void cuandoNoExisteSolicitudEntreUsuariosSonAmigosDebeRetornarFalse() {
    when(solicitudAmistadDaoMock.buscarEntreUsuarios(1L, 2L)).thenReturn(null);

    boolean sonAmigos = servicio.sonAmigos(1L, 2L);

    assertThat(sonAmigos, equalTo(false));
  }

  // ── obtenerAmigos ────────────────────────────────────────────────

  @Test
  public void dadaUnaListaDeSolicitudesAceptadasDebeRetornarElAmigoCorrectoSegunQuienEsElUsuario() {
    Usuario usuario = mock(Usuario.class);
    when(usuario.getId()).thenReturn(1L);
    Usuario amigoComoReceptor = mock(Usuario.class);
    Usuario amigoComoEmisor = mock(Usuario.class);

    SolicitudAmistad solicitudDondeEsEmisor = new SolicitudAmistad();
    solicitudDondeEsEmisor.setEmisor(usuario);
    solicitudDondeEsEmisor.setReceptor(amigoComoReceptor);

    SolicitudAmistad solicitudDondeEsReceptor = new SolicitudAmistad();
    solicitudDondeEsReceptor.setEmisor(amigoComoEmisor);
    solicitudDondeEsReceptor.setReceptor(usuario);

    when(solicitudAmistadDaoMock.buscarAceptadasPorUsuario(1L))
      .thenReturn(List.of(solicitudDondeEsEmisor, solicitudDondeEsReceptor));

    List<Usuario> amigos = servicio.obtenerAmigos(1L);

    assertThat(amigos, containsInAnyOrder(amigoComoReceptor, amigoComoEmisor));
  }

  @Test
  public void cuandoNoHaySolicitudesAceptadasObtenerAmigosDebeRetornarListaVacia() {
    when(solicitudAmistadDaoMock.buscarAceptadasPorUsuario(1L)).thenReturn(List.of());

    List<Usuario> amigos = servicio.obtenerAmigos(1L);

    assertThat(amigos, empty());
  }

  // ── obtenerSolicitudesPendientes ─────────────────────────────────

  @Test
  public void obtenerSolicitudesPendientesDebeDelegarEnElDao() {
    SolicitudAmistad pendiente = new SolicitudAmistad();
    when(solicitudAmistadDaoMock.buscarPendientesPorReceptor(1L)).thenReturn(List.of(pendiente));

    List<SolicitudAmistad> pendientes = servicio.obtenerSolicitudesPendientes(1L);

    assertThat(pendientes, contains(pendiente));
  }

  // ── enviarSolicitudPorEmail ──────────────────────────────────────

  @Test
  public void dadoUnEmailExistenteDebeEnviarSolicitudAlUsuarioEncontrado() {
    Usuario emisor = mock(Usuario.class);
    Usuario receptor = mock(Usuario.class);
    when(receptor.getId()).thenReturn(2L);
    when(repositorioUsuarioMock.buscar("amigo@mail.com")).thenReturn(receptor);
    when(solicitudAmistadDaoMock.buscarEntreUsuarios(1L, 2L)).thenReturn(null);
    when(repositorioUsuarioMock.buscarPorId(1L)).thenReturn(emisor);
    when(repositorioUsuarioMock.buscarPorId(2L)).thenReturn(receptor);

    SolicitudAmistad solicitud = servicio.enviarSolicitudPorEmail(1L, "amigo@mail.com");

    assertThat(solicitud.getReceptor(), equalTo(receptor));
  }

  @Test
  public void dadoUnEmailInexistenteDebeLanzarUsuarioNoEncontrado() {
    when(repositorioUsuarioMock.buscar("noexiste@mail.com")).thenReturn(null);

    assertThrows(
      UsuarioNoEncontrado.class,
      () -> servicio.enviarSolicitudPorEmail(1L, "noexiste@mail.com")
    );
  }

  @Test
  public void siElEmisorIntentaAgregarseASiMismoPorEmailDebeLanzarExcepcion() {
    Usuario receptor = mock(Usuario.class);
    when(receptor.getId()).thenReturn(1L);
    when(repositorioUsuarioMock.buscar("yomismo@mail.com")).thenReturn(receptor);

    assertThrows(
      AccionNoPermitidaEnEsteEstadoException.class,
      () -> servicio.enviarSolicitudPorEmail(1L, "yomismo@mail.com")
    );
  }
}
