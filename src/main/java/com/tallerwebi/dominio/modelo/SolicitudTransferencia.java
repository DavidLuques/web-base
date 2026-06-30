package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.EstadoTransferencia;
import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Table(name = "solicitud_transferencia")
public class SolicitudTransferencia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "mascota_id", nullable = false)
  private Mascota mascota;

  @ManyToOne
  @JoinColumn(name = "usuario_origen_id", nullable = false)
  private Usuario usuarioOrigen;

  @ManyToOne
  @JoinColumn(name = "usuario_destino_id", nullable = false)
  private Usuario usuarioDestino;

  @Column(name = "confirmada_por_origen")
  private Boolean confirmadaPorOrigen = false;

  @Column(name = "confirmada_por_destino")
  private Boolean confirmadaPorDestino = false;

  @Enumerated(EnumType.STRING)
  private EstadoTransferencia estado;

  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion;

  public SolicitudTransferencia() {
    this.fechaCreacion = LocalDateTime.now();
    this.estado = EstadoTransferencia.PENDIENTE;
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

  public Usuario getUsuarioOrigen() {
    return usuarioOrigen;
  }

  public void setUsuarioOrigen(Usuario usuarioOrigen) {
    this.usuarioOrigen = usuarioOrigen;
  }

  public Usuario getUsuarioDestino() {
    return usuarioDestino;
  }

  public void setUsuarioDestino(Usuario usuarioDestino) {
    this.usuarioDestino = usuarioDestino;
  }

  public Boolean getConfirmadaPorOrigen() {
    return confirmadaPorOrigen;
  }

  public void setConfirmadaPorOrigen(Boolean confirmadaPorOrigen) {
    this.confirmadaPorOrigen = confirmadaPorOrigen;
  }

  public Boolean getConfirmadaPorDestino() {
    return confirmadaPorDestino;
  }

  public void setConfirmadaPorDestino(Boolean confirmadaPorDestino) {
    this.confirmadaPorDestino = confirmadaPorDestino;
  }

  public EstadoTransferencia getEstado() {
    return estado;
  }

  public void setEstado(EstadoTransferencia estado) {
    this.estado = estado;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public String getFechaFormateada() {
    if (fechaCreacion == null) return "-";
    return fechaCreacion.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
  }
}
