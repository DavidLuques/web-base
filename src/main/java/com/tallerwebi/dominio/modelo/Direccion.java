package com.tallerwebi.dominio.modelo;

import javax.persistence.Embeddable;

@Embeddable
public class Direccion {

  private String calle;
  private String ciudad;
  private String provincia;
  private String pais;
  private String codigoPostal;

  public String getCalle() {
    return this.calle;
  }

  public void setCalle(String calle) {
    this.calle = calle;
  }

  public String getCiudad() {
    return this.ciudad;
  }

  public void setCiudad(String ciudad) {
    this.ciudad = ciudad;
  }

  public String getProvincia() {
    return this.provincia;
  }

  public void setProvincia(String provincia) {
    this.provincia = provincia;
  }

  public String getPais() {
    return this.pais;
  }

  public void setPais(String pais) {
    this.pais = pais;
  }

  public String getCodigoPostal() {
    return this.codigoPostal;
  }

  public void setCodigoPostal(String codigoPostal) {
    this.codigoPostal = codigoPostal;
  }

  public Direccion() {}
}
