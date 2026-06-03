/* global ApexCharts, idMascota, lucide */
/* exported abrirModal, cerrarModal */

var ultimosDatos  = {};
var rangosVitales = {};

fetch("/spring/analisis/rangos/" + idMascota)
  .then(function(r) { return r.json(); })
  .then(function(r) { rangosVitales = r; })
  .catch(function(e) { console.warn("No se pudieron cargar los rangos:", e); });

// =========================
// MODAL
// =========================
// eslint-disable-next-line no-unused-vars
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
var historialHoras       = [];
var historialFrecuencia  = [];
var historialTemperatura = [];
var historialSistolica   = [];
var historialDiastolica  = [];
var historialPasos       = [];
var MAX_PUNTOS           = 15;
var STORAGE_KEY          = "dashboard_" + idMascota;

function cargarEstado() {
  try {
    var guardado = sessionStorage.getItem(STORAGE_KEY);
    if (!guardado) { return; }
    var s = JSON.parse(guardado);
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
      historialHoras: historialHoras,
      historialFrecuencia: historialFrecuencia,
      historialTemperatura: historialTemperatura,
      historialSistolica: historialSistolica,
      historialDiastolica: historialDiastolica,
      historialPasos: historialPasos
    }));
  } catch (e) { console.warn("No se pudo guardar el estado:", e); }
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
    labels: [label]
  };
}

var chartFrecuencia  = new ApexCharts(document.querySelector("#chart-frecuencia"),  getRadialConfig("#3b82f6", "bpm",    function(val) { return Math.round(val * 1.8); }));
var chartTemperatura = new ApexCharts(document.querySelector("#chart-temperatura"), getRadialConfig("#10b981", "\u00b0C", function(val) { return (val / 100 * 50).toFixed(1); }));
var chartPresion     = new ApexCharts(document.querySelector("#chart-presion"),     getRadialConfig("#f59e0b", "mmHg",   function(val) { return val + "/80"; }));
var chartPasos       = new ApexCharts(document.querySelector("#chart-pasos"),       getRadialConfig("#8b5cf6", "pasos",  function(val) {
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
function getLineaConfig(color, nombreSerie, datosIniciales) {
  return {
    series: [{ name: nombreSerie, data: datosIniciales }],
    chart: { type: "area", height: 200, toolbar: { show: false } },
    colors: [color],
    fill: { type: "gradient", gradient: { shadeIntensity: 1, opacityFrom: 0.3, opacityTo: 0.02, stops: [0, 100] } },
    dataLabels: { enabled: false },
    stroke: { curve: "smooth", width: 3 },
    xaxis: { categories: historialHoras, labels: { style: { colors: "#94a3b8" } }, axisBorder: { show: false }, axisTicks: { show: false } },
    yaxis: { forceNiceScale: true, labels: { style: { colors: "#94a3b8" }, formatter: function(val) { return val.toFixed(1); } } },
    grid: { borderColor: "#f1f5f9", strokeDashArray: 4 }
  };
}

var chartLineaFrecuencia  = new ApexCharts(document.querySelector("#chart-historial-frecuencia"),  getLineaConfig("#60a5fa", "Frecuencia",  historialFrecuencia));
var chartLineaTemperatura = new ApexCharts(document.querySelector("#chart-historial-temperatura"), getLineaConfig("#34d399", "Temperatura", historialTemperatura));
var chartLineaPresion     = new ApexCharts(document.querySelector("#chart-historial-presion"), {
  series: [{ name: "Sist\u00f3lica", data: historialSistolica }, { name: "Diast\u00f3lica", data: historialDiastolica }],
  chart: { type: "area", height: 200, toolbar: { show: false } },
  colors: ["#fbbf24", "#f59e0b"],
  fill: { type: "gradient", gradient: { shadeIntensity: 1, opacityFrom: 0.3, opacityTo: 0.02, stops: [0, 100] } },
  dataLabels: { enabled: false },
  stroke: { curve: "smooth", width: 3 },
  xaxis: { categories: historialHoras, labels: { style: { colors: "#94a3b8" } }, axisBorder: { show: false }, axisTicks: { show: false } },
  yaxis: { forceNiceScale: true, labels: { style: { colors: "#94a3b8" } } },
  grid: { borderColor: "#f1f5f9", strokeDashArray: 4 }
});
var chartLineaPasos = new ApexCharts(document.querySelector("#chart-historial-pasos"), getLineaConfig("#a78bfa", "Pasos", historialPasos));

chartLineaFrecuencia.render();
chartLineaTemperatura.render();
chartLineaPresion.render();
chartLineaPasos.render();

// =========================
// CONEXIÓN Y ACTUALIZACIÓN
// =========================
function conectarYActualizar() {
  fetch("/spring/analisis/estado/" + idMascota)
    .then(function(response) { return response.json(); })
    .then(function(data) {
      ultimosDatos = data;

      if (data.nombreMascota) {
        document.getElementById("nombre-mascota").textContent = data.nombreMascota;
      }

      var horaActual = new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit", second: "2-digit" });
      historialHoras.push(horaActual);
      if (historialHoras.length > MAX_PUNTOS) { historialHoras.shift(); }

      if (data.frecuenciaCardiaca != null) {
        chartFrecuencia.updateOptions({ series: [(data.frecuenciaCardiaca / 180) * 100], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return data.frecuenciaCardiaca; } } } } } });
        historialFrecuencia.push(data.frecuenciaCardiaca);
        if (historialFrecuencia.length > MAX_PUNTOS) { historialFrecuencia.shift(); }
        chartLineaFrecuencia.updateSeries([{ data: historialFrecuencia }]);
        chartLineaFrecuencia.updateOptions({ xaxis: { categories: historialHoras } });
      }

      if (data.temperatura != null) {
        chartTemperatura.updateOptions({ series: [(data.temperatura / 45) * 100], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return data.temperatura.toFixed(1); } } } } } });
        historialTemperatura.push(data.temperatura.toFixed(1));
        if (historialTemperatura.length > MAX_PUNTOS) { historialTemperatura.shift(); }
        chartLineaTemperatura.updateSeries([{ data: historialTemperatura }]);
        chartLineaTemperatura.updateOptions({ xaxis: { categories: historialHoras } });
      }

      if (data.presionSistolica != null && data.presionDiastolica != null) {
        chartPresion.updateOptions({ series: [(data.presionSistolica / 160) * 100], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return data.presionSistolica + "/" + data.presionDiastolica; } } } } } });
        historialSistolica.push(data.presionSistolica);
        historialDiastolica.push(data.presionDiastolica);
        if (historialSistolica.length > MAX_PUNTOS) { historialSistolica.shift(); historialDiastolica.shift(); }
        chartLineaPresion.updateSeries([{ name: "Sist\u00f3lica", data: historialSistolica }, { name: "Diast\u00f3lica", data: historialDiastolica }]);
        chartLineaPresion.updateOptions({ xaxis: { categories: historialHoras } });
      }

      if (data.pasos != null) {
        chartPasos.updateOptions({ series: [Math.min((data.pasos / 10000) * 100, 100)], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return data.pasos > 1000 ? (data.pasos / 1000).toFixed(1) + "k" : data.pasos; } } } } } });
        historialPasos.push(data.pasos);
        if (historialPasos.length > MAX_PUNTOS) { historialPasos.shift(); }
        chartLineaPasos.updateSeries([{ data: historialPasos }]);
        chartLineaPasos.updateOptions({ xaxis: { categories: historialHoras } });
      }

      guardarEstado();
    })
    .catch(function(error) { console.error("Error al conectar con el backend:", error); });
}

