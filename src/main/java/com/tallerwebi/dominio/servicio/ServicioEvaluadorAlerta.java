package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import com.tallerwebi.dominio.modelo.Vallado;

public interface ServicioEvaluadorAlerta {
  void evaluarLectura(Mascota mascota, LecturaSensor lectura, RangoVitalPorTamano rango);

  void evaluarPeso(Mascota mascota);

  void evaluarVallado(
    Mascota mascota,
    LecturaSensor lectura,
    Vallado vallado,
    double distanciaMetros
  );
}
