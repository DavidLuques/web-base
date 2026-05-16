package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.dao.MascotaDao;
import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.dto.ResultadoSimulacionDto;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimulacionActividadServiceTest {

  private SimulacionActividadService simulacionActividadService;

  private MascotaDao mascotaDaoMock;
  private RangoVitalDao rangoVitalDaoMock;
  private SimuladorCollarService simuladorCollarServiceMock;
  private MotorActividadService motorActividadServiceMock;

  @BeforeEach
  public void init() {
    mascotaDaoMock = mock(MascotaDao.class);
    rangoVitalDaoMock = mock(RangoVitalDao.class);
    simuladorCollarServiceMock = mock(SimuladorCollarService.class);
    motorActividadServiceMock = mock(MotorActividadService.class);

    simulacionActividadService =
      new SimulacionActividadService(
        mascotaDaoMock,
        rangoVitalDaoMock,
        simuladorCollarServiceMock,
        motorActividadServiceMock
      );
  }

  @Test
  public void debeSimularYPersistirEstadoDeMascota() {
    Long mascotaId = 1L;

    Mascota mascota = new Mascota();
    mascota.setNombre("Toby");
    mascota.setTamano(TamanoMascota.MEDIANO);

    RangoVitalPorTamano rango = new RangoVitalPorTamano();
    rango.setFrecuenciaMinima(80);
    rango.setFrecuenciaMaxima(120);

    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(85);

    when(mascotaDaoMock.buscarPorId(mascotaId)).thenReturn(mascota);
    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);
    when(simuladorCollarServiceMock.generarLectura(80, 120)).thenReturn(lectura);
    when(motorActividadServiceMock.analizar(mascota, lectura)).thenReturn(EstadoMascota.REPOSO);

    ResultadoSimulacionDto resultado = simulacionActividadService.simularDetalle(mascotaId);

    assertThat(resultado.getEstado(), equalTo(EstadoMascota.REPOSO));

    verify(mascotaDaoMock).modificar(mascota);
    assertThat(mascota.getEstadoActual(), equalTo(EstadoMascota.REPOSO));
  }
}
