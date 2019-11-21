package com.example.rutas.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rutas.Adapter.AdapterRutasActivas;
import com.example.rutas.R;
import com.example.rutas.Utilidades.Utilidades_Request;
import com.example.rutas.entidades.Conductore;
import com.example.rutas.entidades.Rutasa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainColegio extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    private StringRequest stringRequest;

    RecyclerView recyclerRutasActivas;

    ArrayList<Rutasa> rutasas;
    ArrayList<Conductore> conductors;
    ArrayList<String> listaConductores;

    String condu ="0";
    String id_colegio, txt, name_cole;

    AlertDialog dialogr;

    SwipeRefreshLayout swipeRefresh;

    Context context;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_colegio);

        //titulo action bar
        this.setTitle("Panel de control");

        rutasas = new ArrayList<>();
        conductors = new ArrayList<>();

        recyclerRutasActivas= (RecyclerView) findViewById(R.id.idRecyclerView_rutasA);
        recyclerRutasActivas.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerRutasActivas.setHasFixedSize(true);

        request = Volley.newRequestQueue(getApplicationContext());

        context = MainColegio.this;

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRC);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        onRefresh();



        SharedPreferences prefe=getSharedPreferences("datos", Context.MODE_PRIVATE);
        id_colegio = prefe.getString("Sid","");
        name_cole = prefe.getString("Sname","");
    }

    @Override
    public void onRefresh() {
        cargarWebServiceRutasActivas();
    }

    /*------------------------------WS--------------------------------*/
    private void cargarWebServiceRutasActivas() {
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "_ws_rutas-activas_.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                JSONArray json = response.optJSONArray("rutas_activas");
                Rutasa rutasa = null;
                rutasas.clear();

                try {
                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        if (jsonObject.optInt("id") != 0){
                            rutasa = new Rutasa();
                            rutasa.setId(jsonObject.optInt("id"));
                            rutasa.setNombre_ruta(jsonObject.optString("ruta"));
                            rutasa.setNombre_c(jsonObject.optString("conductor_name"));
                            rutasa.setConductor_id(jsonObject.optInt("conductor_id"));
                            rutasa.setEstado(jsonObject.optInt("estado"));
                            rutasas.add(rutasa);
                        }

                    }

                    if (rutasas.size()>0){
                        AdapterRutasActivas adapter = new AdapterRutasActivas(rutasas);
                        recyclerRutasActivas.setAdapter(adapter);
                    }
                    swipeRefresh.setRefreshing(false);
                    cargarWebServiceConductor();

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "Error no hay conexion con la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(jsonObjectRequest);
    }

    private void cargarWebServiceConductor() {
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "_ws_conductor_.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                JSONArray json = response.optJSONArray("conductor");
                Conductore conductore = null;
                conductors.clear();

                try {
                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        conductore = new Conductore();
                        conductore.setId_conductor(jsonObject.optInt("id_conductor"));
                        conductore.setNombre(jsonObject.optString("nombre"));
                        conductore.setApellido(jsonObject.optString("apellido"));
                        conductore.setTelefono(jsonObject.optString("telefono"));
                        conductore.setDireccion(jsonObject.optString("direccion"));

                        conductors.add(conductore);
                    }
                    //
                    obtenerListConductore();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error no hay conexion con la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(jsonObjectRequest);
    }

    private void cargarWebServiceRegistroRuta(final String cole, final String conduc, final String nom) {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"_ws_registro-ruta_.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("registra")){
                    //mensajeAlertaTextViewError("No registro ocurrio un error vuelva a intentarlo. ", 3000);
                    Toast.makeText(getApplicationContext(), "registro exitoso",Toast.LENGTH_SHORT).show();
                    txt="";
                    onRefresh();
                }else{
                    //mensajeAlertaTextViewVerdadero("Obra construida registrada con Exito.", 2000);
                    //cargarWebServiceActivid(id);
                    Toast.makeText(getApplicationContext(), "Ocurrio un error",Toast.LENGTH_SHORT).show();

                }
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
                paramentros.put("id_colegio", cole);
                paramentros.put("id_conductor", conduc);
                paramentros.put("nombre", nom);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    private void cargarWebServiceDeleteConductor(final String id) {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"_ws_delete-conductor_.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("registra")){
                    //mensajeAlertaTextViewError("No registro ocurrio un error vuelva a intentarlo. ", 3000);
                    Toast.makeText(getApplicationContext(), "Eliminado",Toast.LENGTH_SHORT).show();
                    onRefresh();
                }else{
                    //mensajeAlertaTextViewVerdadero("Obra construida registrada con Exito.", 2000);
                    //cargarWebServiceActivid(id);
                    Toast.makeText(getApplicationContext(), "Ocurrio un error",Toast.LENGTH_SHORT).show();

                }
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
                paramentros.put("id", id);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    /*------------------------------ALERTING DIALOG--------------------*/
    private void showAlertPrincipal(final String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title != null) builder.setTitle(title);

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_principal, null);
        builder.setView(viewInflated);

        final AlertDialog dialog = builder.create();


        final Button ruta = (Button) viewInflated.findViewById(R.id.btnAddRuta);
        final Button conductor = (Button) viewInflated.findViewById(R.id.btnAddConductor);


        ruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertRutas("RUTAS", "");
                dialog.cancel();
            }
        });

        conductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistroConductorActivity.class));
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void showAlertRutas(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_add_ruta, null);
        builder.setView(viewInflated);

        final EditText nombre = (EditText) viewInflated.findViewById(R.id.editNombreRuta);
        nombre.setText(txt);
        final Spinner spinner_conductor = (Spinner) viewInflated.findViewById(R.id.spinnerConductor);
        final ImageButton delete = (ImageButton) viewInflated.findViewById(R.id.btnDiaDelete);
        final ImageButton delete_txt = (ImageButton) viewInflated.findViewById(R.id.btnClearText);

        nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Toast.makeText(context, "escrito=" + s.toString(),Toast.LENGTH_SHORT).show();
                if(s.length()> 0){
                    delete_txt.setVisibility(View.VISIBLE);
                }else {
                    delete_txt.setVisibility(View.INVISIBLE);
                }
                txt = s.toString();
            }
        });

        ArrayAdapter<CharSequence> adapter= new ArrayAdapter(this, android.R.layout.simple_spinner_item,listaConductores);
        spinner_conductor.setAdapter(adapter);
        //
        delete_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt="";
                nombre.setText("");
            }
        });
        //
        spinner_conductor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if( !(listaConductores.get(position).equals("Seleccione conductor.")) ){
                    for(int i=0; i<conductors.size(); i++){
                        String name = conductors.get(i).getNombre() + " " + conductors.get(i).getApellido();
                        if (name.equals(listaConductores.get(position))){
                            condu = conductors.get(i).getId_conductor().toString();
                        }
                    }
                }else {
                    condu = "0";
                }
                //Toast.makeText(context, "idco=" + condu,Toast.LENGTH_SHORT).show();

                if ( !(condu.equals("0"))){
                    //Toast.makeText(getApplicationContext(), condu, Toast.LENGTH_SHORT).show();
                    delete.setVisibility(View.VISIBLE);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int dele_vali_condu = 0;
                            for(int i=0; i<rutasas.size(); i++){
                                if (rutasas.get(i).getConductor_id().toString().equals(condu)){
                                    dele_vali_condu=1;
                                }
                            }
                            if(dele_vali_condu==1){
                                Toast.makeText(context, "No se puede eliminar. Este conductor esta activo con la ruta. -ELIMINA PRIMERO LA RUTA-", Toast.LENGTH_SHORT).show();
                            }else {
                                cargarWebServiceDeleteConductor(condu);
                            }
                            dialogr.cancel();

                        }
                    });
                }else {
                    delete.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nom = nombre.getText().toString().trim();

                if(nom.length() == 0 ) {
                    //Toast.makeText(getApplicationContext(), "IDConductor: " + condu + " ID_COL: " + id_colegio,Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Campos vacio, Llenar el nombre.", Toast.LENGTH_SHORT).show();

                }else if(condu.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Seleccione un conductor.", Toast.LENGTH_SHORT).show();
                }else{
                    //
                    int condu_vali=0;
                    int name_route=0;

                    String ruta_name = message + nom;
                    //
                    for(int i=0; i<rutasas.size(); i++){
                        if ( rutasas.get(i).getConductor_id().toString().equals(condu) && rutasas.get(i).getEstado().equals(0)){
                            condu_vali=1;
                        }
                    }

                    for(int a=0; a<rutasas.size(); a++){

                        if ( rutasas.get(a).getNombre_ruta().equals(ruta_name) && rutasas.get(a).getEstado().equals(0)){
                            name_route=1;
                        }
                    }
                    //Toast.makeText(context,null,Toast.LENGTH_SHORT).show();
                    if (name_route == 1){
                        Toast.makeText(getApplicationContext(), "Este nombre de ruta ya esta registrado.", Toast.LENGTH_SHORT).show();
                    }else if(condu_vali==1){
                        Toast.makeText(getApplicationContext(), "Este conductor ya tiene ruta asignada.", Toast.LENGTH_SHORT).show();
                    }else{

                        cargarWebServiceRegistroRuta(id_colegio, condu, ruta_name);
                    }

                }
            }
        });
        dialogr = builder.create();
        dialogr.show();

    }

    //----------------------------ADAPTERS------------------------------
    private void obtenerListConductore() {
        listaConductores = new ArrayList<String>();

        listaConductores.add("Seleccione conductor.");
        for(int i=0; i<conductors.size(); i++) {
            listaConductores.add(/*encuestass.get(i).getId_encuesta() + " - " +*/ conductors.get(i).getNombre() + " " + conductors.get(i).getApellido());
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity, menu);

        Drawable yourdrawable = menu.getItem(0).getIcon();
        yourdrawable.mutate();
        yourdrawable.setColorFilter(getResources().getColor(R.color.White), PorterDuff.Mode.SRC_IN);


        Drawable yourdrawable2 = menu.getItem(1).getIcon();
        yourdrawable2.mutate();
        yourdrawable2.setColorFilter(getResources().getColor(R.color.White), PorterDuff.Mode.SRC_IN);

        Drawable dos = menu.getItem(2).getIcon();
        dos.mutate();
        dos.setColorFilter(getResources().getColor(R.color.White), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.addRutas:
                // startActivity(getIntent());
                String cole_name = name_cole + " - ";
                showAlertRutas("RUTAS", cole_name);
                break;
            case R.id.addConductor:
                startActivity(new Intent(getApplicationContext(), RegistroConductorActivity.class));
                // startActivity(getIntent());
                break;
            case R.id.salir:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                // startActivity(getIntent());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
