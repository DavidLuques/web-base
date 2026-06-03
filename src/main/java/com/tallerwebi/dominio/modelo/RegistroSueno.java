package com.tallerwebi.dominio.modelo;

import java.time.LocalDateTime;
import javax.persistence.*;

/**
 * Entidad del sistema
 */
@Entity
@Table(name = "registro_sueno")
public class RegistroSueno {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer minutosDormido;

  private LocalDateTime fechaYHora;

  @ManyToOne
  @JoinColumn(name = "mascota_id", nullable = false)
  private Mascota mascota;

  public RegistroSueno() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getMinutosDormido() {
    return minutosDormido;
  }

  public void setMinutosDormido(Integer minutosDormido) {
    this.minutosDormido = minutosDormido;
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
