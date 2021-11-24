package com.example.watersos.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.watersos.Activities.MainActivity;
import com.example.watersos.Objetos.Foto;
import com.example.watersos.Objetos.Reporte;
import com.example.watersos.R;
import com.example.watersos.SQLite.AdminSQLiteOpenHelper;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class nav_Reportar extends Fragment {

    EditText edtNumContrato, edtNumExt, edtFecha,edtDireccion,edtDescripcion;

    Button enviar;

    ImageButton imgBtnFoto, imgBtnUbicacion;

    Bitmap bitmap;

    Boolean imagenElegida = false;
    Boolean ubicacionElegida = false;

    ImageView imaCheckFoto,imgCheckUbi;

    AdminSQLiteOpenHelper baseDeDatos;

    double longitud, latitud;

    ProgressDialog progressDialog;

    ProgressBar progressBarDireccion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_nav__reportar, container,false);


        //vinculamos con la intefaz
        edtNumContrato = view.findViewById(R.id.edtNumContrato);
        edtNumExt = view.findViewById(R.id.edtNumExt);
        edtFecha = view.findViewById(R.id.edtFecha);
        edtDireccion = view.findViewById(R.id.edtDireccion);
        edtDescripcion = view.findViewById(R.id.edtDescripcion);
        enviar =view.findViewById(R.id.enviar);
        imgBtnFoto = view.findViewById(R.id.imgBtnCamara);
        imgBtnUbicacion = view.findViewById(R.id.imgBtnUbicacion);

        imaCheckFoto = view.findViewById(R.id.img_check_foto);
        imgCheckUbi = view.findViewById(R.id.img_check_ubi);

        progressBarDireccion = view.findViewById(R.id.progressBarDireccion);
        //Toast.makeText(getActivity(), "Bienvenido a Reporte", Toast.LENGTH_LONG).show();


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Buscando dirección...");
        progressDialog.setCancelable(true);

        //llamamos la clase para generar la clave del reporte
        final String clave = generateRandomString(5);

        //llamamos la base de datos de sqlite
        baseDeDatos = new AdminSQLiteOpenHelper(getContext());

        //pedimos el permiso de camara y mandamos los datos por si deniegan el permiso
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {

            requestPermissions(new String[]{Manifest.permission.CAMERA},200);
            Toast.makeText(getContext(), "se denegaron", Toast.LENGTH_SHORT).show();

        }

        //pedimos el permiso de ubicacion y mandamos los datos por si deniegan el permiso
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions((Activity) getContext(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }



        //boton de la foto
        imgBtnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //verificamos si se tienen los permisos
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA},200);
                    Toast.makeText(getContext(), "se denegaron", Toast.LENGTH_SHORT).show();

                }
                //en caso de tener los permisos se efectuara esto que abrira la camara
                else{
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 10);


                }
            }
        });


        //boton de la ubicacion
        imgBtnUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ubicacionElegida = false;
               // progressDialog.show();
                progressBarDireccion.setVisibility(View.VISIBLE);

               /* if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                    if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),Manifest.permission.ACCESS_FINE_LOCATION)){
                        ActivityCompat.requestPermissions((Activity) getContext(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    }
                }
                else {
*/

                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {


                            if (ubicacionElegida== false) {

                                latitud = location.getLatitude();
                                longitud = location.getLongitude();

                                progressDialog.dismiss();
                                try {

                                    //Toast.makeText(getContext(), "longitud: " + longitud + "\n latitud: " + latitud, Toast.LENGTH_SHORT).show();
                                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                    List<Address> list = geocoder.getFromLocation(latitud, longitud, 1);
                                    if (!list.isEmpty()) {
                                        Address DirCalle = list.get(0);
                                        edtDireccion.setText(DirCalle.getAddressLine(0));
                                        ubicacionElegida = true;

                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }


                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    };

                    int permisosCheck = ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION);

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
                }

        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();


                SimpleDateFormat formato= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                String fechaYHora = formato.format(date.getTime());
                Toast.makeText(getContext(), fechaYHora, Toast.LENGTH_SHORT).show();

                if (imagenElegida == true && ubicacionElegida== true) {
                    SharedPreferences preferences = getContext().getSharedPreferences("preferenciaLogin", Context.MODE_PRIVATE);
                    String usuario = preferences.getString("usuario","");

                    Foto foto = new Foto(clave, bitmap);
                    Reporte reporte = new Reporte(clave,edtDireccion.getText().toString(),fechaYHora,edtDescripcion.getText().toString(),"sin revisar",usuario,Integer.parseInt( edtNumContrato.getText().toString()),Integer.parseInt(edtNumExt.getText().toString()),0,latitud,longitud);



                    boolean insertFoto = baseDeDatos.insertarFoto(foto);
                    boolean insertReporte = baseDeDatos.insertarReporte(reporte);

                    if (insertFoto == true && insertReporte == true) {
                        Toast.makeText(getContext(), "registrado", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), "no se pudo registrar", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getContext(), "por favor tome una foto", Toast.LENGTH_SHORT).show();
                }
                //ejecutarServicio("http://192.168.1.66/reporfuagua/php/guardarReporte.php");
            }

        });




              // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_nav__reportar, container, false);
        return view;
    }


    public static String generateRandomString(int length) {
        // Puede personalizar los personajes que desea agregar a
        // las cadenas al azar
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";

        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
        SecureRandom random = new SecureRandom();

        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // 0-62 (exclusive), retornos aleatorios 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            sb.append(rndChar);
        }

        return sb.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10 && resultCode == RESULT_OK){


            bitmap = (Bitmap) data.getExtras().get("data");

            imaCheckFoto.setVisibility(View.VISIBLE);
            imagenElegida = true;
        }
    }

    private void ejecutarServicio (String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), "Envios Exitoso "+response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String , String> parametros = new HashMap<String, String>();
                parametros.put("No_Contrato",edtNumContrato.getText().toString());
                parametros.put("No_Ext",edtNumExt.getText().toString());
                parametros.put("direccion",edtDireccion.getText().toString());
                parametros.put("fecha",edtFecha.getText().toString());
                parametros.put("descripcion",edtDescripcion.getText().toString());

                return parametros;
            }
        }; if (edtNumContrato.length() ==0){

            Toast.makeText(getActivity(), "El numero de contrato no debe quedar vacío ",Toast.LENGTH_LONG).show();

        }if (edtNumExt.length()==0){
            Toast.makeText(getActivity(), "El numero de exterior no debe quedar vacío ",Toast.LENGTH_LONG).show();

        }if (edtDireccion.length()==0){
            Toast.makeText(getActivity(), "La dirección no debe quedar vacía ",Toast.LENGTH_LONG).show();

        }if (edtDescripcion.length()==0){
            Toast.makeText(getActivity(), "La descripción no debe quedar vacía ",Toast.LENGTH_LONG).show();
        }else {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);

        }


    }

}
