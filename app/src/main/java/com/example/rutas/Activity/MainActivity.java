package com.example.rutas.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.rutas.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnPadre,btnCole,btnCond;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //titulo action bar
        this.setTitle("Inicio");

        //quitar action bar
        getSupportActionBar().hide();

        btnCole = (ImageButton) findViewById(R.id.btnColegio);
        btnCond = (ImageButton) findViewById(R.id.btnConductor);
        btnPadre = (ImageButton) findViewById(R.id.btnPadreF);


        btnPadre.setOnClickListener(this);
        btnCond.setOnClickListener(this);
        btnCole.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.btnColegio:        //"Block SMSs" button pressed
                intent = new Intent(this, Colegio.class);
                intent.putExtra("actividad", "cole");

                //Toast.makeText(getApplicationContext(),"Colegio",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnConductor:        //"Block SMSs" button pressed
//                startActivity(new Intent(this,secret_main.class));
                //Toast.makeText(getApplicationContext(),"Conductor",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, Colegio.class);
                intent.putExtra("actividad", "conductor");


                break;

            case R.id.btnPadreF:        //"Block SMSs" button pressed
//                startActivity(new Intent(this,secret_main.class));
                intent = new Intent(this, MainPadreFamilia.class);
                break;

        }

        startActivity(intent);


    }

    private void diferenciaHoras() {
        try {
            //Lo primero que tienes que hacer es establecer el formato que tiene tu fecha para que puedas obtener un objeto de tipo Date el cual es el que se utiliza para obtener la diferencia.
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

            //Parceas tus fechas en string a variables de tipo date se agrega un try catch porque si el formato declarado anteriormente no es igual a tu fecha obtendrás una excepción
            Date dateStart = dateFormat.parse("2019-11-09 15:41:46");
            Date dateEnd = dateFormat.parse("2019-11-19 17:53:19");
            int d =dateEnd.getDate();

            //Date newf = dateFormat.parse("2019-11-19 17:53:19");
            String strDateFormat = " DD MMM yyyy  hh:mma"; // El formato de fecha está especificado
            SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat); // La cadena de formato de fecha se pasa como un argumento al objeto
            //System.out.println(objSDF.format(dateEnd));

            /*SimpleDateFormat timeHour=new SimpleDateFormat("HH");
            SimpleDateFormat timeMin=new SimpleDateFormat("mm");*/
            //obtienes la diferencia de las fechas
            int horas = (dateEnd.getHours() - dateStart.getHours());
            int minutosn = (60 - dateEnd.getMinutes());
            int minutos = (minutosn- dateStart.getMinutes());
            int m;
            if(minutos<0){
                m = minutos*-1;
            }else {
                m=minutos;
            }



            //obtienes la diferencia en horas ya que la diferencia anterior esta en milisegundos
            System.out.println("DIFERENCIA: " +  (m));

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
