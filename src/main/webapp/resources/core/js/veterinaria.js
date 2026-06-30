/* global L */

// eslint-disable-next-line max-lines-per-function
function inicializarMapaVeterinarias() {
  let map = null;
  let marcadorOrigen = null;
  
  // Capa separada para agrupar las veterinarias y poder borrarlas si el usuario busca otra dirección
  let marcadoresVeterinarias = L.layerGroup(); 

  const inputDireccion = document.getElementById("input-direccion-origen");
  const btnBuscar = document.getElementById("btn-buscar-origen");
  const listaSugerencias = document.getElementById("lista-veterinarias");

  const inputNombreVetForm = document.getElementById("form-vet-nombre");
  const inputDireccionVetForm = document.getElementById("form-vet-direccion");

  function inicializarMapa() {
    // Inicializamos centrado en Buenos Aires por defecto
    const latInicial = -34.6037;
    const lonInicial = -58.3816;

    map = L.map("mapa-veterinarias").setView([latInicial, lonInicial], 12);
    
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      maxZoom: 19,
      attribution: "© OpenStreetMap"
    }).addTo(map);

    marcadoresVeterinarias.addTo(map);
  }

  // PASO 2: Buscar veterinarias reales con Overpass API
  async function buscarVeterinariasCercanas(lat, lon) {
    listaSugerencias.innerHTML = '<p class="text-slate-500 text-sm p-4">Buscando veterinarias en un radio de 3km...</p>';
    marcadoresVeterinarias.clearLayers();

    // Consulta QL a Overpass: Buscar nodos, caminos o relaciones de tipo veterinaria alrededor de las coordenadas
    const query = `
      [out:json];
      (
        node["amenity"="veterinary"](around:3000, ${lat}, ${lon});
        way["amenity"="veterinary"](around:3000, ${lat}, ${lon});
        relation["amenity"="veterinary"](around:3000, ${lat}, ${lon});
      );
      out center;
    `;
    const url = `https://overpass-api.de/api/interpreter?data=${encodeURIComponent(query)}`;

    try {
      const response = await fetch(url);
      const data = await response.json();

      listaSugerencias.innerHTML = "";

      if (data.elements.length === 0) {
        listaSugerencias.innerHTML = '<p class="text-slate-500 text-sm p-4">No se encontraron veterinarias registradas en esta zona.</p>';
        return;
      }

      data.elements.forEach(vet => {
        // Extraemos los datos. Overpass devuelve "center" si es un edificio entero en vez de un nodo simple
        const vetLat = vet.lat || vet.center.lat;
        const vetLon = vet.lon || vet.center.lon;
        
        const nombre = vet.tags.name || "Veterinaria sin nombre registrado";
        const direccion = vet.tags['addr:street']
            ? `${vet.tags['addr:street']} ${vet.tags['addr:housenumber'] || ''}`
            : "Dirección no especificada en el mapa";

        // Dibuja el marcador en el mapa
        const marker = L.marker([vetLat, vetLon]).bindPopup(`<b>${nombre}</b><br>${direccion}`);
        marcadoresVeterinarias.addLayer(marker);

        // Crea la tarjeta interactiva para la barra lateral
        const divInfo = document.createElement("div");
        divInfo.className = "p-4 border border-slate-200 rounded-lg mb-3 hover:bg-blue-50 cursor-pointer transition-colors";
        divInfo.innerHTML = `
          <h4 class="font-bold text-slate-800">${nombre}</h4>
          <p class="text-sm text-slate-600 mt-1">${direccion}</p>
          <button class="mt-3 text-sm text-blue-600 font-semibold btn-seleccionar bg-blue-100 px-3 py-1 rounded-md w-full text-center transition-colors">
            Agendar turno aquí
          </button>
        `;

        // Evento al seleccionar una veterinaria para reservar
        divInfo.addEventListener("click", () => {
          // Llena el formulario de Spring
          if (inputNombreVetForm) inputNombreVetForm.value = nombre;
          if (inputDireccionVetForm) inputDireccionVetForm.value = direccion;

          document.querySelectorAll('.btn-seleccionar').forEach(btn => {
            btn.innerText = "Agendar turno aquí";
            btn.classList.replace("bg-emerald-100", "bg-blue-100");
            btn.classList.replace("text-emerald-700", "text-blue-600");
          });
          
          const btn = divInfo.querySelector('.btn-seleccionar');
          btn.innerText = "✓ Seleccionada";
          btn.classList.replace("bg-blue-100", "bg-emerald-100");
          btn.classList.replace("text-blue-600", "text-emerald-700");

          // Mueve el mapa a la veterinaria seleccionada y abre su popup
          map.setView([vetLat, vetLon], 16);
          marker.openPopup();
        });

        listaSugerencias.appendChild(divInfo);
      });

    } catch (error) {
      console.error("Error buscando veterinarias con Overpass:", error);
      listaSugerencias.innerHTML = '<p class="text-red-500 text-sm p-4">Error de conexión al buscar veterinarias.</p>';
    }
  }

  // Busca la dirección del usuario con Nominatim
  if (btnBuscar && inputDireccion) {
    btnBuscar.addEventListener("click", async () => {
      const texto = inputDireccion.value.trim();
      if (!texto) return;

      btnBuscar.innerText = "...";
      listaSugerencias.innerHTML = '<p class="text-slate-500 text-sm p-4">Buscando tu dirección...</p>';

      try {
        const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(texto)}&countrycodes=ar&limit=1`;
        const response = await fetch(url);
        const resultados = await response.json();

        if (Array.isArray(resultados) && resultados.length > 0) {
          const item = resultados[0];
          
          // log para ver que devuelve (debug)
          console.log("Respuesta de Nominatim:", item);

          const lat = parseFloat(item.lat);
          const lon = parseFloat(item.lon);

          // verificamos que no sean NaN antes de usar Leaflet
          if (isNaN(lat) || isNaN(lon)) {
            console.error("Coordenadas inválidas detectadas:", item);
            listaSugerencias.innerHTML = '<p class="text-red-500 text-sm p-4">Hubo un problema al procesar las coordenadas. Intentá con otra dirección.</p>';
            return;
          }

          if (!map) inicializarMapa();
          map.setView([lat, lon], 14);

          // Marca el punto de origen (La casa del usuario)
          if (marcadorOrigen) map.removeLayer(marcadorOrigen);
          marcadorOrigen = L.circleMarker([lat, lon], {
            color: '#ef4444',
            fillColor: '#ef4444',
            fillOpacity: 0.8,
            radius: 8
          }).addTo(map).bindPopup("Tu ubicación base").openPopup();

          // busca veterinarias alrededor
          await buscarVeterinariasCercanas(lat, lon);
        } else {
          listaSugerencias.innerHTML = '<p class="text-orange-500 text-sm p-4">Dirección no encontrada. Probá agregando la ciudad (Ej: San Justo, Buenos Aires).</p>';
        }
      } catch (error) {
        console.error("Error con Nominatim:", error);
        listaSugerencias.innerHTML = '<p class="text-red-500 text-sm p-4">Error de conexión al buscar tu dirección.</p>';
      } finally {
        btnBuscar.innerText = "Buscar";
      }
    });
  }

  inicializarMapa();
}

function inicializarCalendarioTurnos() {
  const turnosProximos = window.turnosProximosData || [];
  const turnosPasados = window.turnosPasadosData || [];
  const urlBase = window.urlBaseTurnos;

  const eventosCalendario = [];

  turnosProximos.forEach(turno => {
    eventosCalendario.push({
      id: turno.id,
      title: turno.nombreVeterinaria,
      start: turno.fechaYHora,
      color: '#3b82f6',
      description: turno.motivo,
      esPasado: false
    });
  });

  turnosPasados.forEach(turno => {
    eventosCalendario.push({
      title: turno.nombreVeterinaria + ' (Completado)',
      start: turno.fechaYHora,
      color: '#94a3b8',
      esPasado: true
    });
  });

  const calendarEl = document.getElementById('calendario-turnos');
  if (calendarEl) {
    const calendar = new FullCalendar.Calendar(calendarEl, {
      initialView: 'dayGridMonth',
      locale: 'es',
      headerToolbar: {
        left: 'prev,next today',
        center: 'title',
        right: 'dayGridMonth,timeGridWeek,listWeek'
      },
      buttonText: {
        today: 'Hoy', month: 'Mes', week: 'Semana', list: 'Agenda'
      },
      events: eventosCalendario,
      eventClick: function(info) {
        const props = info.event.extendedProps;
        const modalVisible = document.getElementById('modal-turno');
        const formParaCancelar = document.getElementById('form-cancelar-turno');
        
        document.getElementById('modal-vet-nombre').innerText = info.event.title;
        
        const opcionesFecha = { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' };
        document.getElementById('modal-vet-fecha').innerText = info.event.start.toLocaleDateString('es-AR', opcionesFecha);
        document.getElementById('modal-vet-motivo').innerText = props.description || 'Sin especificar';

        if (!props.esPasado && info.event.id) {
          formParaCancelar.action = urlBase + info.event.id + '/cancelar';
          formParaCancelar.classList.remove('hidden');
        } else {
          formParaCancelar.classList.add('hidden');
        }

        modalVisible.classList.remove('hidden');
        if (typeof lucide !== 'undefined') {
          lucide.createIcons();
        }
      }
    });
    calendar.render();
  }
}