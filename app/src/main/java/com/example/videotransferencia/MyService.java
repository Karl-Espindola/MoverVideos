package com.example.videotransferencia;


import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyService extends Service {
    private boolean isServiceRunning = false;
    private Handler handler;
    private Runnable tarea;
    File internalStorageDir;

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
        String pathSDCard = System.getenv("SECONDARY_STORAGE"); // LA VERDADERA RUTA A LA SD
        File rutaSD = new File(pathSDCard);
        //Uri sdCardRoot = DocumentFile.fromFile(Environment.getExternalStorageDirectory()).getUri();
        Uri sdCardRoot = DocumentFile.fromFile(rutaSD).getUri();

        DocumentFile uriSDcard = DocumentFile.fromSingleUri(this, sdCardRoot);
        internalStorageDir = Environment.getExternalStorageDirectory();


        /*DocumentFile uriSDcardFinal;

        if(uriSDcard.findFile("SD") == null){
            uriSDcardFinal = uriSDcard.createDirectory("SD");
        }else{
            Uri uriSDcard_1 = DocumentFile.fromFile(new File(pathSDCard+"SD")).getUri();
            DocumentFile uriSDcard_2 = DocumentFile.fromSingleUri(this, uriSDcard_1);
            uriSDcardFinal = uriSDcard_2;
        }*/

        //String folderDestino = pathSDCard+File.separator+"SD";
        //File folder = new File(uriSDcard.getUri().getPath());
        tarea = new Runnable() {
            //File[] lista = folder.listFiles();
            File[] files = internalStorageDir.listFiles();
            int cont = 0;
            @Override
            public void run() {
                if(files != null){
                    for( File file : files){
                        if(file.getName().endsWith("mp4")){
                            Log.d("NombreArchivo", file.getName());

                            //moverArchivo(folderDestino, file);
                            moverArchivo(uriSDcard, file);
                        }
                    }
                }else{
                    Log.i("validación", "Array files vacio");
                }

                /*for(File item : lista){
                    Log.i("Lista SD", item.getName());
                }*/

                handler.postDelayed(this, 10*1000); // ejecutar lo que esta dentro del run cada 1 segundos
            }
        };
        handler.post(tarea);
    }

    private int moverArchivo(DocumentFile sdCardPath, File file){
        if(file == null){
            return 0;
        }

        try {
            //File destinationFile = new File(sdCardPath, file.getName());
            //DocumentFile newFile = sdCardPath.createFile("*/*", file.getName());

            // Crear streams de entrada y salida
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(sdCardPath.getUri().getPath()+"/SD/"+file.getName());
            //FileOutputStream outputStream = (FileOutputStream) getContentResolver().openOutputStream(newFile.getUri());

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

            //borrar el archivo despues de copiarlo
            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
            // Manejar cualquier error que pueda ocurrir durante la copia
        }
        return 1;
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