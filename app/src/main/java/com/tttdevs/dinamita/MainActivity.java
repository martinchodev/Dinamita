package com.tttdevs.dinamita;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    ImageView img;
    SensorManager senSensorManager;
    Sensor senAccelerometer;
    long lastUpdate = 0;
    float last_x, last_y, last_z;
    static final int SHAKE_THRESHOLD = 50;
    boolean gameover = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.imagen);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

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

                        // Cambio el estado de gameover a verdadero
                        gameover = true;
                    }

                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            }
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (senSensorManager != null) {
            senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        senSensorManager.unregisterListener(this);
        super.onStop();
    }
}
