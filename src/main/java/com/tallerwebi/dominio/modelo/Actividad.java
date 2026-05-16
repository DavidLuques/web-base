package com.tallerwebi.dominio.modelo;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Long id;

    private String tipoActividad; // vi que sofi hizo un enum (hablarlo)
    private Integer pasos;
    private Double distanciaRecorrida; 
    private Double caloriasQuemadas;
    private Double velocidadMaxima;
    private Double duracion; 
    private LocalDateTime fechaYHora;

    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    public Actividad() {}

    // campo calculado
    @Transient
    public Double getVelocidadPromedio() {
        if (duracion == null || duracion <= 0) {
            return 0.0;
        }
        return distanciaRecorrida / duracion; 
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoActividad() {
        return this.tipoActividad;
    }

    public void setTipoActividad(String tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public Integer getPasos() {
        return this.pasos;
    }

    public void setPasos(Integer pasos) {
        this.pasos = pasos;
    }

    public Double getDistanciaRecorrida() {
        return this.distanciaRecorrida;
    }

    public void setDistanciaRecorrida(Double distanciaRecorrida) {
        this.distanciaRecorrida = distanciaRecorrida;
    }

    public Double getCaloriasQuemadas() {
        return this.caloriasQuemadas;
    }

    public void setCaloriasQuemadas(Double caloriasQuemadas) {
        this.caloriasQuemadas = caloriasQuemadas;
    }

    public Double getVelocidadMaxima() {
        return this.velocidadMaxima;
    }

    public void setVelocidadMaxima(Double velocidadMaxima) {
        this.velocidadMaxima = velocidadMaxima;
    }

    public Double getDuracion() {
        return this.duracion;
    }

    public void setDuracion(Double duracion) {
        this.duracion = duracion;
    }

    public LocalDateTime getFechaYHora() {
        return this.fechaYHora;
    }

    public void setFechaYHora(LocalDateTime fechaYHora) {
        this.fechaYHora = fechaYHora;
    }

    public Mascota getMascota() {
        return this.mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

}