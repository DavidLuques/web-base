package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Alerta;
import java.util.List;

public interface RepositorioAlerta {
  void save(Alerta alerta);

  List<Alerta> buscarPorMascota(Long idMascota);
}
