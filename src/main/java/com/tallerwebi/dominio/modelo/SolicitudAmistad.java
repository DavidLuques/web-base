package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.EstadoAmistad;
import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Table(name = "solicitud_amistad")
public class SolicitudAmistad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "emisor_id", nullable = false)
  private Usuario emisor;

  @ManyToOne
  @JoinColumn(name = "receptor_id", nullable = false)
  private Usuario receptor;

  @Enumerated(EnumType.STRING)
  private EstadoAmistad estado;

  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion;

  public SolicitudAmistad() {
    this.fechaCreacion = LocalDateTime.now();
    this.estado = EstadoAmistad.PENDIENTE;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getEmisor() {
    return emisor;
  }

  public void setEmisor(Usuario emisor) {
    this.emisor = emisor;
  }

  public Usuario getReceptor() {
    return receptor;
  }

  public void setReceptor(Usuario receptor) {
    this.receptor = receptor;
  }

  public EstadoAmistad getEstado() {
    return estado;
  }

  public void setEstado(EstadoAmistad estado) {
    this.estado = estado;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }
}
