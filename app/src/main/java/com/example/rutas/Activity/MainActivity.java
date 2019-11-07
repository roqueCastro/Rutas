package com.example.rutas.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.rutas.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPadre,btnCole,btnCond;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCole = (Button) findViewById(R.id.btnColegio);
        btnCond = (Button) findViewById(R.id.btnConductor);
        btnPadre = (Button) findViewById(R.id.btnPadreF);


        btnPadre.setOnClickListener(this);
        btnCond.setOnClickListener(this);
        btnCole.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.btnColegio:        //"Block SMSs" button pressed
                intent=null;
                intent = new Intent(this, Colegio.class);
                intent.putExtra("actividad", "cole");


                //Toast.makeText(getApplicationContext(),"Colegio",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnConductor:        //"Block SMSs" button pressed
//                startActivity(new Intent(this,secret_main.class));
                //Toast.makeText(getApplicationContext(),"Conductor",Toast.LENGTH_SHORT).show();
                intent=null;
                intent = new Intent(this, Colegio.class);
                intent.putExtra("actividad", "conductor");


                break;
            case R.id.btnPadreF:        //"Block SMSs" button pressed
//                startActivity(new Intent(this,secret_main.class));
                Toast.makeText(getApplicationContext(),"Padre",Toast.LENGTH_SHORT).show();
                break;

        }

        startActivity(intent);
        finish();

    }
}
