package com.tallerwebi.dominio.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioAlerta;
import com.tallerwebi.dominio.dto.AlertaDto;
import com.tallerwebi.dominio.enums.TipoAlerta;
import com.tallerwebi.dominio.modelo.Alerta;
import com.tallerwebi.dominio.modelo.Mascota;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioAlertaTest {

  private RepositorioAlerta repositorioAlertaMock;
  private ServicioAlerta servicioAlerta;
  private Mascota mascota;

  @BeforeEach
  public void init() {
    repositorioAlertaMock = mock(RepositorioAlerta.class);
    servicioAlerta = new ServicioAlertaImpl(repositorioAlertaMock);

    mascota = new Mascota();
    mascota.setNombre("Firulais");
  }

  @Test
  void debeCrearAlertaConLosDatosCorrectos() {
    servicioAlerta.crearAlerta(mascota, TipoAlerta.EMERGENCIA, "Mensaje de prueba");

    ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
    verify(repositorioAlertaMock).save(captor.capture());

    assertEquals(TipoAlerta.EMERGENCIA, captor.getValue().getTipo());
    assertEquals("Mensaje de prueba", captor.getValue().getMensaje());
    assertEquals(mascota, captor.getValue().getMascota());
    assertEquals(false, captor.getValue().getLeido());
  }

  @Test
  void debeRetornarListaVaciaSiIdMascotaEsNull() {
    List<AlertaDto> resultado = servicioAlerta.obtenerAlertasPorMascota(null);
    assertEquals(0, resultado.size());
    verify(repositorioAlertaMock, never()).buscarPorMascota(any());
  }

  @Test
  void debeRetornarAlertasMapeadasComoDto() {
    Alerta alerta1 = new Alerta();
    alerta1.setTipo(TipoAlerta.ALERTA);
    alerta1.setMensaje("Mensaje 1");

    Alerta alerta2 = new Alerta();
    alerta2.setTipo(TipoAlerta.EMERGENCIA);
    alerta2.setMensaje("Mensaje 2");

    when(repositorioAlertaMock.buscarPorMascota(1L)).thenReturn(Arrays.asList(alerta1, alerta2));

    List<AlertaDto> resultado = servicioAlerta.obtenerAlertasPorMascota(1L);

    assertEquals(2, resultado.size());
    assertEquals(TipoAlerta.ALERTA, resultado.get(0).getTipo());
    assertEquals("Mensaje 1", resultado.get(0).getMensaje());
    assertEquals(TipoAlerta.EMERGENCIA, resultado.get(1).getTipo());
    assertEquals("Mensaje 2", resultado.get(1).getMensaje());
  }
}
