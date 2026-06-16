package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Alerta;
import java.util.List;

/**
 * Repositorio de acceso a datos.
 */
public interface RepositorioAlerta {
  void save(Alerta alerta);

  List<Alerta> buscarPorMascota(Long idMascota);

  Alerta buscarUltimaAlertaDePesoPorMascota(Long idMascota);

  void actualizar(Alerta alerta);

  Alerta buscarPorId(Long idAlerta);
}
