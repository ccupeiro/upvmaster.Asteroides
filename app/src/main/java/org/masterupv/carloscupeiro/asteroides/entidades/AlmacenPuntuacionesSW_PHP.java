package org.masterupv.carloscupeiro.asteroides.entidades;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by carlos.cupeiro on 30/11/2016.
 */

public class AlmacenPuntuacionesSW_PHP implements AlmacenPuntuaciones {
    private String ruta_server;


    public AlmacenPuntuacionesSW_PHP(String ruta_server){
        this.ruta_server = ruta_server;
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        HttpURLConnection conexion=null;
        try {
            URL url=new URL(ruta_server+"/nueva.php?"
                    + "puntos="+ puntos
                    + "&nombre="+ URLEncoder.encode(nombre, "UTF-8")
                    + "&fecha=" + fecha);
            conexion = (HttpURLConnection) url
                    .openConnection();
            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                if (!linea.equals("OK")) {
                    Log.e("Asteroides","Error en servicio Web nueva");
                }
            } else {
                Log.e("Asteroides", conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        } finally {
            if (conexion!=null) conexion.disconnect();
        }
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<String>();
        HttpURLConnection conexion=null;
        try {
            URL url=new URL(ruta_server+"/lista.php"
                    + "?max=20");
            conexion = (HttpURLConnection) url
                    .openConnection();
            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                while (!linea.equals("")) {
                    result.add(linea);
                    linea = reader.readLine();
                }
                reader.close();
            } else {
                Log.e("Asteroides", conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        } finally {
            if (conexion!=null) conexion.disconnect();
            return result;
        }
    }
}
