package com.tallerwebi.dominio.modelo;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class DatosAnalisis {

  @Embedded
  private DatosSensor sensor;

  @Embedded
  private DatosVitalesYUbicacion vitalesYVitales;

  public DatosAnalisis() {
    this.sensor = new DatosSensor();
    this.vitalesYVitales = new DatosVitalesYUbicacion();
  }

  public DatosSensor getSensor() {
    return sensor;
  }

  public void setSensor(DatosSensor sensor) {
    this.sensor = sensor;
  }

  public DatosVitalesYUbicacion getVitalesYVitales() {
    return vitalesYVitales;
  }

  public void setVitalesYVitales(DatosVitalesYUbicacion vitalesYUbicacion) {
    this.vitalesYVitales = vitalesYUbicacion;
  }

  public Integer getFrecuenciaCardiaca() {
    return sensor.getFrecuenciaCardiaca();
  }

  public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
    sensor.setFrecuenciaCardiaca(frecuenciaCardiaca);
  }

  public Double getAccelX() {
    return sensor.getAccelX();
  }

  public void setAccelX(Double accelX) {
    sensor.setAccelX(accelX);
  }

  public Double getAccelY() {
    return sensor.getAccelY();
  }

  public void setAccelY(Double accelY) {
    sensor.setAccelY(accelY);
  }

  public Double getAccelZ() {
    return sensor.getAccelZ();
  }

  public void setAccelZ(Double accelZ) {
    sensor.setAccelZ(accelZ);
  }

  public Double getGyroX() {
    return sensor.getGyroX();
  }

  public void setGyroX(Double gyroX) {
    sensor.setGyroX(gyroX);
  }

  public Double getGyroY() {
    return sensor.getGyroY();
  }

  public void setGyroY(Double gyroY) {
    sensor.setGyroY(gyroY);
  }

  public Double getGyroZ() {
    return sensor.getGyroZ();
  }

  public void setGyroZ(Double gyroZ) {
    sensor.setGyroZ(gyroZ);
  }

  public Integer getPresionSistolica() {
    return vitalesYVitales.getPresionSistolica();
  }

  public void setPresionSistolica(Integer presionSistolica) {
    vitalesYVitales.setPresionSistolica(presionSistolica);
  }

  public Integer getPresionDiastolica() {
    return vitalesYVitales.getPresionDiastolica();
  }

  public void setPresionDiastolica(Integer presionDiastolica) {
    vitalesYVitales.setPresionDiastolica(presionDiastolica);
  }

  public Double getTemperatura() {
    return vitalesYVitales.getTemperatura();
  }

  public void setTemperatura(Double temperatura) {
    vitalesYVitales.setTemperatura(temperatura);
  }
}
