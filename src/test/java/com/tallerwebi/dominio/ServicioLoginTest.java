package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.modelo.Usuario;
import com.tallerwebi.dominio.servicio.ServicioLogin;
import com.tallerwebi.dominio.servicio.ServicioLoginImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class ServicioLoginTest {

  private ServicioLogin servicioLogin;
  private RepositorioUsuario repositorioUsuarioMock;
  private MascotaDao mascotaDaoMock;

  @BeforeEach
  public void init() {
    this.repositorioUsuarioMock = mock(RepositorioUsuario.class);
    this.mascotaDaoMock = mock(MascotaDao.class);
    this.servicioLogin = new ServicioLoginImpl(this.repositorioUsuarioMock, this.mascotaDaoMock);
  }

  @Test
  public void consultarUsuarioDeberiaLlamarAlRepositorio() {
    // preparacion
    String email = "test@test.com";
    String password = "password";
    Usuario usuarioEsperado = new Usuario();
    usuarioEsperado.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
    when(this.repositorioUsuarioMock.buscar(email)).thenReturn(usuarioEsperado);

    // ejecucion
    Usuario usuarioObtenido = this.servicioLogin.consultarUsuario(email, password);

    // validacion
    assertThat(usuarioObtenido, equalTo(usuarioEsperado));
    verify(this.repositorioUsuarioMock, times(1)).buscar(email);
  }

  @Test
  public void registrarUsuarioSiNoExisteDeberiaGuardarlo() throws UsuarioExistente {
    // preparacion
    Usuario usuario = new Usuario();
    usuario.setEmail("nuevo@test.com");
    usuario.setPassword("123");
    when(this.repositorioUsuarioMock.buscar(usuario.getEmail())).thenReturn(null);

    // ejecucion
    this.servicioLogin.registrar(usuario);

    // validacion
    verify(this.repositorioUsuarioMock, times(1)).guardar(usuario);
  }

  @Test
  public void registrarUsuarioSiExisteDeberiaLanzarExcepcion() {
    // preparacion
    Usuario usuario = new Usuario();
    usuario.setEmail("existe@test.com");
    usuario.setPassword("123");
    when(this.repositorioUsuarioMock.buscar(usuario.getEmail())).thenReturn(new Usuario());

    // ejecucion y validacion
    assertThrows(UsuarioExistente.class, () -> this.servicioLogin.registrar(usuario));
    verify(this.repositorioUsuarioMock, times(0)).guardar(usuario);
  }
}
