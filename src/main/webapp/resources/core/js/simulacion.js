/* ── simulacion.js ── */

/* idMascota es inyectado desde el HTML con th:inline="javascript" antes de cargar este script */

let historialDistancias = [0];
let historialCalorias   = [0];
let historialSueno      = [0];
let historialPasos      = [0];
let historialHoras = [new Date().toLocaleTimeString([], {
  hour: '2-digit',
  minute: '2-digit'
})];

function formatearEstado(estado) {
  return estado
    .toLowerCase()
    .replace("_", " ")
    .replace(/\b\w/g, letra => letra.toUpperCase());
}

function getConfiguracionGrafico(colorPrincipal, etiqueta, valorMostrar, porcentajeLlenado) {
  return {
    series: [porcentajeLlenado],
    chart: {
      type: 'radialBar',
      height: 200,
      sparkline: { enabled: true }
    },
    colors: [colorPrincipal],
    plotOptions: {
      radialBar: {
        hollow: { size: '65%' },
        track:  { background: '#f1f5f9' },
        dataLabels: {
          show: true,
          name: {
            show: true,
            color: '#94a3b8',
            fontSize: '12px',
            offsetY: 20
          },
          value: {
            show: true,
            color: '#1e293b',
            fontSize: '22px',
            fontWeight: '600',
            offsetY: -10,
            formatter: function () { return valorMostrar; }
          }
        }
      }
    },
    stroke: { lineCap: 'round' },
    labels: [etiqueta]
  };
}

function getOpcionesHistorial(nombreSerie, datos, horas, color, formatterFn) {
  return {
    series: [{ name: nombreSerie, data: datos }],
    chart: {
      height: 300,
      type: 'line',
      toolbar: { show: false },
      zoom:    { enabled: false }
    },
    stroke: { curve: 'smooth', width: 3 },
    colors: [color],
    xaxis: {
      categories: horas,
      labels:     { style: { colors: '#94a3b8' } },
      axisBorder: { show: false },
      axisTicks:  { show: false }
    },
    yaxis: {
      min: 0,
      tickAmount: 4,
      labels: { formatter: formatterFn, style: { colors: '#94a3b8' } }
    },
    tooltip: { y: { formatter: formatterFn } },
    grid: {
      borderColor: '#f1f5f9',
      strokeDashArray: 4,
      yaxis: { lines: { show: true } }
    }
  };
}

/* ── Radial charts ── */
let chartDistancia = new ApexCharts(
  document.querySelector("#chart-distancia"),
  getConfiguracionGrafico('#3b82f6', 'km', '0', 0)
);
let chartCalorias = new ApexCharts(
  document.querySelector("#chart-calorias"),
  getConfiguracionGrafico('#10b981', 'kcal', '0', 0)
);
let chartSueno = new ApexCharts(
  document.querySelector("#chart-sueno"),
  getConfiguracionGrafico('#f59e0b', 'horas', '0', 0)
);
let chartPasos = new ApexCharts(
  document.querySelector("#chart-pasos"),
  getConfiguracionGrafico('#ec4899', 'pasos', '0', 0)
);

chartDistancia.render();
chartCalorias.render();
chartSueno.render();
chartPasos.render();

/* ── Historial line charts ── */
let chartHistorialDistancia = new ApexCharts(
  document.querySelector("#chart-historial-distancia"),
  getOpcionesHistorial("Distancia (km)", historialDistancias, historialHoras, '#3b82f6',
    v => v.toFixed(2) + " km")
);
chartHistorialDistancia.render();

let chartHistorialCalorias = new ApexCharts(
  document.querySelector("#chart-historial-calorias"),
  getOpcionesHistorial("Calorías (kcal)", historialCalorias, historialHoras, '#10b981',
    v => v.toFixed(1) + " kcal")
);
chartHistorialCalorias.render();

let chartHistorialSueno = new ApexCharts(
  document.querySelector("#chart-historial-sueno"),
  getOpcionesHistorial("Sueño (min)", historialSueno, historialHoras, '#f59e0b', v => {
    if (v >= 60) {
      let h = Math.floor(v / 60);
      let m = Math.round(v % 60);
      return m > 0 ? h + "h " + m + "m" : h + "h";
    }
    return Math.round(v) + "m";
  })
);
chartHistorialSueno.render();

