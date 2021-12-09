package com.example.watersos.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.example.watersos.R;

public class PresentacionActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);

        //llamamos al progressBar de la interfaz
        ProgressBar progressBar=findViewById(R.id.progressBar);
        //hacemos visible el progress bar
        progressBar.setVisibility(View.VISIBLE);
        //le ponemos colo al toolbar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolbar)));

        //iniciamos un contador de 2 segundos para iniciar sesion
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //llamamos las preferincias del usuario
                SharedPreferences preferences=getSharedPreferences("preferenciaLogin", Context.MODE_PRIVATE);

                //mandamos a llamar un boolean dentro de la aplicacion
                boolean sesion=preferences.getBoolean("sesion", false);

                //verificacmos si sesion es verdadero para enviarlo a la interfaz principal
                if (sesion){
                    Intent intent= new Intent(getApplicationContext(),Principal.class);
                    startActivity(intent);
                    finish();
                }
                //en caso de que sesion sea falso se le enviara el inicio de sesion
                else {
                    Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }
}
