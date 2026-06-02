package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.presentacion.DatosAltaMascota;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioMascotaImplTest {

  private MascotaDao mascotaDaoMock;
  private ServicioUsuario servicioUsuarioMock;
  private ServicioMascotaImpl servicioMascota;

  @BeforeEach
  public void init() {
    mascotaDaoMock = mock(MascotaDao.class);
    servicioUsuarioMock = mock(ServicioUsuario.class);
    servicioMascota = new ServicioMascotaImpl(mascotaDaoMock, servicioUsuarioMock);
  }

  @Test
  public void dadoDatosValidosCuandoSeRegistraMascotaEntoncesSeGuardaCorrectamente() {
    // Preparación
    DatosAltaMascota datos = new DatosAltaMascota();
    datos.setNombre("Firulais");
    datos.setTamano(TamanoMascota.MEDIANO);
    datos.setTipo("Perro");
    datos.setPeso(10.5);
    datos.setFechaNacimiento("2020-01-01");
    Long idUsuario = 1L;
    Usuario usuarioMock = new Usuario();
    usuarioMock.setId(idUsuario);

    when(servicioUsuarioMock.obtenerPerfil(idUsuario)).thenReturn(usuarioMock);

    Mascota mascotaCapturada = new Mascota();
    mascotaCapturada.setId(55L); // Mock the ID that would be generated
    doAnswer(invocation -> {
        Mascota m = invocation.getArgument(0);
        m.setId(55L);
        return null;
      })
      .when(mascotaDaoMock)
      .guardar(any(Mascota.class));

    // Ejecución
    Long newId = servicioMascota.registrarMascota(datos, idUsuario);

    // Verificación
    assertEquals(55L, newId);
    ArgumentCaptor<Mascota> mascotaCaptor = ArgumentCaptor.forClass(Mascota.class);
    verify(mascotaDaoMock, times(1)).guardar(mascotaCaptor.capture());

    Mascota mascotaGuardada = mascotaCaptor.getValue();
    assertNotNull(mascotaGuardada);
    assertEquals("Firulais", mascotaGuardada.getNombre());
    assertEquals(TamanoMascota.MEDIANO, mascotaGuardada.getTamano());
    assertEquals(EstadoMascota.REPOSO, mascotaGuardada.getEstadoActual());
    assertEquals(usuarioMock, mascotaGuardada.getUsuario());
    assertNotNull(mascotaGuardada.getDatos());
    assertEquals("Perro", mascotaGuardada.getDatos().getTipo());
    assertEquals(10.5, mascotaGuardada.getPeso());
    assertNotNull(mascotaGuardada.getFechaNacimiento());
  }

  @Test
  public void dadoQueUnUsuarioTieneMascotasCuandoSolicitoMascotasPorUsuarioRetornaLaLista() {
    Long idUsuario = 1L;
    List<Mascota> mascotasSimuladas = Arrays.asList(new Mascota(), new Mascota());
    when(mascotaDaoMock.buscarPorUsuarioId(idUsuario)).thenReturn(mascotasSimuladas);

    List<Mascota> resultado = servicioMascota.obtenerMascotasPorUsuario(idUsuario);

    assertNotNull(resultado);
    assertEquals(2, resultado.size());
    verify(mascotaDaoMock, times(1)).buscarPorUsuarioId(idUsuario);
  }

  @Test
  public void dadoUnUsuarioInexistenteCuandoSeRegistraMascotaEntoncesLanzaExcepcion() {
    // Preparación
    DatosAltaMascota datos = new DatosAltaMascota();
    Long idUsuario = 99L;

    when(servicioUsuarioMock.obtenerPerfil(idUsuario)).thenReturn(null);

    // Ejecución y Verificación
    Exception exception = assertThrows(
      IllegalArgumentException.class,
      () -> {
        servicioMascota.registrarMascota(datos, idUsuario);
      }
    );

    assertEquals("Usuario no encontrado", exception.getMessage());
    verify(mascotaDaoMock, never()).guardar(any(Mascota.class));
  }
}
