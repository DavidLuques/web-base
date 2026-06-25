/* global ApexCharts, idMascota, lucide */
/* exported abrirModalSim, cerrarModalSim */
/* ── simulacion.js ── */

var ultimosDatosSimulacion = {};

var historialDistancias = [0];
var historialCalorias   = [0];
var historialSueno      = [0];
var historialPasos      = [0];
var historialHoras = [new Date().toLocaleTimeString([], {
  hour: "2-digit",
  minute: "2-digit"
})];

var nivelesHoras = {};

/* ─────────────────────────────────────────
   Persistencia en sessionStorage
───────────────────────────────────────── */
var STORAGE_KEY = "analisis_" + idMascota;

function cargarEstado() {
  try {
    var guardado = sessionStorage.getItem(STORAGE_KEY);
    if (!guardado) { return; }
    var s = JSON.parse(guardado);
    historialDistancias = s.historialDistancias || [0];
    historialCalorias   = s.historialCalorias   || [0];
    historialSueno      = s.historialSueno      || [0];
    historialPasos      = s.historialPasos      || [0];
    historialHoras      = s.historialHoras      || [new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })];
    nivelesHoras        = s.nivelesHoras        || {};
  } catch (e) {
    console.warn("No se pudo cargar el estado guardado:", e);
  }
}

function guardarEstado() {
  try {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify({
      historialDistancias: historialDistancias,
      historialCalorias:   historialCalorias,
      historialSueno:      historialSueno,
      historialPasos:      historialPasos,
      historialHoras:      historialHoras,
      nivelesHoras:        nivelesHoras
    }));
  } catch (e) {
    console.warn("No se pudo guardar el estado:", e);
  }
}

cargarEstado();

/* ─────────────────────────────────────────
   Niveles de actividad cada 5 minutos
───────────────────────────────────────── */
function getHoraRedondeada() {
  var ahora = new Date();
  var hh = ahora.getHours().toString().padStart(2, "0");
  var mm = (Math.floor(ahora.getMinutes() / 5) * 5).toString().padStart(2, "0");
  return hh + ":" + mm;
}

