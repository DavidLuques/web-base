/* global lucide */

// eslint-disable-next-line no-unused-vars
function inicializarValla(idMascota) {
  lucide.createIcons();

  let latHogar = -34.7222;
  let lonHogar = -58.5250;

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

  function calcularDistancia(lat1, lon1, lat2, lon2) {
    const R = 6371e3;
    const phi1 = lat1 * Math.PI / 180;
    const phi2 = lat2 * Math.PI / 180;
    const deltaPhi = (lat2 - lat1) * Math.PI / 180;
    const deltaLambda = (lon2 - lon1) * Math.PI / 180;

    const a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
        Math.cos(phi1) * Math.cos(phi2) *
        Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }

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
        headers: {
          "Content-Type": "application/x-www-form-urlencoded"
        },
        body: formData
      });

      if (response.ok) {
        // Feedback visual temporal para que el usuario sepa que funcionó
        const textoOriginal = btnConfirmar.innerText;
        btnConfirmar.innerText = "Guardado";
        btnConfirmar.classList.replace("bg-blue-600", "bg-emerald-600");
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

      latHogar = datos.latitud;
      lonHogar = datos.longitud;
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

      const nuevaLat = datos.latitud;
      const nuevaLon = datos.longitud;

      ultimosMetrosY = -(nuevaLat - latHogar) * 111320;
      ultimosMetrosX = (nuevaLon - lonHogar) * (111320 * Math.cos(latHogar * Math.PI / 180));
      ultimaDistanciaReal = calcularDistancia(latHogar, lonHogar, nuevaLat, nuevaLon);

      aplicarEscalaVisual();
      evaluarAlerta();

    } catch (error) {
      console.error("Error conectando con la API:", error);
    }
  }

  cargarVallado();
  actualizarPosicion();
setInterval(actualizarPosicion, 60000);
}