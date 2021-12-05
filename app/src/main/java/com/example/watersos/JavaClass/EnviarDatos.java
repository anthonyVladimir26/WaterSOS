package com.example.watersos.JavaClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
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
import com.example.watersos.R;
import com.example.watersos.SQLite.AdminSQLiteOpenHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EnviarDatos {

    Context context;
    AdminSQLiteOpenHelper baseDeDatos;
    String nombreImagen ="";

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

    public void enviarDatosMysqlReporte (String URL, final Reporte reporte){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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

    public void enviarDatosMysqlFoto (String URL, final Foto foto, final String nombreImagen){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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
                String imagen = convertirImgString(foto.getFoto());


                parametros.put("clave",foto.getClave_Reporte());
                parametros.put("nombreImagen",nombreImagen);
                parametros.put("foto",imagen);

                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private String convertirImgString(Bitmap bitmap) {

        ByteArrayOutputStream array= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte,Base64.DEFAULT);

        return imagenString;
    }

    public void enviarReporteConConexion(){

        ArrayList<Reporte> arrayList = baseDeDatos.verificarStatus();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            for (int i = 0; i < arrayList.size(); i++) {

                Reporte reporte = arrayList.get(i);
                Bitmap bitmap = baseDeDatos.conseguirImagen(reporte.getClave_Reporte());
                Foto foto = new Foto(reporte.getClave_Reporte(),bitmap);
                baseDeDatos.actualizarEstatus(reporte.getClave_Reporte());

                Long consecutivo= System.currentTimeMillis()/1000;
                String nombreImagen=consecutivo.toString()+i+".jpg";



                enviarDatosMysqlReporte("https://watersos01.000webhostapp.com/php/guardarReporte.php",reporte);
                enviarDatosMysqlFoto("https://watersos01.000webhostapp.com/php/guardarFoto.php",foto,nombreImagen);


            }
        }

    }
}

