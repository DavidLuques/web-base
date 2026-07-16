package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.RepositorioActividad;
import com.tallerwebi.dominio.RepositorioAnalisis;
import com.tallerwebi.dominio.RepositorioRegistroEstado;
import com.tallerwebi.dominio.RepositorioSueno;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.dao.ValladoDao;
import com.tallerwebi.dominio.dto.HistorialDto;
import com.tallerwebi.dominio.dto.ImpactoDatosDto;
import com.tallerwebi.dominio.dto.NivelHoraDto;
import com.tallerwebi.dominio.dto.PuntoHistorialDto;
import com.tallerwebi.dominio.dto.RangosVitalesDto;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.estado.ComportamientoEstado;
import com.tallerwebi.dominio.modelo.Actividad;
import com.tallerwebi.dominio.modelo.Analisis;
import com.tallerwebi.dominio.modelo.DatosAnalisis;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import com.tallerwebi.dominio.modelo.RegistroEstado;
import com.tallerwebi.dominio.modelo.RegistroSueno;
import com.tallerwebi.dominio.modelo.Vallado;
import com.tallerwebi.dominio.tamano.ComportamientoTamano;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
  private static final DateTimeFormatter FORMATTER_HORA = DateTimeFormatter.ofPattern("HH:mm");

  private final MascotaDao mascotaDao;
  private final ValladoDao valladoDao;
  private final LectorCollarService lectorCollarService;
  private final AnalizadorDeDatosService analizadorDeDatosService;
  private final ServicioEvaluadorAlerta servicioEvaluadorAlerta;
  private final RepositorioActividad repositorioActividad;
  private final RepositorioSueno repositorioSueno;
  private final RepositorioAnalisis repositorioAnalisis;
  private final RangoVitalDao rangoVitalDao;
  private final RepositorioRegistroEstado repositorioRegistroEstado;

  @Autowired
  public OrquestadorServiceImpl(
    MascotaDao mascotaDao,
    LectorCollarService lectorCollarService,
    AnalizadorDeDatosService analizadorDeDatosService,
    ServicioEvaluadorAlerta servicioEvaluadorAlerta,
    RepositorioActividad repositorioActividad,
    RepositorioSueno repositorioSueno,
    RepositorioAnalisis repositorioAnalisis,
    RepositorioRegistroEstado repositorioRegistroEstado,
    RangoVitalDao rangoVitalDao,
    ValladoDao valladoDao
  ) {
    this.mascotaDao = mascotaDao;
    this.lectorCollarService = lectorCollarService;
    this.analizadorDeDatosService = analizadorDeDatosService;
    this.servicioEvaluadorAlerta = servicioEvaluadorAlerta;
    this.repositorioActividad = repositorioActividad;
    this.repositorioSueno = repositorioSueno;
    this.repositorioAnalisis = repositorioAnalisis;
    this.repositorioRegistroEstado = repositorioRegistroEstado;
    this.rangoVitalDao = rangoVitalDao;
    this.valladoDao = valladoDao;
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
    LecturaSensor lectura = lectorCollarService.obtenerLectura(idMascota);

    EstadoMascota estado = analizadorDeDatosService.determinarEstado(mascota, lectura);
    mascota.setEstadoActual(estado);
    mascotaDao.modificar(mascota);

    persistirEstado(mascota, estado);
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
    RangoVitalPorTamano rango = rangoVitalDao.buscarPorTipoYTamano(
      mascota.getTipo(),
      mascota.getTamano()
    );
    LecturaSensor lectura = lectorCollarService.obtenerLectura(idMascota);

    servicioEvaluadorAlerta.evaluarLectura(mascota, lectura, rango);

    persistirLectura(mascota, lectura);

    return armarDto(mascota, lectura, mascota.getEstadoActual());
  }

  @Override
  public RangosVitalesDto obtenerRangosVitales(Long idMascota) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    RangoVitalPorTamano rango = rangoVitalDao.buscarPorTipoYTamano(
      mascota.getTipo(),
      mascota.getTamano()
    );
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
    Vallado vallado = valladoDao.buscarPorMascota(idMascota);

    Double latHogar = (vallado != null && vallado.getLatitudCentro() != null)
      ? vallado.getLatitudCentro()
      : -34.7222;
    Double lonHogar = (vallado != null && vallado.getLongitudCentro() != null)
      ? vallado.getLongitudCentro()
      : -58.5250;

    Boolean valladoActivo = (vallado != null && vallado.getActivo() != null)
      ? vallado.getActivo()
      : true;

    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put(CLAVE_LATITUD, latHogar);
    respuesta.put(CLAVE_LONGITUD, lonHogar);
    respuesta.put("activo", valladoActivo);

    if (vallado != null && vallado.getRadioMetros() != null) {
      respuesta.put(CLAVE_RADIO, vallado.getRadioMetros().doubleValue());
    } else {
      respuesta.put(CLAVE_RADIO, 150.0);
    }
    return respuesta;
  }

  @Override
  public Map<String, Object> obtenerUltimaUbicacion(Long idMascota) {
    Analisis ultimo = repositorioAnalisis.obtenerUltimoAnalisis(idMascota);
    Vallado vallado = valladoDao.buscarPorMascota(idMascota);

    Double latHogar = (vallado != null && vallado.getLatitudCentro() != null)
      ? vallado.getLatitudCentro()
      : -34.7222;
    Double lonHogar = (vallado != null && vallado.getLongitudCentro() != null)
      ? vallado.getLongitudCentro()
      : -58.5250;

    Map<String, Object> respuesta = new HashMap<>();

    if (ultimo != null) {
      respuesta.put(CLAVE_LATITUD, ultimo.getLatitud());
      respuesta.put(CLAVE_LONGITUD, ultimo.getLongitud());
      respuesta.put("timestamp", ultimo.getFechaYHora().toString());

      double distanciaKm = analizadorDeDatosService.calcularDistanciaEntreUbicaciones(
        latHogar,
        lonHogar,
        ultimo.getLatitud(),
        ultimo.getLongitud()
      );
      respuesta.put("distancia", distanciaKm * 1000.0);

      double metrosY = -(ultimo.getLatitud() - latHogar) * 111320.0;
      double metrosX =
        (ultimo.getLongitud() - lonHogar) * (111320.0 * Math.cos(Math.toRadians(latHogar)));

      respuesta.put("metrosX", metrosX);
      respuesta.put("metrosY", metrosY);
    } else {
      respuesta.put(CLAVE_LATITUD, latHogar);
      respuesta.put(CLAVE_LONGITUD, lonHogar);
      respuesta.put("timestamp", LocalDateTime.now().toString());
      respuesta.put("distancia", 0.0);
      respuesta.put("metrosX", 0.0);
      respuesta.put("metrosY", 0.0);
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

  @Override
  public ImpactoDatosDto obtenerImpactoDatos(Long idMascota) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    if (mascota == null) {
      return null;
    }

    ComportamientoTamano comportamientoTamano = mascota.getTamano().getComportamiento();
    EstadoMascota estadoActual = mascota.getEstadoActual();
    ComportamientoEstado comportamientoEstado = estadoActual != null
      ? estadoActual.getComportamiento()
      : null;

    ImpactoDatosDto dto = new ImpactoDatosDto();
    dto.setPeso(mascota.getPeso());
    dto.setTamano(mascota.getTamano().name());
    dto.setPasosPorKm(comportamientoTamano.getPasosPorKm());
    dto.setPesoMinimoTamano(comportamientoTamano.getPesoMinimo());
    dto.setPesoMaximoTamano(comportamientoTamano.getPesoMaximo());
    dto.setEstadoActual(estadoActual != null ? estadoActual.name() : null);
    dto.setMetActual(comportamientoEstado != null ? comportamientoEstado.getMET() : null);
    dto.setVelocidadActualKmH(
      comportamientoEstado != null ? comportamientoEstado.getVelocidadKmH() : null
    );
    return dto;
  }

  private void persistirEstado(Mascota mascota, EstadoMascota estado) {
    RegistroEstado registro = new RegistroEstado();
    registro.setEstado(estado);
    registro.setFechaYHora(LocalDateTime.now());
    registro.setMascota(mascota);
    repositorioRegistroEstado.guardar(registro);
  }

  /**
   * Se suprime DataflowAnomalyAnalysis: PMD reporta falsos positivos sobre
   * variables definidas antes de un for-each (actividadList, suenoList,
   * acumulador) y usadas en cada vuelta del loop. Es un patrón acumulador
   * estándar; no hay una anomalía real de dataflow.
   */
  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  @Override
  public HistorialDto obtenerHistorial(Long idMascota) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    if (mascota == null) {
      return new HistorialDto();
    }

    List<Analisis> analisisList = repositorioAnalisis.buscarPorMascotaAsc(idMascota);
    List<Actividad> actividadList = repositorioActividad.buscarPorMascota(idMascota);
    List<RegistroSueno> suenoList = repositorioSueno.buscarPorMascota(idMascota);
    List<RegistroEstado> estadoList = repositorioRegistroEstado.buscarPorMascota(idMascota);

    AcumuladorHistorial acumulador = new AcumuladorHistorial(mascota.getEstadoActual());
    List<PuntoHistorialDto> puntos = new ArrayList<>();

    for (Analisis analisis : analisisList) {
      acumulador.avanzarHasta(analisis.getFechaYHora(), actividadList, suenoList, estadoList);
      puntos.add(construirPunto(analisis, acumulador, mascota));
    }

    HistorialDto dto = new HistorialDto();
    dto.setPuntos(puntos);
    dto.setNivelesActividad(construirNivelesActividad(estadoList));
    return dto;
  }

  private PuntoHistorialDto construirPunto(
    Analisis analisis,
    AcumuladorHistorial acumulador,
    Mascota mascota
  ) {
    Integer pasos = analizadorDeDatosService.calcularPasos(
      acumulador.distanciaAcumulada,
      mascota.getTamano()
    );
    Double calorias = analizadorDeDatosService.calcularCalorias(
      acumulador.distanciaAcumulada,
      acumulador.estadoVigente,
      mascota.getPeso()
    );

    PuntoHistorialDto punto = new PuntoHistorialDto();
    punto.setFechaYHora(
      analisis.getFechaYHora().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );
    punto.setFrecuenciaCardiaca(analisis.getDatos().getFrecuenciaCardiaca());
    punto.setPresionSistolica(analisis.getDatos().getPresionSistolica());
    punto.setPresionDiastolica(analisis.getDatos().getPresionDiastolica());
    punto.setTemperatura(analisis.getDatos().getTemperatura());
    punto.setDistanciaAcumulada(Math.round(acumulador.distanciaAcumulada * 100.0) / 100.0);
    punto.setCaloriasAcumuladas(calorias != null ? Math.round(calorias * 10.0) / 10.0 : null);
    punto.setMinutosDormidosAcumulados(acumulador.minutosAcumulados);
    punto.setPasosAcumulados(pasos);
    return punto;
  }

  /**
   * Encapsula el estado que se va acumulando a medida que se recorren
   * cronológicamente los análisis, para no tener variables primitivas
   * sueltas viviendo a lo largo de todo el método (evita anomalías de
   * dataflow y baja la complejidad ciclomática de obtenerHistorial).
   */
  private static final class AcumuladorHistorial {

    private double distanciaAcumulada = 0.0;
    private int minutosAcumulados = 0;
    private int idxActividad = 0;
    private int idxSueno = 0;
    private int idxEstado = 0;
    private EstadoMascota estadoVigente;

    private AcumuladorHistorial(EstadoMascota estadoInicial) {
      this.estadoVigente = estadoInicial;
    }

    private void avanzarHasta(
      LocalDateTime ts,
      List<Actividad> actividadList,
      List<RegistroSueno> suenoList,
      List<RegistroEstado> estadoList
    ) {
      while (
        idxActividad < actividadList.size() &&
        !actividadList.get(idxActividad).getFechaYHora().isAfter(ts)
      ) {
        distanciaAcumulada += actividadList.get(idxActividad).getDistanciaRecorrida();
        idxActividad++;
      }
      while (idxSueno < suenoList.size() && !suenoList.get(idxSueno).getFechaYHora().isAfter(ts)) {
        minutosAcumulados += suenoList.get(idxSueno).getMinutosDormido();
        idxSueno++;
      }
      while (
        idxEstado < estadoList.size() && !estadoList.get(idxEstado).getFechaYHora().isAfter(ts)
      ) {
        estadoVigente = estadoList.get(idxEstado).getEstado();
        idxEstado++;
      }
    }
  }

  private List<NivelHoraDto> construirNivelesActividad(List<RegistroEstado> estadoList) {
    LinkedHashMap<String, ContadorNivel> mapa = new LinkedHashMap<>();

    for (RegistroEstado registro : estadoList) {
      String hora = redondearHora(registro.getFechaYHora());
      ContadorNivel contador = mapa.computeIfAbsent(hora, k -> new ContadorNivel());
      contador.incrementar(registro.getEstado());
    }

    List<NivelHoraDto> resultado = new ArrayList<>();
    for (Map.Entry<String, ContadorNivel> entry : mapa.entrySet()) {
      ContadorNivel contador = entry.getValue();
      NivelHoraDto nivel = new NivelHoraDto();
      nivel.setHora(entry.getKey());
      nivel.setIntenso(contador.intenso);
      nivel.setModerado(contador.moderado);
      nivel.setLiviano(contador.liviano);
      resultado.add(nivel);
    }
    return resultado;
  }

  private String redondearHora(LocalDateTime ts) {
    int minutoRedondeado = (ts.getMinute() / 5) * 5;
    LocalDateTime tsRedondeado = ts.withMinute(minutoRedondeado).withSecond(0).withNano(0);
    return tsRedondeado.format(FORMATTER_HORA);
  }

  /**
   * Reemplaza el int[3] original: cada entrada del mapa de niveles de
   * actividad tiene su propio contador, en vez de reusar un mismo array
   * a lo largo de todas las iteraciones del loop.
   */
  private static final class ContadorNivel {

    private int intenso = 0;
    private int moderado = 0;
    private int liviano = 0;

    private void incrementar(EstadoMascota estado) {
      if (estado == EstadoMascota.CORRIENDO) {
        intenso++;
      } else if (estado == EstadoMascota.CAMINANDO) {
        moderado++;
      } else {
        liviano++;
      }
    }
  }
}
