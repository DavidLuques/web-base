package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import com.tallerwebi.punta_a_punta.vistas.VistaNuevoUsuario;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaLoginE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaLogin vistaLogin;

  @BeforeAll
  static void abrirNavegador() {
    playwright = Playwright.create();
    browser =
      playwright
        .chromium()
        .launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(1000));
  }

  @AfterAll
  static void cerrarNavegador() {
    playwright.close();
  }

  @BeforeEach
  void crearContextoYPagina() {
    ReiniciarDB.limpiarBaseDeDatos();

    context = browser.newContext();
    Page page = context.newPage();
    vistaLogin = new VistaLogin(page);
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaDecirUNLAMEnElNavbar() throws MalformedURLException {
    dadoQueElUsuarioEstaEnLaVistaDeLogin();
    entoncesDeberiaVerElNombreDelProyectoEnElNavbar();
  }

  @Test
  void deberiaDarUnErrorAlIntentarIniciarSesionConUnUsuarioQueNoExiste() {
    dadoQueElUsuarioCargaSusDatosDeLoginCon("damian@unlam.edu.ar", "unlam");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaVerUnMensajeDeError();
  }

  @Test
  void deberiaNavegarALaVista1SiElUsuarioExiste() throws MalformedURLException {
    dadoQueElUsuarioCargaSusDatosDeLoginCon("test@unlam.edu.ar", "test");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaSerRedirigidoALaVista1();
  }

  @Test
  void deberiaRegistrarUnUsuarioEIniciarSesionAutomaticamente() throws MalformedURLException {
    dadoQueElUsuarioNavegaALaVistaDeRegistro();
    dadoQueElUsuarioSeRegistraCon("juan@unlam.edu.ar", "123456");
    entoncesDeberiaSerRedirigidoASinMascota();
  }

  @Test
  void deberiaRedirigirALoginSiElUsuarioIntentaAccederARutaProtegida()
    throws MalformedURLException {
    context.pages().get(0).navigate("http://localhost:8080/spring/analisis/vista/1");
    dadoQueElUsuarioEstaEnLaVistaDeLogin();
  }

  @Test
  void deberiaCerrarSesionYRedirigirALogin() throws MalformedURLException {
    dadoQueElUsuarioCargaSusDatosDeLoginCon("test@unlam.edu.ar", "test");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaSerRedirigidoALaVista1();

    // Toca el boton de logout (usando el atributo title="Cerrar Sesión")
    context.pages().get(0).click("a[title='Cerrar Sesión']");
    dadoQueElUsuarioEstaEnLaVistaDeLogin();
  }

  private void entoncesDeberiaVerElNombreDelProyectoEnElNavbar() {
    String texto = vistaLogin.obtenerTextoDeLaBarraDeNavegacion();
    assertThat("PetTracker", equalToIgnoringCase(texto));
  }

  private void dadoQueElUsuarioEstaEnLaVistaDeLogin() throws MalformedURLException {
    URL urlLogin = vistaLogin.obtenerURLActual();
    assertThat(urlLogin.getPath(), matchesPattern("^/spring/login(?:;jsessionid=[^/\\s]+)?$"));
  }

  private void cuandoElUsuarioTocaElBotonDeLogin() {
    vistaLogin.darClickEnIniciarSesion();
  }

  private void entoncesDeberiaSerRedirigidoALaVista1() throws MalformedURLException {
    System.out.println("URL actual antes del wait: " + context.pages().get(0).url());
    context
      .pages()
      .get(0)
      .waitForURL(url ->
        url.matches(".*\\/spring\\/analisis\\/dashboard\\/\\d+(?:;jsessionid=[^/\\s]+)?(?:#.*)?$")
      );
    URL url = vistaLogin.obtenerURLActual();
    assertThat(
      url.getPath(),
      matchesPattern("^/spring/analisis/dashboard/\\d+(?:;jsessionid=[^/\\s]+)?$")
    );
  }

  private void entoncesDeberiaVerUnMensajeDeError() {
    String texto = vistaLogin.obtenerMensajeDeError();
    assertThat("Usuario o clave incorrecta", equalToIgnoringCase(texto));
  }

  private void dadoQueElUsuarioCargaSusDatosDeLoginCon(String email, String clave) {
    vistaLogin.escribirEMAIL(email);
    vistaLogin.escribirClave(clave);
  }

  private void dadoQueElUsuarioNavegaALaVistaDeRegistro() {
    vistaLogin.darClickEnRegistrarse();
  }

  private void dadoQueElUsuarioSeRegistraCon(String email, String clave) {
    VistaNuevoUsuario vistaNuevoUsuario = new VistaNuevoUsuario(context.pages().get(0));
    vistaNuevoUsuario.escribirNombre("Juan Pérez");
    vistaNuevoUsuario.escribirEMAIL(email);
    vistaNuevoUsuario.escribirTelefono("1122334455");
    vistaNuevoUsuario.escribirClave(clave);
    vistaNuevoUsuario.escribirCalle("Av. Principal 123");
    vistaNuevoUsuario.escribirCiudad("Morón");
    vistaNuevoUsuario.escribirProvincia("Buenos Aires");
    vistaNuevoUsuario.escribirPais("Argentina");
    vistaNuevoUsuario.escribirCodigoPostal("1708");
    vistaNuevoUsuario.darClickEnRegistrarme();
  }

  private void entoncesDeberiaSerRedirigidoASinMascota() throws MalformedURLException {
    context
      .pages()
      .get(0)
      .waitForURL(url -> url.matches(".*\\/spring\\/sin-mascota(?:;jsessionid=[^/\\s]+)?(?:#.*)?$")
      );
    URL url = vistaLogin.obtenerURLActual();
    assertThat(url.getPath(), matchesPattern("^/spring/sin-mascota(?:;jsessionid=[^/\\s]+)?$"));
  }
}
