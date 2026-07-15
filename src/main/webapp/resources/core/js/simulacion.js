/* global ApexCharts, idMascota, lucide */
/* exported abrirModalSim, cerrarModalSim */

var ultimosDatosSimulacion = {};

var historialTimestamps = [];
var historialDistancias = [];
var historialCalorias   = [];
var historialSueno      = [];
var historialPasos      = [];
var historialHoras      = [];
var nivelesHoras        = {};

var STORAGE_KEY  = "analisis_" + idMascota;
var rangoMinutos = 0;

// =========================
// RANGO DE TIEMPO
// =========================
var selectorRango = document.getElementById("selector-rango");
if (selectorRango) {
  selectorRango.addEventListener("change", function() {
    rangoMinutos = parseInt(this.value, 10);
    aplicarFiltroRango();
  });
}

function ahora() { return new Date(); }

function ultimoNoNulo(arr) {
  for (var i = arr.length - 1; i >= 0; i--) {
    if (arr[i] != null) { return arr[i]; }
  }
  return null;
}

// Distancia, calorías, sueño y pasos son contadores ACUMULADOS desde que
// arrancó la app (por eso se comparan contra una "meta diaria"). Para saber
// cuánto se acumuló DENTRO de una ventana de tiempo (ej: "últimos 15 min")
// hay que restarle el valor que ya tenían justo antes de que arrancara esa
// ventana. Esta función busca ese valor base.
function valorAcumuladoEnLimite(valores, limite) {
  var val = 0;
  for (var i = 0; i < historialTimestamps.length; i++) {
    if (historialTimestamps[i] > limite) { break; }
    if (valores[i] != null) { val = valores[i]; }
  }
  return val;
}

function aplicarFiltroRango() {
  var limite = rangoMinutos === 0 ? null : new Date(ahora().getTime() - rangoMinutos * 60 * 1000);

  var indices;
  if (limite === null) {
    indices = historialTimestamps.map(function(_, i) { return i; });
  } else {
    indices = historialTimestamps
      .map(function(ts, i) { return ts >= limite ? i : -1; })
      .filter(function(i) { return i !== -1; });
  }

  // Valor acumulado justo antes de que arrancara la ventana elegida.
  // Si "todo el historial" está seleccionado, la base es 0 (se muestra el total tal cual).
  var baseDistancia = limite !== null ? valorAcumuladoEnLimite(historialDistancias, limite) : 0;
  var baseCalorias  = limite !== null ? valorAcumuladoEnLimite(historialCalorias, limite)   : 0;
  var baseSueno     = limite !== null ? valorAcumuladoEnLimite(historialSueno, limite)      : 0;
  var basePasos     = limite !== null ? valorAcumuladoEnLimite(historialPasos, limite)      : 0;

  var horasFiltradas      = indices.map(function(i) { return historialHoras[i]; });
  var distanciasFiltradas = indices.map(function(i) { return historialDistancias[i] != null ? Math.max(0, Number((historialDistancias[i] - baseDistancia).toFixed(2))) : null; });
  var caloriasFiltradas   = indices.map(function(i) { return historialCalorias[i]   != null ? Math.max(0, Number((historialCalorias[i]   - baseCalorias).toFixed(1)))  : null; });
  var suenoFiltrado       = indices.map(function(i) { return historialSueno[i]      != null ? Math.max(0, historialSueno[i]      - baseSueno)                          : null; });
  var pasosFiltrados      = indices.map(function(i) { return historialPasos[i]      != null ? Math.max(0, historialPasos[i]      - basePasos)                          : null; });

  // Actualizar líneas (ahora muestran lo acumulado DENTRO de la ventana elegida, arrancando en 0)
  chartHistorialDistancia.updateOptions({ series: [{ data: distanciasFiltradas }], xaxis: { categories: horasFiltradas } });
  chartHistorialCalorias.updateOptions({  series: [{ data: caloriasFiltradas }],   xaxis: { categories: horasFiltradas } });
  chartHistorialSueno.updateOptions({     series: [{ data: suenoFiltrado }],        xaxis: { categories: horasFiltradas } });
  chartHistorialPasos.updateOptions({     series: [{ data: pasosFiltrados }],       xaxis: { categories: horasFiltradas } });

  // Actualizar radiales con el último valor no nulo del rango filtrado (ya rebasado)
  var ultimaDist  = ultimoNoNulo(distanciasFiltradas);
  var ultimaCal   = ultimoNoNulo(caloriasFiltradas);
  var ultimoSueno = ultimoNoNulo(suenoFiltrado);
  var ultimoPaso  = ultimoNoNulo(pasosFiltrados);

  if (ultimaDist != null) {
    var d = ultimaDist;
    chartDistancia.updateOptions({ series: [Math.min((d / 5.0) * 100, 100)], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return d.toFixed(1); } } } } } });
  }
  if (ultimaCal != null) {
    var c = ultimaCal;
    chartCalorias.updateOptions({ series: [Math.min((c / 200.0) * 100, 100)], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return c.toFixed(1); } } } } } });
  }
  if (ultimoSueno != null) {
    var su = ultimoSueno;
    var pctSueno = Math.min((su / 480) * 100, 100);
    var etqSueno = su >= 60
      ? (Math.floor(su / 60) + "h" + (su % 60 > 0 ? " " + (su % 60) + "m" : ""))
      : (su + "m");
    chartSueno.updateOptions({ series: [pctSueno], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return etqSueno; } } } } } });
  }
  if (ultimoPaso != null) {
    var pa = ultimoPaso;
    chartPasos.updateOptions({ series: [Math.min((pa / 10000) * 100, 100)], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return pa.toLocaleString("es-AR"); } } } } } });
  }
}

