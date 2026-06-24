/* global Notification */

(function() {
  const STORAGE_KEY = 'alertas-emergencia-notificadas-global';

  function cargarNotificadas() {
    try {
      return new Set(JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]'));
    } catch (e) {
      return new Set();
    }
  }

  function guardarNotificadas(set) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(Array.from(set)));
    } catch (e) {
      // noop
    }
  }

  const notificadas = cargarNotificadas();

  function puedeNotificar() {
    return ('Notification' in window) && Notification.permission === 'granted';
  }

  function pedirPermisoSiCorresponde() {
    if (!('Notification' in window)) return;
    if (Notification.permission === 'default') {
      Notification.requestPermission().then(function(p) {
        if (p === 'granted') {
          // Al obtener permiso, realizar una consulta inmediata
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
        tag: 'alerta-global-' + id,
        requireInteraction: true
      });
    } catch (e) {
      console.error('Error creando notificacion:', e);
    }
  }

  async function consultarEmergenciasGlobales() {
    if (!puedeNotificar()) return;
    try {
      const response = await fetch('/spring/analisis/alertas/emergencias-activas');
      if (!response.ok) return;
      const alertas = await response.json();
      if (!alertas || !Array.isArray(alertas)) return;

      alertas.forEach(a => {
        const id = String(a.id);
        if (!notificadas.has(id)) {
          notificadas.add(id);
          guardarNotificadas(notificadas);
          crearNotificacion('EMERGENCIA - ' + (a.nombreMascota || ''), a.mensaje || '', id);
        }
      });
    } catch (err) {
      console.error('Error consultando emergencias globales:', err);
    }
  }

  async function consultarAlertasUsuario() {
    if (!puedeNotificar()) return;
    try {
      const response = await fetch('/spring/analisis/alertas/usuario');
      if (!response.ok) return;
      const alertas = await response.json();
      if (!alertas || !Array.isArray(alertas)) return;

      alertas.forEach(a => {
        // Estructura: AlertaDto con tipo y leido
        const id = String(a.id);
        const tipo = a.tipo || (a.tipo && a.tipo.name) || null;
        const esEmergencia = tipo === 'EMERGENCIA' || (a.tipo && a.tipo === 'EMERGENCIA');
        const leido = !!a.leido;
        if (esEmergencia && !leido && !notificadas.has(id)) {
          notificadas.add(id);
          guardarNotificadas(notificadas);
          crearNotificacion('EMERGENCIA', a.mensaje || '', id);
        }
      });
    } catch (err) {
      console.error('Error consultando alertas de usuario:', err);
    }
  }

  // Inicializacion
  if ('Notification' in window) {
    pedirPermisoSiCorresponde();
    if (Notification.permission === 'granted') {
      consultarEmergenciasGlobales();
      consultarAlertasUsuario();
    }
    // Polling periódico
    setInterval(consultarEmergenciasGlobales, 10000);
    setInterval(consultarAlertasUsuario, 10000);
  }
})();
