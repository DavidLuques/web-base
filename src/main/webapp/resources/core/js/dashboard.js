/* global ApexCharts, idMascota, lucide */
/* exported abrirModal, cerrarModal */

var ultimosDatos  = {};
var rangosVitales = {};

fetch("/spring/analisis/rangos/" + idMascota)
  .then(function(r) { return r.json(); })
  .then(function(r) { rangosVitales = r; })
  .catch(function(e) { console.warn("No se pudieron cargar los rangos:", e); });

// =========================
// RANGO DE TIEMPO
// =========================
var rangoMinutos = 0;

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

// Los pasos son un contador ACUMULADO desde que arrancó la app (por eso se
// comparan contra una meta diaria). Para saber cuántos pasos se hicieron
// DENTRO de una ventana de tiempo (ej: "últimos 15 min") hay que restarle el
// valor que ya tenía justo antes de que arrancara esa ventana. Frecuencia,
// temperatura y presión en cambio son lecturas puntuales (no acumuladas), así
// que a esas no se les resta nada.
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
    // Todo el historial
    indices = historialTimestamps.map(function(_, i) { return i; });
  } else {
    indices = historialTimestamps
      .map(function(ts, i) { return ts >= limite ? i : -1; })
      .filter(function(i) { return i !== -1; });
  }

  var basePasos = limite !== null ? valorAcumuladoEnLimite(historialPasos, limite) : 0;

  var horasFiltradas      = indices.map(function(i) { return historialHoras[i]; });
  var frecuenciaFiltrada  = indices.map(function(i) { return historialFrecuencia[i]; });
  var temperaturaFiltrada = indices.map(function(i) { return historialTemperatura[i]; });
  var sistolicaFiltrada   = indices.map(function(i) { return historialSistolica[i]; });
  var diastolicaFiltrada  = indices.map(function(i) { return historialDiastolica[i]; });
  var pasosFiltrados = indices.map(function(i) { return historialPasos[i] != null ? Math.max(0, historialPasos[i] - basePasos) : null; });

  // Actualizar líneas
  chartLineaFrecuencia.updateOptions({  series: [{ data: frecuenciaFiltrada }],  xaxis: { categories: horasFiltradas } });
  chartLineaTemperatura.updateOptions({ series: [{ data: temperaturaFiltrada }], xaxis: { categories: horasFiltradas } });
  chartLineaPresion.updateSeries([{ name: "Sist\u00f3lica", data: sistolicaFiltrada }, { name: "Diast\u00f3lica", data: diastolicaFiltrada }]);
  chartLineaPresion.updateOptions({ xaxis: { categories: horasFiltradas } });
  chartLineaPasos.updateOptions({ series: [{ data: pasosFiltrados }], xaxis: { categories: horasFiltradas } });

  // Actualizar radiales con el último valor no nulo del rango filtrado
  var ultimaFrecuencia  = ultimoNoNulo(frecuenciaFiltrada);
  var ultimaTemperatura = ultimoNoNulo(temperaturaFiltrada);
  var ultimaSistolica   = ultimoNoNulo(sistolicaFiltrada);
  var ultimaDiastolica  = ultimoNoNulo(diastolicaFiltrada);
  var ultimosPasos      = ultimoNoNulo(pasosFiltrados);

  if (ultimaFrecuencia != null) {
    var f = ultimaFrecuencia;
    chartFrecuencia.updateOptions({ series: [(f / 180) * 100], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return f; } } } } } });
  }
  if (ultimaTemperatura != null) {
    var t = ultimaTemperatura;
    chartTemperatura.updateOptions({ series: [(t / 45) * 100], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return parseFloat(t).toFixed(1); } } } } } });
  }
  if (ultimaSistolica != null && ultimaDiastolica != null) {
    var s = ultimaSistolica;
    var d = ultimaDiastolica;
    chartPresion.updateOptions({ series: [(s / 160) * 100], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return s + "/" + d; } } } } } });
  }
  if (ultimosPasos != null) {
    var p = ultimosPasos;
    chartPasos.updateOptions({ series: [Math.min((p / 10000) * 100, 100)], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return p > 1000 ? (p / 1000).toFixed(1) + "k" : p; } } } } } });
  }
}

