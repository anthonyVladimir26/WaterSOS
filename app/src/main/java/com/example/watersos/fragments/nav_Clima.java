package com.example.watersos.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.watersos.JavaClass.EnviarDatos;
import com.example.watersos.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class nav_Clima extends Fragment {

    EnviarDatos enviarDatos;

    WebView pantallaClima;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_nav__clima, container, false);

            pantallaClima= view.findViewById(R.id.paguinaWeb);



            pantallaClima.loadUrl("https://weather.com/es-MX/tiempo/hoy/l/3e6720c1da23efaa42e23664474bc9860e41d18d0991a48e2b3fedf2a0f76f77");

            enviarDatos = new EnviarDatos(getContext());
            enviarDatos.enviarReporteConConexion();
        return view;
    }
}
