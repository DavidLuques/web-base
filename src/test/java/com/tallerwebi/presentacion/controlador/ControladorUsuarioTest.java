package com.tallerwebi.presentacion.controlador;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ControladorUsuarioTest {

    private ControladorUsuario controlador;
    private ServicioUsuario servicioMock;

    @BeforeEach
    public void init() {
        servicioMock = mock(ServicioUsuario.class);
        controlador = new ControladorUsuario(servicioMock);
    }

    @Test
    public void queAlPedirUnPerfilSeDevuelvaElUsuarioCorrecto() {
        Long idBuscado = 1L;
        Usuario usuarioSimulado = new Usuario();
        when(servicioMock.obtenerPerfil(idBuscado)).thenReturn(usuarioSimulado);

        Usuario resultado = controlador.verPerfil(idBuscado); 

        assertEquals(usuarioSimulado, resultado, "El controlador debe devolver el mismo usuario que le entrega el servicio");
        verify(servicioMock, times(1)).obtenerPerfil(idBuscado);
    }

    @Test
    public void queAlEliminarUnaCuentaSeLlameAlServicioYDevuelvaMensajeDeExito() {
        Long idAEliminar = 1L;

        String resultado = controlador.eliminarCuenta(idAEliminar);

        assertEquals("La cuenta ha sido desactivada exitosamente", resultado);
        verify(servicioMock, times(1)).eliminar(idAEliminar);
    }
}