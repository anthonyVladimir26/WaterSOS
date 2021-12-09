package com.example.watersos.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.watersos.R;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText edtUsuario, edtPassword;
    Button btnLogin;
    Button btnCuenta;
    String usuario, contraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUsuario=findViewById(R.id.edtUsuario);
        edtPassword=findViewById(R.id.edtPassword);
        btnLogin=findViewById(R.id.btnLogin);
        btnCuenta=findViewById(R.id.btnCuenta);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolbar)));

        recuperarPreferencia();

        this.deleteDatabase("Reporte.db");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                usuario=edtUsuario.getText().toString();
                contraseña=edtPassword.getText().toString();

                if (!usuario.isEmpty() && !contraseña.isEmpty()){
                    validarUsuario("https://watersos01.000webhostapp.com/php/validar_usuario.php");
                }else{
                    Toast.makeText(MainActivity.this , "No se permiten campos vacíos", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CrearCuenta.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void  validarUsuario (String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()) {
                    guardarPreferencia();
                    Intent intent = new Intent(getApplicationContext(), Principal.class);
                    startActivity(intent);
                    finish();
                } else{
                    Toast.makeText(MainActivity.this, "El usuario y/o contraseña són incorrectos o bien usuario bloqueado", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros= new HashMap<String, String>();
                parametros.put("usuario",usuario);
                parametros.put("contraseña",contraseña);
                return parametros;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void guardarPreferencia (){
        SharedPreferences preferences = getSharedPreferences("preferenciaLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("usuario",usuario);
        editor.putString("contraseña",contraseña);
        editor.putBoolean("sesion",true);
        editor.commit();

    }

    private void recuperarPreferencia(){
        SharedPreferences preferences = getSharedPreferences("preferenciaLogin",Context.MODE_PRIVATE);
        edtUsuario.setText(preferences.getString("usuario",""));
        edtPassword.setText(preferences.getString("contraseña",""));

    }

}
