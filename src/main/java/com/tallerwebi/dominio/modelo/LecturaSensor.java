package com.tallerwebi.dominio.modelo;

public class LecturaSensor {

  private DatosAnalisis datos;
  private Double latitud;
  private Double longitud;

  public LecturaSensor() {
    this.datos = new DatosAnalisis();
  }

  public DatosAnalisis getDatos() {
    return datos;
  }

  public void setDatos(DatosAnalisis datos) {
    this.datos = datos;
  }

  public Double getLatitud() {
    return latitud;
  }

  public void setLatitud(Double latitud) {
    this.latitud = latitud;
  }

  public Double getLongitud() {
    return longitud;
  }

  public void setLongitud(Double longitud) {
    this.longitud = longitud;
  }

  public Integer getFrecuenciaCardiaca() {
    return datos.getFrecuenciaCardiaca();
  }

  public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
    datos.setFrecuenciaCardiaca(frecuenciaCardiaca);
  }

  public Double getAccelX() {
    return datos.getAccelX();
  }

  public void setAccelX(Double accelX) {
    datos.setAccelX(accelX);
  }

  public Double getAccelY() {
    return datos.getAccelY();
  }

  public void setAccelY(Double accelY) {
    datos.setAccelY(accelY);
  }

  public Double getAccelZ() {
    return datos.getAccelZ();
  }

  public void setAccelZ(Double accelZ) {
    datos.setAccelZ(accelZ);
  }

  public Double getGyroX() {
    return datos.getGyroX();
  }

  public void setGyroX(Double gyroX) {
    datos.setGyroX(gyroX);
  }

  public Double getGyroY() {
    return datos.getGyroY();
  }

  public void setGyroY(Double gyroY) {
    datos.setGyroY(gyroY);
  }

  public Double getGyroZ() {
    return datos.getGyroZ();
  }

  public void setGyroZ(Double gyroZ) {
    datos.setGyroZ(gyroZ);
  }

  public Integer getPresionSistolica() {
    return datos.getPresionSistolica();
  }

  public void setPresionSistolica(Integer presionSistolica) {
    datos.setPresionSistolica(presionSistolica);
  }

  public Integer getPresionDiastolica() {
    return datos.getPresionDiastolica();
  }

  public void setPresionDiastolica(Integer presionDiastolica) {
    datos.setPresionDiastolica(presionDiastolica);
  }

  public Double getTemperatura() {
    return datos.getTemperatura();
  }

  public void setTemperatura(Double temperatura) {
    datos.setTemperatura(temperatura);
  }
}
