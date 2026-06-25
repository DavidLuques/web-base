package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.ValladoDao;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.modelo.DatosMascota;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.Vallado;
import com.tallerwebi.presentacion.DatosAltaMascota;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de lógica de negocio.
 */
@Service
@Transactional
public class ServicioMascotaImpl implements ServicioMascota {

  private final MascotaDao mascotaDao;
  private final ValladoDao valladoDao;
  private final ServicioUsuario servicioUsuario;

  @Autowired
  public ServicioMascotaImpl(
    MascotaDao mascotaDao,
    ValladoDao valladoDao,
    ServicioUsuario servicioUsuario
  ) {
    this.mascotaDao = mascotaDao;
    this.valladoDao = valladoDao;
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

    Vallado valladoPorDefecto = new Vallado(mascota, -34.7222, -58.5250, 150);
    valladoDao.guardar(valladoPorDefecto);

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

  @Override
  public DatosAltaMascota obtenerDatosMascota(Long id) {
    Mascota mascota = mascotaDao.buscarPorId(id);
    if (mascota == null) {
      return null;
    }
    DatosAltaMascota datos = new DatosAltaMascota();
    datos.setNombre(mascota.getNombre());
    datos.setTamano(mascota.getTamano());
    if (mascota.getDatos() != null) {
      datos.setTipo(mascota.getDatos().getTipo());
      datos.setRaza(mascota.getDatos().getRaza());
      datos.setGenero(mascota.getDatos().getGenero());
      datos.setPeso(mascota.getDatos().getPeso());
      if (mascota.getDatos().getFechaNacimiento() != null) {
        datos.setFechaNacimiento(mascota.getDatos().getFechaNacimiento().toString());
      }
    }
    return datos;
  }

  @Override
  public void actualizarMascota(Long idMascota, DatosAltaMascota datos) {
    Mascota mascota = mascotaDao.buscarPorId(idMascota);
    if (mascota != null) {
      mascota.setNombre(datos.getNombre());
      mascota.setTamano(datos.getTamano());
      if (mascota.getDatos() == null) {
        mascota.setDatos(new DatosMascota());
      }
      mascota.getDatos().setTipo(datos.getTipo());
      mascota.getDatos().setRaza(datos.getRaza());
      mascota.getDatos().setGenero(datos.getGenero());
      mascota.getDatos().setPeso(datos.getPeso());

      if (datos.getFechaNacimiento() != null && !datos.getFechaNacimiento().isEmpty()) {
        mascota
          .getDatos()
          .setFechaNacimiento(
            LocalDate.parse(datos.getFechaNacimiento(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
          );
      } else {
        mascota.getDatos().setFechaNacimiento(null);
      }

      mascotaDao.modificar(mascota);
    }
  }

  @Override
  public void eliminarMascota(Long id) {
    Mascota mascota = mascotaDao.buscarPorId(id);
    if (mascota != null) {
      mascota.setActivo(false);
      mascotaDao.modificar(mascota);
    }
  }
}