function cargarAlertasRecientes() {
  fetch("/spring/analisis/alertas/datos/" + idMascota)
    .then(function(response) { return response.json(); })
    .then(function(alertas) {
      var contenedor = document.getElementById("contenedor-alertas");
      if (!alertas || alertas.length === 0) {
        contenedor.innerHTML = "<div class=\"bg-emerald-50 border border-emerald-200 rounded-xl p-4 text-center\"><p class=\"text-xs text-emerald-600 font-medium\">Sin alertas recientes</p></div>";
        return;
      }
      var ultimas = alertas.slice(-3).reverse();
      contenedor.innerHTML = ultimas.map(function(alerta) {
        var esEmergencia = alerta.tipo === "EMERGENCIA";
        var bgClass  = esEmergencia ? "bg-rose-50 border-rose-200" : "bg-amber-50 border-amber-200";
        var dotColor = esEmergencia ? "bg-rose-500" : "bg-amber-500";
        return "<div class=\"" + bgClass + " border rounded-xl p-4 flex flex-col gap-1\">" +
                 "<div class=\"flex items-center gap-2\">" +
                   "<div class=\"w-2 h-2 rounded-full " + dotColor + " shrink-0\"></div>" +
                   "<span class=\"font-bold text-slate-800 text-sm\">" + alerta.tipo + "</span>" +
                 "</div>" +
                 "<p class=\"text-xs text-slate-600 ml-4 leading-relaxed\">" + alerta.mensaje + "</p>" +
               "</div>";
      }).join("");
    })
    .catch(function(err) { console.error("Error al cargar alertas en dashboard:", err); });
}

lucide.createIcons();
conectarYActualizar();
cargarAlertasRecientes();
setInterval(conectarYActualizar, 5000);