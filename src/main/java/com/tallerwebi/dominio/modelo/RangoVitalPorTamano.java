package com.tallerwebi.dominio.modelo;

import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.enums.TipoMascota;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rango_vital_por_tamano")
public class RangoVitalPorTamano {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_mascota", nullable = false)
  private TipoMascota tipoMascota;

  @Enumerated(EnumType.STRING)
  private TamanoMascota tamano;

  @Embedded
  private final RangoFrecuenciaCardiaca frecuenciaCardiaca = new RangoFrecuenciaCardiaca();

  @Embedded
  private final RangoPresionArterial presionArterial = new RangoPresionArterial();

  @Embedded
  private final RangoTemperatura temperatura = new RangoTemperatura();

  public RangoVitalPorTamano() {}

  public Long getId() {
    return id;
  }

  public TipoMascota getTipoMascota() {
    return tipoMascota;
  }

  public void setTipoMascota(TipoMascota tipoMascota) {
    this.tipoMascota = tipoMascota;
  }

  public TamanoMascota getTamano() {
    return tamano;
  }

  public void setTamano(TamanoMascota tamano) {
    this.tamano = tamano;
  }

  public Integer getFrecuenciaMinima() {
    return frecuenciaCardiaca.getMinima();
  }

  public void setFrecuenciaMinima(Integer frecuenciaMinima) {
    frecuenciaCardiaca.setMinima(frecuenciaMinima);
  }

  public Integer getFrecuenciaMaxima() {
    return frecuenciaCardiaca.getMaxima();
  }

  public void setFrecuenciaMaxima(Integer frecuenciaMaxima) {
    frecuenciaCardiaca.setMaxima(frecuenciaMaxima);
  }

  public Integer getSistolicaMinima() {
    return presionArterial.getSistolicaMinima();
  }

  public void setSistolicaMinima(Integer sistolicaMinima) {
    presionArterial.setSistolicaMinima(sistolicaMinima);
  }

  public Integer getSistolicaMaxima() {
    return presionArterial.getSistolicaMaxima();
  }

  public void setSistolicaMaxima(Integer sistolicaMaxima) {
    presionArterial.setSistolicaMaxima(sistolicaMaxima);
  }

  public Integer getDiastolicaMinima() {
    return presionArterial.getDiastolicaMinima();
  }

  public void setDiastolicaMinima(Integer diastolicaMinima) {
    presionArterial.setDiastolicaMinima(diastolicaMinima);
  }

  public Integer getDiastolicaMaxima() {
    return presionArterial.getDiastolicaMaxima();
  }

  public void setDiastolicaMaxima(Integer diastolicaMaxima) {
    presionArterial.setDiastolicaMaxima(diastolicaMaxima);
  }

  public Double getTemperaturaMinima() {
    return temperatura.getMinima();
  }

  public void setTemperaturaMinima(Double temperaturaMinima) {
    temperatura.setMinima(temperaturaMinima);
  }

  public Double getTemperaturaMaxima() {
    return temperatura.getMaxima();
  }

  public void setTemperaturaMaxima(Double temperaturaMaxima) {
    temperatura.setMaxima(temperaturaMaxima);
  }
}
