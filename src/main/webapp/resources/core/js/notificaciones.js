/* global lucide */
(function () {
  function inicializarNotificacionesBg(idMascota) {
    if (!("Notification" in window)) return;

    if (Notification.permission === "default") {
      Notification.requestPermission();
    }

    const sessionKey = "alertas-notificadas-sesion-" + idMascota;
    const notificadas = new Set(
      JSON.parse(sessionStorage.getItem(sessionKey) || "[]")
    );

    function guardarNotificadas() {
      sessionStorage.setItem(sessionKey, JSON.stringify(Array.from(notificadas)));
    }

    function notificacionesWindowsActivas() {
      const notifWindowsKey = "notificaciones-windows-activas-" + idMascota;
      return localStorage.getItem(notifWindowsKey) !== "false";
    }

    // Flag que indica si en el ciclo anterior estaban desactivadas
    let estabanDesactivadas = !notificacionesWindowsActivas();

    function chequearEmergencias() {
      if (Notification.permission !== "granted") return;
      if (!idMascota) return;

      const activasAhora = notificacionesWindowsActivas();

      // Recién se activaron: registrar todo lo existente sin notificar
      const recienActivadas = estabanDesactivadas && activasAhora;
      estabanDesactivadas = !activasAhora;

      if (!activasAhora) return;

      fetch("/spring/analisis/alertas/datos/" + idMascota)
        .then(function (res) {
          if (!res.ok) return null;
          return res.json();
        })
        .then(function (alertas) {
          if (!alertas || !Array.isArray(alertas)) return;

          alertas.forEach(function (alerta) {
            if (alerta.tipo === "EMERGENCIA" && !alerta.leido) {
              if (recienActivadas) {
                // Solo registrar, no notificar
                notificadas.add(alerta.id);
              } else if (!notificadas.has(alerta.id)) {
                const notif = new Notification("⚠️ EMERGENCIA - PetTracker", {
                  body: alerta.mensaje,
                  tag: "emergencia-" + alerta.id,
                  requireInteraction: false,
                });

                setTimeout(function () {
                  notif.close();
                }, 5000);

                notificadas.add(alerta.id);
              }
            }
          });
          guardarNotificadas();
        })
        .catch(function () {});
    }

    chequearEmergencias();
    setInterval(chequearEmergencias, 10000);
  }

  window.inicializarNotificacionesBg = inicializarNotificacionesBg;
})();