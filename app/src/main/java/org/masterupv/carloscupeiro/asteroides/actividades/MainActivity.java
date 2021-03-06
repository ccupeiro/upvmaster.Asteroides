package org.masterupv.carloscupeiro.asteroides.actividades;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuaciones;
import org.masterupv.carloscupeiro.asteroides.entidades.AlmacenPuntuacionesArray;
import org.masterupv.carloscupeiro.asteroides.R;

public class MainActivity extends AppCompatActivity{
    static AlmacenPuntuaciones almacen;
    private MediaPlayer mp_general;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MediaPlayer mp_play = MediaPlayer.create(this, R.raw.play);
        almacen = new AlmacenPuntuacionesArray();
        TextView tv_titulo = (TextView) findViewById(R.id.titulo);
        Animation anim_titulo = AnimationUtils.loadAnimation(this,R.anim.giro_con_zoom);
        tv_titulo.startAnimation(anim_titulo);
        Button btn_play = (Button) findViewById(R.id.btn_play);
        Animation anim_izq = AnimationUtils.loadAnimation(this,R.anim.entrada_izq);
        btn_play.startAnimation(anim_izq);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp_play.start();
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

        //Musica
        mp_general = MediaPlayer.create(this, R.raw.audio_general);
        mp_general.start();
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

    @Override
    protected void onResume() {
        super.onResume();
        mp_general.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        startActivity(i);
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
}