// =========================
// MODAL
// =========================
function abrirModal(tipo) {
  var modal     = document.getElementById("modal-vermas");
  var titulo    = document.getElementById("modal-titulo");
  var contenido = document.getElementById("modal-contenido");
  var html      = "";
  var actual, min, max;

  if (tipo === "frecuencia") {
    titulo.textContent = "Frecuencia Card\u00edaca";
    actual = ultimosDatos.frecuenciaCardiaca;
    min    = rangosVitales.frecuenciaMinima;
    max    = rangosVitales.frecuenciaMaxima;
    html   = construirContenidoModal(
      actual != null ? actual + " bpm" : "Sin datos",
      min != null && max != null ? min + " \u2013 " + max + " bpm" : "Sin datos",
      actual, min, max,
      "La frecuencia card\u00edaca mide los latidos por minuto del coraz\u00f3n de tu mascota. " +
      "Valores fuera del rango pueden indicar estr\u00e9s, fiebre, dolor o problemas card\u00edacos."
    );
  } else if (tipo === "temperatura") {
    titulo.textContent = "Temperatura";
    actual = ultimosDatos.temperatura;
    min    = rangosVitales.temperaturaMinima;
    max    = rangosVitales.temperaturaMaxima;
    html   = construirContenidoModal(
      actual != null ? parseFloat(actual).toFixed(1) + " \u00b0C" : "Sin datos",
      min != null && max != null ? min + " \u2013 " + max + " \u00b0C" : "Sin datos",
      actual, min, max,
      "La temperatura corporal normal var\u00eda seg\u00fan el tama\u00f1o y la actividad del animal. " +
      "La fiebre puede indicar infecci\u00f3n o golpe de calor."
    );
  } else if (tipo === "presion") {
    titulo.textContent = "Presi\u00f3n Arterial";
    var actualSist = ultimosDatos.presionSistolica;
    var actualDias = ultimosDatos.presionDiastolica;
    var minSist    = rangosVitales.sistolicaMinima;
    var maxSist    = rangosVitales.sistolicaMaxima;
    var minDias    = rangosVitales.diastolicaMinima;
    var maxDias    = rangosVitales.diastolicaMaxima;
    var dentroSist = actualSist != null && minSist != null && maxSist != null && actualSist >= minSist && actualSist <= maxSist;
    var dentroDias = actualDias != null && minDias != null && maxDias != null && actualDias >= minDias && actualDias <= maxDias;
    html = construirFilaInfo("Sist\u00f3lica actual",    actualSist != null ? actualSist + " mmHg" : "Sin datos") +
           construirFilaInfo("Sist\u00f3lica esperada",  minSist != null ? minSist + " \u2013 " + maxSist + " mmHg" : "Sin datos") +
           construirFilaInfo("Diast\u00f3lica actual",   actualDias != null ? actualDias + " mmHg" : "Sin datos") +
           construirFilaInfo("Diast\u00f3lica esperada", minDias != null ? minDias + " \u2013 " + maxDias + " mmHg" : "Sin datos") +
           construirEstado(dentroSist && dentroDias, actualSist != null && actualDias != null) +
           construirDescripcion("La presi\u00f3n arterial tiene dos componentes: sist\u00f3lica (contracci\u00f3n) y diast\u00f3lica (relajaci\u00f3n). " +
             "Una presi\u00f3n elevada sostenida puede indicar problemas renales o card\u00edacos.");
  } else if (tipo === "pasos") {
    titulo.textContent = "Pasos Diarios";
    actual = ultimosDatos.pasos;
    var dentro    = actual != null && actual >= 10000 * 0.7;
    var actualStr = actual != null ? (actual > 1000 ? (actual / 1000).toFixed(1) + "k" : actual) + " pasos" : "Sin datos";
    html = construirFilaInfo("Pasos hoy", actualStr) +
           construirFilaInfo("Meta diaria recomendada", "~10.000 pasos") +
           construirEstado(dentro, actual != null) +
           construirDescripcion("La actividad f\u00edsica diaria es fundamental para el peso y la salud articular de tu mascota. " +
             "Se recomienda alcanzar la meta con caminatas y juego activo.");
  }

  contenido.innerHTML = html;
  modal.classList.remove("hidden");
}

function cerrarModal() {
  document.getElementById("modal-vermas").classList.add("hidden");
}

document.getElementById("modal-vermas").addEventListener("click", function(e) {
  if (e.target === this) { cerrarModal(); }
});

function construirContenidoModal(valorActual, rangoEsperado, actual, min, max, descripcion) {
  var dentro = actual != null && min != null && max != null && actual >= min && actual <= max;
  return construirFilaInfo("Valor actual", valorActual) +
         construirFilaInfo("Rango esperado", rangoEsperado) +
         construirEstado(dentro, actual != null) +
         construirDescripcion(descripcion);
}

function construirFilaInfo(etiqueta, valor) {
  return "<div class=\"flex justify-between items-center bg-slate-50 rounded-xl px-4 py-3\">" +
           "<span class=\"text-slate-500 font-medium\">" + etiqueta + "</span>" +
           "<span class=\"text-slate-800 font-bold\">" + valor + "</span>" +
         "</div>";
}

