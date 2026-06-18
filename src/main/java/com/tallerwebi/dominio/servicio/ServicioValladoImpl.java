package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dao.ValladoDao;
import com.tallerwebi.dominio.modelo.Vallado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
    servicio
*/

@Service
@Transactional
public class ServicioValladoImpl implements ServicioVallado {

  private final ValladoDao valladoDao;

  public ServicioValladoImpl(ValladoDao valladoDao) {
    this.valladoDao = valladoDao;
  }

  @Override
  public void actualizarRadioValla(Long idMascota, Integer radioValla) {
    Vallado vallado = valladoDao.buscarPorMascota(idMascota);
    if (vallado != null) {
      vallado.setRadioMetros(radioValla);
      valladoDao.modificar(vallado);
    }
  }
}
