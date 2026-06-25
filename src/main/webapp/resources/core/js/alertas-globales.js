/* global Notification */

(function () {
  const STORAGE_KEY = "alertas-emergencia-notificadas-global";

  function cargarNotificadas() {
    try {
      return new Set(JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]"));
    } catch (_err) {
      return new Set();
    }
  }

  function guardarNotificadas(set) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(Array.from(set)));
  }

  const notificadas = cargarNotificadas();

  function puedeNotificar() {
    return ("Notification" in window) && Notification.permission === "granted";
  }

  function pedirPermisoSiCorresponde() {
    if (!("Notification" in window)) return;
    if (Notification.permission === "default") {
      Notification.requestPermission().then(function (p) {
        if (p === "granted") {
          consultarEmergenciasGlobales();
          consultarAlertasUsuario();
        }
      });
    }
  }

  function crearNotificacion(titulo, cuerpo, id) {
    try {
      new Notification(titulo, {
        body: cuerpo,
        tag: "alerta-global-" + id,
        requireInteraction: true
      });
    } catch (err) {
      console.error("Error creando notificacion:", err);
    }
  }

  async function consultarEmergenciasGlobales() {
    if (!puedeNotificar()) return;
    try {
      const response = await fetch("/spring/analisis/alertas/emergencias-activas");
      if (!response.ok) return;
      const alertas = await response.json();
      if (!alertas || !Array.isArray(alertas)) return;

      alertas.forEach(function (a) {
        const id = String(a.id);
        if (!notificadas.has(id)) {
          notificadas.add(id);
          guardarNotificadas(notificadas);
          crearNotificacion("EMERGENCIA - " + (a.nombreMascota || ""), a.mensaje || "", id);
        }
      });
    } catch (err) {
      console.error("Error consultando emergencias globales:", err);
    }
  }

  async function consultarAlertasUsuario() {
    if (!puedeNotificar()) return;
    try {
      const response = await fetch("/spring/analisis/alertas/usuario");
      if (!response.ok) return;
      const alertas = await response.json();
      if (!alertas || !Array.isArray(alertas)) return;

      alertas.forEach(function (a) {
        const id = String(a.id);
        const esEmergencia = a.tipo === "EMERGENCIA";
        const leido = !!a.leido;
        if (esEmergencia && !leido && !notificadas.has(id)) {
          notificadas.add(id);
          guardarNotificadas(notificadas);
          crearNotificacion("EMERGENCIA", a.mensaje || "", id);
        }
      });
    } catch (err) {
      console.error("Error consultando alertas de usuario:", err);
    }
  }

  if ("Notification" in window) {
    pedirPermisoSiCorresponde();
    if (Notification.permission === "granted") {
      consultarEmergenciasGlobales();
      consultarAlertasUsuario();
    }
    setInterval(consultarEmergenciasGlobales, 10000);
    setInterval(consultarAlertasUsuario, 10000);
  }
})();
