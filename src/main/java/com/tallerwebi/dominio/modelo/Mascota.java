package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import java.math.BigDecimal;
import javax.persistence.*;

@Entity
@Table(name = "mascota")
public class Mascota {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado_actual")
  private EstadoMascota estadoActual;

  @Enumerated(EnumType.STRING)
  @Column(name = "tamano")
  private TamanoMascota tamano;

  @Embedded
  private DatosMascota datos;

  @ManyToOne
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  // Relaciono con base Tabla Raza
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fk_id_raza", nullable = false)
  private Raza raza;

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

  public BigDecimal getPeso() {
    if (datos.getPeso() == null) return null;
    return BigDecimal.valueOf(datos.getPeso());
  }

  public void setPeso(BigDecimal peso) {
    if (peso != null) {
      datos.setPeso(peso.doubleValue());
    } else {
      datos.setPeso(null);
    }
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

  // Retorno objeto Raza
  public Raza getRaza() {
    return this.raza;
  }

  public void setRaza(Raza raza) {
    this.raza = raza;
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
}
