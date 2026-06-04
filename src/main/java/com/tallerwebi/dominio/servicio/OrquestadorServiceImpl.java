package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioActividad;
import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.RepositorioSueno;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.dto.RangosVitalesDto;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.modelo.Actividad;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.DatosAnalisis;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import com.tallerwebi.dominio.modelo.RegistroSueno;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de orquestación de la simulación.
 */
@Service
@Transactional
public class OrquestadorServiceImpl implements OrquestadorService {

  private static final int MINUTOS_POR_TICK = 2;
  private static final String CLAVE_LATITUD = "latitud";
  private static final String CLAVE_LONGITUD = "longitud";
  private static final String CLAVE_RADIO = "radio";

  private final MascotaDao mascotaDao;
  private final LectorCollarService lectorCollarService;
  private final AnalizadorDeDatosService analizadorDeDatosService;
  private final EvaluadorAlertaService evaluadorAlertaService;
  private final RepositorioActividad repositorioActividad;
  private final RepositorioSueno repositorioSueno;
  private final RepositorioAnalisis repositorioAnalisis;
  private final RangoVitalDao rangoVitalDao;

  @Autowired
  public OrquestadorServiceImpl(
    MascotaDao mascotaDao,
    LectorCollarService lectorCollarService,
    AnalizadorDeDatosService analizadorDeDatosService,
    EvaluadorAlertaService evaluadorAlertaService,
    RepositorioActividad repositorioActividad,
    RepositorioSueno repositorioSueno,
    RepositorioAnalisis repositorioAnalisis,
    RangoVitalDao rangoVitalDao
  ) {
    this.mascotaDao = mascotaDao;
    this.lectorCollarService = lectorCollarService;
    this.analizadorDeDatosService = analizadorDeDatosService;
    this.evaluadorAlertaService = evaluadorAlertaService;
    this.repositorioActividad = repositorioActividad;
    this.repositorioSueno = repositorioSueno;
    this.repositorioAnalisis = repositorioAnalisis;
    this.rangoVitalDao = rangoVitalDao;
  }

  @Override
  public void procesarTodasLasMascotas() {
    List<Mascota> mascotas = mascotaDao.buscarTodas();
    for (Mascota mascota : mascotas) {
      procesarMascota(mascota.getId());
    }
  }

  @Override
  public ResultadoSimulacionDto procesarMascota(Long idMascota) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    RangoVitalPorTamano rango = rangoVitalDao.buscarPorTamano(mascota.getTamano());
    LecturaSensor lectura = lectorCollarService.obtenerLectura(idMascota);

    EstadoMascota estado = analizadorDeDatosService.determinarEstado(mascota, lectura);
    mascota.setEstadoActual(estado);
    mascotaDao.modificar(mascota);

    // Evalúa signos vitales, peso y vallado (todo en un solo llamado)
    evaluadorAlertaService.evaluarLectura(mascota, lectura, rango);

    persistirSuenoSiCorresponde(mascota, estado);
    persistirActividadSiCorresponde(mascota, estado, lectura);
    persistirLectura(mascota, lectura);

    return armarDto(mascota, lectura, estado);
  }

  @Override
  public void refrescarTodasLasLecturas() {
    List<Mascota> mascotas = mascotaDao.buscarTodas();
    for (Mascota mascota : mascotas) {
      refrescarLectura(mascota.getId());
    }
  }

  @Override
  public ResultadoSimulacionDto refrescarLectura(Long idMascota) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    LecturaSensor lectura = lectorCollarService.obtenerLectura(idMascota);

    persistirLectura(mascota, lectura);

    return armarDto(mascota, lectura, mascota.getEstadoActual());
  }

  @Override
  public RangosVitalesDto obtenerRangosVitales(Long idMascota) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    RangoVitalPorTamano rango = rangoVitalDao.buscarPorTamano(mascota.getTamano());
    return new RangosVitalesDto(rango);
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

  @Override
  public Map<String, Object> obtenerVallado(Long idMascota) {
    // Vallado ahora se obtiene desde EvaluadorAlertaService
    // Pero el frontend sigue necesitando este endpoint
    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put(CLAVE_LATITUD, -34.7222);
    respuesta.put(CLAVE_LONGITUD, -58.5250);
    respuesta.put(CLAVE_RADIO, 150.0);
    return respuesta;
  }

  @Override
  public Map<String, Object> obtenerUltimaUbicacion(Long idMascota) {
    Analisis ultimo = repositorioAnalisis.obtenerUltimoAnalisis(idMascota);
    Map<String, Object> respuesta = new HashMap<>();

    if (ultimo != null) {
      respuesta.put(CLAVE_LATITUD, ultimo.getLatitud());
      respuesta.put(CLAVE_LONGITUD, ultimo.getLongitud());
    } else {
      respuesta.put(CLAVE_LATITUD, -34.7222);
      respuesta.put(CLAVE_LONGITUD, -58.5250);
    }
    return respuesta;
  }

  // ── privados ─────────────────────────────────────────────

  private void persistirLectura(Mascota mascota, LecturaSensor lectura) {
    Analisis analisis = new Analisis();
    analisis.setMascota(mascota);
    analisis.setLatitud(lectura.getLatitud());
    analisis.setLongitud(lectura.getLongitud());
    analisis.setFechaYHora(LocalDateTime.now());

    DatosAnalisis datos = new DatosAnalisis();
    datos.setFrecuenciaCardiaca(lectura.getFrecuenciaCardiaca());
    datos.setTemperatura(lectura.getTemperatura());
    datos.setPresionSistolica(lectura.getPresionSistolica());
    datos.setPresionDiastolica(lectura.getPresionDiastolica());
    datos.setAccelX(lectura.getAccelX());
    datos.setAccelY(lectura.getAccelY());
    datos.setAccelZ(lectura.getAccelZ());
    datos.setGyroX(lectura.getGyroX());
    datos.setGyroY(lectura.getGyroY());
    datos.setGyroZ(lectura.getGyroZ());

    analisis.setDatos(datos);
    repositorioAnalisis.guardar(analisis);
  }

  private void persistirSuenoSiCorresponde(Mascota mascota, EstadoMascota estado) {
    if (!estado.getComportamiento().registraSueno()) return;
    RegistroSueno registro = new RegistroSueno();
    registro.setMinutosDormido(MINUTOS_POR_TICK);
    registro.setFechaYHora(LocalDateTime.now());
    registro.setMascota(mascota);
    repositorioSueno.guardar(registro);
  }

  private void persistirActividadSiCorresponde(
    Mascota mascota,
    EstadoMascota estado,
    LecturaSensor lecturaActual
  ) {
    if (!estado.getComportamiento().registraActividad()) return;
    Analisis ultimoAnalisis = repositorioAnalisis.obtenerUltimoAnalisis(mascota.getId());
    if (ultimoAnalisis == null) return;
    double distanciaEnKm = analizadorDeDatosService.calcularDistanciaEntreUbicaciones(
      ultimoAnalisis.getLatitud(),
      ultimoAnalisis.getLongitud(),
      lecturaActual.getLatitud(),
      lecturaActual.getLongitud()
    );

    if (distanciaEnKm > 0) {
      Actividad actividad = new Actividad();
      actividad.setDistanciaRecorrida(distanciaEnKm);
      actividad.setFechaYHora(LocalDateTime.now());
      actividad.setMascota(mascota);
      repositorioActividad.guardar(actividad);
    }
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
