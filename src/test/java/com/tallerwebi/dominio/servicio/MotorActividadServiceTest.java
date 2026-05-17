package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.dao.RangoVitalDao;
import com.tallerwebi.dominio.enums.EstadoMascota;
import com.tallerwebi.dominio.enums.TamanoMascota;
import com.tallerwebi.dominio.modelo.LecturaSensor;
import com.tallerwebi.dominio.modelo.Mascota;
import com.tallerwebi.dominio.modelo.RangoVitalPorTamano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MotorActividadServiceTest {

  private MotorActividadService motorActividadService;
  private RangoVitalDao rangoVitalDaoMock;

  private Mascota mascota;
  private RangoVitalPorTamano rango;

  @BeforeEach
  public void init() {
    rangoVitalDaoMock = mock(RangoVitalDao.class);
    motorActividadService = new MotorActividadService(rangoVitalDaoMock);

    motorActividadService.limpiarMemoria();

    mascota = new Mascota();
    mascota.setId(1L);
    mascota.setNombre("Toby");
    mascota.setTamano(TamanoMascota.MEDIANO);

    rango = new RangoVitalPorTamano();
    rango.setTamano(TamanoMascota.MEDIANO);
    rango.setFrecuenciaMinima(80);
    rango.setFrecuenciaMaxima(120);

    when(rangoVitalDaoMock.buscarPorTamano(TamanoMascota.MEDIANO)).thenReturn(rango);
  }

  @Test
  public void dadaUnaMascotaMedianaYUnaLecturaConBajoMovimientoCuandoAnalizoDevuelveDurmiendo() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(85);
    lectura.setAccelX(0.5);
    lectura.setAccelY(0.5);
    lectura.setAccelZ(0.5);
    lectura.setGyroX(0.2);
    lectura.setGyroY(0.2);
    lectura.setGyroZ(0.2);

    EstadoMascota estado = motorActividadService.analizar(mascota, lectura);

    assertThat(estado, equalTo(EstadoMascota.DURMIENDO));
  }

  @Test
  public void dadaUnaMascotaMedianaYUnaLecturaConActividadLeveCuandoAnalizoDevuelveReposo() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(95);
    lectura.setAccelX(2.0);
    lectura.setAccelY(2.0);
    lectura.setAccelZ(1.5);
    lectura.setGyroX(1.0);
    lectura.setGyroY(0.8);
    lectura.setGyroZ(0.7);

    EstadoMascota estado = motorActividadService.analizar(mascota, lectura);

    assertThat(estado, equalTo(EstadoMascota.REPOSO));
  }

  @Test
  public void dadaUnaMascotaMedianaYUnaLecturaConActividadModeradaCuandoAnalizoDevuelveCaminando() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(108);
    lectura.setAccelX(3.5);
    lectura.setAccelY(3.0);
    lectura.setAccelZ(2.5);
    lectura.setGyroX(1.8);
    lectura.setGyroY(1.6);
    lectura.setGyroZ(1.5);

    EstadoMascota estado = motorActividadService.analizar(mascota, lectura);

    assertThat(estado, equalTo(EstadoMascota.CAMINANDO));
  }

  @Test
  public void dadaUnaMascotaMedianaYUnaLecturaConActividadAltaCuandoAnalizoDevuelveCorriendo() {
    LecturaSensor lectura = new LecturaSensor();
    lectura.setFrecuenciaCardiaca(118);
    lectura.setAccelX(6.0);
    lectura.setAccelY(5.5);
    lectura.setAccelZ(4.5);
    lectura.setGyroX(3.0);
    lectura.setGyroY(2.8);
    lectura.setGyroZ(2.5);

    EstadoMascota estado = motorActividadService.analizar(mascota, lectura);

    assertThat(estado, equalTo(EstadoMascota.CORRIENDO));
  }

  @Test
  public void noDebePermitirSaltoBruscoDeDormidoACorriendo() {
    LecturaSensor dormido = new LecturaSensor();
    dormido.setFrecuenciaCardiaca(80);
    dormido.setAccelX(0.1);
    dormido.setAccelY(0.1);
    dormido.setAccelZ(0.1);
    dormido.setGyroX(0.1);
    dormido.setGyroY(0.1);
    dormido.setGyroZ(0.1);

    LecturaSensor corridaBrusca = new LecturaSensor();
    corridaBrusca.setFrecuenciaCardiaca(130);
    corridaBrusca.setAccelX(10.0);
    corridaBrusca.setAccelY(10.0);
    corridaBrusca.setAccelZ(10.0);
    corridaBrusca.setGyroX(6.0);
    corridaBrusca.setGyroY(6.0);
    corridaBrusca.setGyroZ(6.0);

    motorActividadService.analizar(mascota, dormido);
    EstadoMascota segundoEstado = motorActividadService.analizar(mascota, corridaBrusca);

    assertThat(segundoEstado, equalTo(EstadoMascota.DURMIENDO));
  }
}
