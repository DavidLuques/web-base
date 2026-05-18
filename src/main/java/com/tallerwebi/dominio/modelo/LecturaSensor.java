package com.tallerwebi.dominio.modelo;

public class LecturaSensor {

  private Integer frecuenciaCardiaca;

  private Double accelX;
  private Double accelY;
  private Double accelZ;

  private Double gyroX;
  private Double gyroY;
  private Double gyroZ;

  public LecturaSensor() {}

  public Integer getFrecuenciaCardiaca() {
    return frecuenciaCardiaca;
  }

  public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
    this.frecuenciaCardiaca = frecuenciaCardiaca;
  }

  public Double getAccelX() {
    return accelX;
  }

  public void setAccelX(Double accelX) {
    this.accelX = accelX;
  }

  public Double getAccelY() {
    return accelY;
  }

  public void setAccelY(Double accelY) {
    this.accelY = accelY;
  }

  public Double getAccelZ() {
    return accelZ;
  }

  public void setAccelZ(Double accelZ) {
    this.accelZ = accelZ;
  }

  public Double getGyroX() {
    return gyroX;
  }

  public void setGyroX(Double gyroX) {
    this.gyroX = gyroX;
  }

  public Double getGyroY() {
    return gyroY;
  }

  public void setGyroY(Double gyroY) {
    this.gyroY = gyroY;
  }

  public Double getGyroZ() {
    return gyroZ;
  }

  public void setGyroZ(Double gyroZ) {
    this.gyroZ = gyroZ;
  }
}
