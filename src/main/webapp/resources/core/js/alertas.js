/* global lucide */
/* eslint-disable-next-line no-unused-vars */
function inicializarAlertas(idMascota) {
  lucide.createIcons();

  function formatearFecha(fechaStr) {
    const fecha = new Date(fechaStr);
    const hoy = new Date();
    const ayer = new Date(hoy);
    ayer.setDate(ayer.getDate() - 1);

    const fechaFormato = fecha.toLocaleDateString("es-AR");
    const horaFormato = fecha.toLocaleTimeString("es-AR", {
      hour: "2-digit",
      minute: "2-digit"
    });

    if (fechaFormato === hoy.toLocaleDateString("es-AR")) {
      return "Hoy a las " + horaFormato;
    } else if (fechaFormato === ayer.toLocaleDateString("es-AR")) {
      return "Ayer a las " + horaFormato;
    } else {
      return fechaFormato + " a las " + horaFormato;
    }
  }

  function construirHtmlAlerta(alerta) {
    const esEmergencia = alerta.tipo === "EMERGENCIA";
    const esInfo = alerta.tipo === "INFO";
    let bgClass, textClass, iconColor, iconName;

    if (esInfo) {
      bgClass = "bg-indigo-50 border-indigo-200";
      textClass = "text-indigo-800";
      iconColor = "text-indigo-500";
      iconName = "bell";
    } else if (esEmergencia) {
      bgClass = "bg-rose-50 border-rose-200";
      textClass = "text-rose-800";
      iconColor = "text-rose-500";
      iconName = "octagon-alert";
    } else {
      bgClass = "bg-amber-50 border-amber-200";
      textClass = "text-amber-800";
      iconColor = "text-amber-500";
      iconName = "triangle-alert";
    }

    const opacidad = alerta.leido ? "opacity-60" : "";
    const badge = alerta.leido
      ? "<span class=\"bg-green-100 text-green-700 text-xs px-2 py-1 rounded-full font-bold\">&#10003; Le\u00eddo</span>"
      : "<span class=\"bg-blue-100 text-blue-700 text-xs px-2 py-1 rounded-full font-bold cursor-pointer hover:bg-blue-200\">Sin leer</span>";
    const fechaFormato = formatearFecha(alerta.fechaYHora);

    return (
      "<div class=\"bg-white border border-slate-200 rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow flex items-start gap-4 cursor-pointer " + opacidad + "\" onclick=\"marcarAlertaComoLeida(" + alerta.id + ")\">" +
      "<div class=\"p-3 rounded-xl " + bgClass + " " + iconColor + " shrink-0\">" +
      "<i data-lucide=\"" + iconName + "\" class=\"w-6 h-6\"></i>" +
      "</div>" +
      "<div class=\"flex-1 min-w-0\">" +
      "<div class=\"flex items-center justify-between gap-2 mb-2\">" +
      "<span class=\"text-xs font-bold uppercase tracking-wider " + textClass + " " + bgClass + " px-2 py-0.5 rounded-md\">" + alerta.tipoFormato + "</span>" +
      badge +
      "</div>" +
      "<p class=\"text-slate-700 text-sm font-medium leading-relaxed mb-2\">" + alerta.mensaje + "</p>" +
      "<p class=\"text-slate-500 text-xs\">" + fechaFormato + "</p>" +
      "</div>" +
      "</div>"
    );
  }

  function cargarAlertasMascota() {
    fetch("/spring/analisis/alertas/datos/" + idMascota)
      .then(function (response) {
        if (!response.ok) {
          throw new Error("Error en el servidor. Codigo HTTP: " + response.status);
        }
        return response.json();
      })
      .then(function (listaDeAlertas) {
        const contenedor = document.getElementById("lista-alertas-exclusiva");
        const contador = document.getElementById("contador-alertas");

        if (!listaDeAlertas || !Array.isArray(listaDeAlertas) || listaDeAlertas.length === 0) {
          contador.textContent = "0 Alertas";
          contenedor.innerHTML =
            "<div class=\"bg-white border border-slate-200 rounded-2xl p-12 text-center shadow-sm\">" +
            "<i data-lucide=\"check-circle-2\" class=\"w-12 h-12 text-emerald-500 mx-auto mb-3\"></i>" +
            "<h3 class=\"text-lg font-bold text-slate-800 mb-1\">Todo controlado</h3>" +
            "<p class=\"text-sm text-slate-500\">No se registran anomal\u00edas ni alertas para esta mascota en este momento.</p>" +
            "</div>";
          lucide.createIcons();
          return;
        }

        contador.textContent = listaDeAlertas.length + " Alertas";
        contenedor.innerHTML = [...listaDeAlertas].reverse()
          .map(function (alerta) {
            return construirHtmlAlerta(alerta);
          })
          .join("");
        lucide.createIcons();
      })
      .catch(function (error) {
        console.error("Detalle del error:", error);
        document.getElementById("lista-alertas-exclusiva").innerHTML =
          "<div class=\"bg-red-50 border border-red-200 text-red-700 p-4 rounded-xl text-sm shadow-sm\">" +
          "<strong>Error al cargar alertas:</strong> " + error.message + "." +
          "</div>";
      });
  }

  function cargarAlertasUsuario() {
    fetch("/spring/analisis/alertas/usuario")
      .then(function (response) {
        if (!response.ok) {
          throw new Error("Error en el servidor. Codigo HTTP: " + response.status);
        }
        return response.json();
      })
      .then(function (listaDeAlertas) {
        const contenedor = document.getElementById("lista-alertas-usuario");

        if (!listaDeAlertas || !Array.isArray(listaDeAlertas) || listaDeAlertas.length === 0) {
          contenedor.innerHTML =
            "<div class=\"bg-white border border-slate-200 rounded-2xl p-12 text-center shadow-sm\">" +
            "<i data-lucide=\"bell-off\" class=\"w-12 h-12 text-slate-300 mx-auto mb-3\"></i>" +
            "<p class=\"text-sm text-slate-500\">No ten\u00e9s notificaciones de amigos ni transferencias.</p>" +
            "</div>";
          lucide.createIcons();
          return;
        }

        contenedor.innerHTML = listaDeAlertas
          .map(function (a) { return construirHtmlAlerta(a); })
          .join("");
        lucide.createIcons();
      })
      .catch(function (error) {
        console.error("Detalle del error:", error);
        document.getElementById("lista-alertas-usuario").innerHTML =
          "<div class=\"bg-red-50 border border-red-200 text-red-700 p-4 rounded-xl text-sm shadow-sm\">" +
          "<strong>Error al cargar notificaciones:</strong> " + error.message + "." +
          "</div>";
      });
  }

  function actualizarBotonWindows() {
    const btn = document.getElementById("btn-notif-windows");
    if (btn) {
      btn.textContent = notifWindowsActivas
        ? "Desactivar notificaciones Windows"
        : "Activar notificaciones Windows";
    }
  }

  function inicializarBotonMail() {
    fetch("/spring/perfil/usuario/notificaciones-mail")
      .then(function(response) { return response.json(); })
      .then(function(activo) {
        const btn = document.getElementById("btn-notif-mail");
        if (btn) {
          btn.textContent = activo
            ? "Desactivar notificaciones por mail"
            : "Activar notificaciones por mail";
        }
      });
  }

  function actualizarBadgeInmediato(valor) {
    const badge = document.getElementById("badge-alertas-sin-leer");
    if (!badge) return;

    if (valor === 0) {
      badge.classList.add("hidden");
      return;
    }

    const actual = parseInt(badge.textContent) || 0;
    const nuevo = Math.max(0, actual + valor);

    if (nuevo === 0) {
      badge.classList.add("hidden");
    } else {
      badge.textContent = nuevo > 99 ? "99+" : nuevo;
      badge.classList.remove("hidden");
    }
  }

  window.marcarAlertaComoLeida = function (idAlerta) {
    fetch("/spring/analisis/alertas/" + idAlerta + "/leer", {
      method: "PUT",
      headers: { "Content-Type": "application/json" }
    })
      .then(function (response) {
        if (response.ok) {
          cargarAlertasMascota();
          cargarAlertasUsuario();
          actualizarBadgeInmediato(-1);
        }
      })
      .catch(function (error) {
        console.error("Error marcando alerta como leida:", error);
      });
  };

  const notifWindowsKey = "notificaciones-windows-activas-" + idMascota;
  let notifWindowsActivas = localStorage.getItem(notifWindowsKey) === "true";

  window.toggleNotificacionesWindows = function() {
    notifWindowsActivas = !notifWindowsActivas;
    localStorage.setItem(notifWindowsKey, String(notifWindowsActivas));
    actualizarBotonWindows();

    if (notifWindowsActivas && idMascota) {
      const sessionKey = "alertas-notificadas-sesion-" + idMascota;
      const notificadas = new Set(
        JSON.parse(sessionStorage.getItem(sessionKey) || "[]")
      );
      fetch("/spring/analisis/alertas/datos/" + idMascota)
        .then(function (res) { return res.json(); })
        .then(function (alertas) {
          alertas.forEach(function (alerta) {
            if (alerta.tipo === "EMERGENCIA" && !alerta.leido) {
              notificadas.add(alerta.id);
            }
          });
          sessionStorage.setItem(sessionKey, JSON.stringify(Array.from(notificadas)));
        });
    }
  };

  window.toggleNotificacionesMail = function() {
    fetch("/spring/perfil/usuario/notificaciones-mail", {
      method: "PUT",
      headers: { "Content-Type": "application/json" }
    }).then(function(response) {
      if (response.ok) {
        inicializarBotonMail();
      }
    });
  };

  window.marcarTodasComoLeidas = function () {
    fetch("/spring/analisis/alertas/todas-leidas/" + idMascota, {
      method: "PUT",
      headers: { "Content-Type": "application/json" }
    }).then(function (res) {
      if (res.ok) {
        cargarAlertasMascota();
        cargarAlertasUsuario();
        actualizarBadgeInmediato(0);
      }
    });
  };

  if (idMascota !== null && idMascota !== undefined) {
    cargarAlertasMascota();
    setInterval(cargarAlertasMascota, 5000);
  }

  cargarAlertasUsuario();
  actualizarBotonWindows();
  inicializarBotonMail();
  setInterval(cargarAlertasUsuario, 5000);
}