function registrarNivel(estado) {
  var hora = getHoraRedondeada();
  if (!nivelesHoras[hora]) {
    nivelesHoras[hora] = { intenso: 0, moderado: 0, liviano: 0 };
  }
  if (estado === "CORRIENDO") {
    nivelesHoras[hora].intenso++;
  } else if (estado === "CAMINANDO") {
    nivelesHoras[hora].moderado++;
  } else {
    nivelesHoras[hora].liviano++;
  }
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

/* ─────────────────────────────────────────
   Helpers de configuración de charts
───────────────────────────────────────── */
function formatearEstado(estado) {
  return estado
    .toLowerCase()
    .replace("_", " ")
    .replace(/\b\w/g, function(letra) { return letra.toUpperCase(); });
}

function getConfiguracionGrafico(colorPrincipal, etiqueta, valorMostrar, porcentajeLlenado) {
  return {
    series: [porcentajeLlenado],
    chart: {
      type: "radialBar",
      height: 200,
      sparkline: { enabled: true }
    },
    colors: [colorPrincipal],
    plotOptions: {
      radialBar: {
        hollow: { size: "65%" },
        track:  { background: "#f1f5f9" },
        dataLabels: {
          show: true,
          name: {
            show: true,
            color: "#94a3b8",
            fontSize: "12px",
            offsetY: 20
          },
          value: {
            show: true,
            color: "#1e293b",
            fontSize: "22px",
            fontWeight: "600",
            offsetY: -10,
            formatter: function() { return valorMostrar; }
          }
        }
      }
    },
    stroke: { lineCap: "round" },
    labels: [etiqueta],
    responsive: [{
      breakpoint: 640,
      options: {
        chart: { height: 170 },
        plotOptions: {
          radialBar: {
            hollow: { size: "55%" },
            dataLabels: {
              name: { fontSize: "10px", offsetY: 15 },
              value: { fontSize: "18px", offsetY: -5 }
            }
          }
        }
      }
    }]
  };
}

function getOpcionesHistorial(nombreSerie, datos, horas, color, formatterFn) {
  return {
    series: [{ name: nombreSerie, data: datos }],
    chart: {
      height: 300,
      type: "line",
      toolbar: { show: false },
      zoom:    { enabled: false }
    },
    stroke: { curve: "smooth", width: 3 },
    colors: [color],
    xaxis: {
      categories: horas,
      labels:     { style: { colors: "#94a3b8" } },
      axisBorder: { show: false },
      axisTicks:  { show: false }
    },
    yaxis: {
      min: 0,
      tickAmount: 4,
      labels: { formatter: formatterFn, style: { colors: "#94a3b8" } }
    },
    tooltip: { y: { formatter: formatterFn } },
    grid: {
      borderColor: "#f1f5f9",
      strokeDashArray: 4,
      yaxis: { lines: { show: true } }
    }
  };
}

/* ─────────────────────────────────────────
   Radial charts
───────────────────────────────────────── */
var chartDistancia = new ApexCharts(
  document.querySelector("#chart-distancia"),
  getConfiguracionGrafico("#3b82f6", "km", "0", 0)
);
var chartCalorias = new ApexCharts(
  document.querySelector("#chart-calorias"),
  getConfiguracionGrafico("#10b981", "kcal", "0", 0)
);
var chartSueno = new ApexCharts(
  document.querySelector("#chart-sueno"),
  getConfiguracionGrafico("#f59e0b", "horas", "0", 0)
);
var chartPasos = new ApexCharts(
  document.querySelector("#chart-pasos"),
  getConfiguracionGrafico("#ec4899", "pasos", "0", 0)
);

chartDistancia.render();
chartCalorias.render();
chartSueno.render();
chartPasos.render();

/* ─────────────────────────────────────────
   Historial line charts
───────────────────────────────────────── */
var chartHistorialDistancia = new ApexCharts(
  document.querySelector("#chart-historial-distancia"),
  getOpcionesHistorial("Distancia (km)", historialDistancias, historialHoras, "#3b82f6",
    function(v) { return v.toFixed(2) + " km"; })
);
chartHistorialDistancia.render();

var chartHistorialCalorias = new ApexCharts(
  document.querySelector("#chart-historial-calorias"),
  getOpcionesHistorial("Calor\u00edas (kcal)", historialCalorias, historialHoras, "#10b981",
    function(v) { return v.toFixed(1) + " kcal"; })
);
chartHistorialCalorias.render();

var chartHistorialSueno = new ApexCharts(
  document.querySelector("#chart-historial-sueno"),
  getOpcionesHistorial("Sue\u00f1o (min)", historialSueno, historialHoras, "#f59e0b", function(v) {
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
  getOpcionesHistorial("Pasos", historialPasos, historialHoras, "#ec4899",
    function(v) { return Math.round(v).toLocaleString("es-AR") + " pasos"; })
);
chartHistorialPasos.render();

/* ─────────────────────────────────────────
   Chart niveles de actividad
───────────────────────────────────────── */
var nivelesIniciales = getNivelesSeriesYCategorias();

var chartNiveles = new ApexCharts(
  document.querySelector("#chart-niveles-actividad"),
  {
    series: [
      { name: "Intenso",  data: nivelesIniciales.intenso  },
      { name: "Moderado", data: nivelesIniciales.moderado },
      { name: "Liviano",  data: nivelesIniciales.liviano  }
    ],
    chart: {
      type: "bar",
      height: 300,
      stacked: true,
      toolbar: { show: false },
      zoom:    { enabled: false }
    },
    colors: ["#ef4444", "#d97706", "#3b82f6"],
    plotOptions: {
      bar: {
        horizontal: false,
        columnWidth: "55%",
        borderRadius: 4,
        borderRadiusApplication: "end",
        borderRadiusWhenStacked: "last"
      }
    },
    dataLabels: { enabled: false },
    legend: {
      position: "top",
      horizontalAlign: "right",
      labels: { colors: "#64748b" }
    },
    xaxis: {
      categories: nivelesIniciales.categorias,
      labels: { style: { colors: "#94a3b8" } },
      axisBorder: { show: false },
      axisTicks:  { show: false }
    },
    yaxis: {
      min: 0,
      tickAmount: 4,
      labels: {
        formatter: function(v) { return Math.round(v); },
        style: { colors: "#94a3b8" }
      }
    },
    tooltip: {
      y: { formatter: function(v) { return Math.round(v) + "min"; } }
    },
    grid: {
      borderColor: "#f1f5f9",
      strokeDashArray: 4,
      yaxis: { lines: { show: true } }
    },
    fill: { opacity: 1 }
  }
);
chartNiveles.render();

/* ─────────────────────────────────────────
   Badge helpers
───────────────────────────────────────── */
var CONFIG_BADGE = {
  CORRIENDO: {
    clase: "bg-red-100 text-red-600",
    dot:   "bg-red-500",
    texto: "Nivel alto de actividad"
  },
  CAMINANDO: {
    clase: "bg-yellow-100 text-yellow-700",
    dot:   "bg-yellow-600",
    texto: "Nivel moderado de actividad"
  },
  DURMIENDO: {
    clase: "bg-blue-100 text-blue-600",
    dot:   "bg-blue-500",
    texto: "Nivel liviano de actividad"
  },
  _DEFAULT: {
    clase: "bg-blue-100 text-blue-600",
    dot:   "bg-blue-500",
    texto: "Nivel liviano de actividad"
  }
};

function actualizarBadge(estado) {
  var cfg   = CONFIG_BADGE[estado] || CONFIG_BADGE["_DEFAULT"];
  var badge = document.getElementById("badge-actividad");
  badge.className = cfg.clase + " px-4 py-2 rounded-full font-medium text-sm flex items-center gap-2";
  badge.innerHTML = "<div class=\"w-2 h-2 rounded-full " + cfg.dot + "\"></div> " + cfg.texto;
}

/* ─────────────────────────────────────────
   Polling principal
───────────────────────────────────────── */
function actualizarEstado() {
  fetch("/spring/analisis/estado/" + idMascota)
    .then(function(response) { return response.json(); })
    .then(function(data) {
      ultimosDatosSimulacion = data;

      document.getElementById("estado").textContent = formatearEstado(data.estado);
      actualizarBadge(data.estado);
      registrarNivel(data.estado);

      if (data.nombreMascota) {
        document.getElementById("nombre-mascota").textContent = data.nombreMascota;
      }

      var horaActual = new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });

      if (data.distanciaRecorrida != null) {
        var pctDist = Math.min((data.distanciaRecorrida / 5.0) * 100, 100);
        chartDistancia.updateOptions({
          series: [pctDist],
          plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return data.distanciaRecorrida.toFixed(1); } } } } }
        });
        historialDistancias.push(Number(data.distanciaRecorrida.toFixed(2)));
        if (historialDistancias.length > 20) { historialDistancias.shift(); }
      }

      if (data.calorias != null) {
        var pctCal = Math.min((data.calorias / 200.0) * 100, 100);
        chartCalorias.updateOptions({
          series: [pctCal],
          plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return data.calorias.toFixed(1); } } } } }
        });
        historialCalorias.push(Number(data.calorias.toFixed(1)));
        if (historialCalorias.length > 20) { historialCalorias.shift(); }
      }

      if (data.minutosDormidos != null) {
        var pctSueno = Math.min((data.minutosDormidos / 480) * 100, 100);
        var etiquetaSueno = data.minutosDormidos >= 60
          ? (Math.floor(data.minutosDormidos / 60) + "h" + (data.minutosDormidos % 60 > 0 ? " " + (data.minutosDormidos % 60) + "m" : ""))
          : (data.minutosDormidos + "m");
        chartSueno.updateOptions({
          series: [pctSueno],
          plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return etiquetaSueno; } } } } }
        });
        historialSueno.push(data.minutosDormidos);
        if (historialSueno.length > 20) { historialSueno.shift(); }
      }

      if (data.pasos != null) {
        var pctPasos = Math.min((data.pasos / 10000) * 100, 100);
        chartPasos.updateOptions({
          series: [pctPasos],
          plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return data.pasos.toLocaleString("es-AR"); } } } } }
        });
        historialPasos.push(data.pasos);
        if (historialPasos.length > 20) { historialPasos.shift(); }
      }

      historialHoras.push(horaActual);
      if (historialHoras.length > 20) { historialHoras.shift(); }

      chartHistorialDistancia.updateOptions({ series: [{ data: historialDistancias }], xaxis: { categories: historialHoras } });
      chartHistorialCalorias.updateOptions({  series: [{ data: historialCalorias }],   xaxis: { categories: historialHoras } });
      chartHistorialSueno.updateOptions({     series: [{ data: historialSueno }],       xaxis: { categories: historialHoras } });
      chartHistorialPasos.updateOptions({     series: [{ data: historialPasos }],       xaxis: { categories: historialHoras } });

      var niveles = getNivelesSeriesYCategorias();
      chartNiveles.updateOptions({
        series: [
          { name: "Intenso",  data: niveles.intenso  },
          { name: "Moderado", data: niveles.moderado },
          { name: "Liviano",  data: niveles.liviano  }
        ],
        xaxis: { categories: niveles.categorias }
      });

      guardarEstado();
    })
    .catch(function(error) { console.error("Error al actualizar estado:", error); });
}

