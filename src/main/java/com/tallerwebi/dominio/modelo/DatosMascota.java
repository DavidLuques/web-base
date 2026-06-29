package com.tallerwebi.dominio.modelo;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Entidad del sistema
 */
@Embeddable
public class DatosMascota {

  private String raza;
  private String genero;
  private String tipo;
  private Double peso;
  private Boolean esteril;

  @Column(name = "fecha_nacimiento")
  private LocalDate fechaNacimiento;

  @Column(name = "imagen_mascota")
  private String imagenMascota;

  public DatosMascota() {}

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

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public Double getPeso() {
    return peso;
  }

  public void setPeso(Double peso) {
    this.peso = peso;
  }

  public Boolean getEsteril() {
    return esteril;
  }

  public Boolean isEsteril() {
    return esteril;
  }

  public void setEsteril(Boolean esteril) {
    this.esteril = esteril;
  }

  public LocalDate getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(LocalDate fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }

  public String getImagenMascota() {
    return imagenMascota;
  }

  public void setImagenMascota(String imagenMascota) {
    this.imagenMascota = imagenMascota;
  }
}
