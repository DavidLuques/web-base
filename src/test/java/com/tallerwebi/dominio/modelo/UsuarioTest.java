package com.tallerwebi.dominio.modelo;

import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.Usuario;
import org.junit.jupiter.api.Test;

public class UsuarioTest {

  @Test
  public void queLosGettersYSettersDeUsuarioFuncionen() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    usuario.setEmail("test@correo.com");
    usuario.setPassword("123456");
    usuario.setRol("ADMIN");
    usuario.setActivo(true);

    assertEquals(1L, usuario.getId());
    assertEquals("test@correo.com", usuario.getEmail());
    assertEquals("123456", usuario.getPassword());
    assertEquals("ADMIN", usuario.getRol());
    assertTrue(usuario.getActivo());
  }
}
