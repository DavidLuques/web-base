/* global lucide */

// eslint-disable-next-line no-unused-vars
function inicializarValla(idMascota) {
  lucide.createIcons();

  let ultimoTimestamp = null;

  const inputRadio = document.getElementById("input-radio");
  const inputZoom = document.getElementById("input-zoom");
  const zoomValor = document.getElementById("zoom-valor");
  const puntoMascota = document.getElementById("punto-mascota");
  const zonaPermitida = document.getElementById("zona-permitida");
  const badgeAlerta = document.getElementById("badge-alerta");
  const indicadorLuz = document.getElementById("indicador-luz");
  const estadoTexto = document.getElementById("estado-texto");
  const distanciaTexto = document.getElementById("distancia-texto");
  const btnConfirmar = document.getElementById("btn-confirmar-radio");

  let radioValla = parseInt(inputRadio.value) || 150;
  let escalaVisual = parseFloat(inputZoom.value) || 1.0;

  let ultimosMetrosX = 0;
  let ultimosMetrosY = 0;
  let ultimaDistanciaReal = 0;

  function aplicarEscalaVisual() {
    zonaPermitida.style.width = `${radioValla * 2 * escalaVisual}px`;
    zonaPermitida.style.height = `${radioValla * 2 * escalaVisual}px`;
    puntoMascota.style.left = `calc(50% + ${ultimosMetrosX * escalaVisual}px)`;
    puntoMascota.style.top = `calc(50% + ${ultimosMetrosY * escalaVisual}px)`;
  }

  function evaluarAlerta() {
    distanciaTexto.innerText = `${Math.round(ultimaDistanciaReal)} metros del hogar`;

    if (ultimaDistanciaReal > radioValla) {
      estadoTexto.innerText = "¡Mascota fuera de la zona segura!";
      estadoTexto.className = "text-2xl font-semibold text-red-600";
      badgeAlerta.className = "bg-red-50 text-red-700 px-4 py-2 rounded-full font-bold text-sm flex items-center gap-2 border-2 border-red-200";
      indicadorLuz.className = "w-3 h-3 rounded-full bg-red-600 animate-pulse";
      zonaPermitida.style.borderColor = "#ef4444";
      zonaPermitida.style.backgroundColor = "rgba(239, 68, 68, 0.1)";
      puntoMascota.style.color = "#ef4444";
    } else {
      estadoTexto.innerText = "Dentro del perímetro establecido";
      estadoTexto.className = "text-2xl font-semibold text-green-700";
      badgeAlerta.className = "bg-green-50 text-green-700 px-4 py-2 rounded-full font-bold text-sm flex items-center gap-2 border-2 border-green-200";
      indicadorLuz.className = "w-3 h-3 rounded-full bg-green-500";
      zonaPermitida.style.borderColor = "#3b82f6";
      zonaPermitida.style.backgroundColor = "rgba(59, 130, 246, 0.1)";
      puntoMascota.style.color = "#22c55e";
    }
  }

  inputRadio.addEventListener("input", (e) => {
    const nuevoValor = parseInt(e.target.value);
    if (nuevoValor > 0) {
      radioValla = nuevoValor;
      aplicarEscalaVisual();
    }
  });

  btnConfirmar.addEventListener("click", async () => {
    evaluarAlerta();

    const formData = new URLSearchParams();
    formData.append("radio", radioValla.toString());

    try {
      const response = await fetch(`/spring/analisis/valla/${idMascota}/actualizar`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formData
      });

      if (response.ok) {
        const textoOriginal = btnConfirmar.innerText;
        btnConfirmar.innerText = "Guardado";
        // btnConfirmar.classList.replace("bg-blue-600", "bg-emerald-600");
        btnConfirmar.classList.replace("hover:bg-blue-700", "hover:bg-emerald-700");

        setTimeout(() => {
          btnConfirmar.innerText = textoOriginal;
          btnConfirmar.classList.replace("bg-emerald-600", "bg-blue-600");
          btnConfirmar.classList.replace("hover:bg-emerald-700", "hover:bg-blue-700");
        }, 2000);
      }
    } catch (error) {
      console.error("Error al guardar el radio:", error);
    }
  });

  inputZoom.addEventListener("input", (e) => {
    escalaVisual = parseFloat(e.target.value);
    zoomValor.innerText = `${escalaVisual.toFixed(1)}x`;
    aplicarEscalaVisual();
  });

  async function cargarVallado() {
    try {
      const response = await fetch(`/spring/api/mascotas/${idMascota}/vallado`);
      const datos = await response.json();

      radioValla = datos.radio;
      inputRadio.value = datos.radio;
      aplicarEscalaVisual();
    } catch (error) {
      console.error("Error cargando vallado:", error);
    }
  }

  async function actualizarPosicion() {
    try {
      const response = await fetch(`/spring/api/mascotas/${idMascota}/ubicacion`);
      const datos = await response.json();

      if (ultimoTimestamp && ultimoTimestamp === datos.timestamp) {
        return;
      }
      ultimoTimestamp = datos.timestamp;

      // El backend ahora nos entrega todo calculado
      ultimosMetrosX = datos.metrosX;
      ultimosMetrosY = datos.metrosY;
      ultimaDistanciaReal = datos.distancia;

      aplicarEscalaVisual();
      evaluarAlerta();

    } catch (error) {
      console.error("Error conectando con la API:", error);
    }
  }

  // Notificaciones de emergencia globales
  const GLOBAL_NOTIFICADAS_KEY = "alertas-emergencia-notificadas-global";

  function cargarNotificadasGlobales() {
    try {
      return new Set(JSON.parse(localStorage.getItem(GLOBAL_NOTIFICADAS_KEY) || "[]"));
    } catch (_err) {
      return new Set();
    }
  }

  function guardarNotificadasGlobales(set) {
    localStorage.setItem(GLOBAL_NOTIFICADAS_KEY, JSON.stringify(Array.from(set)));
  }

  const alertasGlobalesNotificadas = cargarNotificadasGlobales();

  async function consultarEmergenciasGlobales() {
    if (Notification.permission !== "granted") return;
    try {
      const response = await fetch("/spring/analisis/alertas/emergencias-activas");
      if (!response.ok) return;
      const alertas = await response.json();
      if (!alertas || !Array.isArray(alertas)) return;
      alertas.forEach(function (alerta) {
        const id = String(alerta.id);
        if (!alertasGlobalesNotificadas.has(id)) {
          alertasGlobalesNotificadas.add(id);
          guardarNotificadasGlobales(alertasGlobalesNotificadas);
          new Notification("EMERGENCIA - " + (alerta.nombreMascota || ""), {
            body: alerta.mensaje,
            tag: "emergencia-" + id,
            requireInteraction: true
          });
        }
      });
    } catch (err) {
      console.error("Error consultando emergencias:", err);
    }
  }

  if ("Notification" in window) {
    if (Notification.permission === "granted") {
      consultarEmergenciasGlobales();
      setInterval(consultarEmergenciasGlobales, 10000);
    } else if (Notification.permission === "default") {
      Notification.requestPermission().then(function (permission) {
        if (permission === "granted") {
          consultarEmergenciasGlobales();
          setInterval(consultarEmergenciasGlobales, 10000);
        }
      });
    }
  }

  cargarVallado();
  actualizarPosicion();
  setInterval(actualizarPosicion, 30000);
}