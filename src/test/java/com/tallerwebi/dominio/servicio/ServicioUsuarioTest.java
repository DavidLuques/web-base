package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.modelo.Direccion;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.presentacion.DatosPerfil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class ServicioUsuarioTest {

  private RepositorioUsuario repositorioMock;
  private ServicioUsuario servicio;

  @BeforeEach
  public void init() {
    repositorioMock = mock(RepositorioUsuario.class);
    servicio = new ServicioUsuarioImpl(repositorioMock);
  }

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
  public void queAlEliminarUnUsuarioSuEstadoPaseAInactivo() {
    Usuario usuarioSimulado = new Usuario();
    usuarioSimulado.setActivo(true);

    when(repositorioMock.buscarPorId(1L)).thenReturn(usuarioSimulado);

    servicio.eliminar(1L);

    assertFalse(usuarioSimulado.getActivo(), "El usuario debe estar inactivo");
  }

  @Test
  public void queDevuelvaNullSiSeBuscaUnPerfilQueNoExiste() {
    Long idInexistente = 1L;
    when(repositorioMock.buscarPorId(idInexistente)).thenReturn(null);

    Usuario resultado = repositorioMock.buscarPorId(idInexistente);

    assertNull(resultado, "El resultado debe ser null si el usuario no existe");
  }

  @Test
  public void queNoOcurraNingunErrorAlIntentarEliminarUnUsuarioInexistente() {
    Long idInexistente = 99L;
    when(repositorioMock.buscarPorId(idInexistente)).thenReturn(null);
    servicio.eliminar(idInexistente);

    verify(repositorioMock, times(1)).buscarPorId(idInexistente);
  }

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
    Long id = 99L;
    when(repositorioMock.buscarPorId(id)).thenReturn(null);

    assertThrows(RuntimeException.class, () -> servicio.actualizarPerfil(id, new DatosPerfil()));
  }

  @Test
  public void queAlActualizarPerfilConUsuarioInexistenteNoSeLlamaAModificar() {
    Long id = 99L;
    when(repositorioMock.buscarPorId(id)).thenReturn(null);

    try {
      servicio.actualizarPerfil(id, new DatosPerfil());
    } catch (RuntimeException e) {
      // esperada
    }

    verify(repositorioMock, never()).modificar(any());
  }

  @Test
  public void queAlEliminarUnUsuarioNoSeLlamaAModificar() {
    Long id = 1L;
    Usuario usuario = new Usuario();
    usuario.setActivo(true);
    when(repositorioMock.buscarPorId(id)).thenReturn(usuario);

    servicio.eliminar(id);

    verify(repositorioMock, never()).modificar(any());
  }

  @Test
  public void queObtenerPerfilLlamaBuscarPorIdConElIdCorrecto() {
    Long id = 42L;
    when(repositorioMock.buscarPorId(id)).thenReturn(new Usuario());

    servicio.obtenerPerfil(id);

    verify(repositorioMock, times(1)).buscarPorId(42L);
  }
}
