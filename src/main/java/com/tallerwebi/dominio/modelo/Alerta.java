package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.TipoAlerta;
import java.time.LocalDateTime;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "alerta")
public class Alerta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pk_id_alerta")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fk_id_mascota", nullable = true)
  private Mascota mascota;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fk_id_usuario", nullable = true)
  private Usuario usuario;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoAlerta tipo;

  @Lob
  @Column(name = "mensaje", nullable = false)
  private String mensaje;

  @Column(name = "fecha_y_hora")
  private LocalDateTime fechaYHora;

  @Column(nullable = false)
  private Boolean leido;

  public Alerta() {}

  public Alerta(Mascota mascota, TipoAlerta tipo, String mensaje) {
    this.mascota = mascota;
    this.tipo = tipo;
    this.mensaje = mensaje;
    this.fechaYHora = LocalDateTime.now();
    this.leido = false;
  }

  public Alerta(Usuario usuario, TipoAlerta tipo, String mensaje) {
    this.usuario = usuario;
    this.tipo = tipo;
    this.mensaje = mensaje;
    this.fechaYHora = LocalDateTime.now();
    this.leido = false;
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

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public TipoAlerta getTipo() {
    return tipo;
  }

  public void setTipo(TipoAlerta tipo) {
    this.tipo = tipo;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public LocalDateTime getFechaYHora() {
    return fechaYHora;
  }

  public void setFechaYHora(LocalDateTime fechaYHora) {
    this.fechaYHora = fechaYHora;
  }

  public Boolean getLeido() {
    return leido;
  }

  public void setLeido(Boolean leido) {
    this.leido = leido;
  }

  public String obtenerTipoFormato() {
    if (tipo == null) return "";
    Locale localeAR = new Locale("es", "AR");
    return (
      tipo.name().substring(0, 1).toUpperCase(localeAR) +
      tipo.name().substring(1).toLowerCase(localeAR)
    );
  }
}
