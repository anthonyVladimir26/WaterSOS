package com.example.watersos.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.example.watersos.Objetos.Foto;
import com.example.watersos.Objetos.Reporte;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(@Nullable Context context) {
        super(context, "name.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {
        //creamos una tabla usuario con sus atributos
        BaseDeDatos.execSQL("create table usuario" +
                "(usuario text primary key ," +
                "nombre text," +
                "contraseña text," +
                "email text," +
                "rol enum," +
                "status timyint)");

        //creamos una tabla foto con sus atributos
        BaseDeDatos.execSQL("create table foto" +
                "(idFoto INTEGER PRIMARY KEY AUTOINCREMENT," +
                "clave_Reporte text," +
                "foto blob," +
                "foreign key(clave_Reporte)references reporte(clave_Reporte))");

        //creamos una tabla Reporte con sus atributos
        BaseDeDatos.execSQL("create table reporte" +
                "(clave_Reporte text primary key," +
                "NO_Contrato INTEGER," +
                "NO_Ext INTEGER, " +
                "direccion text," +
                "latitud double," +
                "longitud double," +
                "fecha datatime," +
                "descripcion text," +
                "seguimiento text," +
                "Usuario text," +
                "estatus INTEGER,"+
                "foreign key(Usuario)references usuario(usuario))");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public  boolean insertarReporte(Reporte reporte){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put("clave_Reporte", reporte.getClave_Reporte());
        content.put("NO_Contrato", reporte.getNO_Contrato());
        content.put("NO_Ext", reporte.getNO_Ext());
        content.put("direccion", reporte.getDireccion());
        content.put("latitud", reporte.getLatitud());
        content.put("longitud", reporte.getLongitud());
        content.put("fecha", reporte.getFecha());
        content.put("descripcion", reporte.getDescripcion());
        content.put("seguimiento", reporte.getSeguimiento());
        content.put("Usuario", reporte.getUsuario());
        content.put("estatus", reporte.getEstatus());

        Long ins = db.insert("reporte",null,content);
        if (ins==-1) return false;
        else return true;

    }

    public  boolean insertarFoto(Foto foto){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        Bitmap bitmap = foto.getFoto();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArray);
        byte[] img = byteArray.toByteArray();


        content.put("clave_Reporte", foto.getClave_Reporte());
        content.put("foto", img);



        Long ins = db.insert("foto",null,content);
        if (ins==-1) return false;
        else return true;

    }

    public ArrayList<Reporte> mostraReportes(){

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Reporte> listaReporte = new ArrayList<>();

        Cursor cursor = null;

        cursor = db.rawQuery("select * from reporte",null);

        while (cursor.moveToNext()){
            String clave_Reporte =cursor.getString(0);
            int NO_Contrato = cursor.getInt(1);
            int NO_Ext = cursor.getInt(2);
            String direccion = cursor.getString(3);
            double latitud = cursor.getDouble(4);
            double longitud =cursor.getDouble(5);
            String fecha = cursor.getString(6);
            String descripcion = cursor.getString(7);
            String seguimiento = cursor.getString(8);
            String usuario = cursor.getString(9);
            int estatus = cursor.getInt(10);

            listaReporte.add(new Reporte(clave_Reporte,direccion,fecha,descripcion,seguimiento,usuario,NO_Contrato,NO_Ext,estatus,latitud,longitud));

        }
        cursor.close();
        return listaReporte;
    }

    public Bitmap conseguirImagen (String clave){
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from foto where clave_Reporte=? ",new String[]{clave});
        cursor.moveToFirst();

        byte[] bitmap = cursor.getBlob(2);
        Bitmap imagen = BitmapFactory.decodeByteArray(bitmap,0 ,bitmap.length);
        return imagen;
    }

}
