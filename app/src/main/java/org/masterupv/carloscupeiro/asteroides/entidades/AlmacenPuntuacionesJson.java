package org.masterupv.carloscupeiro.asteroides.entidades;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by carlos.cupeiro on 30/11/2016.
 */

public class AlmacenPuntuacionesJSon implements AlmacenPuntuaciones {
    private static String PREFERENCIAS = "puntuaciones_json";
    private Context context;
    public AlmacenPuntuacionesJSon(Context context) {
        /*guardarPuntuacion(45000, "Mi nombre", System.currentTimeMillis());
        guardarPuntuacion(31000, "Otro nombre", System.currentTimeMillis());*/
        this.context= context;
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        String string = leerFichero();
        List<Puntuacion> puntuaciones = leerJSon(string);
        puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = guardarJSon(puntuaciones);
        guardarString(string);
    }

    private void guardarString(String string) {
        SharedPreferences preferencias =context.getSharedPreferences(
                PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("json", string);
        editor.apply();
    }

    private String leerFichero() {
        SharedPreferences preferencias =context.getSharedPreferences( PREFERENCIAS, Context.MODE_PRIVATE);
        return preferencias.getString("json", "");
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        String string = leerFichero();
        List<Puntuacion> puntuaciones = leerJSon(string);
        Vector<String> salida = new Vector<>();
        for (Puntuacion puntuacion : puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }
        return salida;
    }

    
    
    private String guardarJSon(List<Puntuacion> puntuaciones) {
        String string = "";
        try {
            JSONArray jsonArray = new JSONArray();
            for (Puntuacion puntuacion : puntuaciones) {
                JSONObject objeto = new JSONObject();
                objeto.put("puntos", puntuacion.getPuntos());
                objeto.put("nombre", puntuacion.getNombre());
                objeto.put("fecha", puntuacion.getFecha());
                jsonArray.put(objeto);
            }
            string = jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    private List<Puntuacion> leerJSon(String string) {
        List<Puntuacion> puntuaciones = new ArrayList<>();
        try {
            JSONArray json_array = new JSONArray(string);
            for (int i = 0; i < json_array.length(); i++) {
                JSONObject objeto = json_array.getJSONObject(i);
                puntuaciones.add(new Puntuacion(objeto.getInt("puntos"), objeto.getString("nombre"), objeto.getLong("fecha")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return puntuaciones;
    }
}
