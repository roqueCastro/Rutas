package com.example.rutas.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rutas.Adapter.AdapterRutasActivas;
import com.example.rutas.Adapter.AdapterSubCole;
import com.example.rutas.R;
import com.example.rutas.Utilidades.Utilidades_Request;
import com.example.rutas.entidades.Rutasa;
import com.example.rutas.entidades.SubCole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubMainColegio extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RecyclerView.OnItemTouchListener {

    //  var de la ruta
    String id_ruta;

    RecyclerView recyclerRutasActivas;

    SwipeRefreshLayout swipeRefresh;

    //VOLLEY
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    private StringRequest stringRequest;

    //ARRAY LIST
    ArrayList<SubCole> subColes;

    //GESTO TOUCH

    Context context;
    GestureDetector mGestureDetector;

    AlertDialog dialogr;

    TextView  fecha, nombre_ruta, nombre_conductor,pasajeros,duracion,telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_main_colegio);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        request = Volley.newRequestQueue(getApplicationContext());

        subColes = new ArrayList<>();

        context = SubMainColegio.this;

        id_ruta = getIntent().getStringExtra("id_ruta");

        recyclerRutasActivas= (RecyclerView) findViewById(R.id.idRecyclerView_rutasSC);
        recyclerRutasActivas.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerRutasActivas.setHasFixedSize(true);
        recyclerRutasActivas.addOnItemTouchListener(this);

        mGestureDetector = new GestureDetector(SubMainColegio.this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRSC);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        onRefresh();
    }

    /*-----------------WS ----------------------------*/
    private void cargarWebServiceSubCole() {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"_ws_sub-colegio_.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                SubCole subCole =null;
                if(response.equals("00")){
                    Toast.makeText(getApplicationContext(), "Este colegio esta vacio." ,Toast.LENGTH_SHORT).show();
                    finish();
                }else  if(response.equals("000")){
                    Toast.makeText(getApplicationContext(), "Error Volley." ,Toast.LENGTH_SHORT).show();
                }else {
                    subColes.clear();
                    try {
                        //
                        JSONObject respons = new JSONObject(response);
                        JSONArray json = respons.optJSONArray("rutas_sub_cole");

                        /**/
                        for (int i = 0; i < json.length(); i++) {

                            JSONObject jsonObject = null;
                            jsonObject = json.getJSONObject(i);

                            subCole = new SubCole();
                            subCole.setId_resu_ruta(jsonObject.optInt("id_resu_ruta"));
                            subCole.setNom_ruta(jsonObject.optString("nombre_ruta"));
                            subCole.setEstado(jsonObject.optString("estado_ruta"));
                            subCole.setFecha_inicio(jsonObject.optString("fecha_inicio"));
                            subCole.setFecha_fin(jsonObject.optString("fecha_final"));
                            subCole.setPasajeros(jsonObject.optString("pasajeros_finales_ruta"));
                            subCole.setName_conductor(jsonObject.optString("name_conductor"));
                            subCole.setTelefono_conductor(jsonObject.optString("telefono_conductor"));
                            subColes.add(subCole);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //titulo action bar
                    SubMainColegio.this.setTitle(subColes.get(0).getNom_ruta());
                    //
                    AdapterSubCole adapter = new AdapterSubCole(subColes);
                    recyclerRutasActivas.setAdapter(adapter);

                }
                swipeRefresh.setRefreshing(false);
                //Toast.makeText(getApplicationContext(), response ,Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mensajeAlertaTextViewError("Ocurrio un error en el servidor ", 3000);
                Log.i("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> paramentros = new HashMap<>();
                paramentros.put("id", id_ruta);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    /*------------------------ON REFRESH-----------------------*/
    @Override
    public void onRefresh() {
        cargarWebServiceSubCole();
    }

    /*----------------------------RECYCLER ON ITEM CLIC*/
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

        try {
            View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

            if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                int position = recyclerView.getChildAdapterPosition(child);

                //
                if (subColes.get(position).getEstado().equals("0")){
                    Intent intent = new Intent(getApplication(), NavigationActivityRoute.class);
                    intent.putExtra("ids", subColes.get(position).getId_resu_ruta().toString());
                    startActivity(intent);
                }else {
                    showAlertViewInfoRuta("Info Ruta", position);
                }
                //

                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        int position = recyclerView.getChildAdapterPosition(child);

        Toast.makeText(getApplicationContext(),"name conductor: "+ subColes.get(position).getName_conductor() ,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    /*---------------------------- PROGRES DIALOG--------------*/

    private void showAlertViewInfoRuta(String title, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title != null) builder.setTitle(title);

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_view_ruta, null);
        builder.setView(viewInflated);


         fecha = (TextView) viewInflated.findViewById(R.id.textViewFechaI);

         nombre_ruta = (TextView) viewInflated.findViewById(R.id.textViewNamRut);
         nombre_conductor = (TextView) viewInflated.findViewById(R.id.textViewNameConductor);
         pasajeros = (TextView) viewInflated.findViewById(R.id.textViewPasajeros);
         duracion = (TextView) viewInflated.findViewById(R.id.textViewDuracion);
         telefono = (TextView) viewInflated.findViewById(R.id.textViewTelefono);

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

            Date dateStart = dateFormat.parse(subColes.get(position).getFecha_inicio());

            Date dateEnd = dateFormat.parse(subColes.get(position).getFecha_fin());

            /*--------------------Duracion------------------------*/
            int horas = (dateEnd.getHours() - dateStart.getHours());
            int minutosn = (60 - dateEnd.getMinutes());
            int minutos = (minutosn- dateStart.getMinutes());
            int m;
            String msj;
            if(minutos < 0){
                m = minutos * -1;
            }else {
                m=minutos;
            }

            String hourr;
            if (horas <= 1){
                hourr = " Hora, ";
            }else{
                hourr=" Horas, ";
            }
            msj = horas + hourr + m + " Minutos";

            /*--------------------Fecha VIew Principal-----------*/
            String MES[] = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

            // Dias de la semana
            String DIA[] = {"Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"};

            String strDateFormat = "yyyy hh:mm a"; // El formato de fecha está especificado
            SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);



            String fechaview  = DIA[dateEnd.getDay()] + " " + dateEnd.getDate() + " de " + MES[dateEnd.getMonth()]
                    + " del " +  objSDF.format(dateEnd);


            fecha.setText(fechaview);
            nombre_ruta.setText(subColes.get(position).getNom_ruta());
            nombre_conductor.setText(subColes.get(position).getName_conductor());
            pasajeros.setText(subColes.get(position).getPasajeros());
            duracion.setText(msj);
            telefono.setText(subColes.get(position).getTelefono_conductor());


        } catch (ParseException e) {
            e.printStackTrace();
        }

        builder.setNegativeButton("Cerrar", null);
        dialogr = builder.create();
        dialogr.show();

    }


    /*--------------------MENU OPTIONS-----------------------*/

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
