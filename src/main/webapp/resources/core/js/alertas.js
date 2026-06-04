function inicializarAlertas(idMascota) {
    lucide.createIcons();

    // Función interna que hace el fetch
    function cargarAlertasPantalla() {
        fetch("/spring/analisis/alertas/datos/" + idMascota)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error en el servidor. Código HTTP: " + response.status);
                }
                return response.json();
            })
            .then(listaDeAlertas => {
                const contenedor = document.getElementById("lista-alertas-exclusiva");
                const contador = document.getElementById("contador-alertas");

                if (!listaDeAlertas || !Array.isArray(listaDeAlertas) || listaDeAlertas.length === 0) {
                    contador.textContent = "0 Alertas";
                    contenedor.innerHTML = `
                      <div class="bg-white border border-slate-200 rounded-2xl p-12 text-center shadow-sm">
                        <i data-lucide="check-circle-2" class="w-12 h-12 text-emerald-500 mx-auto mb-3"></i>
                        <h3 class="text-lg font-bold text-slate-800 mb-1">¡Todo controlado!</h3>
                        <p class="text-sm text-slate-500">No se registran anomalias ni alertas para esta mascota en este momento.</p>
                      </div>
                    `;
                    lucide.createIcons();
                    return;
                }

                contador.textContent = `${listaDeAlertas.length} Alertas`;
                let htmlContent = "";

                listaDeAlertas.reverse().forEach(alerta => {
                    const esEmergencia = alerta.tipo === 'EMERGENCIA';
                    const bgClass = esEmergencia ? 'bg-rose-50 border-rose-200' : 'bg-amber-50 border-amber-200';
                    const textClass = esEmergencia ? 'text-rose-800' : 'text-amber-800';
                    const iconColor = esEmergencia ? 'text-rose-500' : 'text-amber-500';
                    const iconName = esEmergencia ? 'octagon-alert' : 'triangle-alert';

                    htmlContent += `
                      <div class="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow flex items-start gap-4">
                        <div class="p-3 rounded-xl ${bgClass} ${iconColor} shrink-0">
                          <i data-lucide="${iconName}" class="w-6 h-6"></i>
                        </div>
                        <div class="flex-1 min-w-0">
                          <div class="flex items-center justify-between gap-2 mb-1">
                            <span class="text-xs font-bold uppercase tracking-wider ${textClass} ${bgClass} px-2 py-0.5 rounded-md">
                              ${alerta.tipo}
                            </span>
                          </div>
                          <p class="text-slate-700 text-sm font-medium leading-relaxed">${alerta.mensaje}</p>
                        </div>
                      </div>
                    `;
                });

                contenedor.innerHTML = htmlContent;
                lucide.createIcons();
            })
            .catch(error => {
                console.error("Detalle del error:", error);
                document.getElementById("lista-alertas-exclusiva").innerHTML = `
                    <div class="bg-red-50 border border-red-200 text-red-700 p-4 rounded-xl text-sm shadow-sm">
                      <strong>Error al cargar alertas:</strong> ${error.message}. <br>
                    </div>
                `;
            });
    }

    // Ejecución inicial y bucle cada 5 segundos
    cargarAlertasPantalla();
    setInterval(cargarAlertasPantalla, 5000);
}