// =========================
// PERSISTENCIA
// =========================
function cargarEstado() {
  try {
    var guardado = sessionStorage.getItem(STORAGE_KEY);
    if (!guardado) { return; }
    var s = JSON.parse(guardado);
    historialTimestamps = (s.historialTimestamps || []).map(function(t) { return new Date(t); });
    historialDistancias = s.historialDistancias || [];
    historialCalorias   = s.historialCalorias   || [];
    historialSueno      = s.historialSueno      || [];
    historialPasos      = s.historialPasos      || [];
    historialHoras      = s.historialHoras      || [];
    nivelesHoras        = s.nivelesHoras        || {};
  } catch (e) { console.warn("No se pudo cargar el estado guardado:", e); }
}

function guardarEstado() {
  try {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify({
      historialTimestamps: historialTimestamps.map(function(t) { return t.toISOString(); }),
      historialDistancias: historialDistancias,
      historialCalorias:   historialCalorias,
      historialSueno:      historialSueno,
      historialPasos:      historialPasos,
      historialHoras:      historialHoras,
      nivelesHoras:        nivelesHoras
    }));
  } catch (e) { console.warn("No se pudo guardar el estado:", e); }
}

function limpiarViejos() {
  if (rangoMinutos === 0) { return; }
  var limite = new Date(ahora().getTime() - 60 * 60 * 1000);
  while (historialTimestamps.length > 0 && historialTimestamps[0] < limite) {
    historialTimestamps.shift();
    historialHoras.shift();
    historialDistancias.shift();
    historialCalorias.shift();
    historialSueno.shift();
    historialPasos.shift();
  }
}

cargarEstado();

// =========================
// NIVELES DE ACTIVIDAD
// =========================
function getHoraRedondeada() {
  var a = ahora();
  var hh = a.getHours().toString().padStart(2, "0");
  var mm = (Math.floor(a.getMinutes() / 5) * 5).toString().padStart(2, "0");
  return hh + ":" + mm;
}

function registrarNivel(estado) {
  var hora = getHoraRedondeada();
  if (!nivelesHoras[hora]) {
    nivelesHoras[hora] = { intenso: 0, moderado: 0, liviano: 0 };
  }
  if (estado === "CORRIENDO")      { nivelesHoras[hora].intenso++;  }
  else if (estado === "CAMINANDO") { nivelesHoras[hora].moderado++; }
  else                             { nivelesHoras[hora].liviano++;  }
}

function getNivelesSeriesYCategorias() {
  var horas = Object.keys(nivelesHoras).sort();
  return {
    categorias: horas,
    intenso:    horas.map(function(h) { return nivelesHoras[h].intenso; }),
    moderado:   horas.map(function(h) { return nivelesHoras[h].moderado; }),
    liviano:    horas.map(function(h) { return nivelesHoras[h].liviano; })
  };
}

// =========================
// HELPERS CHARTS
// =========================
function formatearEstado(estado) {
  return estado.toLowerCase().replace("_", " ").replace(/\b\w/g, function(l) { return l.toUpperCase(); });
}

