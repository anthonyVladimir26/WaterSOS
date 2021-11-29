package com.example.watersos.JavaClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.watersos.Objetos.Foto;
import com.example.watersos.Objetos.Reporte;
import com.example.watersos.SQLite.AdminSQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class EnviarDatos {

    Context context;
    AdminSQLiteOpenHelper baseDeDatos;

    public EnviarDatos(Context context) {
        this.context = context;
        baseDeDatos = new AdminSQLiteOpenHelper(context);
    }

    public void enviarDatosSQlite(Reporte reporte, Foto foto){

        boolean insertFoto = baseDeDatos.insertarFoto(foto);
        boolean insertReporte = baseDeDatos.insertarReporte(reporte);

        if (insertFoto == true && insertReporte == true) {
            Toast.makeText(context, "registrado", Toast.LENGTH_SHORT).show();
        }

    }

    public void enviarDatosMysql (String URL, final Reporte reporte){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Envio Exitoso "+response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String , String> parametros = new HashMap<String, String>();
                parametros.put("clave_Reporte",reporte.getClave_Reporte());
                parametros.put("No_Contrato",reporte.getNO_Contrato()+"");
                parametros.put("No_Ext",reporte.getNO_Ext()+"");
                parametros.put("direccion",reporte.getDireccion());
                parametros.put("latitud",reporte.getLatitud()+"");
                parametros.put("longitud",reporte.getLongitud()+"");
                parametros.put("fecha",reporte.getFecha());
                parametros.put("descripcion",reporte.getDescripcion());
                parametros.put("seguimiento",reporte.getSeguimiento());
                parametros.put("usuario",reporte.getUsuario());

                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
