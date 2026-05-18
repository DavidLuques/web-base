package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.TamanoMascota;
import javax.persistence.*;

@Entity
@Table(name = "rango_vital_por_tamano")
public class RangoVitalPorTamano {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private TamanoMascota tamano;

  private Integer frecuenciaMinima;

  private Integer frecuenciaMaxima;

  public RangoVitalPorTamano() {}

  public Long getId() {
    return id;
  }

  public TamanoMascota getTamano() {
    return tamano;
  }

  public void setTamano(TamanoMascota tamano) {
    this.tamano = tamano;
  }

  public Integer getFrecuenciaMinima() {
    return frecuenciaMinima;
  }

  public void setFrecuenciaMinima(Integer frecuenciaMinima) {
    this.frecuenciaMinima = frecuenciaMinima;
  }

  public Integer getFrecuenciaMaxima() {
    return frecuenciaMaxima;
  }

  public void setFrecuenciaMaxima(Integer frecuenciaMaxima) {
    this.frecuenciaMaxima = frecuenciaMaxima;
  }
}
