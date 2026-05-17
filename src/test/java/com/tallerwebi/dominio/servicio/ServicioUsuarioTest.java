package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
