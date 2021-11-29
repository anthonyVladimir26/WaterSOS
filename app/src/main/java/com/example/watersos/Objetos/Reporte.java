package com.example.watersos.Objetos;

public class Reporte {
    String clave_Reporte,direccion,fecha,descripcion,seguimiento,Usuario;
    int NO_Contrato, NO_Ext,estatus;
    double latitud, longitud;

    public Reporte(String clave_Reporte, String direccion, String fecha, String descripcion,
                   String seguimiento, String usuario, int NO_Contrato, int NO_Ext,
                   int estatus, double latitud, double longitud) {
        this.clave_Reporte = clave_Reporte;
        this.direccion = direccion;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.seguimiento = seguimiento;
        Usuario = usuario;
        this.NO_Contrato = NO_Contrato;
        this.NO_Ext = NO_Ext;
        this.estatus = estatus;
        this.latitud = latitud;
        this.longitud = longitud;
    }


    public String getClave_Reporte() {
        return clave_Reporte;
    }

    public void setClave_Reporte(String clave_Reporte) {
        this.clave_Reporte = clave_Reporte;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSeguimiento() {
        return seguimiento;
    }

    public void setSeguimiento(String seguimiento) {
        this.seguimiento = seguimiento;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public int getNO_Contrato() {
        return NO_Contrato;
    }

    public void setNO_Contrato(int NO_Contrato) {
        this.NO_Contrato = NO_Contrato;
    }

    public int getNO_Ext() {
        return NO_Ext;
    }

    public void setNO_Ext(int NO_Ext) {
        this.NO_Ext = NO_Ext;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
