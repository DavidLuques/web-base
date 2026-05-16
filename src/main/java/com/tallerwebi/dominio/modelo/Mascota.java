package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import javax.persistence.*;

@Entity
@Table(name = "mascota")
public class Mascota {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  private Double peso;

  @Enumerated(EnumType.STRING)
  private EstadoMascota estadoActual;

  @Enumerated(EnumType.STRING)
  private TamanoMascota tamano;

  public Mascota() {}

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
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

  public EstadoMascota getEstadoActual() {
    return estadoActual;
  }

  public void setEstadoActual(EstadoMascota estadoActual) {
    this.estadoActual = estadoActual;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
