package com.example.watersos.Objetos;

import android.graphics.Bitmap;

public class Foto {
    int idFoto;
    String clave_Reporte;
    Bitmap foto;

    public Foto( String clave_Reporte, Bitmap foto) {
        this.clave_Reporte = clave_Reporte;
        this.foto = foto;
    }

    public int getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(int idFoto) {
        this.idFoto = idFoto;
    }

    public String getClave_Reporte() {
        return clave_Reporte;
    }

    public void setClave_Reporte(String clave_Reporte) {
        this.clave_Reporte = clave_Reporte;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }
}
