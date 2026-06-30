package com.tallerwebi.dominio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tallerwebi.dominio.enums.EstadoTurno;
import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
public class TurnoVeterinaria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mascota_id", nullable = false)
  private Mascota mascota;

  @Column(nullable = false)
  private String nombreVeterinaria;

  @Column(nullable = false)
  private String direccionVeterinaria;

  @Column(nullable = false)
  private LocalDateTime fechaYHora;

  private String motivo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoTurno estado = EstadoTurno.PENDIENTE;

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

  public String getNombreVeterinaria() {
    return nombreVeterinaria;
  }

  public void setNombreVeterinaria(String nombreVeterinaria) {
    this.nombreVeterinaria = nombreVeterinaria;
  }

  public String getDireccionVeterinaria() {
    return direccionVeterinaria;
  }

  public void setDireccionVeterinaria(String direccionVeterinaria) {
    this.direccionVeterinaria = direccionVeterinaria;
  }

  public LocalDateTime getFechaYHora() {
    return fechaYHora;
  }

  public void setFechaYHora(LocalDateTime fechaYHora) {
    this.fechaYHora = fechaYHora;
  }

  public String getMotivo() {
    return motivo;
  }

  public void setMotivo(String motivo) {
    this.motivo = motivo;
  }

  public EstadoTurno getEstado() {
    return estado;
  }

  public void setEstado(EstadoTurno estado) {
    this.estado = estado;
  }
}
