package com.example.videotransferencia;


import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyService extends Service {
    private boolean isServiceRunning = false;
    private Handler handler;
    private Runnable tarea;
    File internalStorageDir = getFilesDir();

    @Override
    public void onCreate() {

        super.onCreate();
        handler = new Handler();

        Log.w("Start", "Inicio el servicio");
        startLoop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceRunning = true;
        // Realiza cualquier tarea que desees cuando el servicio se inicie aquí.

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
        handler.removeCallbacks(tarea); // detiene el hilo asociado al objeto runnable
        //handler.removeCallbacks(runnable); //detiene todos los hilos asociados a un objeto runnable
        Log.e("Destroy","Se detuvo el Servicio");
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    public boolean isServiceRunning() {
        return isServiceRunning;
    }

    private void startLoop() {
        tarea = new Runnable() {
            File folder = new File(internalStorageDir.getPath());
            File[] files = folder.listFiles();
            int cont = 0;
            @Override
            public void run() {
                //Log.i("Contador","Iteración: "+cont);
                //cont++;

                for( File file : files){
                    Log.d("NombreArchivo", file.getName());
                }
                handler.postDelayed(this, 1000); // ejecutar lo que esta dentro del run cada 1 segundos
            }
        };
        handler.post(tarea);
    }



    private void transferVideos() {
        // Ruta de la carpeta de videos en la memoria interna
        String internalVideoPath = Environment.getExternalStorageDirectory() + "/Videos";

        // Ruta de la carpeta de videos en la tarjeta SD
        String sdCardVideoPath = Environment.getExternalStorageDirectory() + "/MyAppVideos";

        File internalVideoDirectory = new File(internalVideoPath);

        if (internalVideoDirectory.exists() && internalVideoDirectory.isDirectory()) {
            File[] videoFiles = internalVideoDirectory.listFiles();

            if (videoFiles != null) {
                for (File videoFile : videoFiles) {
                    // Crear objetos File para el archivo de origen y destino
                    File sourceFile = videoFile;
                    File destinationFile = new File(sdCardVideoPath, videoFile.getName());

                    try {
                        // Crear streams de entrada y salida
                        FileInputStream inputStream = new FileInputStream(sourceFile);
                        FileOutputStream outputStream = new FileOutputStream(destinationFile);

                        // Preparar un búfer para la transferencia
                        byte[] buffer = new byte[1024];
                        int length;

                        // Realizar la copia del archivo
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }

                        // Cerrar los streams después de la copia
                        inputStream.close();
                        outputStream.close();

                        // Si llegamos aquí, la copia fue exitosa

                    } catch (IOException e) {
                        e.printStackTrace();
                        // Manejar cualquier error que pueda ocurrir durante la copia
                    }
                }
            }
        }
    }
}