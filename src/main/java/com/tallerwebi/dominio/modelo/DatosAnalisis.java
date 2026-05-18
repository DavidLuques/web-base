package com.tallerwebi.dominio.modelo;

import javax.persistence.Embeddable;

@Embeddable
public class DatosAnalisis {

  private Integer frecuenciaCardiaca;
  private Double accelX;
  private Double accelY;
  private Double accelZ;
  private Double gyroX;
  private Double gyroY;
  private Double gyroZ;

  public DatosAnalisis() {}

  public Integer getFrecuenciaCardiaca() {
    return this.frecuenciaCardiaca;
  }

  public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
    this.frecuenciaCardiaca = frecuenciaCardiaca;
  }

  public Double getAccelX() {
    return this.accelX;
  }

  public void setAccelX(Double accelX) {
    this.accelX = accelX;
  }

  public Double getAccelY() {
    return this.accelY;
  }

  public void setAccelY(Double accelY) {
    this.accelY = accelY;
  }

  public Double getAccelZ() {
    return this.accelZ;
  }

  public void setAccelZ(Double accelZ) {
    this.accelZ = accelZ;
  }

  public Double getGyroX() {
    return this.gyroX;
  }

  public void setGyroX(Double gyroX) {
    this.gyroX = gyroX;
  }

  public Double getGyroY() {
    return this.gyroY;
  }

  public void setGyroY(Double gyroY) {
    this.gyroY = gyroY;
  }

  public Double getGyroZ() {
    return this.gyroZ;
  }

  public void setGyroZ(Double gyroZ) {
    this.gyroZ = gyroZ;
  }
}
