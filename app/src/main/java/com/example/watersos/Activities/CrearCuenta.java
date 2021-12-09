package com.example.watersos.Activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

        //llamamos los componentes de la interfaz
        edtUsuario=findViewById(R.id.edtUsuario);
        edtNombre=findViewById(R.id.edtNombre);
        edtCorreo=findViewById(R.id.edtCorreo);
        edtPassword=findViewById(R.id.edtPassword);
        edtPassword2=findViewById(R.id.edtPassword2);
        btnCuenta=findViewById(R.id.btnCuenta);


        //mandamos el color del toolbar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolbar)));

        //boton de crear usuario
        btnCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //guardamos los datos ingresados por el usuario
                usuario=edtUsuario.getText().toString();
                nombre=edtNombre.getText().toString();
                contraseña=edtPassword.getText().toString();
                contraseña2=edtPassword2.getText().toString();
                correo=edtCorreo.getText().toString();

                //verificamos que se ingresaron todos los datos
                if (!usuario.isEmpty()&& !nombre.isEmpty() && !contraseña.isEmpty() && !contraseña2.isEmpty() && !correo.isEmpty() && (contraseña.equals(contraseña2))){
                    //mandomos los datos al webservice
                    validarUsuario("https://watersos01.000webhostapp.com/php/crearUsuario.php");
                }
                //en caso de que que falten los datos le enviamos un mensaje
                else{

                    Toast.makeText(CrearCuenta.this , "No se permiten campos vacíos Y/O las contraseñas deben ser iguales", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    //enviamos los datos del nuevo usuario
    private void  validarUsuario (String URL){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.trim();

                //en caso de que el usuario no existo se le manda un mensaje y se le redirige al inicio de sesion
                if (response.equals("true")) {
                    Toast.makeText(CrearCuenta.this, "Cuenta Creada", Toast.LENGTH_LONG).show();
                     Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                     startActivity(intent);
                    finish();
                }
                //en caso de que el usuario exista se le manda un mensaje de que dicho usuario ya existe
                else {
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

                //ponemos los datos que se mandaran al webservice
                Map<String, String> parametros= new HashMap<String, String>();
                parametros.put("usuario",usuario);
                parametros.put("nombre",nombre);
                parametros.put("contraseña",contraseña);
                parametros.put("correo",correo);
                return parametros;
            }
        };

        //inicializamos el requestQueue
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        //enviamos los datos al webservice
        requestQueue.add(stringRequest);
    }

}
