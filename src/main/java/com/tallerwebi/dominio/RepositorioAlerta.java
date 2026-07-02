package com.tallerwebi.dominio;

import com.tallerwebi.dominio.modelo.Alerta;
import java.util.List;

public interface RepositorioAlerta {
  void save(Alerta alerta);

  List<Alerta> buscarPorMascota(Long idMascota);

  List<Alerta> buscarPorUsuario(Long idUsuario);

  Alerta buscarUltimaAlertaDePesoPorMascota(Long idMascota);

  Alerta buscarUltimaAlertaDeValladoPorMascota(Long idMascota);

  void actualizar(Alerta alerta);

  Alerta buscarPorId(Long idAlerta);

  List<Alerta> buscarEmergenciasActivasPorUsuario(Long idUsuario);

  void eliminarPorUsuario(Long idUsuario);

  void eliminarPorMascota(Long id);

  void marcarTodasComoLeidasPorMascota(Long idMascota);

  void eliminarPorIds(List<Long> ids);

  void marcarTodasComoLeidasPorUsuario(Long idUsuario);
}
