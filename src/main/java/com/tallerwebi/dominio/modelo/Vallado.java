package com.tallerwebi.dominio.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "vallado")
public class Vallado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "id_mascota", nullable = false, unique = true)
  private Mascota mascota;

  @Column(name = "latitud_centro", nullable = false)
  private Double latitudCentro;

  @Column(name = "longitud_centro", nullable = false)
  private Double longitudCentro;

  @Column(name = "radio_metros", nullable = false)
  private Double radioMetros;

  public Vallado() {}

  public Vallado(Mascota mascota, Double latitudCentro, Double longitudCentro, Double radioMetros) {
    this.mascota = mascota;
    this.latitudCentro = latitudCentro;
    this.longitudCentro = longitudCentro;
    this.radioMetros = radioMetros;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Mascota getMascota() {
    return mascota;
  }

  public void setMascota(Mascota mascota) {
    this.mascota = mascota;
  }

  public Double getLatitudCentro() {
    return latitudCentro;
  }

  public void setLatitudCentro(Double latitudCentro) {
    this.latitudCentro = latitudCentro;
  }

  public Double getLongitudCentro() {
    return longitudCentro;
  }

  public void setLongitudCentro(Double longitudCentro) {
    this.longitudCentro = longitudCentro;
  }

  public Double getRadioMetros() {
    return radioMetros;
  }

  public void setRadioMetros(Double radioMetros) {
    this.radioMetros = radioMetros;
  }
}
