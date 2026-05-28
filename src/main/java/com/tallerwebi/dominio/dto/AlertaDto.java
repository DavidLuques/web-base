package com.tallerwebi.dominio.dto;

import com.tallerwebi.dominio.enums.TipoAlerta;

public class AlertaDto {

  private Long id;
  private TipoAlerta tipo;
  private String mensaje;

  public AlertaDto(Long id, TipoAlerta tipo, String mensaje) {
    this.id = id;
    this.tipo = tipo;
    this.mensaje = mensaje;
  }

  public Long getId() {
    return id;
  }

  public TipoAlerta getTipo() {
    return tipo;
  }

  public String getMensaje() {
    return mensaje;
  }
}
