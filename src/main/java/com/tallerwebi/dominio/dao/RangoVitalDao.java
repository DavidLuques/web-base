package com.tallerwebi.dominio.dao;

import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;

/**
 * Repositorio de acceso a datos.
 */
public interface RangoVitalDao {
  RangoVitalPorTamano buscarPorTamano(TamanoMascota tamano);
}
