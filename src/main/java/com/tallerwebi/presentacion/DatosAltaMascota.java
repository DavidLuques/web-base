package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.enums.TamanoMascota;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * as
 */
public class DatosAltaMascota {

  private String nombre;
  private String tipo;
  private String raza;
  private String genero;
  @NotNull(message = "El peso no puede ser nulo")
  @DecimalMin(value = "0.1", message = "El peso debe ser mayor a 0")
  private Double peso;
  private TamanoMascota tamano;
  private String fechaNacimiento;

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public String getRaza() {
    return raza;
  }

  public void setRaza(String raza) {
    this.raza = raza;
  }

  public String getGenero() {
    return genero;
  }

  public void setGenero(String genero) {
    this.genero = genero;
  }

  public Double getPeso() {
    return peso;
  }

  public void setPeso(Double peso) {
    this.peso = peso;
  }

  public TamanoMascota getTamano() {
    return tamano;
  }

  public void setTamano(TamanoMascota tamano) {
    this.tamano = tamano;
  }

  public String getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(String fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }
}
