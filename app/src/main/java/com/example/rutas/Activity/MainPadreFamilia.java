package com.example.rutas.Activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rutas.Adapter.AdapterRutasActivas;
import com.example.rutas.Adapter.AdaterRutasView;
import com.example.rutas.R;
import com.example.rutas.Utilidades.Utilidades_Request;
import com.example.rutas.entidades.Rutasa;
import com.example.rutas.entidades.Ruuta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainPadreFamilia extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener {

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    SwipeRefreshLayout swipeRefresh;

    ArrayList<Ruuta> rutas;

    RecyclerView recyclerRutasActivas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_padre_familia);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        request = Volley.newRequestQueue(getApplicationContext());

        rutas = new ArrayList<>();

        recyclerRutasActivas= (RecyclerView) findViewById(R.id.idRecyclerView_rutasV);
        recyclerRutasActivas.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerRutasActivas.setHasFixedSize(true);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeR);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        onRefresh();

    }

    private void cargarWebServiceRutasActivas() {
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "_ws_rutas-view_.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();


                JSONArray json = response.optJSONArray("rutas_view");
                Ruuta ruuta = null;
                rutas.clear();

                try {
                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        if (jsonObject.optInt("resu_ruta_id") != 0){
                            ruuta = new Ruuta();
                            ruuta.setId_resu_ruta(jsonObject.optInt("resu_ruta_id"));
                            ruuta.setNombre_ruta(jsonObject.optString("ruta_name"));
                            ruuta.setNombre_conductor(jsonObject.optString("conductor_name"));
                            ruuta.setFecha_inicio(jsonObject.optString("inicio_fecha"));
                            rutas.add(ruuta);
                        }

                    }

                    if (rutas.size()>0){
                        AdaterRutasView adapter = new AdaterRutasView(rutas);
                        recyclerRutasActivas.setAdapter(adapter);
                    }

                    swipeRefresh.setRefreshing(false);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error no hay conexion con la base de datos", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });
        request.add(jsonObjectRequest);
    }

    @Override
    public void onRefresh() {
        cargarWebServiceRutasActivas();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}

