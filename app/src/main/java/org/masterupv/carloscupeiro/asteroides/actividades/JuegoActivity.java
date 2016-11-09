package org.masterupv.carloscupeiro.asteroides.actividades;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.masterupv.carloscupeiro.asteroides.R;
import org.masterupv.carloscupeiro.asteroides.vistas.VistaJuego;

public class JuegoActivity extends AppCompatActivity {

    private VistaJuego vistaJuego;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);
        //Con esto en a partir de la version 4.1 se oculta los botones
        // de navegacion y las barras
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
    }

    @Override protected void onPause() {
        super.onPause();
        vistaJuego.getThread().pausar();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getBoolean("sensores",false)){

            vistaJuego.desactivarSensores();
        }
    }
    @Override protected void onResume() {
        super.onResume();
        vistaJuego.getThread().reanudar();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getBoolean("sensores",false)){

            vistaJuego.activarSensores();
        }
    }
    @Override protected void onDestroy() {
        vistaJuego.getThread().detener();
        super.onDestroy();
    }
}
