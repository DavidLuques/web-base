(function () {
  function inicializarNotificacionesBg(idMascota) {
    // Ya no abortamos la función entera si el navegador no soporta notificaciones nativas,
    // así permitimos que el badge siga funcionando de forma independiente.
    const soporteNotification = "Notification" in window;

    if (soporteNotification && Notification.permission === "default") {
      Notification.requestPermission();
    }

    // Si no hay mascota, usamos una clave genérica para no concatenar un "null"
    const sessionKey = "alertas-notificadas-sesion-" + (idMascota || "global");
    const notificadas = new Set(
        JSON.parse(sessionStorage.getItem(sessionKey) || "[]")
    );

    function guardarNotificadas() {
      sessionStorage.setItem(sessionKey, JSON.stringify(Array.from(notificadas)));
    }

    function notificacionesWindowsActivas() {
      const notifWindowsKey = "notificaciones-windows-activas-" + (idMascota || "global");
      return localStorage.getItem(notifWindowsKey) === "true";
    }

    let estabanDesactivadas = !notificacionesWindowsActivas();

    // 1. LÓGICA DE EMERGENCIAS (Exclusiva para notificaciones nativas de escritorio)
    function chequearEmergencias() {
      if (!soporteNotification || Notification.permission !== "granted") return;
      if (!idMascota) return; // Si no hay mascota, no hay telemetría de emergencias que procesar en escritorio

      const activasAhora = notificacionesWindowsActivas();
      const recienActivadas = estabanDesactivadas && activasAhora;
      estabanDesactivadas = !activasAhora;

      if (!activasAhora) return;

      fetch("/spring/analisis/alertas/datos/" + idMascota)
          .then(function(res) { return res.ok ? res.json() : null; })
          .then(function(alertas) {
            if (!alertas || !Array.isArray(alertas)) return;

            alertas.forEach(function(alerta) {
              if (alerta.tipo === "EMERGENCIA" && !alerta.leido) {
                if (recienActivadas) {
                  notificadas.add(alerta.id);
                } else if (!notificadas.has(alerta.id)) {
                  const notif = new Notification("⚠️ EMERGENCIA - PetTracker", {
                    body: alerta.mensaje,
                    tag: "emergencia-" + alerta.id,
                    requireInteraction: false,
                  });
                  setTimeout(function() { notif.close(); }, 5000);
                  notificadas.add(alerta.id);
                }
              }
            });
            guardarNotificadas();
          })
          .catch(function() {});
    }

    // 2. LÓGICA DEL BADGE (Completamente independiente)
    function actualizarBadge() {
      const badge = document.getElementById("badge-alertas-sin-leer");
      if (!badge) return;

      // Si no hay idMascota, evitamos el fetch de mascota y devolvemos un array vacío de inmediato
      const fetchMascota = idMascota
          ? fetch("/spring/analisis/alertas/datos/" + idMascota).then(function(r) { return r.ok ? r.json() : []; })
          : Promise.resolve([]);

      const fetchUsuario = fetch("/spring/analisis/alertas/usuario").then(function(r) { return r.ok ? r.json() : []; });

      Promise.all([fetchMascota, fetchUsuario])
          .then(function(resultados) {
            const alertasMascota = resultados[0] || [];
            const alertasUsuario = resultados[1] || [];
            const todas = alertasMascota.concat(alertasUsuario);

            const sinLeer = todas.filter(function(a) { return !a.leido; }).length;

            if (sinLeer > 0) {
              badge.textContent = sinLeer > 99 ? "99+" : sinLeer;
              badge.classList.remove("hidden");
            } else {
              badge.classList.add("hidden");
            }
          })
          .catch(function() {});
    }

    // --- Inicialización de hilos e intervalos ---

    // El badge se ejecuta SIEMPRE (tenga o no mascota, tenga o no permisos de notificación)
    actualizarBadge();
    setInterval(actualizarBadge, 10000);

    // Las emergencias nativas solo se evalúan si hay mascota activa y soporte del navegador
    if (idMascota && soporteNotification) {
      chequearEmergencias();
      setInterval(chequearEmergencias, 10000);
    }
  }

  window.inicializarNotificacionesBg = inicializarNotificacionesBg;
})();