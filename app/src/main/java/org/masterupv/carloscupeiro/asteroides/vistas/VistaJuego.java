package org.masterupv.carloscupeiro.asteroides.vistas;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.masterupv.carloscupeiro.asteroides.R;
import org.masterupv.carloscupeiro.asteroides.entidades.Grafico;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos.cupeiro on 05/10/2016.
 */

public class VistaJuego extends View implements SensorEventListener {
    private boolean musica_activa = true;
    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private ThreadJuego thread;
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;
    //DRAWABLES
    Drawable drawableNave, drawableAsteroide, drawableMisil;
    // //// NAVE //////
    private Grafico nave; // Gráfico de la nave
    private int giroNave; // Incremento de dirección
    private double aceleracionNave; // aumento de velocidad
    private static final int MAX_VELOCIDAD_NAVE = 20;
    // Incremento estándar de giro y aceleración
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;
    // //// ASTEROIDES //////
    private List<Grafico> asteroides; // Vector con los Asteroides
    private int numAsteroides = 5; // Número inicial de asteroides
    private int numFragmentos = 3; // Fragmentos en que se divide
    // //// MISIL //////
    private List<Grafico> misiles;
    private static int PASO_VELOCIDAD_MISIL = 12;
    private List<Integer> tiempoMisiles;
    // //// TACTIL //////
    private float mX=0, mY=0;
    private boolean disparo=false;
    // //// SENSORES //////
    private boolean hayValorInicial_giro = false;
    private float valorInicial_giro;
    private boolean hayValorInicial_aceleracion = false;
    private float valorInicial_aceleracion;
    // //// MULTIMEDIA //////
    SoundPool soundPool;
    int idDisparo, idExplosion;
    View viewVistaJuego;

