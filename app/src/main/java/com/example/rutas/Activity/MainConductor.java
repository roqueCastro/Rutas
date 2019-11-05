package com.example.rutas.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.rutas.R;

public class MainConductor extends AppCompatActivity {

    String id_conductor;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_conductor);

        context = MainConductor.this;

        SharedPreferences prefe=getSharedPreferences("datos", Context.MODE_PRIVATE);
        id_conductor = prefe.getString("Sid","");
        Toast.makeText(context, "id="+id_conductor,Toast.LENGTH_SHORT).show();
    }
}
