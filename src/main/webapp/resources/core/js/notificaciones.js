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
      return localStorage.getItem(notifWindowsKey) === "true";
    }

    // Flag que indica si en el ciclo anterior estaban desactivadas
    let estabanDesactivadas = !notificacionesWindowsActivas();

    function chequearEmergencias() {
      if (Notification.permission !== "granted") return;
      if (!idMascota) return;

      const activasAhora = notificacionesWindowsActivas();
      const recienActivadas = estabanDesactivadas && activasAhora;
      estabanDesactivadas = !activasAhora;

      fetch("/spring/analisis/alertas/datos/" + idMascota)
          .then(function (res) {
            if (!res.ok) return null;
            return res.json();
          })
          .then(function (alertas) {
            if (!alertas || !Array.isArray(alertas)) return;

            actualizarBadge(alertas);

            if (!activasAhora) return; // corta solo las notificaciones, no el badge

            alertas.forEach(function (alerta) {
              if (alerta.tipo === "EMERGENCIA" && !alerta.leido) {
                if (recienActivadas) {
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

    function actualizarBadge(alertas) {
      const badge = document.getElementById("badge-alertas-sin-leer");
      if (!badge) return;

      const sinLeer = alertas.filter(function (a) {
        return !a.leido;
      }).length;

      if (sinLeer > 0) {
        badge.textContent = sinLeer > 99 ? "99+" : sinLeer;
        badge.classList.remove("hidden");
      } else {
        badge.classList.add("hidden");
      }
    }

    chequearEmergencias();
    setInterval(chequearEmergencias, 10000);
  }

  window.inicializarNotificacionesBg = inicializarNotificacionesBg;
})();