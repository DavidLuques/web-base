package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaDashboard {

  private Page page;

  public VistaDashboard(Page page) {
    this.page = page;
  }

  public void navegar(Long idMascota) {
    page.navigate("http://localhost:8080/spring/analisis/dashboard/" + idMascota);
  }

  public void esperarAQueCargueElFetch() {
    page
        .locator("#nombre-mascota")
        .waitFor(
            new com.microsoft.playwright.Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
  }

  public String obtenerNombreDeMascota() {
    return page.locator("#nombre-mascota").innerText();
  }
  
  public void darClickEnIconoActividad() {
    page.locator("a[href*='/analisis/vista/']").click();
  }

  public boolean graficosEstanVisibles() {
    // waitFor asegura que le demos tiempo a la librería gráfica de ejecutarse
    page.waitForSelector(".apexcharts-canvas");
    return page.locator(".apexcharts-canvas").count() > 0;
  }

  public boolean panelFrecuenciaEsVisible() {
    return this.page.locator("p:has-text('Frecuencia Cardíaca')").isVisible();
  }

  public boolean panelTemperaturaEsVisible() {
    return this.page.locator("p:has-text('Temperatura')").isVisible();
  }
}