    public VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewVistaJuego = this;
        if(thread== null || !thread.isAlive()){
            thread = new ThreadJuego();
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (pref.getString("graficos", "1").equals("0")) {
            //Asteroide
            Path pathAsteroide = new Path();
            pathAsteroide.moveTo((float) 0.3, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.3);
            pathAsteroide.lineTo((float) 0.8, (float) 0.2);
            pathAsteroide.lineTo((float) 1.0, (float) 0.4);
            pathAsteroide.lineTo((float) 0.8, (float) 0.6);
            pathAsteroide.lineTo((float) 0.9, (float) 0.9);
            pathAsteroide.lineTo((float) 0.8, (float) 1.0);
            pathAsteroide.lineTo((float) 0.4, (float) 1.0);
            pathAsteroide.lineTo((float) 0.0, (float) 0.6);
            pathAsteroide.lineTo((float) 0.0, (float) 0.2);
            pathAsteroide.lineTo((float) 0.3, (float) 0.0);
            ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(pathAsteroide, 1, 1));
            dAsteroide.getPaint().setColor(Color.WHITE);
            dAsteroide.getPaint().setStyle(Paint.Style.STROKE);
            dAsteroide.setIntrinsicWidth(50);
            dAsteroide.setIntrinsicHeight(50);
            drawableAsteroide = dAsteroide;
            //Nave
            Path pathNave = new Path();
            pathNave.lineTo((float) 1.5, (float) 0.5);
            pathNave.lineTo((float) 0.0, (float) 1.0);
            pathNave.lineTo((float) 0.0, (float) 0.0);
            ShapeDrawable dNave = new ShapeDrawable(new PathShape(pathNave, 1, 1));
            dNave.getPaint().setColor(Color.WHITE);
            dNave.getPaint().setStyle(Paint.Style.STROKE);
            dNave.setIntrinsicWidth(20);
            dNave.setIntrinsicHeight(15);
            drawableNave = dNave;
            //Misil
            ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
            dMisil.getPaint().setColor(Color.WHITE);
            dMisil.getPaint().setStyle(Paint.Style.STROKE);
            dMisil.setIntrinsicWidth(15);
            dMisil.setIntrinsicHeight(3);
            drawableMisil = dMisil;
            //Global
            setBackgroundColor(Color.BLACK);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            //Asteroide
            drawableAsteroide = ContextCompat.getDrawable(getContext(), R.drawable.asteroide1);
            //Nave
            drawableNave = ContextCompat.getDrawable(getContext(), R.drawable.nave);
            //Misil
            ImageView misilImage = new ImageView(context);
            misilImage.setBackgroundResource(R.drawable.misil_animado);
            drawableMisil = misilImage.getBackground();
            //Global
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        nave = new Grafico(this, drawableNave);
        asteroides = new ArrayList<Grafico>();
        misiles = new ArrayList<Grafico>();
        tiempoMisiles = new ArrayList<Integer>();
        for (int i = 0; i < numAsteroides; i++) {
            Grafico asteroide = new Grafico(this, drawableAsteroide);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngulo((int) (Math.random() * 360));
            asteroide.setRotacion((int) (Math.random() * 8 - 4));
            asteroides.add(asteroide);
        }
        //Musica
        if(pref.getBoolean("musica",true)){
            musica_activa = true;
            soundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC , 0);
            idDisparo = soundPool.load(context, R.raw.disparo, 0);
            idExplosion = soundPool.load(context, R.raw.explosion, 0);
        }else{
            musica_activa = false;
        }


    }

    protected synchronized void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return; // Salir si el período de proceso no se ha cumplido.
        }
        // Para una ejecución en tiempo real calculamos retardo
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez
        // Actualizamos velocidad y dirección de la nave a partir de // giroNave y aceleracionNave (según la entrada del jugador)
        nave.setAngulo((int) (nave.getAngulo() + giroNave * retardo));
        double nIncX = nave.getIncX() + aceleracionNave * Math.cos(Math.toRadians(nave.getAngulo())) * retardo;
        double nIncY = nave.getIncY() + aceleracionNave * Math.sin(Math.toRadians(nave.getAngulo())) * retardo;
        // Actualizamos si el módulo de la velocidad no excede el máximo
        if (Math.hypot(nIncX, nIncY) <= MAX_VELOCIDAD_NAVE) {
            nave.setIncX(nIncX);
            nave.setIncY(nIncY);
        }
        //actualizamos posición nave
        nave.incrementaPos(retardo);
        // Actualizamos posición asteroides
        for (Grafico asteroide : asteroides) {
            asteroide.incrementaPos(retardo);
        }
        // Actualizamos posición misil
        List<Integer> misil_borrar = new ArrayList<Integer>();
        int pos_misil = 0;
        for (Grafico misil : misiles) {
            misil.incrementaPos(retardo);
            tiempoMisiles.set(pos_misil, (int)(tiempoMisiles.get(pos_misil)-retardo));
            if (tiempoMisiles.get(pos_misil) < 0) {
                misil_borrar.add(pos_misil);
            } else {
                for (int i = 0; i < asteroides.size(); i++)
                    if (misil.verificaColision(asteroides.get(i))) {
                        destruyeAsteroide(i);
                        misil_borrar.add(pos_misil);
                        break;
                    }
            }
            pos_misil++;
        }
        synchronized (misil_borrar) {
            for (int pos = misil_borrar.size() - 1; pos >= 0; pos--) {
                borrar_misil(pos);
            }
        }
    }

    private synchronized void borrar_misil(int i){
        tiempoMisiles.remove(i);
        misiles.remove(i);
    }

    private void destruyeAsteroide(int i) {
        synchronized(asteroides) {
            if(musica_activa){
                soundPool.play(idExplosion, 1, 1, 0, 0, 1.5f);
            }
            asteroides.remove(i);
        }
    }
    private void activaMisil() {
        if(musica_activa){
            soundPool.play(idDisparo, 1, 1, 1, 0, 2.0f);
        }
        Grafico misil = new Grafico(this, drawableMisil);
        misil.setCenX(nave.getCenX());
        misil.setCenY(nave.getCenY());
        misil.setAngulo(nave.getAngulo());
        misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
        misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
        misiles.add(misil);
        int tiempoMisil = (int) Math.min(this.getWidth() / Math.abs( misil. getIncX()), this.getHeight() / Math.abs(misil.getIncY())) - 2;
        tiempoMisiles.add(tiempoMisil);
    }

    // MANEJO TACTIL

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                disparo=true;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dy<10 && dx>10){
                    giroNave = Math.round((x - mX) / 2);
                    disparo = false;
                } else if (dx<10 && dy>10){
                    aceleracionNave = Math.abs(Math.round((mY - y) / 40));
                    disparo = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                giroNave = 0;
                aceleracionNave = 0;
                if (disparo){
                    activaMisil();
                }
                break;
        }
        mX=x; mY=y;
        return true;
    }

    //SENSORES

    public void activarSensores(){
        SensorManager mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors = mSensorManager.getSensorList( Sensor.TYPE_ACCELEROMETER);
        if (!listSensors.isEmpty()) {
            Sensor acelerometerSensor = listSensors.get(0);
            mSensorManager.registerListener(this, acelerometerSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void desactivarSensores(){
        SensorManager mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                //VELOCIDAD
                float valor_acelerar = event.values[1];
                if (!hayValorInicial_aceleracion){
                    valorInicial_aceleracion = valor_acelerar;
                    hayValorInicial_aceleracion = true;
                }
                aceleracionNave = Math.round(((valor_acelerar-valorInicial_aceleracion) / 10));
                //GIRO
                float valor_giro = event.values[0];
                if (!hayValorInicial_giro){
                    valorInicial_giro = valor_giro;
                    hayValorInicial_giro = true;
                }
                giroNave=(int) (valor_giro-valorInicial_giro) ;
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //TECLADO FISICO

    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
        super.onKeyDown(codigoTecla, evento);
        // Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = +PASO_ACELERACION_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                giroNave = -PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = +PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                activaMisil();
                break;
            default:
                // Si estamos aquí, no hay pulsación que nos interese
                procesada = false;
                break;
        }
        return procesada;
    }

    @Override public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);
        // Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = 0;
                break;
            default:
                // Si estamos aquí, no hay pulsación que nos interese
                procesada = false;
                break;
        }
        return procesada;
    }

    // CAMBIOS GENERALES

    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        // Una vez que conocemos nuestro ancho y alto.
        //Colocar la nave en el centro
        nave.setCenX(ancho / 2);
        nave.setCenY(alto / 2);
        for (Grafico asteroide : asteroides) {
            do {
                asteroide.setCenX((int) (Math.random() * ancho));
                asteroide.setCenY((int) (Math.random() * alto));
            } while (asteroide.distancia(nave) < (ancho + alto) / 5);
        }
        ultimoProceso = System.currentTimeMillis();
        if(!thread.isAlive()){
            thread.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized(asteroides){
            for (Grafico asteroide : asteroides) {
                asteroide.dibujaGrafico(canvas);
            }
        }
        nave.dibujaGrafico(canvas);
        synchronized(misiles){
            for (Grafico misil : misiles) {
                if (misil.getDrawable() instanceof AnimationDrawable) {
                    ((AnimationDrawable) drawableMisil).start();
                }
                misil.dibujaGrafico(canvas);
            }
        }
    }

    // HILOS

    public ThreadJuego getThread() {
        return thread;
    }

    public class ThreadJuego extends Thread {
        private boolean pausa,corriendo;
        public synchronized void pausar() {
            pausa = true;
        }
        public synchronized void reanudar() {
            pausa = false;
            notify();
        }
        public void detener() {
            corriendo = false;
            if (pausa) reanudar();
        }

        @Override
        public void run() {
            corriendo = true;
            while (corriendo) {
                actualizaFisica();
                synchronized (this) {
                    while (pausa) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }
}
