package com.tallerwebi.dominio.modelo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PreferenciasUsuario {

  @Column(name = "notificaciones_mail_activas", nullable = false)
  private Boolean notificacionesMailActivas = true;

  public Boolean getNotificacionesMailActivas() {
    return notificacionesMailActivas;
  }

  public void setNotificacionesMailActivas(Boolean notificacionesMailActivas) {
    this.notificacionesMailActivas = notificacionesMailActivas;
  }
}
