package org.masterupv.carloscupeiro.asteroides.actividades;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.MediaPlayer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuaciones;
import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuacionesArray;
import org.masterupv.carloscupeiro.asteroides.R;
import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuacionesFicheroExtApl;
import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuacionesFicheroExterno;
import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuacionesFicheroInterno;
import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuacionesPreferencias;
import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuacionesRecursoAssets;
import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuacionesRecursoRaw;
import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuacionesSQLite;
import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuacionesSocket;
import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuacionesXML_SAX;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    public static final int COD_PLAY = 1234;
    private static final int SOLICITUD_PERMISO_ESCRITURA_EXTERNA = 0;

    static AlmacenPuntuaciones almacen;
    private GestureLibrary libreria;
    private MediaPlayer mp_general;

    private boolean musica_activa = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MediaPlayer mp_play = MediaPlayer.create(this, R.raw.play);
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        TextView tv_titulo = (TextView) findViewById(R.id.titulo);
        Animation anim_titulo = AnimationUtils.loadAnimation(this,R.anim.giro_con_zoom);
        tv_titulo.startAnimation(anim_titulo);
        Button btn_play = (Button) findViewById(R.id.btn_play);
        Animation anim_izq = AnimationUtils.loadAnimation(this,R.anim.entrada_izq);
        btn_play.startAnimation(anim_izq);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musica_activa){
                    mp_play.start();
                }
                lanzarPlay(null);
            }
        });
        Button btn_config = (Button) findViewById(R.id.btn_config);
        Animation anim_der = AnimationUtils.loadAnimation(this,R.anim.entrada_der);
        btn_config.startAnimation(anim_der);
        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarConfiguracion(null);
            }
        });
        Button btn_about = (Button) findViewById(R.id.btn_about);
        btn_about.startAnimation(anim_izq);
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarAcercaDe(v);
            }
        });
        Button btn_puntuaciones = (Button) findViewById(R.id.btn_scores);
        btn_puntuaciones.startAnimation(anim_der);
        btn_puntuaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarPuntuaciones(null);
            }
        });
        //Gestos
        libreria = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!libreria.load()) {
            finish();
        }
        GestureOverlayView gesturesView = (GestureOverlayView) findViewById(R.id.gestures);
        gesturesView.addOnGesturePerformedListener(this);
        //Musica
        mp_general = MediaPlayer.create(this, R.raw.audio_general);
    }

    void solicitarPermisoExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make (this.getCurrentFocus(), "", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SOLICITUD_PERMISO_ESCRITURA_EXTERNA);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SOLICITUD_PERMISO_ESCRITURA_EXTERNA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SOLICITUD_PERMISO_ESCRITURA_EXTERNA:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    almacen = new AlmacenPuntuacionesFicheroExterno(this);
                } else {
                    Snackbar.make(this.getCurrentFocus(), "No se puede utilizar guardar en el externo, se almacena en Prefrencias", Snackbar.LENGTH_SHORT).show();
                    almacen = new AlmacenPuntuacionesPreferencias(this);
                }
                break;
            default:
                finish();
                break;
        }
    }

    @Override protected void onSaveInstanceState(Bundle guardarEstado) {
        super.onSaveInstanceState(guardarEstado);
        if (mp_general != null) {
            int pos_actual_musica = mp_general.getCurrentPosition();
            guardarEstado.putInt("posicion", pos_actual_musica);
        }
    }
    @Override protected void onRestoreInstanceState(Bundle recEstado) {
        super.onRestoreInstanceState(recEstado);
        if (recEstado != null && mp_general != null) {
            int pos_actual_musica = recEstado.getInt("posicion");
            mp_general.seekTo(pos_actual_musica);
        }
    }

    private void guardadoPuntucaiones(SharedPreferences pref){
        int op_guardado;
        try{
            op_guardado = Integer.valueOf(pref.getString("puntuaciones", "1"));
        }catch(ClassCastException e){
            op_guardado = 1;
        }catch(NumberFormatException e){
            op_guardado = 1;
        }
        switch (op_guardado){
            case 0:
                almacen = new AlmacenPuntuacionesArray();
                break;
            case 1:
                almacen = new AlmacenPuntuacionesPreferencias(this);
                break;
            case 2:
                almacen = new AlmacenPuntuacionesFicheroInterno(this);
                break;
            case 3:
                //Pedir Permisos
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    almacen = new AlmacenPuntuacionesFicheroExterno(this);
                }else{
                    solicitarPermisoExternalStorage();
                }

                break;
            case 4:
                //Pedir Permisos
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    almacen = new AlmacenPuntuacionesFicheroExtApl(this);
                }else{
                    solicitarPermisoExternalStorage();
                }

                break;
            case 5:
                almacen = new AlmacenPuntuacionesRecursoRaw(this);
                break;
            case 6:
                almacen = new AlmacenPuntuacionesRecursoAssets(this);
                break;
            case 7:
                almacen = new AlmacenPuntuacionesXML_SAX(this);
                break;
            case 8:
                almacen = new AlmacenPuntuacionesSQLite(this);
                break;
            case 9:
                almacen = new AlmacenPuntuacionesSocket();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        guardadoPuntucaiones(pref);
        if(pref.getBoolean("musica",true)){
            musica_activa = true;
            mp_general.start();
        }else{
            musica_activa = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(musica_activa)
            mp_general.pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_pref:
                lanzarConfiguracion(null);
                return true;
            case R.id.menu_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void lanzarPlay(View view){
        Intent i = new Intent(this, JuegoActivity.class);
        startActivityForResult(i,COD_PLAY);
    }

    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }
    public void lanzarConfiguracion(View view){
        Intent intent = new Intent(this,PreferenciasActivity.class);
        startActivity(intent);
    }
    public void mostrarPreferencias(){
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String s = "música: " + pref.getBoolean("musica",true)
                +", gráficos: " + pref.getString("graficos","?")
                +", fragmentos: " + pref.getString("fragmentos","?")
                +", Multijugador activo: " + pref.getBoolean("multijugador",false)
                +", Max jugadores: " + pref.getString("max_jugadores","?")
                +", Tipo Conexión: " + pref.getString("conexion","?");
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    public void lanzarPuntuaciones(View view){
        Intent i = new Intent(this, PuntuacionesActivity.class);
        startActivity(i);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions=libreria.recognize(gesture);
        if (predictions.size()>0) {
            String comando = predictions.get(0).name;
            if (comando.equals("jugar")){
                lanzarPlay(null);
            } else if (comando.equals("configurar")){
                lanzarConfiguracion(null);
            } else if (comando.equals("acerca_de")){
                lanzarAcercaDe(null);
            } else if (comando.equals("salir")){
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==COD_PLAY && resultCode==RESULT_OK && data!=null) {
            final int puntuacion = data.getExtras().getInt("puntuacion");
            //TODO poner la captura del nombre

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pon tu nombre");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String nombre = input.getText().toString();
                    almacen.guardarPuntuacion(puntuacion, nombre,
                            System.currentTimeMillis());
                    lanzarPuntuaciones(null);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }
}
