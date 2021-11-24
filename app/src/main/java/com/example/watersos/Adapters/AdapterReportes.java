package com.example.watersos.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watersos.Activities.DatosReporteActivity;
import com.example.watersos.Objetos.Reporte;
import com.example.watersos.R;

import java.util.ArrayList;

public class AdapterReportes extends RecyclerView.Adapter<AdapterReportes.ViewHolder>{

    Context context;
    ArrayList<Reporte> reporteArrayList;

    public AdapterReportes(Context context, ArrayList<Reporte> reporteArrayList) {
        this.context = context;
        this.reporteArrayList = reporteArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cardview_reportes,
                parent,false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Reporte reporte = reporteArrayList.get(position);

        holder.txvNumContrato.setText(reporte.getDireccion()+"");
        holder.txvSeguimiento.setText(reporte.getSeguimiento());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, DatosReporteActivity.class);
                intent.putExtra("clave",reporte.getClave_Reporte());
                intent.putExtra("numContrato",reporte.getNO_Contrato());
                intent.putExtra("numExterior",reporte.getNO_Ext());
                intent.putExtra("fechaYhora",reporte.getFecha());
                intent.putExtra("direccion",reporte.getDireccion());
                intent.putExtra("descripcion",reporte.getDescripcion());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reporteArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txvNumContrato,txvSeguimiento;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txvNumContrato = itemView.findViewById(R.id.num_contrato_cardview);
            txvSeguimiento = itemView.findViewById(R.id.seguimiento_cardview);

        }
    }
}