function getConfiguracionGrafico(colorPrincipal, etiqueta, valorMostrar, porcentajeLlenado) {
  return {
    series: [porcentajeLlenado],
    chart: { type: "radialBar", height: 200, sparkline: { enabled: true } },
    colors: [colorPrincipal],
    plotOptions: {
      radialBar: {
        hollow: { size: "65%" },
        track:  { background: "#f1f5f9" },
        dataLabels: {
          show: true,
          name:  { show: true, color: "#94a3b8", fontSize: "12px", offsetY: 20 },
          value: { show: true, color: "#1e293b", fontSize: "22px", fontWeight: "600", offsetY: -10, formatter: function() { return valorMostrar; } }
        }
      }
    },
    stroke: { lineCap: "round" },
    labels: [etiqueta],
    responsive: [{ breakpoint: 640, options: { chart: { height: 170 } } }]
  };
}

function getOpcionesHistorial(nombreSerie, color, formatterFn) {
  return {
    series: [{ name: nombreSerie, data: [] }],
    chart: { height: 300, type: "line", toolbar: { show: false }, zoom: { enabled: false } },
    stroke: { curve: "smooth", width: 3 },
    colors: [color],
    xaxis: { categories: [], labels: { style: { colors: "#94a3b8" } }, axisBorder: { show: false }, axisTicks: { show: false } },
    yaxis: { min: 0, tickAmount: 4, labels: { formatter: formatterFn, style: { colors: "#94a3b8" } } },
    tooltip: { y: { formatter: formatterFn } },
    grid: { borderColor: "#f1f5f9", strokeDashArray: 4, yaxis: { lines: { show: true } } }
  };
}

// =========================
// RADIAL CHARTS
// =========================
var chartDistancia = new ApexCharts(document.querySelector("#chart-distancia"), getConfiguracionGrafico("#3b82f6", "km",    "0", 0));
var chartCalorias  = new ApexCharts(document.querySelector("#chart-calorias"),  getConfiguracionGrafico("#10b981", "kcal",  "0", 0));
var chartSueno     = new ApexCharts(document.querySelector("#chart-sueno"),     getConfiguracionGrafico("#f59e0b", "horas", "0", 0));
var chartPasos     = new ApexCharts(document.querySelector("#chart-pasos"),     getConfiguracionGrafico("#ec4899", "pasos", "0", 0));

chartDistancia.render();
chartCalorias.render();
chartSueno.render();
chartPasos.render();

// =========================
// HISTORIAL LINE CHARTS
// =========================
var chartHistorialDistancia = new ApexCharts(
  document.querySelector("#chart-historial-distancia"),
  getOpcionesHistorial("Distancia (km)", "#3b82f6", function(v) { return v.toFixed(2) + " km"; })
);
chartHistorialDistancia.render();

var chartHistorialCalorias = new ApexCharts(
  document.querySelector("#chart-historial-calorias"),
  getOpcionesHistorial("Calor\u00edas (kcal)", "#10b981", function(v) { return v.toFixed(1) + " kcal"; })
);
chartHistorialCalorias.render();

var chartHistorialSueno = new ApexCharts(
  document.querySelector("#chart-historial-sueno"),
  getOpcionesHistorial("Sue\u00f1o (min)", "#f59e0b", function(v) {
    if (v >= 60) {
      var h = Math.floor(v / 60);
      var m = Math.round(v % 60);
      return m > 0 ? h + "h " + m + "m" : h + "h";
    }
    return Math.round(v) + "m";
  })
);
chartHistorialSueno.render();

var chartHistorialPasos = new ApexCharts(
  document.querySelector("#chart-historial-pasos"),
  getOpcionesHistorial("Pasos", "#ec4899", function(v) { return Math.round(v).toLocaleString("es-AR") + " pasos"; })
);
chartHistorialPasos.render();

