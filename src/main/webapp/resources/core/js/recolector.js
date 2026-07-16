/* global idMascotaActual */

(function () {
  if (typeof idMascotaActual === "undefined" || idMascotaActual == null) {
    return;
  }

  var STORAGE_KEY = "historial_" + idMascotaActual;
  var contadorTicks = 0;
  var TICKS_PARA_ACTIVIDAD = 6;

  function ahora() { return new Date(); }

  function estructuraVacia() {
    return {
      timestamps: [], horas: [], frecuencia: [], temperatura: [], sistolica: [],
      diastolica: [], pasos: [], distancia: [], calorias: [], sueno: [], estado: []
    };
  }

  function cargarHistorial() {
    try {
      var guardado = sessionStorage.getItem(STORAGE_KEY);
      return guardado ? JSON.parse(guardado) : estructuraVacia();
    } catch (e) {
      console.warn("No se pudo cargar el historial:", e);
      return estructuraVacia();
    }
  }

  function guardarHistorial(historial) {
    try {
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(historial));
    } catch (e) {
      console.warn("No se pudo guardar el historial:", e);
    }
  }

  function recolectar() {
    contadorTicks++;
    var esTickDeActividad = contadorTicks % TICKS_PARA_ACTIVIDAD === 0;

    fetch("/spring/analisis/estado/" + idMascotaActual, { cache: "no-store" })
      .then(function (response) { return response.json(); })
      .then(function (data) {
        var historial = cargarHistorial();
        var ts = ahora();
        var horaActual = ts.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit", second: "2-digit" });

        historial.timestamps.push(ts.toISOString());
        historial.horas.push(horaActual);
        historial.frecuencia.push(data.frecuenciaCardiaca != null ? data.frecuenciaCardiaca : null);
        historial.temperatura.push(data.temperatura != null ? parseFloat(data.temperatura.toFixed(1)) : null);
        historial.sistolica.push(data.presionSistolica != null ? data.presionSistolica : null);
        historial.diastolica.push(data.presionDiastolica != null ? data.presionDiastolica : null);
        historial.pasos.push(data.pasos != null ? data.pasos : null);

        historial.distancia.push(esTickDeActividad && data.distanciaRecorrida != null ? Number(data.distanciaRecorrida.toFixed(2)) : null);
        historial.calorias.push(esTickDeActividad && data.calorias != null ? Number(data.calorias.toFixed(1)) : null);
        historial.sueno.push(esTickDeActividad && data.minutosDormidos != null ? data.minutosDormidos : null);
        historial.estado.push(esTickDeActividad && data.estado != null ? data.estado : null);

        guardarHistorial(historial);

        document.dispatchEvent(new CustomEvent("nuevo-estado-mascota", {
          detail: { data: data, esTickDeActividad: esTickDeActividad }
        }));
      })
      .catch(function (error) {
        console.error("Error al recolectar estado:", error);
      });
  }

  recolectar();
  setInterval(recolectar, 5000);
})();