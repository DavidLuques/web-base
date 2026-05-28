let historialHoras        = [];
let historialFrecuencia   = [];
let historialTemperatura  = [];
let historialSistolica    = [];
let historialDiastolica   = [];
let historialPasos        = [];
const MAX_PUNTOS  = 15;
const STORAGE_KEY = "dashboard_" + idMascota;

function cargarEstado() {
  try {
    const guardado = sessionStorage.getItem(STORAGE_KEY);
    if (!guardado) return;
    const s = JSON.parse(guardado);
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
      historialHoras, historialFrecuencia, historialTemperatura,
      historialSistolica, historialDiastolica, historialPasos
    }));
  } catch (e) { console.warn("No se pudo guardar el estado:", e); }
}

cargarEstado();

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
          name: { show: true, color: "#94a3b8", fontSize: "10px", offsetY: 20 },
          value: { show: true, color: "#1e293b", fontSize: "24px", fontWeight: "bold", offsetY: -5, formatter: formatFunction }
        }
      }
    },
    stroke: { lineCap: "round" },
    labels: [label]
  };
}

const chartFrecuencia  = new ApexCharts(document.querySelector("#chart-frecuencia"),  getRadialConfig("#3b82f6", "bpm",   function(val) { return Math.round(val * 1.8); }));
const chartTemperatura = new ApexCharts(document.querySelector("#chart-temperatura"), getRadialConfig("#10b981", "°C",    function(val) { return (val / 100 * 50).toFixed(1); }));
const chartPresion     = new ApexCharts(document.querySelector("#chart-presion"),     getRadialConfig("#f59e0b", "mmHg",  function(val) { return val + "/80"; }));
const chartPasos       = new ApexCharts(document.querySelector("#chart-pasos"),       getRadialConfig("#8b5cf6", "pasos", function(val) {
  var pasosReales = Math.round((val / 100) * 10000);
  return pasosReales > 1000 ? (pasosReales / 1000).toFixed(1) + "k" : pasosReales;
}));

chartFrecuencia.render();
chartTemperatura.render();
chartPresion.render();
chartPasos.render();

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

const chartLineaFrecuencia  = new ApexCharts(document.querySelector("#chart-historial-frecuencia"),  getLineaConfig("#60a5fa", "Frecuencia",  historialFrecuencia));
const chartLineaTemperatura = new ApexCharts(document.querySelector("#chart-historial-temperatura"), getLineaConfig("#34d399", "Temperatura", historialTemperatura));
const chartLineaPresion     = new ApexCharts(document.querySelector("#chart-historial-presion"), {
  series: [{ name: "Sistólica", data: historialSistolica }, { name: "Diastólica", data: historialDiastolica }],
  chart: { type: "area", height: 200, toolbar: { show: false } },
  colors: ["#fbbf24", "#f59e0b"],
  fill: { type: "gradient", gradient: { shadeIntensity: 1, opacityFrom: 0.3, opacityTo: 0.02, stops: [0, 100] } },
  dataLabels: { enabled: false },
  stroke: { curve: "smooth", width: 3 },
  xaxis: { categories: historialHoras, labels: { style: { colors: "#94a3b8" } }, axisBorder: { show: false }, axisTicks: { show: false } },
  yaxis: { forceNiceScale: true, labels: { style: { colors: "#94a3b8" } } },
  grid: { borderColor: "#f1f5f9", strokeDashArray: 4 }
});
const chartLineaPasos = new ApexCharts(document.querySelector("#chart-historial-pasos"), getLineaConfig("#a78bfa", "Pasos", historialPasos));

chartLineaFrecuencia.render();
chartLineaTemperatura.render();
chartLineaPresion.render();
chartLineaPasos.render();

function conectarYActualizar() {
  fetch("/spring/simulacion/estado/" + idMascota)
    .then(function(response) { return response.json(); })
    .then(function(data) {
      if (data.nombreMascota) document.getElementById("nombre-mascota").textContent = data.nombreMascota;

      var horaActual = new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit", second: "2-digit" });
      historialHoras.push(horaActual);
      if (historialHoras.length > MAX_PUNTOS) historialHoras.shift();

      if (data.frecuenciaCardiaca != null) {
        chartFrecuencia.updateOptions({ series: [(data.frecuenciaCardiaca / 180) * 100], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return data.frecuenciaCardiaca; } } } } } });
        historialFrecuencia.push(data.frecuenciaCardiaca);
        if (historialFrecuencia.length > MAX_PUNTOS) historialFrecuencia.shift();
        chartLineaFrecuencia.updateSeries([{ data: historialFrecuencia }]);
        chartLineaFrecuencia.updateOptions({ xaxis: { categories: historialHoras } });
      }

      if (data.temperatura != null) {
        chartTemperatura.updateOptions({ series: [(data.temperatura / 45) * 100], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return data.temperatura.toFixed(1); } } } } } });
        historialTemperatura.push(data.temperatura.toFixed(1));
        if (historialTemperatura.length > MAX_PUNTOS) historialTemperatura.shift();
        chartLineaTemperatura.updateSeries([{ data: historialTemperatura }]);
        chartLineaTemperatura.updateOptions({ xaxis: { categories: historialHoras } });
      }

      if (data.presionSistolica != null && data.presionDiastolica != null) {
        chartPresion.updateOptions({ series: [(data.presionSistolica / 160) * 100], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return data.presionSistolica + "/" + data.presionDiastolica; } } } } } });
        historialSistolica.push(data.presionSistolica);
        historialDiastolica.push(data.presionDiastolica);
        if (historialSistolica.length > MAX_PUNTOS) { historialSistolica.shift(); historialDiastolica.shift(); }
        chartLineaPresion.updateSeries([{ name: "Sistólica", data: historialSistolica }, { name: "Diastólica", data: historialDiastolica }]);
        chartLineaPresion.updateOptions({ xaxis: { categories: historialHoras } });
      }

      if (data.pasos != null) {
        chartPasos.updateOptions({ series: [Math.min((data.pasos / 10000) * 100, 100)], plotOptions: { radialBar: { dataLabels: { value: { formatter: function() { return data.pasos > 1000 ? (data.pasos / 1000).toFixed(1) + "k" : data.pasos; } } } } } });
        historialPasos.push(data.pasos);
        if (historialPasos.length > MAX_PUNTOS) historialPasos.shift();
        chartLineaPasos.updateSeries([{ data: historialPasos }]);
        chartLineaPasos.updateOptions({ xaxis: { categories: historialHoras } });
      }

      guardarEstado();
    })
    .catch(function(error) { console.error("Error al conectar con el backend:", error); });
}

conectarYActualizar();
setInterval(conectarYActualizar, 5000);