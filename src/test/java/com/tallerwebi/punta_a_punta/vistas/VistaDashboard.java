package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaDashboard {

  private Page page;

  public VistaDashboard(Page page) {
    this.page = page;
  }

  public void navegar(Long idMascota) {
    page.navigate("http://localhost:8080/spring/simulacion/dashboard/" + idMascota);
  }

  public String obtenerNombreDeMascota() {
    return page.locator("#nombre-mascota").innerText();
  }

  public void esperarAQueCargueElFetch() {
    page
      .locator("#nombre-mascota")
      .waitFor(
        new com.microsoft.playwright.Locator.WaitForOptions()
          .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
      );
  }

  public void darClickEnIconoActividad() {
    page.locator("a[href*='/simulacion/vista/']").click();
  }

  public boolean graficosEstanVisibles() {
    // waitFor asegura que le demos tiempo a la librería gráfica de ejecutarse
    page.waitForSelector(".apexcharts-canvas");
    return page.locator(".apexcharts-canvas").count() > 0;
  }
}
