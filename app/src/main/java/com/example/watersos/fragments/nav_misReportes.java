package com.example.watersos.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.watersos.Adapters.AdapterReportes;
import com.example.watersos.JavaClass.EnviarDatos;
import com.example.watersos.Objetos.Foto;
import com.example.watersos.Objetos.Reporte;
import com.example.watersos.R;
import com.example.watersos.SQLite.AdminSQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class nav_misReportes extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Reporte> reporteArrayList;
    AdapterReportes adapterReportes;
    AdminSQLiteOpenHelper baseDeDatos;

    EnviarDatos enviarDatos;

    SwipeRefreshLayout rlReportes;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =inflater.inflate(R.layout.fragment_nav_mis_reportes, container, false);


        enviarDatos = new EnviarDatos(getContext());
        enviarDatos.enviarReporteConConexion();

        recyclerView = view.findViewById(R.id.idRecyclerReportes);
        rlReportes = view.findViewById(R.id.refreshListaReporte);


        baseDeDatos = new AdminSQLiteOpenHelper(getActivity());

        reporteArrayList = new ArrayList<>();

        verificarConexion();

        rlReportes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                verificarConexion();
            }
        });



        return view;
    }



    public void obtenerDatosReporte(String URL){
        rlReportes.setRefreshing(true);
        reporteArrayList.clear();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject =null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        String clave_Reporte = jsonObject.getString("clave_Reporte");
                        String direccion = jsonObject.getString("direccion");
                        String fecha = jsonObject.getString("fecha");
                        String descripcion = jsonObject.getString("descripcion");
                        String seguimiento = jsonObject.getString("seguimiento");
                        String usuario = jsonObject.getString("usuario");
                        int No_Contrato = jsonObject.getInt("No_Contrato");
                        int No_Ext = jsonObject.getInt("No_Ext");
                        int estatus = 1;
                        double latitud = jsonObject.getDouble("latitud");
                        double longitud = jsonObject.getDouble("longitud");


                        obtenerDatosFoto("https://watersos01.000webhostapp.com/php/consultarFoto.php?clave="+clave_Reporte,clave_Reporte);
                        baseDeDatos.insertarReporte(new Reporte(clave_Reporte,direccion,fecha,descripcion,seguimiento,
                                usuario,No_Contrato,No_Ext,estatus,latitud,longitud));
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

               

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    public void obtenerDatosFoto(String URL, final String clave){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject =null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        String foto = jsonObject.getString("foto");

                        String enlace= "https://watersos01.000webhostapp.com/"+foto;

                        descargarImagen(enlace, clave);

                        //Toast.makeText(getContext(), bitmap.toString(), Toast.LENGTH_SHORT).show();
                        //baseDeDatos.insertarFoto(new Foto(clave,bitmap));
                        

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                rlReportes.setRefreshing(false);
                mostrarDatosSqlite();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }

    public void descargarImagen(String url, final String clave) {


        ImageRequest peticion = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        baseDeDatos.insertarFoto(new Foto(clave,response));
                    }
                }, 0, 0, null, // maxWidth, maxHeight, decodeConfig
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(peticion);

    }

    public void mostrarDatosSqlite(){
        reporteArrayList = baseDeDatos.mostraReportes();
        adapterReportes = new AdapterReportes(getContext(), reporteArrayList);
        recyclerView.setAdapter(adapterReportes);
    }

    public void verificarConexion(){
        SharedPreferences preferences = getContext().getSharedPreferences("preferenciaLogin",Context.MODE_PRIVATE);
        String usuario =preferences.getString("usuario","");
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {


            baseDeDatos.borrarDatosTablaReporte();
            baseDeDatos.borrarDatosTablaFoto();

            obtenerDatosReporte("https://watersos01.000webhostapp.com/php/consultarReporte.php?usuario="+usuario);
            rlReportes.setRefreshing(false);
        }else {

            rlReportes.setRefreshing(true);
            mostrarDatosSqlite();
            rlReportes.setRefreshing(false);
        }
    }
}
