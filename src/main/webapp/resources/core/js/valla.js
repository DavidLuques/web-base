/* global L, lucide */

// eslint-disable-next-line no-unused-vars
function inicializarValla(idMascota) {
  if (typeof lucide !== "undefined") {
    lucide.createIcons();
  }

  let map = null;
  let vallaCircle = null;
  let mascotaMarker = null;
  let ultimoTimestamp = null;

  let latHogar = -34.7222;
  let lonHogar = -58.5250;
  let radioValla = 150;
  let ultimaDistanciaReal = 0;

  const inputRadio = document.getElementById("input-radio");
  const btnConfirmar = document.getElementById("btn-confirmar-radio");
  
  const inputDireccionValla = document.getElementById("input-direccion-valla");
  const btnActualizarCentro = document.getElementById("btn-actualizar-centro");

  const badgeAlerta = document.getElementById("badge-alerta");
  const indicadorLuz = document.getElementById("indicador-luz");
  const estadoTexto = document.getElementById("estado-texto");
  const distanciaTexto = document.getElementById("distancia-texto");

  function inicializarMapaReal() {
    if (map !== null) return;

    map = L.map("mapa-valla").setView([latHogar, lonHogar], 16);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      maxZoom: 19,
      attribution: "© OpenStreetMap"
    }).addTo(map);

    vallaCircle = L.circle([latHogar, lonHogar], {
      color: "#3b82f6",
      fillColor: "#3b82f6",
      fillOpacity: 0.1,
      radius: radioValla
    }).addTo(map);

    mascotaMarker = L.marker([latHogar, lonHogar]).addTo(map);
  }

  function evaluarAlerta() {
    if (!distanciaTexto || !estadoTexto) return;

    distanciaTexto.innerText = `${Math.round(ultimaDistanciaReal)} metros del hogar`;

    if (ultimaDistanciaReal > radioValla) {
      estadoTexto.innerText = "¡Mascota fuera de la zona segura!";
      estadoTexto.className = "text-2xl font-semibold text-red-600";
      
      if (badgeAlerta) badgeAlerta.className = "bg-red-50 text-red-700 px-4 py-2 rounded-full font-bold text-sm flex items-center gap-2 border-2 border-red-200";
      if (indicadorLuz) indicadorLuz.className = "w-3 h-3 rounded-full bg-red-600 animate-pulse";

      if (vallaCircle) vallaCircle.setStyle({ color: "#ef4444", fillColor: "#ef4444" });
    } else {
      estadoTexto.innerText = "Dentro del perímetro establecido";
      estadoTexto.className = "text-2xl font-semibold text-green-700";
      
      if (badgeAlerta) badgeAlerta.className = "bg-green-50 text-green-700 px-4 py-2 rounded-full font-bold text-sm flex items-center gap-2 border-2 border-green-200";
      if (indicadorLuz) indicadorLuz.className = "w-3 h-3 rounded-full bg-green-500";

      if (vallaCircle) vallaCircle.setStyle({ color: "#3b82f6", fillColor: "#3b82f6" });
    }
  }

  // --- Eventos para el Radio ---
  if (inputRadio) {
    inputRadio.addEventListener("input", (e) => {
      const nuevoValor = parseInt(e.target.value);
      if (nuevoValor > 0) {
        radioValla = nuevoValor;
        if (vallaCircle) vallaCircle.setRadius(radioValla);
      }
    });
  }

  if (btnConfirmar) {
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
          btnConfirmar.classList.replace("bg-blue-600", "bg-emerald-600");
          setTimeout(() => {
            btnConfirmar.innerText = textoOriginal;
            btnConfirmar.classList.replace("bg-emerald-600", "bg-blue-600");
          }, 2000);
        }
      } catch (error) {
        console.error("Error al guardar el radio:", error);
      }
    });
  }

  // --- Eventos para la Dirección del Centro (Buscador Visual) ---
  const btnBuscarDireccion = document.getElementById("btn-buscar-direccion");
  const inputDireccion = document.getElementById("input-direccion-valla");
  const listaResultados = document.getElementById("lista-resultados");

  if (btnBuscarDireccion && inputDireccion && listaResultados) {
    btnBuscarDireccion.addEventListener("click", async () => {
      const texto = inputDireccion.value.trim();
      if (!texto) return;

      btnBuscarDireccion.innerText = "...";
      listaResultados.innerHTML = "";
      listaResultados.classList.remove("hidden");

      try {
        // Consultamos a OpenStreetMap directamente desde el navegador (limitado a Argentina)
        const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(texto)}&countrycodes=ar&limit=5`;
        const response = await fetch(url);
        const resultados = await response.json();

        if (resultados.length === 0) {
          listaResultados.innerHTML = "<li class='p-3 text-sm text-gray-500'>No se encontraron resultados.</li>";
        } else {
          // Armamos la lista de opciones clickeables
          resultados.forEach(lugar => {
            const li = document.createElement("li");
            li.className = "p-3 text-sm border-b border-gray-100 hover:bg-blue-50 cursor-pointer transition-colors";
            li.innerText = lugar.display_name;
            
            // Evento al hacer clic en un resultado
            li.addEventListener("click", () => {
              const lat = parseFloat(lugar.lat);
              const lon = parseFloat(lugar.lon);
              
              // Ocultamos la lista y actualizamos el input
              listaResultados.classList.add("hidden");
              inputDireccion.value = lugar.display_name.split(","); // Mostramos solo la calle corta

              //  Movemos el mapa y el círculo visualmente
              latHogar = lat;
              lonHogar = lon;
              if (map) map.setView([lat, lon], 16);
              if (mascotaMarker) mascotaMarker.setLatLng([lat, lon]);
              if (vallaCircle) vallaCircle.setLatLng([lat, lon]);

              
              guardarCentroEnBaseDeDatos(lat, lon);
            });

            listaResultados.appendChild(li);
          });
        }
      } catch (error) {
        console.error("Error buscando dirección:", error);
        listaResultados.innerHTML = "<li class='p-3 text-sm text-red-500'>Error en la búsqueda.</li>";
      } finally {
        btnBuscarDireccion.innerText = "Buscar";
      }
    });

    // Cerrar la lista si hace clic afuera
    document.addEventListener("click", (e) => {
      if (!inputDireccion.contains(e.target) && !btnBuscarDireccion.contains(e.target) && !listaResultados.contains(e.target)) {
        listaResultados.classList.add("hidden");
      }
    });
  }

  // Función separada para guardar
  async function guardarCentroEnBaseDeDatos(lat, lon) {
    const formData = new URLSearchParams();
    formData.append("latitud", lat.toString());
    formData.append("longitud", lon.toString());

    try {
      const response = await fetch(`/spring/analisis/valla/${idMascota}/centro`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formData
      });

      if (response.ok) {
        btnBuscarDireccion.innerText = "¡Guardado!";
        btnBuscarDireccion.classList.replace("bg-slate-800", "bg-emerald-600");
        setTimeout(() => {
          btnBuscarDireccion.innerText = "Buscar";
          btnBuscarDireccion.classList.replace("bg-emerald-600", "bg-slate-800");
        }, 2000);
      }
    } catch (error) {
      console.error("Error guardando centro:", error);
    }
  }

  // --- Carga de datos iniciales e intervalos ---
  async function cargarVallado() {
    try {
      const response = await fetch(`/spring/api/mascotas/${idMascota}/vallado`);
      const datos = await response.json();

      latHogar = datos.latitud;
      lonHogar = datos.longitud;
      radioValla = datos.radio;

      if (inputRadio) inputRadio.value = datos.radio;

      inicializarMapaReal();
    } catch (error) {
      console.error("Error cargando vallado:", error);
    }
  }

  async function actualizarPosicion() {
    try {
      const response = await fetch(`/spring/api/mascotas/${idMascota}/ubicacion`);
      const datos = await response.json();

      if (ultimoTimestamp && ultimoTimestamp === datos.timestamp) return;
      ultimoTimestamp = datos.timestamp;

      ultimaDistanciaReal = datos.distancia;

      if (mascotaMarker) {
        mascotaMarker.setLatLng([datos.latitud, datos.longitud]);
      }

      evaluarAlerta();
    } catch (error) {
      console.error("Error conectando con la API:", error);
    }
  }

  cargarVallado().then(() => {
    actualizarPosicion();
    setInterval(actualizarPosicion, 60000);
  });
}