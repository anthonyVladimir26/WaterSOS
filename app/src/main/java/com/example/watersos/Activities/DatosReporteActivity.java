package com.example.watersos.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.watersos.R;
import com.example.watersos.SQLite.AdminSQLiteOpenHelper;

public class DatosReporteActivity extends AppCompatActivity {

    String clave,fechaYhora,direccion,descripcion;

    int numContrata,numExterior;
    ImageView imageView ;

    TextView txvNumContrata,txvNumExterior,txvFechaYHora,txvDireccion,txvDescripcion;

    AdminSQLiteOpenHelper baseDeDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_reporte);

        //llamamos los componentes de la interfaz
        txvNumContrata = findViewById(R.id.textView_num_contrato);
        txvNumExterior = findViewById(R.id.textView_num_exterior);
        txvFechaYHora = findViewById(R.id.textView_fecha_hora);
        txvDireccion = findViewById(R.id.textView_direccion);
        txvDescripcion = findViewById(R.id.textView_descripcion);
        imageView = findViewById(R.id.imageViewReporte);


        //llamamos los datos de la consulta de la pagina anteriso
        clave = getIntent().getStringExtra("clave");
        numContrata = getIntent().getIntExtra("numContrato",0);
        numExterior = getIntent().getIntExtra("numExterior",0);
        fechaYhora = getIntent().getStringExtra("fechaYhora");
        direccion = getIntent().getStringExtra("direccion");
        descripcion = getIntent().getStringExtra("descripcion");

        //inicializamos la base de datos sqlite
        baseDeDatos = new AdminSQLiteOpenHelper(this);

        //llamamos la imagen de la base de datos sqlite
        Bitmap bitmap = baseDeDatos.conseguirImagen(clave);

        //mandamos los datos a la interfaz
        imageView.setImageBitmap(bitmap);
        txvNumContrata.setText(numContrata+"");
        txvNumExterior.setText(numExterior+"");
        txvFechaYHora.setText(fechaYhora);
        txvDireccion.setText(direccion);
        txvDescripcion.setText(descripcion);
    }
}