function construirEstado(dentro, hayDatos) {
  if (!hayDatos) {
    return "<div class=\"rounded-xl px-4 py-3 bg-slate-100 text-slate-500 text-center font-medium\">Sin datos suficientes para evaluar</div>";
  }
  if (dentro) {
    return "<div class=\"rounded-xl px-4 py-3 bg-emerald-50 border border-emerald-200 text-emerald-700 text-center font-semibold\">\u2713 Dentro del rango esperado</div>";
  }
  return "<div class=\"rounded-xl px-4 py-3 bg-rose-50 border border-rose-200 text-rose-700 text-center font-semibold\">\u26a0 Fuera del rango esperado</div>";
}

function construirDescripcion(texto) {
  return "<p class=\"text-slate-400 text-xs leading-relaxed px-1\">" + texto + "</p>";
}

// =========================
// HISTORIAL Y SESSION STORAGE
// =========================
var historialTimestamps  = [];
var historialHoras       = [];
var historialFrecuencia  = [];
var historialTemperatura = [];
var historialSistolica   = [];
var historialDiastolica  = [];
var historialPasos       = [];
var STORAGE_KEY          = "dashboard_" + idMascota;

function cargarEstado() {
  try {
    var guardado = sessionStorage.getItem(STORAGE_KEY);
    if (!guardado) { return; }
    var s = JSON.parse(guardado);
    historialTimestamps  = (s.historialTimestamps  || []).map(function(t) { return new Date(t); });
    historialHoras       = s.historialHoras       || [];
    historialFrecuencia  = s.historialFrecuencia  || [];
    historialTemperatura = s.historialTemperatura || [];
    historialSistolica   = s.historialSistolica   || [];
    historialDiastolica  = s.historialDiastolica  || [];
    historialPasos       = s.historialPasos       || [];
  } catch (e) { console.warn("No se pudo cargar el estado:", e); }
}

function guardarEstado() {
  try {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify({
      historialTimestamps:  historialTimestamps.map(function(t) { return t.toISOString(); }),
      historialHoras:       historialHoras,
      historialFrecuencia:  historialFrecuencia,
      historialTemperatura: historialTemperatura,
      historialSistolica:   historialSistolica,
      historialDiastolica:  historialDiastolica,
      historialPasos:       historialPasos
    }));
  } catch (e) { console.warn("No se pudo guardar el estado:", e); }
}

function limpiarViejos() {
  // Solo limpia si NO estamos en modo "todo el historial"
  if (rangoMinutos === 0) { return; }
  var limite = new Date(ahora().getTime() - 60 * 60 * 1000);
  while (historialTimestamps.length > 0 && historialTimestamps[0] < limite) {
    historialTimestamps.shift();
    historialHoras.shift();
    historialFrecuencia.shift();
    historialTemperatura.shift();
    historialSistolica.shift();
    historialDiastolica.shift();
    historialPasos.shift();
  }
}

cargarEstado();

// =========================
// GRÁFICOS RADIALES
// =========================
function getRadialConfig(color, label, formatFunction) {
  return {
    series: [0],
    chart: { type: "radialBar", height: 220, sparkline: { enabled: true } },
    colors: [color],
    plotOptions: {
      radialBar: {
        hollow: { size: "60%" },
        track: { background: "#f1f5f9", strokeWidth: "100%" },
        dataLabels: {
          show: true,
          name:  { show: true, color: "#94a3b8", fontSize: "10px", offsetY: 20 },
          value: { show: true, color: "#1e293b", fontSize: "24px", fontWeight: "bold", offsetY: -5, formatter: formatFunction }
        }
      }
    },
    stroke: { lineCap: "round" },
    labels: [label],
    responsive: [{
      breakpoint: 640,
      options: {
        chart: { height: 170 },
        plotOptions: {
          radialBar: {
            hollow: { size: "55%" },
            dataLabels: {
              name: { fontSize: "9px", offsetY: 15 },
              value: { fontSize: "18px", offsetY: -5 }
            }
          }
        }
      }
    }]
  };
}

