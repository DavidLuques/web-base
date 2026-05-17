package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.Usuario;
import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "mascota")
public class Mascota {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private Boolean esteril;

  @Column(name = "fecha_nacimiento")
  private LocalDate fechaNacimiento;

  private Double peso;
  private String raza;
  private String genero;
  private String tipo;

  @Column(name = "imagen_mascota")
  private String imagenMascota;

  @ManyToOne
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  public Mascota() {}

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Double getPeso() {
    return peso;
  }

  public void setPeso(Double peso) {
    this.peso = peso;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Boolean isEsteril() {
    return this.esteril;
  }

  public Boolean getEsteril() {
    return this.esteril;
  }

  public void setEsteril(Boolean esteril) {
    this.esteril = esteril;
  }

  public LocalDate getFechaNacimiento() {
    return this.fechaNacimiento;
  }

  public void setFechaNacimiento(LocalDate fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }

  public String getRaza() {
    return this.raza;
  }

  public void setRaza(String raza) {
    this.raza = raza;
  }

  public String getGenero() {
    return this.genero;
  }

  public void setGenero(String genero) {
    this.genero = genero;
  }

  public String getTipo() {
    return this.tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public String getImagenMascota() {
    return this.imagenMascota;
  }

  public void setImagenMascota(String imagenMascota) {
    this.imagenMascota = imagenMascota;
  }

  public Usuario getUsuario() {
    return this.usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }
}
