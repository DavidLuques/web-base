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

    let estabanDesactivadas = !notificacionesWindowsActivas();

    function chequearEmergencias() {
      if (Notification.permission !== "granted") return;
      if (!idMascota) return;

      actualizarBadge(); // badge ahora es independiente

      const activasAhora = notificacionesWindowsActivas();
      const recienActivadas = estabanDesactivadas && activasAhora;
      estabanDesactivadas = !activasAhora;

      if (!activasAhora) return;

      fetch("/spring/analisis/alertas/datos/" + idMascota)
          .then(function (res) { return res.ok ? res.json() : null; })
          .then(function (alertas) {
            if (!alertas || !Array.isArray(alertas)) return;

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
                  setTimeout(function () { notif.close(); }, 5000);
                  notificadas.add(alerta.id);
                }
              }
            });
            guardarNotificadas();
          })
          .catch(function () {});
    }

    function actualizarBadge() {
      const badge = document.getElementById("badge-alertas-sin-leer");
      if (!badge) return;

      const fetchMascota = idMascota
          ? fetch("/spring/analisis/alertas/datos/" + idMascota).then(function (r) { return r.ok ? r.json() : []; })
          : Promise.resolve([]);

      const fetchUsuario = fetch("/spring/analisis/alertas/usuario").then(function (r) { return r.ok ? r.json() : []; });

      Promise.all([fetchMascota, fetchUsuario])
          .then(function (resultados) {
            const alertasMascota = resultados[0] || [];
            const alertasUsuario = resultados[1] || [];
            const todas = alertasMascota.concat(alertasUsuario);

            const sinLeer = todas.filter(function (a) { return !a.leido; }).length;

            if (sinLeer > 0) {
              badge.textContent = sinLeer > 99 ? "99+" : sinLeer;
              badge.classList.remove("hidden");
            } else {
              badge.classList.add("hidden");
            }
          })
          .catch(function () {});
    }

    chequearEmergencias();
    setInterval(chequearEmergencias, 10000);
  }

  window.inicializarNotificacionesBg = inicializarNotificacionesBg;
})();