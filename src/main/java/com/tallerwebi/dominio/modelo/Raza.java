package com.tallerwebi.dominio.modelo;

import java.math.BigDecimal;
import javax.persistence.*;

@Entity
@Table(name = "Raza")
public class Raza {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pk_id_raza")
  private Long id;

  @Column(nullable = false, unique = true)
  private String nombre;

  @Column(nullable = false)
  private String tipo; // 'Pequeño', 'Mediano', 'Grande'

  @Column(name = "peso_min_macho", precision = 5, scale = 2)
  private BigDecimal pesoMinMacho;

  @Column(name = "peso_max_macho", precision = 5, scale = 2)
  private BigDecimal pesoMaxMacho;

  @Column(name = "peso_min_hembra", precision = 5, scale = 2)
  private BigDecimal pesoMinHembra;

  @Column(name = "peso_max_hembra", precision = 5, scale = 2)
  private BigDecimal pesoMaxHembra;

  public Raza() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public BigDecimal getPesoMinMacho() {
    return pesoMinMacho;
  }

  public void setPesoMinMacho(BigDecimal pesoMinMacho) {
    this.pesoMinMacho = pesoMinMacho;
  }

  public BigDecimal getPesoMaxMacho() {
    return pesoMaxMacho;
  }

  public void setPesoMaxMacho(BigDecimal pesoMaxMacho) {
    this.pesoMaxMacho = pesoMaxMacho;
  }

  public BigDecimal getPesoMinHembra() {
    return pesoMinHembra;
  }

  public void setPesoMinHembra(BigDecimal pesoMinHembra) {
    this.pesoMinHembra = pesoMinHembra;
  }

  public BigDecimal getPesoMaxHembra() {
    return pesoMaxHembra;
  }

  public void setPesoMaxHembra(BigDecimal pesoMaxHembra) {
    this.pesoMaxHembra = pesoMaxHembra;
  }
}
