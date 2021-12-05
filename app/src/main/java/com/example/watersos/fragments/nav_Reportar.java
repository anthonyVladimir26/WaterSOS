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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.example.watersos.JavaClass.EnviarDatos;
import com.example.watersos.Objetos.Foto;
import com.example.watersos.Objetos.Reporte;
import com.example.watersos.R;
import com.example.watersos.SQLite.AdminSQLiteOpenHelper;
import com.example.watersos.ui.home.HomeFragment;

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

    //inicializamos los componentes que iran con la interfaz
    EditText edtNumContrato, edtNumExt,edtDireccion,edtDescripcion;
    Button enviar;
    ImageButton imgBtnFoto, imgBtnUbicacion;

    //creamos el bitmap en donde se guardara la imagen
    Bitmap bitmap;

    //booleadnos que nos direan si se a tomado una foto o no se a elegido la ubicacion
    Boolean imagenElegida = false;
    Boolean ubicacionElegida = false;

    AdminSQLiteOpenHelper baseDeDatos;

    double longitud, latitud;

    ProgressDialog progressDialog;

    int status =0;

    EnviarDatos enviarDatos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_nav__reportar, container,false);


        //vinculamos con la intefaz
        edtNumContrato = view.findViewById(R.id.edtNumContrato);
        edtNumExt = view.findViewById(R.id.edtNumExt);

        edtDireccion = view.findViewById(R.id.edtDireccion);
        edtDescripcion = view.findViewById(R.id.edtDescripcion);
        enviar =view.findViewById(R.id.enviar);
        imgBtnFoto = view.findViewById(R.id.imgBtnCamara);
        imgBtnUbicacion = view.findViewById(R.id.imgBtnUbicacion);



        //Toast.makeText(getActivity(), "Bienvenido a Reporte", Toast.LENGTH_LONG).show();

        enviarDatos=new EnviarDatos(getContext());

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Buscando dirección...");
        progressDialog.setCancelable(true);

        //llamamos la clase para generar la clave del reporte


        //llamamos la base de datos de sqlite
        baseDeDatos = new AdminSQLiteOpenHelper(getContext());

        //pedimos el permiso de camara y mandamos los datos por si deniegan el permiso
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {

            //iniciamos la camra
            requestPermissions(new String[]{Manifest.permission.CAMERA},200);
            Toast.makeText(getContext(), "se denegaron", Toast.LENGTH_SHORT).show();

        }

        //pedimos el permiso de ubicacion y mandamos los datos por si deniegan el permiso
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions( getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }

        enviarDatos.enviarReporteConConexion();

        //boton de la foto
        imgBtnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //verificamos si se tienen los permisos
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA},200);
                    Toast.makeText(getActivity(), "se denegaron", Toast.LENGTH_SHORT).show();

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


                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 200);
                    Toast.makeText(getActivity(), "se denegaron", Toast.LENGTH_SHORT).show();

                } else {

                    progressDialog.show();
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {


                            if (ubicacionElegida == false) {

                                latitud = location.getLatitude();
                                longitud = location.getLongitude();


                                try {

                                    //Toast.makeText(getContext(), "longitud: " + longitud + "\n latitud: " + latitud, Toast.LENGTH_SHORT).show();
                                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                    List<Address> list = geocoder.getFromLocation(latitud, longitud, 1);
                                    if (!list.isEmpty()) {
                                        Address DirCalle = list.get(0);
                                        edtDireccion.setText(DirCalle.getAddressLine(0));
                                        ubicacionElegida = true;
                                        edtDireccion.setVisibility(View.VISIBLE);
                                        progressDialog.dismiss();
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



                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }

        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();

                final String clave = generateRandomString(5);

                SimpleDateFormat formato= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                String fechaYHora = formato.format(date.getTime());

                ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (edtNumContrato.length() ==0){

                    Toast.makeText(getActivity(), "El numero de contrato no debe quedar vacío ",Toast.LENGTH_LONG).show();

                }
                else if (edtNumExt.length()==0){
                    Toast.makeText(getActivity(), "El numero de exterior no debe quedar vacío ",Toast.LENGTH_LONG).show();

                }
                else if (edtDireccion.length()==0){
                    Toast.makeText(getActivity(), "La dirección no debe quedar vacía ",Toast.LENGTH_LONG).show();

                }
                else if (edtDescripcion.length()==0){
                    Toast.makeText(getActivity(), "La descripción no debe quedar vacía ",Toast.LENGTH_LONG).show();
                }
                else if (imagenElegida == false){
                    Toast.makeText(getActivity(), "por favor tome una foto",Toast.LENGTH_LONG).show();
                }
                else if (ubicacionElegida == false){
                    Toast.makeText(getActivity(), "por favor tome una foto",Toast.LENGTH_LONG).show();
                }
                else {

                    SharedPreferences preferences = getContext().getSharedPreferences("preferenciaLogin", Context.MODE_PRIVATE);
                    String usuario = preferences.getString("usuario", "");
                    Foto foto;
                    Reporte reporte;
                    if (networkInfo != null && networkInfo.isConnected()) {

                        status = 1;

                        foto = new Foto(clave, bitmap);
                        reporte = new Reporte(clave, edtDireccion.getText().toString(), fechaYHora, edtDescripcion.getText().toString(), "sin revisar", usuario, Integer.parseInt(edtNumContrato.getText().toString()), Integer.parseInt(edtNumExt.getText().toString()), status, latitud, longitud);
                        Long consecutivo= System.currentTimeMillis()/1000;
                        String nombreImagen=consecutivo.toString()+".jpg";
                        enviarDatos.enviarDatosMysqlReporte("https://watersos01.000webhostapp.com/php/guardarReporte.php", reporte);
                        enviarDatos.enviarDatosMysqlFoto("https://watersos01.000webhostapp.com/php/guardarFoto.php",foto,nombreImagen);
                    }
                    else {
                        status = 0;
                        foto = new Foto(clave, bitmap);
                        reporte = new Reporte(clave, edtDireccion.getText().toString(), fechaYHora, edtDescripcion.getText().toString(), "sin revisar", usuario, Integer.parseInt(edtNumContrato.getText().toString()), Integer.parseInt(edtNumExt.getText().toString()), status, latitud, longitud);

                    }

                    edtDescripcion.setText("");

                    edtDireccion.setText("");
                    edtDireccion.setVisibility(View.GONE);

                    edtNumContrato.setText("");
                    edtNumExt.setText("");

                    imagenElegida= false;
                    ubicacionElegida=false;

                    bitmap =null;


                    enviarDatos.enviarDatosSQlite(reporte, foto);

                }

            }

        });





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

            imagenElegida = true;
        }
    }

}