// =========================
// CHART NIVELES DE ACTIVIDAD
// =========================
var nivelesIniciales = getNivelesSeriesYCategorias();
var chartNiveles = new ApexCharts(document.querySelector("#chart-niveles-actividad"), {
  series: [
    { name: "Intenso",  data: nivelesIniciales.intenso  },
    { name: "Moderado", data: nivelesIniciales.moderado },
    { name: "Liviano",  data: nivelesIniciales.liviano  }
  ],
  chart: { type: "bar", height: 300, stacked: true, toolbar: { show: false }, zoom: { enabled: false } },
  colors: ["#ef4444", "#d97706", "#3b82f6"],
  plotOptions: { bar: { horizontal: false, columnWidth: "55%", borderRadius: 4, borderRadiusApplication: "end", borderRadiusWhenStacked: "last" } },
  dataLabels: { enabled: false },
  legend: { position: "top", horizontalAlign: "right", labels: { colors: "#64748b" } },
  xaxis: { categories: nivelesIniciales.categorias, labels: { style: { colors: "#94a3b8" } }, axisBorder: { show: false }, axisTicks: { show: false } },
  yaxis: { min: 0, tickAmount: 4, labels: { formatter: function(v) { return Math.round(v); }, style: { colors: "#94a3b8" } } },
  tooltip: { y: { formatter: function(v) { return Math.round(v) + "min"; } } },
  grid: { borderColor: "#f1f5f9", strokeDashArray: 4, yaxis: { lines: { show: true } } },
  fill: { opacity: 1 }
});
chartNiveles.render();

// =========================
// BADGE
// =========================
var CONFIG_BADGE = {
  CORRIENDO: { clase: "bg-red-100 text-red-600",       dot: "bg-red-500",    texto: "Nivel alto de actividad"     },
  CAMINANDO: { clase: "bg-yellow-100 text-yellow-700", dot: "bg-yellow-600", texto: "Nivel moderado de actividad" },
  DURMIENDO: { clase: "bg-blue-100 text-blue-600",     dot: "bg-blue-500",   texto: "Nivel liviano de actividad"  },
  _DEFAULT:  { clase: "bg-blue-100 text-blue-600",     dot: "bg-blue-500",   texto: "Nivel liviano de actividad"  }
};

function actualizarBadge(estado) {
  var cfg   = CONFIG_BADGE[estado] || CONFIG_BADGE["_DEFAULT"];
  var badge = document.getElementById("badge-actividad");
  badge.className = cfg.clase + " px-4 py-2 rounded-full font-medium text-sm flex items-center gap-2";
  badge.innerHTML = "<div class=\"w-2 h-2 rounded-full " + cfg.dot + "\"></div> " + cfg.texto;
}

// =========================
// POLLING
// =========================
function conectarYActualizar() {
  fetch("/spring/analisis/estado/" + idMascota, { cache: "no-store" })
	.then(function(response) { return response.json(); })
    .then(function(data) {
      ultimosDatosSimulacion = data;

      document.getElementById("estado").textContent = formatearEstado(data.estado);
      actualizarBadge(data.estado);
      registrarNivel(data.estado);

      var ts = ahora();
      var horaActual = ts.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });

      historialTimestamps.push(ts);
      historialHoras.push(horaActual);
      historialDistancias.push(data.distanciaRecorrida != null ? Number(data.distanciaRecorrida.toFixed(2)) : null);
      historialCalorias.push(data.calorias != null ? Number(data.calorias.toFixed(1)) : null);
      historialSueno.push(data.minutosDormidos != null ? data.minutosDormidos : null);
      historialPasos.push(data.pasos != null ? data.pasos : null);

      var niveles = getNivelesSeriesYCategorias();
      chartNiveles.updateOptions({
        series: [
          { name: "Intenso",  data: niveles.intenso  },
          { name: "Moderado", data: niveles.moderado },
          { name: "Liviano",  data: niveles.liviano  }
        ],
        xaxis: { categories: niveles.categorias }
      });

      limpiarViejos();
      aplicarFiltroRango();
      guardarEstado();
    })
    .catch(function(error) { console.error("Error al actualizar estado:", error); });
}

