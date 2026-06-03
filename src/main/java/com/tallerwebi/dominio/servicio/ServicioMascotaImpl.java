package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.modelo.DatosMascota;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.presentacion.DatosAltaMascota;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioMascotaImpl implements ServicioMascota {

  private final MascotaDao mascotaDao;
  private final ServicioUsuario servicioUsuario;

  @Autowired
  public ServicioMascotaImpl(MascotaDao mascotaDao, ServicioUsuario servicioUsuario) {
    this.mascotaDao = mascotaDao;
    this.servicioUsuario = servicioUsuario;
  }

  @Override
  public Long registrarMascota(DatosAltaMascota datos, Long idUsuario) {
    Usuario usuario = servicioUsuario.obtenerPerfil(idUsuario);
    if (usuario == null) {
      throw new IllegalArgumentException("Usuario no encontrado");
    }

    Mascota mascota = new Mascota();
    mascota.setNombre(datos.getNombre());
    mascota.setTamano(datos.getTamano());
    mascota.setEstadoActual(EstadoMascota.CORRIENDO); // Estado por defecto
    mascota.setUsuario(usuario);

    DatosMascota datosMascota = new DatosMascota();
    datosMascota.setTipo("Perro");
    datosMascota.setRaza(datos.getRaza());
    datosMascota.setGenero(datos.getGenero());
    datosMascota.setPeso(datos.getPeso());

    if (datos.getFechaNacimiento() != null && !datos.getFechaNacimiento().isEmpty()) {
      datosMascota.setFechaNacimiento(
        LocalDate.parse(datos.getFechaNacimiento(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
      );
    }

    mascota.setDatos(datosMascota);

    mascotaDao.guardar(mascota);
    return mascota.getId();
  }

  @Override
  public List<Mascota> obtenerMascotasPorUsuario(Long idUsuario) {
    return mascotaDao.buscarPorUsuarioId(idUsuario);
  }

  @Override
  public Mascota obtenerMascotaPorId(Long id) {
    return mascotaDao.buscarPorId(id);
  }
}
