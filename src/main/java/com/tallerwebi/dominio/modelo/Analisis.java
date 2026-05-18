package com.tallerwebi.dominio.modelo;

import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
public class Analisis {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_analisis")
  private Long id;

  private Double latitud;
  private Double longitud;
  private LocalDateTime fechaYHora;

  @Embedded
  private DatosAnalisis datos;

  @ManyToOne
  @JoinColumn(name = "mascota_id", nullable = false)
  private Mascota mascota;

  public Analisis() {
    this.datos = new DatosAnalisis();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getLatitud() {
    return this.latitud;
  }

  public void setLatitud(Double latitud) {
    this.latitud = latitud;
  }

  public Double getLongitud() {
    return this.longitud;
  }

  public void setLongitud(Double longitud) {
    this.longitud = longitud;
  }

  public LocalDateTime getFechaYHora() {
    return this.fechaYHora;
  }

  public void setFechaYHora(LocalDateTime fechaYHora) {
    this.fechaYHora = fechaYHora;
  }

  public Mascota getMascota() {
    return this.mascota;
  }

  public void setMascota(Mascota mascota) {
    this.mascota = mascota;
  }

  public DatosAnalisis getDatos() {
    return this.datos;
  }

  public void setDatos(DatosAnalisis datos) {
    this.datos = datos;
  }
}