/* ─────────────────────────────────────────
   Modal Ver más
───────────────────────────────────────── */
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
           simFila("Meta diaria",   "~5 km") +
           simFila("Progreso",      pctDist2 != null ? pctDist2 + "%" : "Sin datos") +
           simEstado(dentroDist, actual != null, "Buen avance", "Tu mascota puede caminar m\u00e1s") +
           simDesc("La distancia recorrida refleja la actividad f\u00edsica acumulada del d\u00eda. " +
             "Se recomienda que tu mascota recorra al menos 2\u20135 km diarios seg\u00fan su tama\u00f1o y raza.");

  } else if (tipo === "calorias") {
    titulo.textContent = "Calor\u00edas Quemadas";
    actual = ultimosDatosSimulacion.calorias;
    var dentroCal = actual != null && actual >= 200.0 * 0.6;
    html = simFila("Calor\u00edas hoy", actual != null ? actual.toFixed(1) + " kcal" : "Sin datos") +
           simFila("Meta diaria",       "~200 kcal") +
           simEstado(dentroCal, actual != null, "Buen gasto energ\u00e9tico", "Actividad insuficiente") +
           simDesc("Las calor\u00edas quemadas dependen del peso, estado y actividad de tu mascota. " +
             "Un gasto energ\u00e9tico adecuado ayuda a mantener el peso ideal y la salud cardiovascular.");

  } else if (tipo === "sueno") {
    titulo.textContent = "Tiempo de Sue\u00f1o";
    actual = ultimosDatosSimulacion.minutosDormidos;
    var dentroSueno   = actual != null && actual >= 480 * 0.6;
    var etiquetaModal = actual != null
      ? (actual >= 60 ? Math.floor(actual / 60) + "h " + (actual % 60 > 0 ? actual % 60 + "m" : "") : actual + "m")
      : "Sin datos";
    html = simFila("Sue\u00f1o hoy", etiquetaModal) +
           simFila("Meta diaria",    "~8 horas") +
           simEstado(dentroSueno, actual != null, "Descanso adecuado", "Tu mascota descans\u00f3 poco") +
           simDesc("El sue\u00f1o es esencial para la recuperaci\u00f3n y el bienestar de tu mascota. " +
             "Los perros adultos necesitan entre 8 y 14 horas de sue\u00f1o por d\u00eda.");

  } else if (tipo === "pasos") {
    titulo.textContent = "Pasos Diarios";
    actual = ultimosDatosSimulacion.pasos;
    var dentroPasos   = actual != null && actual >= 10000 * 0.7;
    var actualStr     = actual != null ? (actual > 1000 ? (actual / 1000).toFixed(1) + "k" : actual) + " pasos" : "Sin datos";
    html = simFila("Pasos hoy",               actualStr) +
           simFila("Meta diaria recomendada", "~10.000 pasos") +
           simEstado(dentroPasos, actual != null, "Meta casi alcanzada", "Tu mascota necesita m\u00e1s actividad") +
           simDesc("La actividad f\u00edsica diaria es fundamental para el peso y la salud articular de tu mascota. " +
             "Se recomienda alcanzar la meta con caminatas y juego activo.");
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

lucide.createIcons();
actualizarEstado();
setInterval(actualizarEstado, 30000);