// =========================
// MODAL VER MÁS
// =========================
function abrirModalSim(tipo) {
  var modal     = document.getElementById("modal-sim-vermas");
  var titulo    = document.getElementById("modal-sim-titulo");
  var contenido = document.getElementById("modal-sim-contenido");
  var html      = "";
  var actual;

  if (tipo === "distancia") {
    titulo.textContent = "Distancia Recorrida";
    actual = ultimosDatosSimulacion.distanciaRecorrida;
    var pctDist2   = actual != null ? Math.min((actual / 5.0) * 100, 100).toFixed(0) : null;
    var dentroDist = actual != null && actual >= 5.0 * 0.5;
    html = simFila("Distancia hoy", actual != null ? actual.toFixed(2) + " km" : "Sin datos") +
           simFila("Meta diaria", "~5 km") +
           simFila("Progreso", pctDist2 != null ? pctDist2 + "%" : "Sin datos") +
           simEstado(dentroDist, actual != null, "Buen avance", "Tu mascota puede caminar m\u00e1s") +
           simDesc("La distancia recorrida refleja la actividad f\u00edsica acumulada del d\u00eda.");
  } else if (tipo === "calorias") {
    titulo.textContent = "Calor\u00edas Quemadas";
    actual = ultimosDatosSimulacion.calorias;
    var dentroCal = actual != null && actual >= 200.0 * 0.6;
    html = simFila("Calor\u00edas hoy", actual != null ? actual.toFixed(1) + " kcal" : "Sin datos") +
           simFila("Meta diaria", "~200 kcal") +
           simEstado(dentroCal, actual != null, "Buen gasto energ\u00e9tico", "Actividad insuficiente") +
           simDesc("Las calor\u00edas quemadas dependen del peso, estado y actividad de tu mascota.");
  } else if (tipo === "sueno") {
    titulo.textContent = "Tiempo de Sue\u00f1o";
    actual = ultimosDatosSimulacion.minutosDormidos;
    var dentroSueno   = actual != null && actual >= 480 * 0.6;
    var etiquetaModal = actual != null
      ? (actual >= 60 ? Math.floor(actual / 60) + "h " + (actual % 60 > 0 ? actual % 60 + "m" : "") : actual + "m")
      : "Sin datos";
    html = simFila("Sue\u00f1o hoy", etiquetaModal) +
           simFila("Meta diaria", "~8 horas") +
           simEstado(dentroSueno, actual != null, "Descanso adecuado", "Tu mascota descans\u00f3 poco") +
           simDesc("El sue\u00f1o es esencial para la recuperaci\u00f3n y el bienestar de tu mascota.");
  } else if (tipo === "pasos") {
    titulo.textContent = "Pasos Diarios";
    actual = ultimosDatosSimulacion.pasos;
    var dentroPasos = actual != null && actual >= 10000 * 0.7;
    var actualStr   = actual != null ? (actual > 1000 ? (actual / 1000).toFixed(1) + "k" : actual) + " pasos" : "Sin datos";
    html = simFila("Pasos hoy", actualStr) +
           simFila("Meta diaria recomendada", "~10.000 pasos") +
           simEstado(dentroPasos, actual != null, "Meta casi alcanzada", "Tu mascota necesita m\u00e1s actividad") +
           simDesc("La actividad f\u00edsica diaria es fundamental para el peso y la salud articular de tu mascota.");
  }

  contenido.innerHTML = html;
  modal.classList.remove("hidden");
}

function cerrarModalSim() {
  document.getElementById("modal-sim-vermas").classList.add("hidden");
}

document.getElementById("modal-sim-vermas").addEventListener("click", function(e) {
  if (e.target === this) { cerrarModalSim(); }
});

function simFila(etiqueta, valor) {
  return "<div class=\"flex justify-between items-center bg-slate-50 rounded-xl px-4 py-3\">" +
           "<span class=\"text-slate-500 font-medium\">" + etiqueta + "</span>" +
           "<span class=\"text-slate-800 font-bold\">" + valor + "</span>" +
         "</div>";
}

function simEstado(dentro, hayDatos, textoOk, textoBad) {
  if (!hayDatos) {
    return "<div class=\"rounded-xl px-4 py-3 bg-slate-100 text-slate-500 text-center font-medium\">Sin datos suficientes para evaluar</div>";
  }
  if (dentro) {
    return "<div class=\"rounded-xl px-4 py-3 bg-emerald-50 border border-emerald-200 text-emerald-700 text-center font-semibold\">\u2713 " + textoOk + "</div>";
  }
  return "<div class=\"rounded-xl px-4 py-3 bg-rose-50 border border-rose-200 text-rose-700 text-center font-semibold\">\u26a0 " + textoBad + "</div>";
}

function simDesc(texto) {
  return "<p class=\"text-slate-400 text-xs leading-relaxed px-1\">" + texto + "</p>";
}

function abrirModalImpactoSim() {
  document.getElementById("modal-impacto-sim").classList.remove("hidden");
  lucide.createIcons();
}

function cerrarModalImpactoSim() {
  document.getElementById("modal-impacto-sim").classList.add("hidden");
}

document.getElementById("modal-impacto-sim").addEventListener("click", function(e) {
  if (e.target === this) { cerrarModalImpactoSim(); }
});

lucide.createIcons();
actualizarEstado();
setInterval(actualizarEstado, 30000);