let chartHistorialPasos = new ApexCharts(
  document.querySelector("#chart-historial-pasos"),
  getOpcionesHistorial("Pasos", historialPasos, historialHoras, '#ec4899',
    v => Math.round(v).toLocaleString('es-AR') + " pasos")
);
chartHistorialPasos.render();

/* ── Polling ── */
function actualizarEstado() {
  fetch('/spring/simulacion/estado/' + idMascota)
    .then(response => response.json())
    .then(data => {

      document.getElementById('estado').textContent = formatearEstado(data.estado);

      const badge = document.getElementById('badge-actividad');
      if (data.estado === 'CORRIENDO' || data.estado === 'CAMINANDO') {
        badge.className = 'bg-red-100 text-red-600 px-4 py-2 rounded-full font-medium text-sm flex items-center gap-2';
        badge.innerHTML = '<div class="w-2 h-2 rounded-full bg-red-500"></div> Nivel Alto de Actividad';
      } else {
        badge.className = 'bg-blue-100 text-blue-600 px-4 py-2 rounded-full font-medium text-sm flex items-center gap-2';
        badge.innerHTML = '<div class="w-2 h-2 rounded-full bg-blue-500"></div> En Reposo';
      }

      const horaActual = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

      /* Distancia */
      if (data.distanciaRecorrida != null) {
        let pct = Math.min((data.distanciaRecorrida / 5.0) * 100, 100);
        chartDistancia.updateOptions({
          series: [pct],
          plotOptions: { radialBar: { dataLabels: { value: { formatter: () => data.distanciaRecorrida.toFixed(1) } } } }
        });
        historialDistancias.push(Number(data.distanciaRecorrida.toFixed(2)));
        if (historialDistancias.length > 20) historialDistancias.shift();
      }

      /* Calorías */
      if (data.calorias != null) {
        let pct = Math.min((data.calorias / 200.0) * 100, 100);
        chartCalorias.updateOptions({
          series: [pct],
          plotOptions: { radialBar: { dataLabels: { value: { formatter: () => data.calorias.toFixed(1) } } } }
        });
        historialCalorias.push(Number(data.calorias.toFixed(1)));
        if (historialCalorias.length > 20) historialCalorias.shift();
      }

      /* Sueño */
      if (data.minutosDormidos != null) {
        let pct = Math.min((data.minutosDormidos / 480) * 100, 100);
        let etiqueta = data.minutosDormidos >= 60
          ? (Math.floor(data.minutosDormidos / 60) + 'h' + (data.minutosDormidos % 60 > 0 ? ' ' + (data.minutosDormidos % 60) + 'm' : ''))
          : (data.minutosDormidos + 'm');
        chartSueno.updateOptions({
          series: [pct],
          plotOptions: { radialBar: { dataLabels: { value: { formatter: () => etiqueta } } } }
        });
        historialSueno.push(data.minutosDormidos);
        if (historialSueno.length > 20) historialSueno.shift();
      }

      /* Pasos */
      if (data.pasos != null) {
        let pct = Math.min((data.pasos / 10000) * 100, 100);
        chartPasos.updateOptions({
          series: [pct],
          plotOptions: { radialBar: { dataLabels: { value: { formatter: () => data.pasos.toLocaleString('es-AR') } } } }
        });
        historialPasos.push(data.pasos);
        if (historialPasos.length > 20) historialPasos.shift();
      }

      /* Horas (eje X compartido) */
      historialHoras.push(horaActual);
      if (historialHoras.length > 20) historialHoras.shift();

      /* Actualizar line charts */
      chartHistorialDistancia.updateOptions({ series: [{ data: historialDistancias }], xaxis: { categories: historialHoras } });
      chartHistorialCalorias.updateOptions({  series: [{ data: historialCalorias }],   xaxis: { categories: historialHoras } });
      chartHistorialSueno.updateOptions({     series: [{ data: historialSueno }],       xaxis: { categories: historialHoras } });
      chartHistorialPasos.updateOptions({     series: [{ data: historialPasos }],       xaxis: { categories: historialHoras } });
    });
}

actualizarEstado();
setInterval(actualizarEstado, 60000);