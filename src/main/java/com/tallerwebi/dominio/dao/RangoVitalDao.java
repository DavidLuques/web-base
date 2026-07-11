package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.enums.TipoMascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;

public interface RangoVitalDao {
  RangoVitalPorTamano buscarPorTipoYTamano(TipoMascota tipo, TamanoMascota tamano);
}
