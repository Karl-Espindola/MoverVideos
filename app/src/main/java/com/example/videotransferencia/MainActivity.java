package com.example.videotransferencia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button startServiceButton;
    private Button stopServiceButton;
    private Intent serviceIntent;
    private MyService myService;
    private boolean isServiceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startServiceButton = findViewById(R.id.startServiceButton);
        stopServiceButton = findViewById(R.id.stopServiceButton);
        serviceIntent = new Intent(this, MyService.class);

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isServiceRunning) {
                    startService(serviceIntent);
                    isServiceRunning = true;
                    startServiceButton.setEnabled(false);
                    stopServiceButton.setEnabled(true);
                }
            }
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServiceRunning) {
                    stopService(serviceIntent);
                    isServiceRunning = false;
                    startServiceButton.setEnabled(true);
                    stopServiceButton.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        pedirPermisos();
        myService = new MyService();
        isServiceRunning = myService.isServiceRunning();
        updateUI();
    }

    private void updateUI() {
        if (isServiceRunning) {
            startServiceButton.setEnabled(false);
            stopServiceButton.setEnabled(true);
        } else {
            startServiceButton.setEnabled(true);
            stopServiceButton.setEnabled(false);
        }
    }
    private void pedirPermisos(){
        // Verifica si se tiene permiso de escritura en almacenamiento externo
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no se tiene permiso, solicita el permiso al usuario
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1982);
        } else {
            // Si ya se tiene permiso, realiza la operación de escritura
            // Tu código para escribir en el almacenamiento externo aquí
        }
    }
}