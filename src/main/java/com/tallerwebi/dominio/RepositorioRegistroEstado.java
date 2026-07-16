package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.RegistroEstado;
import java.util.List;

public interface RepositorioRegistroEstado {
  void guardar(RegistroEstado registro);
  List<RegistroEstado> buscarPorMascota(Long idMascota);
}
