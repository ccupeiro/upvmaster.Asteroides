package org.masterupv.carloscupeiro.asteroides.entidades;

import java.util.List;

/**
 * Created by carlos.cupeiro on 03/10/2016.
 */

public interface AlmacenPuntuaciones {
    public void guardarPuntuacion(int puntos,String nombre,long fecha);
    public List<String> listaPuntuaciones(int cantidad);
}
