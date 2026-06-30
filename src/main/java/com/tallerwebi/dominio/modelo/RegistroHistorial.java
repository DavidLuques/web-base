package com.tallerwebi.dominio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import javax.persistence.*;

@Entity
public class RegistroHistorial {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mascota_id", nullable = false)
  private Mascota mascota;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "turno_id")
  private TurnoVeterinaria turnoOriginal;

  @Column(nullable = false)
  private LocalDate fechaVisita;

  @Column(columnDefinition = "TEXT")
  private String diagnostico;

  @Column(columnDefinition = "TEXT")
  private String tratamiento;

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

  public TurnoVeterinaria getTurnoOriginal() {
    return turnoOriginal;
  }

  public void setTurnoOriginal(TurnoVeterinaria turnoOriginal) {
    this.turnoOriginal = turnoOriginal;
  }

  public LocalDate getFechaVisita() {
    return fechaVisita;
  }

  public void setFechaVisita(LocalDate fechaVisita) {
    this.fechaVisita = fechaVisita;
  }

  public String getDiagnostico() {
    return diagnostico;
  }

  public void setDiagnostico(String diagnostico) {
    this.diagnostico = diagnostico;
  }

  public String getTratamiento() {
    return tratamiento;
  }

  public void setTratamiento(String tratamiento) {
    this.tratamiento = tratamiento;
  }
}
