package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entidad del sistema
 */
@Entity
@Table(name = "mascota")
public class Mascota {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  @Column(name = "radio_valla")
  private Integer radioValla;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado_actual")
  private EstadoMascota estadoActual;

  @Enumerated(EnumType.STRING)
  @Column(name = "tamano")
  private TamanoMascota tamano;

  private Boolean activo = true;

  @Embedded
  private DatosMascota datos;

  @ManyToOne
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  public Mascota() {
    this.datos = new DatosMascota();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public EstadoMascota getEstadoActual() {
    return estadoActual;
  }

  public void setEstadoActual(EstadoMascota estadoActual) {
    this.estadoActual = estadoActual;
  }

  public TamanoMascota getTamano() {
    return tamano;
  }

  public void setTamano(TamanoMascota tamano) {
    this.tamano = tamano;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public DatosMascota getDatos() {
    return datos;
  }

  public void setDatos(DatosMascota datos) {
    this.datos = datos;
  }

  public Double getPeso() {
    return datos.getPeso();
  }

  public void setPeso(Double peso) {
    datos.setPeso(peso);
  }

  public Boolean isEsteril() {
    return datos.isEsteril();
  }

  public Boolean getEsteril() {
    return datos.getEsteril();
  }

  public void setEsteril(Boolean esteril) {
    datos.setEsteril(esteril);
  }

  public java.time.LocalDate getFechaNacimiento() {
    return datos.getFechaNacimiento();
  }

  public void setFechaNacimiento(java.time.LocalDate fechaNacimiento) {
    datos.setFechaNacimiento(fechaNacimiento);
  }

  public String getRaza() {
    return datos.getRaza();
  }

  public void setRaza(String raza) {
    datos.setRaza(raza);
  }

  public String getGenero() {
    return datos.getGenero();
  }

  public void setGenero(String genero) {
    datos.setGenero(genero);
  }

  public String getTipo() {
    return datos.getTipo();
  }

  public void setTipo(String tipo) {
    datos.setTipo(tipo);
  }

  public String getImagenMascota() {
    return datos.getImagenMascota();
  }

  public void setImagenMascota(String imagenMascota) {
    datos.setImagenMascota(imagenMascota);
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public Integer getRadioValla() {
    return radioValla;
  }

  public void setRadioValla(Integer radioValla) {
    this.radioValla = radioValla;
  }
}
