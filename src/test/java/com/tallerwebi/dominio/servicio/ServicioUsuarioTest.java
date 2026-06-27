package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.SolicitudAmistadDao;
import com.tallerwebi.dominio.dao.SolicitudTransferenciaDao;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.modelo.Direccion;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.presentacion.DatosPerfil;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class ServicioUsuarioTest {

  private RepositorioUsuario repositorioMock;
  private MascotaDao mascotaDaoMock;
  private RepositorioAlerta repositorioAlertaMock;
  private SolicitudAmistadDao solicitudAmistadDaoMock;
  private SolicitudTransferenciaDao solicitudTransferenciaDaoMock;
  private ServicioUsuario servicio;

  @BeforeEach
  public void init() {
    repositorioMock = mock(RepositorioUsuario.class);
    mascotaDaoMock = mock(MascotaDao.class);
    repositorioAlertaMock = mock(RepositorioAlerta.class);
    solicitudAmistadDaoMock = mock(SolicitudAmistadDao.class);
    solicitudTransferenciaDaoMock = mock(SolicitudTransferenciaDao.class);
    servicio =
      new ServicioUsuarioImpl(
        repositorioMock,
        mascotaDaoMock,
        repositorioAlertaMock,
        solicitudAmistadDaoMock,
        solicitudTransferenciaDaoMock
      );
  }

  // ─── obtenerPerfil ────────────────────────────────────────────────────────

  @Test
  public void queSePuedaObtenerUnPerfilPorId() {
    Long idBuscado = 1L;
    Usuario usuarioSimulado = new Usuario();
    when(repositorioMock.buscarPorId(idBuscado)).thenReturn(usuarioSimulado);

    Usuario resultado = servicio.obtenerPerfil(idBuscado);

    assertNotNull(resultado, "El usuario no debe ser nulo");
    verify(repositorioMock, times(1)).buscarPorId(idBuscado);
  }

  @Test
  public void queObtenerPerfilDevuelveNullSiElUsuarioNoExiste() {
    when(repositorioMock.buscarPorId(99L)).thenReturn(null);

    Usuario resultado = servicio.obtenerPerfil(99L);

    assertNull(resultado, "Debe retornar null si el usuario no existe");
  }

  @Test
  public void queObtenerPerfilLlamaBuscarPorIdConElIdCorrecto() {
    Long id = 42L;
    when(repositorioMock.buscarPorId(id)).thenReturn(new Usuario());

    servicio.obtenerPerfil(id);

    verify(repositorioMock, times(1)).buscarPorId(42L);
  }

  // ─── obtenerDatosPerfil ──────────────────────────────────────────────────

  @Test
  public void queObtenerDatosPerfilDevuelveDatosDelUsuario() {
    Long id = 1L;
    Usuario usuario = new Usuario();
    usuario.setNombre("Ana");
    usuario.setEmail("ana@mail.com");
    usuario.setTelefono(1234L);
    when(repositorioMock.buscarPorId(id)).thenReturn(usuario);

    DatosPerfil datos = servicio.obtenerDatosPerfil(id);

    assertEquals("Ana", datos.getNombre());
    assertEquals("ana@mail.com", datos.getEmail());
    assertEquals(1234L, datos.getTelefono());
  }

  @Test
  public void queObtenerDatosPerfilDevuelveDireccionSiElUsuarioLaTiene() {
    Long id = 1L;
    Usuario usuario = new Usuario();
    Direccion dir = new Direccion();
    dir.setCalle("Av. Corrientes 1234");
    dir.setCiudad("Buenos Aires");
    usuario.setUbicacion(dir);
    when(repositorioMock.buscarPorId(id)).thenReturn(usuario);

    DatosPerfil datos = servicio.obtenerDatosPerfil(id);

    assertEquals("Av. Corrientes 1234", datos.getCalle());
    assertEquals("Buenos Aires", datos.getCiudad());
  }

  @Test
  public void queObtenerDatosPerfilLanzaExcepcionSiElUsuarioNoExiste() {
    when(repositorioMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(UsuarioNoEncontrado.class, () -> servicio.obtenerDatosPerfil(99L));
  }

  // ─── eliminar ─────────────────────────────────────────────────────────────

  @Test
  public void queAlEliminarUnUsuarioSuEstadoPaseAInactivo() {
    Usuario usuario = new Usuario();
    usuario.setActivo(true);
    when(repositorioMock.buscarPorId(1L)).thenReturn(usuario);

    servicio.eliminar(1L);

    assertFalse(usuario.getActivo(), "El usuario debe estar inactivo");
  }

  @Test
  public void queNoOcurraNingunErrorAlIntentarEliminarUnUsuarioInexistente() {
    when(repositorioMock.buscarPorId(99L)).thenReturn(null);

    assertDoesNotThrow(() -> servicio.eliminar(99L));
    verify(repositorioMock, times(1)).buscarPorId(99L);
  }

  @Test
  public void queAlEliminarUnUsuarioNoSeLlamaAModificar() {
    Usuario usuario = new Usuario();
    usuario.setActivo(true);
    when(repositorioMock.buscarPorId(1L)).thenReturn(usuario);

    servicio.eliminar(1L);

    verify(repositorioMock, never()).modificar(any());
  }

  // ─── actualizarPerfil ────────────────────────────────────────────────────

  @Test
  public void queAlActualizarPerfilSeModifiqueElNombreCorrectamente() {
    Long id = 1L;
    Usuario usuario = new Usuario();
    usuario.setNombre("ViEjo");
    when(repositorioMock.buscarPorId(id)).thenReturn(usuario);

    DatosPerfil datos = new DatosPerfil();
    datos.setNombre("Nuevo");

    servicio.actualizarPerfil(id, datos);

    assertEquals("Nuevo", usuario.getNombre());
  }

  @Test
  public void queAlActualizarPerfilSeModifiqueElEmailCorrectamente() {
    Long id = 1L;
    Usuario usuario = new Usuario();
    when(repositorioMock.buscarPorId(id)).thenReturn(usuario);

    DatosPerfil datos = new DatosPerfil();
    datos.setEmail("nuevo@mail.com");

    servicio.actualizarPerfil(id, datos);

    assertEquals("nuevo@mail.com", usuario.getEmail());
  }

  @Test
  public void queAlActualizarPerfilConPasswordSeLaHashea() {
    Long id = 1L;
    Usuario usuario = new Usuario();
    when(repositorioMock.buscarPorId(id)).thenReturn(usuario);

    DatosPerfil datos = new DatosPerfil();
    datos.setPassword("miPassword123");

    servicio.actualizarPerfil(id, datos);

    assertNotNull(usuario.getPassword());
    assertNotEquals("miPassword123", usuario.getPassword());
    assertTrue(BCrypt.checkpw("miPassword123", usuario.getPassword()));
  }

  @Test
  public void queAlActualizarPerfilConPasswordVaciaNoSeCambiaLaPassword() {
    Long id = 1L;
    Usuario usuario = new Usuario();
    String passwordOriginal = BCrypt.hashpw("original", BCrypt.gensalt());
    usuario.setPassword(passwordOriginal);
    when(repositorioMock.buscarPorId(id)).thenReturn(usuario);

    DatosPerfil datos = new DatosPerfil();
    datos.setPassword("");

    servicio.actualizarPerfil(id, datos);

    assertEquals(passwordOriginal, usuario.getPassword());
  }

  @Test
  public void queAlActualizarPerfilConPasswordNulaNoSeCambiaLaPassword() {
    Long id = 1L;
    Usuario usuario = new Usuario();
    String passwordOriginal = BCrypt.hashpw("original", BCrypt.gensalt());
    usuario.setPassword(passwordOriginal);
    when(repositorioMock.buscarPorId(id)).thenReturn(usuario);

    DatosPerfil datos = new DatosPerfil();
    datos.setPassword(null);

    servicio.actualizarPerfil(id, datos);

    assertEquals(passwordOriginal, usuario.getPassword());
  }

  @Test
  public void queAlActualizarPerfilSeCreaDireccionSiElUsuarioNoTenia() {
    Long id = 1L;
    Usuario usuario = new Usuario();
    usuario.setUbicacion(null);
    when(repositorioMock.buscarPorId(id)).thenReturn(usuario);

    DatosPerfil datos = new DatosPerfil();
    datos.setCalle("Av. Siempre Viva 742");
    datos.setCiudad("Springfield");

    servicio.actualizarPerfil(id, datos);

    assertNotNull(usuario.getUbicacion());
    assertEquals("Av. Siempre Viva 742", usuario.getUbicacion().getCalle());
    assertEquals("Springfield", usuario.getUbicacion().getCiudad());
  }

  @Test
  public void queAlActualizarPerfilSeActualizaDireccionExistente() {
    Long id = 1L;
    Usuario usuario = new Usuario();
    Direccion direccionExistente = new Direccion();
    direccionExistente.setCalle("Calle Vieja 1");
    usuario.setUbicacion(direccionExistente);
    when(repositorioMock.buscarPorId(id)).thenReturn(usuario);

    DatosPerfil datos = new DatosPerfil();
    datos.setCalle("Calle Nueva 999");

    servicio.actualizarPerfil(id, datos);

    assertEquals("Calle Nueva 999", usuario.getUbicacion().getCalle());
  }

  @Test
  public void queAlActualizarPerfilSeLlamaAModificarUnaVez() {
    Long id = 1L;
    when(repositorioMock.buscarPorId(id)).thenReturn(new Usuario());

    servicio.actualizarPerfil(id, new DatosPerfil());

    verify(repositorioMock, times(1)).modificar(any(Usuario.class));
  }

  @Test
  public void queAlActualizarPerfilConUsuarioInexistenteLanzaExcepcion() {
    when(repositorioMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(
      UsuarioNoEncontrado.class,
      () -> servicio.actualizarPerfil(99L, new DatosPerfil())
    );
  }

  @Test
  public void queAlActualizarPerfilConUsuarioInexistenteNoSeLlamaAModificar() {
    when(repositorioMock.buscarPorId(99L)).thenReturn(null);

    try {
      servicio.actualizarPerfil(99L, new DatosPerfil());
    } catch (UsuarioNoEncontrado e) {
      // esperada
    }

    verify(repositorioMock, never()).modificar(any());
  }

  // ─── eliminarCuenta ──────────────────────────────────────────────────────

  @Test
  public void queEliminarCuentaDesvinculaTodasLasMascotas() {
    Long id = 1L;
    Mascota m1 = new Mascota();
    Mascota m2 = new Mascota();
    when(mascotaDaoMock.buscarTodoPorUsuarioId(id)).thenReturn(Arrays.asList(m1, m2));
    when(repositorioMock.buscarPorId(id)).thenReturn(new Usuario());

    servicio.eliminarCuenta(id);

    assertNull(m1.getUsuario());
    assertNull(m2.getUsuario());
    verify(mascotaDaoMock, times(2)).modificar(any(Mascota.class));
  }

  @Test
  public void queEliminarCuentaLlamaAEliminarDelRepositorio() {
    Long id = 1L;
    Usuario usuario = new Usuario();
    when(mascotaDaoMock.buscarTodoPorUsuarioId(id)).thenReturn(Collections.emptyList());
    when(repositorioMock.buscarPorId(id)).thenReturn(usuario);

    servicio.eliminarCuenta(id);

    verify(repositorioMock, times(1)).eliminar(usuario);
  }

  @Test
  public void queEliminarCuentaFuncionaSiElUsuarioNoTieneMascotas() {
    Long id = 1L;
    when(mascotaDaoMock.buscarTodoPorUsuarioId(id)).thenReturn(null);
    when(repositorioMock.buscarPorId(id)).thenReturn(new Usuario());

    assertDoesNotThrow(() -> servicio.eliminarCuenta(id));
  }

  @Test
  public void queEliminarCuentaNoLlamaAEliminarSiElUsuarioNoExiste() {
    Long id = 99L;
    when(mascotaDaoMock.buscarTodoPorUsuarioId(id)).thenReturn(Collections.emptyList());
    when(repositorioMock.buscarPorId(id)).thenReturn(null);

    servicio.eliminarCuenta(id);

    verify(repositorioMock, never()).eliminar(any());
  }

  @Test
  public void queEliminarCuentaLlamaAEliminarAlertasPorUsuario() {
    Long id = 1L;
    when(mascotaDaoMock.buscarTodoPorUsuarioId(id)).thenReturn(Collections.emptyList());
    when(repositorioMock.buscarPorId(id)).thenReturn(new Usuario());

    servicio.eliminarCuenta(id);

    verify(repositorioAlertaMock, times(1)).eliminarPorUsuario(id);
  }

  @Test
  public void queEliminarCuentaLlamaAEliminarSolicitudesDeTransferencia() {
    Long id = 1L;
    when(mascotaDaoMock.buscarTodoPorUsuarioId(id)).thenReturn(Collections.emptyList());
    when(repositorioMock.buscarPorId(id)).thenReturn(new Usuario());

    servicio.eliminarCuenta(id);

    verify(solicitudTransferenciaDaoMock, times(1)).eliminarPorUsuario(id);
  }

  @Test
  public void queEliminarCuentaLlamaAEliminarSolicitudesDeAmistad() {
    Long id = 1L;
    when(mascotaDaoMock.buscarTodoPorUsuarioId(id)).thenReturn(Collections.emptyList());
    when(repositorioMock.buscarPorId(id)).thenReturn(new Usuario());

    servicio.eliminarCuenta(id);

    verify(solicitudAmistadDaoMock, times(1)).eliminarPorUsuario(id);
  }
}
