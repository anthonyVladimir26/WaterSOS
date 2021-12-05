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
public class Aqua extends Fragment {


    EnviarDatos enviarDatos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_aqua, container, false);

        enviarDatos = new EnviarDatos(getContext());

        enviarDatos.enviarReporteConConexion();
        // Inflate the layout for this fragment
        return view;


    }
}
