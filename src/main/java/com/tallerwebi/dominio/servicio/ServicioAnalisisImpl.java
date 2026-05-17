package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.infraestructura.RepositorioAnalisisImpl;
import com.tallerwebi.infraestructura.RepositorioMascotaImpl;
import java.time.LocalDateTime;
import java.util.Random;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioAnalisisImpl implements ServicioAnalisis {

  private RepositorioAnalisisImpl repositorioAnalisis;
  private RepositorioMascotaImpl repositorioMascota;
  private Random random = new Random();

  // Punto de partida inicial
  private Double latitudActual = -34.7222;
  private Double longitudActual = -58.5250;

  @Autowired
  public ServicioAnalisisImpl(
    RepositorioAnalisisImpl repositorioAnalisis,
    RepositorioMascotaImpl repositorioMascota
  ) {
    this.repositorioAnalisis = repositorioAnalisis;
    this.repositorioMascota = repositorioMascota;
  }

  @Scheduled(fixedRate = 60000) // 1 minuto
  @Override
  public void simularGeolocalizacion() {
    // por ahora similacion
    Mascota perro = repositorioMascota.buscarPorId(1L);

    if (perro != null) {
      // salto maximo de aprox 50 metros
      latitudActual += (random.nextDouble() - 0.5) * 0.001;
      longitudActual += (random.nextDouble() - 0.5) * 0.001;

      Analisis nuevoAnalisis = new Analisis();
      nuevoAnalisis.setLatitud(latitudActual);
      nuevoAnalisis.setLongitud(longitudActual);
      nuevoAnalisis.setFechaYHora(LocalDateTime.now());
      nuevoAnalisis.setMascota(perro);

      repositorioAnalisis.guardar(nuevoAnalisis);
    }
  }

  public Double calcularDistancia(Double lat1, Double lon1, Double lat2, Double lon2 ){
    final int RADIO_TIERRA = 6371;
    double disLat = Math.toRadians(lat1 - lat2);
    double disLon = Math.toRadians(lon1 - lon2);

    double a = Math.sin(disLat / 2) * Math.sin(disLat / 2) +
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
               Math.sin(disLon / 2) * Math.sin(disLon / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
    return RADIO_TIERRA * c;
  }
}
