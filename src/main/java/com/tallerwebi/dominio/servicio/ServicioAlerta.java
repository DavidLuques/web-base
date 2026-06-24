package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.Mascota;
import java.util.List;

public interface ServicioAlerta {
  Alerta buscarUltimaAlertaDePeso(Long idMascota);

  void crearAlerta(Mascota mascota, TipoAlerta tipo, String mensaje);

  void crearAlertaUsuario(Usuario usuario, TipoAlerta tipo, String mensaje);

  List<AlertaDto> obtenerAlertasPorMascota(Long idMascota);

  List<AlertaDto> obtenerAlertasPorUsuario(Long idUsuario);

  void marcarComoLeida(Long idAlerta);
}
