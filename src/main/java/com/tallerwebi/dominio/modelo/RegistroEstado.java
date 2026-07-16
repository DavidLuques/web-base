package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.EstadoMascota;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "registro_estado")
public class RegistroEstado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private EstadoMascota estado;

  private LocalDateTime fechaYHora;

  @ManyToOne
  @JoinColumn(name = "mascota_id", nullable = false)
  private Mascota mascota;

  public RegistroEstado() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public EstadoMascota getEstado() {
    return estado;
  }

  public void setEstado(EstadoMascota estado) {
    this.estado = estado;
  }

  public LocalDateTime getFechaYHora() {
    return fechaYHora;
  }

  public void setFechaYHora(LocalDateTime fechaYHora) {
    this.fechaYHora = fechaYHora;
  }

  public Mascota getMascota() {
    return mascota;
  }

  public void setMascota(Mascota mascota) {
    this.mascota = mascota;
  }
}
