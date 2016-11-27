package org.masterupv.carloscupeiro.asteroides.entidades;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos on 09/11/2016.
 */

public class AlmacenPuntuacionesFicheroExtApl implements AlmacenPuntuaciones {
    private static String RUTA = Environment.getExternalStorageDirectory() + "/Android/data/org.example.asteroides/files/";
    private static String FICHERO = "carpeta/puntuaciones.txt";
    private Context context;
    public AlmacenPuntuacionesFicheroExtApl(Context context) {
        this.context = context;
    }
    public void guardarPuntuacion(int puntos, String nombre, long fecha){
        String stadoSD = Environment.getExternalStorageState();
        if (!stadoSD.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "No puedo escribir en la memoria externa", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            File ruta = new File(RUTA);
            if (!ruta.exists()) {
                ruta.mkdirs();
            }
            FileOutputStream f = new FileOutputStream(RUTA+FICHERO, true);
            String texto = puntos + " " + nombre + "\n";
            f.write(texto.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }
    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<String>();
        String stadoSD = Environment.getExternalStorageState();
        if (!stadoSD.equals(Environment.MEDIA_MOUNTED) && !stadoSD.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(context, "No puedo leer en la memoria externa", Toast.LENGTH_LONG).show();
            return result;
        }
        try {
            FileInputStream f = new FileInputStream(FICHERO);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(f));
            int n = 0;
            String linea;
            do {
                linea = entrada.readLine();
                if (linea != null) {
                    result.add(linea);
                    n++;
                }
            } while (n < cantidad && linea != null);
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return result;
    }
}
