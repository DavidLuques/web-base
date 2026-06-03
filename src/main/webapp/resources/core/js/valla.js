function inicializarValla(idMascota) {
    lucide.createIcons();

    const latHogar = -34.7222;
    const lonHogar = -58.5250;

    const inputRadio = document.getElementById('input-radio');
    const inputZoom = document.getElementById('input-zoom');
    const zoomValor = document.getElementById('zoom-valor');
    const puntoMascota = document.getElementById('punto-mascota');
    const zonaPermitida = document.getElementById('zona-permitida');
    const badgeAlerta = document.getElementById('badge-alerta');
    const indicadorLuz = document.getElementById('indicador-luz');
    const estadoTexto = document.getElementById('estado-texto');
    const distanciaTexto = document.getElementById('distancia-texto');

    let radioValla = parseInt(inputRadio.value) || 150;
    let escalaVisual = parseFloat(inputZoom.value) || 1.0;

    // Guardamos la última posición en memoria para que el zoom funcione sin hacer fetch
    let ultimosMetrosX = 0;
    let ultimosMetrosY = 0;
    let ultimaDistanciaReal = 0;

    function calcularDistancia(lat1, lon1, lat2, lon2) {
      const R = 6371e3; // Radio de la Tierra en metros
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

    // actualización visual
    function aplicarEscalaVisual() {
      // Redimensiona la zona permitida multiplicando por la escala
      zonaPermitida.style.width = `${radioValla * 2 * escalaVisual}px`;
      zonaPermitida.style.height = `${radioValla * 2 * escalaVisual}px`;

      // Reposiciona a la mascota multiplicando sus metros por la escala
      puntoMascota.style.left = `calc(50% + ${ultimosMetrosX * escalaVisual}px)`;
      puntoMascota.style.top = `calc(50% + ${ultimosMetrosY * escalaVisual}px)`;
    }

    function evaluarAlerta() {
      distanciaTexto.innerText = `${Math.round(ultimaDistanciaReal)} metros del hogar`;

      if (ultimaDistanciaReal > radioValla) {
        // ESTADO PELIGRO
        estadoTexto.innerText = "¡Mascota fuera de la zona segura!";
        estadoTexto.className = "text-2xl font-semibold text-red-600";

        badgeAlerta.className = "bg-red-50 text-red-700 px-4 py-2 rounded-full font-bold text-sm flex items-center gap-2 border-2 border-red-200";
        indicadorLuz.className = "w-3 h-3 rounded-full bg-red-600 animate-pulse";

        // Modificar el mapa
        zonaPermitida.style.borderColor = '#ef4444';
        zonaPermitida.style.backgroundColor = 'rgba(239, 68, 68, 0.1)';
        puntoMascota.style.color = '#ef4444';
      } else {
        // ESTADO SEGURO
        estadoTexto.innerText = "Dentro del perímetro establecido";
        estadoTexto.className = "text-2xl font-semibold text-green-700";

        badgeAlerta.className = "bg-green-50 text-green-700 px-4 py-2 rounded-full font-bold text-sm flex items-center gap-2 border-2 border-green-200";
        indicadorLuz.className = "w-3 h-3 rounded-full bg-green-500";

        // Modifica el mapa
        zonaPermitida.style.borderColor = '#3b82f6';
        zonaPermitida.style.backgroundColor = 'rgba(59, 130, 246, 0.1)';
        puntoMascota.style.color = '#22c55e'; // Verde para el SVG
      }
    }

    inputRadio.addEventListener('input', (e) => {
      const nuevoValor = parseInt(e.target.value);
      if (nuevoValor > 0) {
        radioValla = nuevoValor;
        aplicarEscalaVisual();
        evaluarAlerta();
      }
    });

    inputZoom.addEventListener('input', (e) => {
      escalaVisual = parseFloat(e.target.value);
      zoomValor.innerText = `${escalaVisual.toFixed(1)}x`;
      aplicarEscalaVisual();
    });

    async function actualizarPosicion() {
      try {
        const response = await fetch(`/spring/api/mascotas/${idMascota}/ubicacion`);
        const datos = await response.json();

        const nuevaLat = datos.latitud;
        const nuevaLon = datos.longitud;

        // Calculamos metros pero los guardamos en memoria (sin escala aun)
        ultimosMetrosY = -(nuevaLat - latHogar) * 111320;
        ultimosMetrosX = (nuevaLon - lonHogar) * (111320 * Math.cos(latHogar * Math.PI / 180));
        ultimaDistanciaReal = calcularDistancia(latHogar, lonHogar, nuevaLat, nuevaLon);

        // Disparamos los cambios en la pantalla
        aplicarEscalaVisual();
        evaluarAlerta();

      } catch (error) {
        console.error("Error conectando con la API:", error);
      }
    }

    // 2 min y 5 seg
    setInterval(actualizarPosicion, 125000);
    actualizarPosicion();
}