package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaNuevoUsuario extends VistaWeb {

  public VistaNuevoUsuario(Page page) {
    super(page);
  }

  public void escribirEMAIL(String email) {
    this.escribirEnElElemento("#email", email);
  }

  public void escribirClave(String clave) {
    this.escribirEnElElemento("#password", clave);
  }

  public void escribirNombre(String nombre) {
    this.escribirEnElElemento("#nombre", nombre);
  }

  public void escribirTelefono(String telefono) {
    this.escribirEnElElemento("#telefono", telefono);
  }

  public void escribirCalle(String calle) {
    this.escribirEnElElemento("#calle", calle);
  }

  public void escribirCiudad(String ciudad) {
    this.escribirEnElElemento("#ciudad", ciudad);
  }

  public void escribirProvincia(String provincia) {
    this.escribirEnElElemento("#provincia", provincia);
  }

  public void escribirPais(String pais) {
    this.escribirEnElElemento("#pais", pais);
  }

  public void escribirCodigoPostal(String codigoPostal) {
    this.escribirEnElElemento("#codigoPostal", codigoPostal);
  }

  public void darClickEnRegistrarme() {
    this.darClickEnElElemento("#btn-registrarme");
  }

  public String obtenerMensajeDeError() {
    return this.obtenerTextoDelElemento("p.alert.alert-danger");
  }
}
