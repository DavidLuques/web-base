/* global idMascota, lucide */

(function() {

  var TAMANO_LEGIBLE = {
    PEQUENO: "Pequeño",
    MEDIANO: "Mediano",
    GRANDE:  "Grande"
  };

  var ESTADO_LEGIBLE = {
    DURMIENDO: "Durmiendo",
    REPOSO:    "En reposo",
    CAMINANDO: "Caminando",
    CORRIENDO: "Corriendo"
  };

  function tamanoLegible(t)  { return TAMANO_LEGIBLE[t] || t; }
  function estadoLegible(e)  { return ESTADO_LEGIBLE[e] || e; }

  function elemento(id) { return document.getElementById(id); }

  function pintarPanelImpacto(impacto, rangos) {
    if (!impacto) { return; }

    // ---- PESO ----
    elemento("impacto-peso-valor").textContent = impacto.peso != null ? impacto.peso + " kg" : "Sin datos";

    var badgePeso = elemento("impacto-peso-badge");
    if (badgePeso && impacto.peso != null && impacto.pesoMinimoTamano != null && impacto.pesoMaximoTamano != null) {
      var dentroDeRango = impacto.peso >= impacto.pesoMinimoTamano && impacto.peso <= impacto.pesoMaximoTamano;
      badgePeso.textContent = dentroDeRango
        ? "Dentro del rango típico para tamaño " + tamanoLegible(impacto.tamano) + " (" + impacto.pesoMinimoTamano + "–" + impacto.pesoMaximoTamano + " kg)"
        : "Fuera del rango típico para tamaño " + tamanoLegible(impacto.tamano) + " (" + impacto.pesoMinimoTamano + "–" + impacto.pesoMaximoTamano + " kg)";
      badgePeso.className = "text-xs font-medium mt-1 " + (dentroDeRango ? "text-emerald-500" : "text-amber-500");
    }

    if (impacto.metActual != null && impacto.peso != null) {
      var kcalPorHora = (impacto.metActual * impacto.peso).toFixed(1);
	  elemento("impacto-peso-formula").textContent =
	    "El peso de tu mascota se combina con la intensidad de cada estado (dormir, caminar, correr) " +
	    "para estimar las calorías que quema en cada uno.";
      elemento("impacto-peso-resultado").textContent =
        "≈ " + kcalPorHora + " kcal por cada hora en estado " + estadoLegible(impacto.estadoActual).toLowerCase();
    } else {
      elemento("impacto-peso-formula").textContent = "Todavía no hay suficientes datos para calcular esto.";
      elemento("impacto-peso-resultado").textContent = "Sin datos";
    }

    // ---- TAMAÑO ----
    elemento("impacto-tamano-valor").textContent = impacto.tamano != null ? tamanoLegible(impacto.tamano) : "Sin datos";

    if (impacto.pasosPorKm != null) {
      elemento("impacto-tamano-formula").innerHTML =
        "El tamaño de tu mascota define <strong>cuántos pasos equivalen a cada km</strong> recorrido " +
        "y también los <strong>rangos vitales normales</strong> (frecuencia cardíaca, temperatura y presión) " +
        "que usamos para avisarte si algo está fuera de lo esperado.";

      var lineas = [];
      lineas.push("1 km recorrido = " + impacto.pasosPorKm.toLocaleString("es-AR") + " pasos");
      if (rangos && rangos.frecuenciaMinima != null && rangos.frecuenciaMaxima != null) {
        lineas.push("Frecuencia cardíaca esperada: " + rangos.frecuenciaMinima + "–" + rangos.frecuenciaMaxima + " bpm");
      }
      if (rangos && rangos.temperaturaMinima != null && rangos.temperaturaMaxima != null) {
        lineas.push("Temperatura esperada: " + rangos.temperaturaMinima + "–" + rangos.temperaturaMaxima + " °C");
      }
      elemento("impacto-tamano-resultado").innerHTML = lineas.join("<br>");
    } else {
      elemento("impacto-tamano-formula").textContent = "Todavía no hay suficientes datos para calcular esto.";
      elemento("impacto-tamano-resultado").textContent = "Sin datos";
    }

    if (window.lucide && typeof lucide.createIcons === "function") {
      lucide.createIcons();
    }
  }

  function cargarPanelImpacto() {
    if (!elemento("panel-impacto")) { return; } // esta página no tiene el panel

    Promise.all([
      fetch("/spring/analisis/impacto/" + idMascota).then(function(r) { return r.json(); }),
      fetch("/spring/analisis/rangos/" + idMascota).then(function(r) { return r.json(); })
    ])
      .then(function(resultados) {
        pintarPanelImpacto(resultados[0], resultados[1]);
      })
      .catch(function(e) {
        console.warn("No se pudo cargar el panel de impacto de datos:", e);
      });
  }

  cargarPanelImpacto();

})();