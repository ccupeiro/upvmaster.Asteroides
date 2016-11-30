package org.masterupv.carloscupeiro.asteroides.entidades;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Carlos on 27/11/2016.
 */

public class AlmacenPuntuacionesGSon implements AlmacenPuntuaciones {
    private String string; //Almacena puntuaciones en formato JSON
    private Gson gson = new Gson();
    private static String PREFERENCIAS = "puntuaciones_json";
    private Context context;
    private Type type = new TypeToken<Clase>() {
    }.getType();

    public class Clase {
        private ArrayList<Puntuacion> puntuaciones = new ArrayList<>();
        private boolean guardado;
    }

    public AlmacenPuntuacionesGSon(Context context) {
        /*guardarPuntuacion(45000, "Mi nombre", System.currentTimeMillis());
        guardarPuntuacion(31000, "Otro nombre", System.currentTimeMillis());*/
        this.context = context;
    }

    private void guardarString(String string) {
        SharedPreferences preferencias =context.getSharedPreferences(
                PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("gson", string);
        editor.apply();
    }

    private String leerString() {
        SharedPreferences preferencias =context.getSharedPreferences( PREFERENCIAS, Context.MODE_PRIVATE);
        return preferencias.getString("gson", "");
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        string = leerString();
        Clase objeto = gson.fromJson(string, type);
        objeto.puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = gson.toJson(objeto, type);
        guardarString(string);
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        string = leerString();
        Clase objeto = gson.fromJson(string, type);
        List<String> salida = new ArrayList<>();
        for (Puntuacion puntuacion : objeto.puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }
        return salida;
    }
}
