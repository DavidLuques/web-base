package com.tallerwebi.dominio.dto;

import com.tallerwebi.dominio.enums.TipoAlerta;

public class AlertaDto {

  private Long id;
  private TipoAlerta tipo;
  private String tipoFormato;
  private String mensaje;
  private String fechaYHora;
  private Boolean leido;

  public AlertaDto(
    Long id,
    TipoAlerta tipo,
    String tipoFormato,
    String mensaje,
    String fechaYHora,
    Boolean leido
  ) {
    this.id = id;
    this.tipo = tipo;
    this.tipoFormato = tipoFormato;
    this.mensaje = mensaje;
    this.fechaYHora = fechaYHora;
    this.leido = leido;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TipoAlerta getTipo() {
    return tipo;
  }

  public void setTipo(TipoAlerta tipo) {
    this.tipo = tipo;
  }

  public String getTipoFormato() {
    return tipoFormato;
  }

  public void setTipoFormato(String tipoFormato) {
    this.tipoFormato = tipoFormato;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public String getFechaYHora() {
    return fechaYHora;
  }

  public void setFechaYHora(String fechaYHora) {
    this.fechaYHora = fechaYHora;
  }

  public Boolean getLeido() {
    return leido;
  }

  public void setLeido(Boolean leido) {
    this.leido = leido;
  }
}
