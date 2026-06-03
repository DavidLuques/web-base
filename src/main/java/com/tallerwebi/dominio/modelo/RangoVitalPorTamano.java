package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.TamanoMascota;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entidad del sistema
 */
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

  private Integer sistolicaMinima;
  private Integer sistolicaMaxima;

  private Integer diastolicaMinima;
  private Integer diastolicaMaxima;

  private Double temperaturaMinima;
  private Double temperaturaMaxima;

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

  public Integer getSistolicaMinima() {
    return sistolicaMinima;
  }

  public void setSistolicaMinima(Integer sistolicaMinima) {
    this.sistolicaMinima = sistolicaMinima;
  }

  public Integer getSistolicaMaxima() {
    return sistolicaMaxima;
  }

  public void setSistolicaMaxima(Integer sistolicaMaxima) {
    this.sistolicaMaxima = sistolicaMaxima;
  }

  public Integer getDiastolicaMinima() {
    return diastolicaMinima;
  }

  public void setDiastolicaMinima(Integer diastolicaMinima) {
    this.diastolicaMinima = diastolicaMinima;
  }

  public Integer getDiastolicaMaxima() {
    return diastolicaMaxima;
  }

  public void setDiastolicaMaxima(Integer diastolicaMaxima) {
    this.diastolicaMaxima = diastolicaMaxima;
  }

  public Double getTemperaturaMinima() {
    return temperaturaMinima;
  }

  public void setTemperaturaMinima(Double temperaturaMinima) {
    this.temperaturaMinima = temperaturaMinima;
  }

  public Double getTemperaturaMaxima() {
    return temperaturaMaxima;
  }

  public void setTemperaturaMaxima(Double temperaturaMaxima) {
    this.temperaturaMaxima = temperaturaMaxima;
  }
}
