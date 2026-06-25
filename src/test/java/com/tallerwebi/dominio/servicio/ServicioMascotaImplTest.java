package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.ValladoDao;
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
  private ValladoDao valladoDaoMock;
  private ServicioUsuario servicioUsuarioMock;
  private ServicioMascotaImpl servicioMascota;

  @BeforeEach
  public void init() {
    mascotaDaoMock = mock(MascotaDao.class);
    servicioUsuarioMock = mock(ServicioUsuario.class);
    valladoDaoMock = mock(ValladoDao.class);
    servicioMascota = new ServicioMascotaImpl(mascotaDaoMock, valladoDaoMock, servicioUsuarioMock);
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
    assertEquals(EstadoMascota.CORRIENDO, mascotaGuardada.getEstadoActual());
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

  @Test
  public void cuandoLaFechaNacimientoEsNullNoSeAsignaFecha() {
    DatosAltaMascota datos = new DatosAltaMascota();

    datos.setNombre("Firulais");
    datos.setTamano(TamanoMascota.MEDIANO);
    datos.setFechaNacimiento(null);

    Usuario usuario = new Usuario();
    usuario.setId(1L);

    when(servicioUsuarioMock.obtenerPerfil(1L)).thenReturn(usuario);

    doAnswer(invocation -> {
        Mascota mascota = invocation.getArgument(0);
        mascota.setId(1L);
        return null;
      })
      .when(mascotaDaoMock)
      .guardar(any(Mascota.class));

    servicioMascota.registrarMascota(datos, 1L);

    ArgumentCaptor<Mascota> captor = ArgumentCaptor.forClass(Mascota.class);

    verify(mascotaDaoMock).guardar(captor.capture());

    assertNull(captor.getValue().getDatos().getFechaNacimiento());
  }

  @Test
  public void cuandoLaFechaNacimientoEsVaciaNoSeAsignaFecha() {
    DatosAltaMascota datos = new DatosAltaMascota();

    datos.setNombre("Firulais");
    datos.setTamano(TamanoMascota.MEDIANO);
    datos.setFechaNacimiento("");

    Usuario usuario = new Usuario();
    usuario.setId(1L);

    when(servicioUsuarioMock.obtenerPerfil(1L)).thenReturn(usuario);

    doAnswer(invocation -> {
        Mascota mascota = invocation.getArgument(0);
        mascota.setId(1L);
        return null;
      })
      .when(mascotaDaoMock)
      .guardar(any(Mascota.class));

    servicioMascota.registrarMascota(datos, 1L);

    ArgumentCaptor<Mascota> captor = ArgumentCaptor.forClass(Mascota.class);

    verify(mascotaDaoMock).guardar(captor.capture());

    assertNull(captor.getValue().getDatos().getFechaNacimiento());
  }

  @Test
  public void dadoUnIdValidoCuandoObtengoMascotaPorIdRetornaLaMascota() {
    Mascota mascota = new Mascota();
    mascota.setId(10L);
    mascota.setNombre("Luna");

    when(mascotaDaoMock.buscarPorId(10L)).thenReturn(mascota);

    Mascota resultado = servicioMascota.obtenerMascotaPorId(10L);

    assertNotNull(resultado);
    assertEquals("Luna", resultado.getNombre());

    verify(mascotaDaoMock).buscarPorId(10L);
  }

  @Test
  public void dadoUnIdValidoCuandoObtengoDatosMascotaRetornaDatosMapeados() {
    Mascota mascota = new Mascota();
    mascota.setId(1L);
    mascota.setNombre("Boby");
    mascota.setTamano(TamanoMascota.PEQUENO);
    com.tallerwebi.dominio.modelo.DatosMascota datosMascota =
      new com.tallerwebi.dominio.modelo.DatosMascota();
    datosMascota.setTipo("Perro");
    datosMascota.setPeso(5.0);
    mascota.setDatos(datosMascota);

    when(mascotaDaoMock.buscarPorId(1L)).thenReturn(mascota);

    DatosAltaMascota resultado = servicioMascota.obtenerDatosMascota(1L);

    assertNotNull(resultado);
    assertEquals("Boby", resultado.getNombre());
    assertEquals(TamanoMascota.PEQUENO, resultado.getTamano());
    assertEquals("Perro", resultado.getTipo());
    assertEquals(5.0, resultado.getPeso());
  }

  @Test
  public void dadoUnIdInvalidoCuandoObtengoDatosMascotaRetornaNull() {
    when(mascotaDaoMock.buscarPorId(99L)).thenReturn(null);

    DatosAltaMascota resultado = servicioMascota.obtenerDatosMascota(99L);

    assertNull(resultado);
  }

  @Test
  public void dadoDatosValidosCuandoActualizaMascotaSeGuardaCorrectamente() {
    Mascota mascota = new Mascota();
    mascota.setId(1L);
    when(mascotaDaoMock.buscarPorId(1L)).thenReturn(mascota);

    DatosAltaMascota datos = new DatosAltaMascota();
    datos.setNombre("Boby Editado");
    datos.setPeso(6.0);
    datos.setFechaNacimiento("2021-05-10");

    servicioMascota.actualizarMascota(1L, datos);

    ArgumentCaptor<Mascota> captor = ArgumentCaptor.forClass(Mascota.class);
    verify(mascotaDaoMock).modificar(captor.capture());

    Mascota mascotaModificada = captor.getValue();
    assertEquals("Boby Editado", mascotaModificada.getNombre());
    assertEquals(6.0, mascotaModificada.getDatos().getPeso());
    assertNotNull(mascotaModificada.getDatos().getFechaNacimiento());
  }

  @Test
  public void dadoUnIdValidoCuandoEliminarMascotaLlamaAlDaoEliminar() {
    Mascota mascota = new Mascota();
    mascota.setId(1L);
    when(mascotaDaoMock.buscarPorId(1L)).thenReturn(mascota);

    servicioMascota.eliminarMascota(1L);

    assertFalse(mascota.getActivo());
    verify(mascotaDaoMock).modificar(mascota);
  }
}