var chartFrecuencia  = new ApexCharts(document.querySelector("#chart-frecuencia"),  getRadialConfig("#3b82f6", "bpm",   function(val) { return Math.round(val * 1.8); }));
var chartTemperatura = new ApexCharts(document.querySelector("#chart-temperatura"), getRadialConfig("#10b981", "\u00b0C", function(val) { return (val / 100 * 50).toFixed(1); }));
var chartPresion     = new ApexCharts(document.querySelector("#chart-presion"),     getRadialConfig("#f59e0b", "mmHg",  function(val) { return val + "/80"; }));
var chartPasos       = new ApexCharts(document.querySelector("#chart-pasos"),       getRadialConfig("#8b5cf6", "pasos", function(val) {
  var pasosReales = Math.round((val / 100) * 10000);
  return pasosReales > 1000 ? (pasosReales / 1000).toFixed(1) + "k" : pasosReales;
}));

chartFrecuencia.render();
chartTemperatura.render();
chartPresion.render();
chartPasos.render();

// =========================
// GRÁFICOS DE LÍNEAS
// =========================
function getLineaConfig(color, nombreSerie) {
  return {
    series: [{ name: nombreSerie, data: [] }],
    chart: { type: "area", height: 200, toolbar: { show: false } },
    colors: [color],
    fill: { type: "gradient", gradient: { shadeIntensity: 1, opacityFrom: 0.3, opacityTo: 0.02, stops: [0, 100] } },
    dataLabels: { enabled: false },
    stroke: { curve: "smooth", width: 3 },
    xaxis: { categories: [], labels: { style: { colors: "#94a3b8" } }, axisBorder: { show: false }, axisTicks: { show: false } },
    yaxis: { forceNiceScale: true, labels: { style: { colors: "#94a3b8" }, formatter: function(val) { return val.toFixed(1); } } },
    grid: { borderColor: "#f1f5f9", strokeDashArray: 4 }
  };
}

var chartLineaFrecuencia  = new ApexCharts(document.querySelector("#chart-historial-frecuencia"),  getLineaConfig("#60a5fa", "Frecuencia"));
var chartLineaTemperatura = new ApexCharts(document.querySelector("#chart-historial-temperatura"), getLineaConfig("#34d399", "Temperatura"));
var chartLineaPresion     = new ApexCharts(document.querySelector("#chart-historial-presion"), {
  series: [{ name: "Sist\u00f3lica", data: [] }, { name: "Diast\u00f3lica", data: [] }],
  chart: { type: "area", height: 200, toolbar: { show: false } },
  colors: ["#fbbf24", "#f59e0b"],
  fill: { type: "gradient", gradient: { shadeIntensity: 1, opacityFrom: 0.3, opacityTo: 0.02, stops: [0, 100] } },
  dataLabels: { enabled: false },
  stroke: { curve: "smooth", width: 3 },
  xaxis: { categories: [], labels: { style: { colors: "#94a3b8" } }, axisBorder: { show: false }, axisTicks: { show: false } },
  yaxis: { forceNiceScale: true, labels: { style: { colors: "#94a3b8" } } },
  grid: { borderColor: "#f1f5f9", strokeDashArray: 4 }
});
var chartLineaPasos = new ApexCharts(document.querySelector("#chart-historial-pasos"), getLineaConfig("#a78bfa", "Pasos"));

chartLineaFrecuencia.render();
chartLineaTemperatura.render();
chartLineaPresion.render();
chartLineaPasos.render();

// =========================
// CONEXIÓN Y ACTUALIZACIÓN
// =========================
function conectarYActualizar() {
  fetch("/spring/analisis/estado/" + idMascota, { cache: "no-store" })
    .then(function(response) { return response.json(); })
    .then(function(data) {
      ultimosDatos = data;

      var ts = ahora();
      var horaActual = ts.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit", second: "2-digit" });

      historialTimestamps.push(ts);
      historialHoras.push(horaActual);

      historialFrecuencia.push(data.frecuenciaCardiaca != null ? data.frecuenciaCardiaca : null);
      historialTemperatura.push(data.temperatura != null ? parseFloat(data.temperatura.toFixed(1)) : null);
      historialSistolica.push(data.presionSistolica != null ? data.presionSistolica : null);
      historialDiastolica.push(data.presionDiastolica != null ? data.presionDiastolica : null);
      historialPasos.push(data.pasos != null ? data.pasos : null);

      limpiarViejos();
      aplicarFiltroRango();
      guardarEstado();
    })
    .catch(function(error) { console.error("Error al conectar con el backend:", error); });
}

function abrirModalImpacto() {
  document.getElementById("modal-impacto").classList.remove("hidden");
  lucide.createIcons();
}

function cerrarModalImpacto() {
  document.getElementById("modal-impacto").classList.add("hidden");
}

document.getElementById("modal-impacto").addEventListener("click", function(e) {
  if (e.target === this) { cerrarModalImpacto(); }
});

lucide.createIcons();
conectarYActualizar();
setInterval(conectarYActualizar, 5000);