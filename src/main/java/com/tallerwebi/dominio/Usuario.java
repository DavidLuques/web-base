package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Direccion;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entidad.
 */
@Entity
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;
  private String password;
  private String rol;
  private Boolean activo = false;
  private String nombre;
  private Long telefono;
  private String fechaCreacion;

  // refactorizo por que eran muchos atributos
  @Embedded
  private Direccion ubicacion = new Direccion();

  public Usuario() {}

  public Direccion getUbicacion() {
    return this.ubicacion;
  }

  public void setUbicacion(Direccion ubicacion) {
    this.ubicacion = ubicacion;
  }

  public Boolean isActivo() {
    return this.activo;
  }

  public String getFechaCreacion() {
    return this.fechaCreacion;
  }

  public void setFechaCreacion(String fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public String getAvatarUrl() {
    return this.avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  private String avatarUrl;

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRol() {
    return rol;
  }

  public void setRol(String rol) {
    this.rol = rol;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public void activar() {
    activo = true;
  }

  public Long getTelefono() {
    return telefono;
  }

  public void setTelefono(Long telefono) {
    this.telefono = telefono;
  }
}
