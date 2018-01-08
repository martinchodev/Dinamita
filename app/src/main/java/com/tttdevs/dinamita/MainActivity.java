package com.tttdevs.dinamita;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    ImageView img;
    SensorManager senSensorManager;
    Sensor senAccelerometer;
    long lastUpdate = 0;
    float last_x, last_y, last_z;
    static final int SHAKE_THRESHOLD = 50;
    boolean gameover = false;
    MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.imagen);

        // Declaro el SensorManager
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Comportamiento al ahcer clic en la imágen
        img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                // Vuelvo a poner la imágen inicial
                img.setImageDrawable(getDrawable(R.drawable.dinamita));

                // Reinicio el estado del juego
                gameover = false;
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // Sólo procedo si el juego estça "activo", es decir, no se detonó.
        if (!gameover) {
            // Obtengo el sensor a partir del evento recibido
            Sensor mySensor = sensorEvent.sensor;

            // ¿Viene del acelerómetro?
            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                // Extraigo las 3 coordenadas
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                // Guardo el tiempo actual
                long currentTime = System.currentTimeMillis();

                // ¿Pasó más de una décima de segundo desde la última lectura?
                if ((currentTime - lastUpdate) > 100) {

                    // Lógica y cálculos, bla bla bla
                    long diffTime = (currentTime - lastUpdate);
                    lastUpdate = currentTime;
                    float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                    // ¿El teléfono se movió más rápido que el límite que definimos en SHAKE_THRESHOLD?
                    if (speed > SHAKE_THRESHOLD) {
                        // Cambio la imágen por la de Game Over
                        img.setImageDrawable(getDrawable(R.drawable.gameover));

                        // Vibrar medio segundo.
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (v != null) {
                            v.vibrate(500);
                        }

                        // Reproducir sonido
                        reproducirSonido("android.resource://com.tttdevs.dinamita/raw/boom");

                        // Cambio el estado de gameover a verdadero
                        gameover = true;
                    }

                    // Guardo las coordenadas actuales para comparar con las próximas lecturas
                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            }
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // No se usa pero tiene que estar declarado
    }

    // Este método se llama cuando la aplicación comienza a funcionar, sea cuando se abre por
    // primera vez como cuando se sale y vuelve a entrar cambiando entre aplicaciones.
    @Override
    protected void onResume() {
        super.onResume();
        if (senSensorManager != null) {
            // Necesario para que la aplicación reciba la información del acelerómetro
            senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        // Cuando no voy a dejar de
        senSensorManager.unregisterListener(this);
        super.onStop();
    }

    // Método que recibe una ruta a un archivo de audio
    // e inicializa mPlayer para que lo reproduzca
    private void reproducirSonido(String ruta) {

        // Creo un objeto de tipo Uri con la ruta al archivo mp3 recibida como argumento.
        // Más adelante voy a usar esta Uri como argumento de un método de mPlayer.
        Uri uriMp3 = Uri.parse(ruta);

        // Creo una instancia de MediaPlayer
        mPlayer = new MediaPlayer();

        // Asigno onCompletionListener
        mPlayer.setOnCompletionListener(onCompletionListener);

        // Los siguientes 2 métodos de mPlayer requieren que se utilice try/catch
        try {
            // Le digo a mPlayer cuál es el archivo que va a reproducir
            mPlayer.setDataSource(getApplicationContext(), uriMp3);

            // Preparo mPlayer para la reproducción (buffer, etc)
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reproduzco el sonido
        mPlayer.start();

        // Cuando finaliza la reproducción, se llama al onCompletionListener
    }

    // Este listener va a ser llamado cada vez que finalice la reproducción de un osnido
    // para liberar los recursos del MediaPlayer
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.release();
            mPlayer = null;
        }
    };
}
