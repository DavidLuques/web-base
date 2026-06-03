package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaDashboard;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaDashboardE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaLogin vistaLogin;
  VistaDashboard vistaDashboard;

  @BeforeAll
  static void abrirNavegador() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch();
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
    vistaDashboard = new VistaDashboard(page);
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaCargarElNombreDeLaMascotaAlEntrarAlDashboard() {
    dadoQueElUsuarioIniciaSesion("test@unlam.edu.ar", "test");
    cuandoSeEncuentraEnElDashboardDeLaMascota(1L);
    entoncesDeberiaVerElNombreDeLaMascotaActualizado();
  }

  @Test
  void deberiaNavegarALaVistaActividadAlTocarElIconoDeActividad() {
    dadoQueElUsuarioIniciaSesion("test@unlam.edu.ar", "test");
    cuandoSeEncuentraEnElDashboardDeLaMascota(1L);
    cuandoTocaElIconoDeActividad();
    entoncesDeberiaSerRedirigidoALaVistaActividad();
  }

  @Test
  void deberiaRenderizarLosGraficosDeLaVista() {
    dadoQueElUsuarioIniciaSesion("test@unlam.edu.ar", "test");
    cuandoSeEncuentraEnElDashboardDeLaMascota(1L);
    entoncesDeberiaVerLosGraficosRenderizados();
  }

  private void cuandoTocaElIconoDeActividad() {
    vistaDashboard.darClickEnIconoActividad();
  }

  private void entoncesDeberiaSerRedirigidoALaVistaActividad() {
    context
      .pages()
      .get(0)
      .waitForURL(url ->
        url.matches(".*\\/spring\\/analisis\\/vista\\/\\d+(?:;jsessionid=[^/\\s]+)?(?:#.*)?$")
      );
    String urlActual = context.pages().get(0).url();
    assertThat(
      urlActual,
      matchesPattern(".*\\/spring\\/analisis\\/vista\\/\\d+(?:;jsessionid=[^/\\s]+)?$")
    );
  }

  private void entoncesDeberiaVerLosGraficosRenderizados() {
    boolean hayGraficos = vistaDashboard.graficosEstanVisibles();
    assertThat("Los gráficos no se renderizaron en el DOM", hayGraficos);
  }

  private void dadoQueElUsuarioIniciaSesion(String email, String clave) {
    vistaLogin.escribirEMAIL(email);
    vistaLogin.escribirClave(clave);
    vistaLogin.darClickEnIniciarSesion();
  }

  private void cuandoSeEncuentraEnElDashboardDeLaMascota(Long idMascota) {
    vistaDashboard.navegar(idMascota);
  }

  private void entoncesDeberiaVerElNombreDeLaMascotaActualizado() {
    vistaDashboard.esperarAQueCargueElFetch();

    String nombreMascota = vistaDashboard.obtenerNombreDeMascota();

    assertThat(nombreMascota, not(equalToIgnoringCase("Mascota")));
  }
}
