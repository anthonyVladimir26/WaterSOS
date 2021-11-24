package com.example.watersos.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.watersos.Adapters.AdapterReportes;
import com.example.watersos.Objetos.Reporte;
import com.example.watersos.R;
import com.example.watersos.SQLite.AdminSQLiteOpenHelper;

import java.util.ArrayList;

public class nav_misReportes extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Reporte> reporteArrayList;
    AdapterReportes adapterReportes;
    AdminSQLiteOpenHelper baseDeDatos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =inflater.inflate(R.layout.fragment_nav_mis_reportes, container, false);

        recyclerView = view.findViewById(R.id.idRecyclerReportes);

        baseDeDatos = new AdminSQLiteOpenHelper(getContext());

        reporteArrayList = new ArrayList<>();
        reporteArrayList = baseDeDatos.mostraReportes();
        adapterReportes = new AdapterReportes(getContext(), reporteArrayList);
        recyclerView.setAdapter(adapterReportes);


        return view;
    }
}
