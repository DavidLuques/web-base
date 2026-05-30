package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioActividad;
import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.RepositorioSueno;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.modelo.Actividad;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RegistroSueno;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrquestadorServiceImpl implements OrquestadorService {

  private static final int MINUTOS_POR_TICK = 2;

  private final MascotaDao mascotaDao;
  private final LectorCollarService lectorCollarService;
  private final AnalizadorDeDatosService analizadorDeDatosService;
  private final EvaluadorAlertaService evaluadorAlertaService;
  private final RepositorioActividad repositorioActividad;
  private final RepositorioSueno repositorioSueno;
  private final RepositorioAnalisis repositorioAnalisis;

  @Autowired
  public OrquestadorServiceImpl(
    MascotaDao mascotaDao,
    LectorCollarService lectorCollarService,
    AnalizadorDeDatosService analizadorDeDatosService,
    EvaluadorAlertaService evaluadorAlertaService,
    RepositorioActividad repositorioActividad,
    RepositorioSueno repositorioSueno,
    RepositorioAnalisis repositorioAnalisis
  ) {
    this.mascotaDao = mascotaDao;
    this.lectorCollarService = lectorCollarService;
    this.analizadorDeDatosService = analizadorDeDatosService;
    this.evaluadorAlertaService = evaluadorAlertaService;
    this.repositorioActividad = repositorioActividad;
    this.repositorioSueno = repositorioSueno;
    this.repositorioAnalisis = repositorioAnalisis;
  }

  @Override
  public ResultadoSimulacionDto procesarMascota(Long idMascota) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    LecturaSensor lectura = lectorCollarService.obtenerLectura(idMascota);

    EstadoMascota estado = analizadorDeDatosService.determinarEstado(mascota, lectura);
    mascota.setEstadoActual(estado);
    mascotaDao.modificar(mascota);

    evaluadorAlertaService.evaluarLectura(mascota, lectura);
    evaluadorAlertaService.evaluarPeso(mascota);

    persistirSuenoSiCorresponde(mascota, estado);
    persistirActividadSiCorresponde(mascota, estado);

    return armarDto(mascota, lectura, estado);
  }

  @Override
  public void procesarTodasLasMascotas() {
    List<Mascota> mascotas = mascotaDao.buscarTodas();
    for (Mascota mascota : mascotas) {
      procesarMascota(mascota.getId());
    }
  }

  @Override
  public ResultadoSimulacionDto obtenerUltimoEstado(Long idMascota) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);

    if (mascota == null) {
      return new ResultadoSimulacionDto("No encontrada", null, null, null);
    }

    Double distanciaTotal = repositorioActividad.obtenerDistanciaTotalPorMascota(idMascota);
    Integer pasos = analizadorDeDatosService.calcularPasos(distanciaTotal, mascota.getTamano());
    Integer minutosDormidos = repositorioSueno.obtenerTotalMinutosDormidosPorMascota(idMascota);
    Analisis ultimo = repositorioAnalisis.obtenerUltimoAnalisis(idMascota);

    if (ultimo == null) {
      return new ResultadoSimulacionDto(
        mascota.getNombre(),
        mascota.getEstadoActual(),
        distanciaTotal,
        pasos,
        0.0,
        minutosDormidos
      );
    }

    Double calorias = analizadorDeDatosService.calcularCalorias(
      distanciaTotal,
      mascota.getEstadoActual(),
      mascota.getPeso()
    );

    return new ResultadoSimulacionDto(
      mascota.getNombre(),
      mascota.getEstadoActual(),
      ultimo.getDatos().getFrecuenciaCardiaca(),
      ultimo.getDatos().getPresionSistolica(),
      ultimo.getDatos().getPresionDiastolica(),
      ultimo.getDatos().getTemperatura(),
      distanciaTotal,
      pasos,
      calorias,
      minutosDormidos
    );
  }

  // ── privados ─────────────────────────────────────────────

  private void persistirSuenoSiCorresponde(Mascota mascota, EstadoMascota estado) {
    if (!estado.getComportamiento().registraSueno()) return;
    RegistroSueno registro = new RegistroSueno();
    registro.setMinutosDormido(MINUTOS_POR_TICK);
    registro.setFechaYHora(LocalDateTime.now());
    registro.setMascota(mascota);
    repositorioSueno.guardar(registro);
  }

  private void persistirActividadSiCorresponde(Mascota mascota, EstadoMascota estado) {
    if (!estado.getComportamiento().registraActividad()) return;
    double distanciaEnKm = estado.getComportamiento().getVelocidadKmH() * (MINUTOS_POR_TICK / 60.0);
    Actividad actividad = new Actividad();
    actividad.setDistanciaRecorrida(distanciaEnKm);
    actividad.setFechaYHora(LocalDateTime.now());
    actividad.setMascota(mascota);
    repositorioActividad.guardar(actividad);
  }

  private ResultadoSimulacionDto armarDto(
    Mascota mascota,
    LecturaSensor lectura,
    EstadoMascota estado
  ) {
    Double distanciaTotal = repositorioActividad.obtenerDistanciaTotalPorMascota(mascota.getId());
    Integer pasos = analizadorDeDatosService.calcularPasos(distanciaTotal, mascota.getTamano());
    Double calorias = analizadorDeDatosService.calcularCalorias(
      distanciaTotal,
      estado,
      mascota.getPeso()
    );
    Integer minutosDormidos = repositorioSueno.obtenerTotalMinutosDormidosPorMascota(
      mascota.getId()
    );

    return new ResultadoSimulacionDto(
      mascota.getNombre(),
      estado,
      lectura.getFrecuenciaCardiaca(),
      lectura.getPresionSistolica(),
      lectura.getPresionDiastolica(),
      lectura.getTemperatura(),
      distanciaTotal,
      pasos,
      calorias,
      minutosDormidos
    );
  }
}
