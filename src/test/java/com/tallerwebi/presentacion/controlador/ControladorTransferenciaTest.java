package com.tallerwebi.presentacion.controlador;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.enums.EstadoTransferencia;
import com.tallerwebi.dominio.excepcion.AccionNoPermitidaEnEsteEstadoException;
import com.tallerwebi.dominio.excepcion.NoSonAmigosException;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.SolicitudTransferencia;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioAmistad;
import com.tallerwebi.dominio.servicio.ServicioMascota;
import com.tallerwebi.dominio.servicio.ServicioTransferenciaMascota;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorTransferenciaTest {

  private static final String ATRIBUTO_ID_USUARIO = "ID_USUARIO";

  private ControladorTransferencia controlador;
  private ServicioTransferenciaMascota servicioTransferenciaMascotaMock;
  private ServicioMascota servicioMascotaMock;
  private ServicioAmistad servicioAmistadMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioTransferenciaMascotaMock = mock(ServicioTransferenciaMascota.class);
    servicioMascotaMock = mock(ServicioMascota.class);
    servicioAmistadMock = mock(ServicioAmistad.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);

    controlador =
      new ControladorTransferencia(
        servicioTransferenciaMascotaMock,
        servicioMascotaMock,
        servicioAmistadMock
      );
  }

  private void simularUsuarioLogueado(Long idUsuario) {
    when(sessionMock.getAttribute(ATRIBUTO_ID_USUARIO)).thenReturn(idUsuario);
  }

  @Test
  public void siNoHayUsuarioLogueadoVerTransferenciasDebeRedirigirALogin() {
    ModelAndView mav = controlador.verTransferencias(requestMock, null);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siElUsuarioTieneMascotasVerTransferenciasDebeMostrarLaVistaConElModeloCompleto() {
    simularUsuarioLogueado(1L);
    List<Mascota> misMascotas = List.of(new Mascota());
    List<SolicitudTransferencia> pendientes = List.of(new SolicitudTransferencia());
    List<Usuario> amigos = List.of(mock(Usuario.class));
    when(servicioMascotaMock.obtenerMascotasPorUsuario(1L)).thenReturn(misMascotas);
    when(servicioTransferenciaMascotaMock.obtenerPendientesPorUsuario(1L)).thenReturn(pendientes);
    when(servicioAmistadMock.obtenerAmigos(1L)).thenReturn(amigos);

    ModelAndView mav = controlador.verTransferencias(requestMock, 5L);

    assertThat(mav.getViewName(), equalTo("transferencias"));
    assertThat(mav.getModel().get("misMascotas"), equalTo(misMascotas));
    assertThat(mav.getModel().get("transferenciasPendientes"), equalTo(pendientes));
    assertThat(mav.getModel().get("amigos"), equalTo(amigos));
    assertThat(mav.getModel().get("idUsuarioActual"), equalTo(1L));
    assertThat(mav.getModel().get("idMascota"), equalTo(5L));
  }

  @Test
  public void siNoHayUsuarioLogueadoIniciarTransferenciaDebeRedirigirALogin() {
    ModelAndView mav = controlador.iniciarTransferencia(requestMock, 5L, 2L);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siLaTransferenciaSeIniciaCorrectamenteDebeRedirigirAExito() {
    simularUsuarioLogueado(1L);

    ModelAndView mav = controlador.iniciarTransferencia(requestMock, 5L, 2L);

    assertThat(mav.getViewName(), equalTo("redirect:/transferencias?exito=true&idMascota=5"));
  }

  @Test
  public void siLosUsuariosNoSonAmigosIniciarTransferenciaDebeRedirigirConError() {
    simularUsuarioLogueado(1L);
    when(servicioTransferenciaMascotaMock.iniciarTransferencia(5L, 1L, 2L))
      .thenThrow(new NoSonAmigosException("Solo podés transferir mascotas a tus amigos"));

    ModelAndView mav = controlador.iniciarTransferencia(requestMock, 5L, 2L);

    assertThat(
      mav.getViewName(),
      equalTo(
        "redirect:/transferencias?error=Solo podés transferir mascotas a tus amigos&idMascota=5"
      )
    );
  }

  @Test
  public void siNoHayUsuarioLogueadoConfirmarPorOrigenDebeRedirigirALogin() {
    ModelAndView mav = controlador.confirmarPorOrigen(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siLaConfirmacionPorOrigenNoCompletaLaTransferenciaDebeRedirigirAExito() {
    simularUsuarioLogueado(1L);
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    when(servicioTransferenciaMascotaMock.confirmarPorOrigen(10L)).thenReturn(solicitud);

    ModelAndView mav = controlador.confirmarPorOrigen(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/transferencias?exito=true&idMascota=5"));
  }

  @Test
  public void siLaTransferenciaSeCompletaYElLogueadoEraElOrigenDebeRedirigirASinMascota() {
    simularUsuarioLogueado(1L);
    Usuario origen = mock(Usuario.class);
    when(origen.getId()).thenReturn(1L);
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    solicitud.setUsuarioOrigen(origen);
    solicitud.setEstado(EstadoTransferencia.COMPLETADA);
    when(servicioTransferenciaMascotaMock.confirmarPorOrigen(10L)).thenReturn(solicitud);

    ModelAndView mav = controlador.confirmarPorOrigen(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/sin-mascota"));
  }

  @Test
  public void siLaConfirmacionPorOrigenNoEstaPermitidaDebeRedirigirConError() {
    simularUsuarioLogueado(1L);
    when(servicioTransferenciaMascotaMock.confirmarPorOrigen(10L))
      .thenThrow(
        new AccionNoPermitidaEnEsteEstadoException(
          "No se puede confirmar una transferencia en estado COMPLETADA"
        )
      );

    ModelAndView mav = controlador.confirmarPorOrigen(requestMock, 10L, 5L);

    assertThat(
      mav.getViewName(),
      equalTo(
        "redirect:/transferencias?error=No se puede confirmar una transferencia en estado COMPLETADA&idMascota=5"
      )
    );
  }

  @Test
  public void siNoHayUsuarioLogueadoConfirmarPorDestinoDebeRedirigirALogin() {
    ModelAndView mav = controlador.confirmarPorDestino(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siLaConfirmacionPorDestinoNoCompletaLaTransferenciaDebeRedirigirAExito() {
    simularUsuarioLogueado(2L);
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    when(servicioTransferenciaMascotaMock.confirmarPorDestino(10L)).thenReturn(solicitud);

    ModelAndView mav = controlador.confirmarPorDestino(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/transferencias?exito=true&idMascota=5"));
  }

  @Test
  public void siLaTransferenciaSeCompletaPeroElLogueadoEraElDestinoDebeRedirigirAExito() {
    simularUsuarioLogueado(2L);
    Usuario origen = mock(Usuario.class);
    when(origen.getId()).thenReturn(1L);
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    solicitud.setUsuarioOrigen(origen);
    solicitud.setEstado(EstadoTransferencia.COMPLETADA);
    when(servicioTransferenciaMascotaMock.confirmarPorDestino(10L)).thenReturn(solicitud);

    ModelAndView mav = controlador.confirmarPorDestino(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/transferencias?exito=true&idMascota=5"));
  }

  @Test
  public void siLaConfirmacionPorDestinoNoEstaPermitidaDebeRedirigirConError() {
    simularUsuarioLogueado(2L);
    when(servicioTransferenciaMascotaMock.confirmarPorDestino(10L))
      .thenThrow(
        new AccionNoPermitidaEnEsteEstadoException(
          "No se puede confirmar una transferencia en estado CANCELADA"
        )
      );

    ModelAndView mav = controlador.confirmarPorDestino(requestMock, 10L, 5L);

    assertThat(
      mav.getViewName(),
      equalTo(
        "redirect:/transferencias?error=No se puede confirmar una transferencia en estado CANCELADA&idMascota=5"
      )
    );
  }

  @Test
  public void siNoHayUsuarioLogueadoCancelarTransferenciaDebeRedirigirALogin() {
    ModelAndView mav = controlador.cancelarTransferencia(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siSeCancelaLaTransferenciaDebeRedirigirAExitoYLlamarAlServicio() {
    simularUsuarioLogueado(1L);

    ModelAndView mav = controlador.cancelarTransferencia(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/transferencias?exito=true&idMascota=5"));
    verify(servicioTransferenciaMascotaMock, times(1)).cancelarTransferencia(10L);
  }

  @Test
  public void siElUsuarioTieneMascotasYNoVieneCOnIdMascotaVerTransferenciasDebeRedirigirAlDashboard() {
    simularUsuarioLogueado(1L);
    Mascota mascota = new Mascota();
    mascota.setId(7L);
    when(servicioMascotaMock.obtenerMascotasPorUsuario(1L)).thenReturn(List.of(mascota));

    ModelAndView mav = controlador.verTransferencias(requestMock, null);

    assertThat(mav.getViewName(), equalTo("redirect:/analisis/dashboard/7"));
  }

  @Test
  public void siElUsuarioNoTieneMascotasNiPendientesVerTransferenciasDebeRedirigirASinMascota() {
    simularUsuarioLogueado(1L);
    when(servicioMascotaMock.obtenerMascotasPorUsuario(1L)).thenReturn(List.of());
    when(servicioTransferenciaMascotaMock.obtenerPendientesPorUsuario(1L)).thenReturn(List.of());

    ModelAndView mav = controlador.verTransferencias(requestMock, null);

    assertThat(mav.getViewName(), equalTo("redirect:/sin-mascota"));
  }

  @Test
  public void siLaTransferenciaSeCompletaYElOrigenTieneMascotasRestantesDebeRedirigirAlDashboard() {
    simularUsuarioLogueado(1L);
    Usuario origen = mock(Usuario.class);
    when(origen.getId()).thenReturn(1L);
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    solicitud.setUsuarioOrigen(origen);
    solicitud.setEstado(EstadoTransferencia.COMPLETADA);
    when(servicioTransferenciaMascotaMock.confirmarPorOrigen(10L)).thenReturn(solicitud);
    Mascota mascotaRestante = new Mascota();
    mascotaRestante.setId(99L);
    when(servicioMascotaMock.obtenerMascotasPorUsuario(1L)).thenReturn(List.of(mascotaRestante));

    ModelAndView mav = controlador.confirmarPorOrigen(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/analisis/dashboard/99"));
  }

  @Test
  public void siLaTransferenciaSeCompletaYElLogueadoEraElDestinoDebeRedirigirAlDashboardDeLaMascota() {
    simularUsuarioLogueado(2L);
    Usuario destino = mock(Usuario.class);
    when(destino.getId()).thenReturn(2L);
    Mascota mascota = new Mascota();
    mascota.setId(42L);
    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    solicitud.setUsuarioDestino(destino);
    solicitud.setEstado(EstadoTransferencia.COMPLETADA);
    solicitud.setMascota(mascota);
    when(servicioTransferenciaMascotaMock.confirmarPorDestino(10L)).thenReturn(solicitud);

    ModelAndView mav = controlador.confirmarPorDestino(requestMock, 10L, 5L);

    assertThat(mav.getViewName(), equalTo("redirect:/analisis/dashboard/42"));
  }

  @Test
  public void cancelarTransferenciaDebeInvocarElServicioExactamenteUnaVez() {
    simularUsuarioLogueado(1L);

    controlador.cancelarTransferencia(requestMock, 10L, null);

    verify(servicioTransferenciaMascotaMock, times(1)).cancelarTransferencia(10L);
  }

  @Test
  public void cancelarTransferenciaSinIdMascotaDebeRedirigirASoloExito() {
    simularUsuarioLogueado(1L);

    ModelAndView mav = controlador.cancelarTransferencia(requestMock, 10L, null);

    assertThat(mav.getViewName(), equalTo("redirect:/transferencias?exito=true"));
  }
  
  @Test
  public void siNoHayUsuarioLogueadoEstadoTransferenciasDebeDevolverErrorNoSession() {
    java.util.Map<String, Object> respuesta = controlador.estadoTransferencias(requestMock);

    assertThat(respuesta.get("error"), equalTo("no-session"));
  }

  @Test
  public void siHayUsuarioLogueadoEstadoTransferenciasDebeDevolverHashDePendientesYMascotas() {
    simularUsuarioLogueado(1L);

    SolicitudTransferencia solicitud = new SolicitudTransferencia();
    solicitud.setId(10L);
    solicitud.setEstado(EstadoTransferencia.PENDIENTE);

    Mascota mascota = new Mascota();
    mascota.setId(7L);

    when(servicioTransferenciaMascotaMock.obtenerPendientesPorUsuario(1L))
      .thenReturn(List.of(solicitud));
    when(servicioMascotaMock.obtenerMascotasPorUsuario(1L)).thenReturn(List.of(mascota));

    java.util.Map<String, Object> respuesta = controlador.estadoTransferencias(requestMock);

    assertThat(respuesta.get("hash"), equalTo("10:PENDIENTE|7"));
  }

  @Test
  public void siNoHayPendientesNiMascotasEstadoTransferenciasDebeDevolverHashVacio() {
    simularUsuarioLogueado(1L);
    when(servicioTransferenciaMascotaMock.obtenerPendientesPorUsuario(1L)).thenReturn(List.of());
    when(servicioMascotaMock.obtenerMascotasPorUsuario(1L)).thenReturn(List.of());

    java.util.Map<String, Object> respuesta = controlador.estadoTransferencias(requestMock);

    assertThat(respuesta.get("hash"), equalTo("|"));
  }
  
  @Test
  public void siElUsuarioNoTieneMascotasPeroTienePendientesVerTransferenciasDebeMostrarLaVista() {
    simularUsuarioLogueado(1L);
    List<SolicitudTransferencia> pendientes = List.of(new SolicitudTransferencia());
    List<Usuario> amigos = List.of(mock(Usuario.class));
    when(servicioMascotaMock.obtenerMascotasPorUsuario(1L)).thenReturn(List.of());
    when(servicioTransferenciaMascotaMock.obtenerPendientesPorUsuario(1L)).thenReturn(pendientes);
    when(servicioAmistadMock.obtenerAmigos(1L)).thenReturn(amigos);

    ModelAndView mav = controlador.verTransferencias(requestMock, null);

    assertThat(mav.getViewName(), equalTo("transferencias"));
    assertThat(mav.getModel().get("misMascotas"), equalTo(List.of()));
    assertThat(mav.getModel().get("transferenciasPendientes"), equalTo(pendientes));
  }
}
