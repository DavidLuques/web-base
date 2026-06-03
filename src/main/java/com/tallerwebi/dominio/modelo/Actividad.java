package com.tallerwebi.dominio.modelo;

import java.time.LocalDateTime;
import javax.persistence.*;

/**
 * Entidad del sistema
 */
@Entity
public class Actividad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Double distanciaRecorrida;

  private LocalDateTime fechaYHora;

  @ManyToOne
  @JoinColumn(name = "mascota_id", nullable = false)
  private Mascota mascota;

  public Actividad() {}

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getDistanciaRecorrida() {
    return this.distanciaRecorrida;
  }

  public void setDistanciaRecorrida(Double distanciaRecorrida) {
    this.distanciaRecorrida = distanciaRecorrida;
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
}
