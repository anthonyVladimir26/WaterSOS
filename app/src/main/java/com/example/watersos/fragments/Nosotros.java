package com.example.watersos.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.watersos.JavaClass.EnviarDatos;
import com.example.watersos.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Nosotros extends Fragment {
    EnviarDatos enviarDatos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_nosotros, container, false);

        enviarDatos = new EnviarDatos(getContext());
        enviarDatos.enviarReporteConConexion();

        return view;
    }
}
