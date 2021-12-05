package com.example.watersos.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class CrearCuenta extends AppCompatActivity {

    EditText edtUsuario,edtNombre, edtPassword,edtPassword2,edtCorreo;
    Button btnCuenta;
    String usuario, contraseña,contraseña2,correo, nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_cuenta);

        edtUsuario=findViewById(R.id.edtUsuario);
        edtNombre=findViewById(R.id.edtNombre);
        edtCorreo=findViewById(R.id.edtCorreo);
        edtPassword=findViewById(R.id.edtPassword);
        edtPassword2=findViewById(R.id.edtPassword2);
        btnCuenta=findViewById(R.id.btnCuenta);

        btnCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                usuario=edtUsuario.getText().toString();
                nombre=edtNombre.getText().toString();
                contraseña=edtPassword.getText().toString();
                contraseña2=edtPassword2.getText().toString();
                correo=edtCorreo.getText().toString();

                if (!usuario.isEmpty()&& !nombre.isEmpty() && !contraseña.isEmpty() && !contraseña2.isEmpty() && !correo.isEmpty() && (contraseña.equals(contraseña2))){
                    validarUsuario("https://watersos01.000webhostapp.com/php/crearUsuario.php");
                }else{
                    Toast.makeText(CrearCuenta.this , "No se permiten campos vacíos Y/O las contraseñas deben ser iguales", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void  validarUsuario (String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.trim();

                if (response.equals("true")) {
                    Toast.makeText(CrearCuenta.this, "Cuenta Creada", Toast.LENGTH_LONG).show();
                     Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                     startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CrearCuenta.this, "Usuario Repetido, escribir otro usuario", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CrearCuenta.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros= new HashMap<String, String>();
                parametros.put("usuario",usuario);
                parametros.put("nombre",nombre);
                parametros.put("contraseña",contraseña);
                parametros.put("correo",correo);
                return parametros;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
