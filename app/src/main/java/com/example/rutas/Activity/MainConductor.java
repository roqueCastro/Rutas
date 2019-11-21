package com.example.rutas.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rutas.R;
import com.example.rutas.Utilidades.Utilidades_Request;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainConductor extends AppCompatActivity implements PermissionsListener, LocationListener {

    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 15000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

    private PermissionsManager permissionsManager;

    private LocationEngine locationEngine;
    LocationManager manager;

    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);

    String id_conductor, id_ruta, lat, lng, id_resu_ruta;
    Context context;

    Button activar_gps, envio_cantidad;
    EditText cantidad;

//    VAR WEB SERVICES
    StringRequest stringRequest;
    RequestQueue request;

//    DIALOG ALERT
    AlertDialog dialogr;

//    TIEMPO REBUILD
    Timer timer;

    Resources resources;

    int gpsEnable = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_conductor);


        context = MainConductor.this;

        request = Volley.newRequestQueue(getApplicationContext());

        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
        id_conductor = prefe.getString("Sid", "");
        id_ruta = prefe.getString("Sruta", "");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        resources = context.getResources();

        cantidad = (EditText) findViewById(R.id.input_cantidad);
        envio_cantidad = (Button) findViewById(R.id.btnEnvioCantidad);
        activar_gps = (Button) findViewById(R.id.btnActivarGps);
        activar_gps.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //
                cargarServicioCoordenadas();
            }
        });

        envio_cantidad.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(!validcam()){
                    return;
                }
                cantidad.setFocusable(false);
                envio_cantidad.setEnabled(false);
                envio_cantidad.setCompoundDrawableTintList(ColorStateList.valueOf(R.color.colorPrimaryDark));
                cargarWebServiceUpdateCantidad();

            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton fab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fabReporte);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertRutas("Reporte un fallo.");
            }
        });

        cargarWebServiceInfo();
        time();

    }

    /*---------------------------ALERTAS---------------------------------*/

    private void showAlertRutas(String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title != null) builder.setTitle(title);

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_add_reporte, null);
        builder.setView(viewInflated);

        final EditText nombre = (EditText) viewInflated.findViewById(R.id.editDesReporte);


        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nom = nombre.getText().toString().trim();

                if(nom.length() == 0 ) {
                    //Toast.makeText(getApplicationContext(), "IDConductor: " + condu + " ID_COL: " + id_colegio,Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Campos vacio, Llenar la descripcion.", Toast.LENGTH_SHORT).show();

                }else if(id_resu_ruta == null && lat == null && lng == null) {
                    Toast.makeText(getApplicationContext(), "No tienes la ubicacion..!.", Toast.LENGTH_SHORT).show();
                }else{
                    cargarWebServiceRegistroRepote(nom);
                }
            }
        });
        dialogr = builder.create();
        dialogr.show();

    }

    /*--------------------------CARGA EL BOTON DE ACTIVAR GPS--------------*/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void cargarServicioCoordenadas() {
        cantidad.setFocusable(true);
        cantidad.setEnabled(true);
        activar_gps.setEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activar_gps.setCompoundDrawableTintList(resources.getColorStateList(R.color.Verde_Claro, context.getTheme()));
        }

        activar_gps.setBackgroundColor(Color.parseColor("#00000000"));
        activar_gps.setTextColor(Color.parseColor("#00000000"));
        envio_cantidad.setVisibility(View.VISIBLE);
        cargarWebServiceRegistroResuRuta();
    }

    /*-------------------------VALIDA CAMPO DE CANTIDAD-------------------*/
    private boolean validcam() {
        Boolean valid = true;

        String can = cantidad.getText().toString();


        if(can.isEmpty()) {
            cantidad.setError("Introduzca una cantidad");
            valid=false;
        }else {
            cantidad.setError(null);
        }

        return valid;
    }

    /*--------------------------WEB SERVICES ---------------------------*/

    private void cargarWebServiceInfo() {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"_ws_resu-ruta-info_.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("000")){
                    //mensajeAlertaTextViewError("No registro ocurrio un error vuelva a intentarlo. ", 3000);
                    Toast.makeText(getApplicationContext(), "Error en el sql PDO",Toast.LENGTH_SHORT).show();
                }else if(response.trim().equals("0")){
                    //mensajeAlertaTextViewVerdadero("Obra construida registrada con Exito.", 2000);
                }else {
                    //enviar coordenada -------
                    id_resu_ruta = response.trim();
                    cargarServicioCoordenadas();
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
                paramentros.put("id", id_ruta);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    private void cargarWebServiceRegistroResuRuta() {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"_ws_registro-resu-ruta_.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("000")){
                    //mensajeAlertaTextViewError("No registro ocurrio un error vuelva a intentarlo. ", 3000);
                    Toast.makeText(getApplicationContext(), "Error en el sql PDO",Toast.LENGTH_SHORT).show();
                }else if(response.trim().equals("00")){
                    //mensajeAlertaTextViewVerdadero("Obra construida registrada con Exito.", 2000);
                    Toast.makeText(getApplicationContext(), "Ocurrio un error no se pudo insertar",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Registro exitoso!..",Toast.LENGTH_SHORT).show();
                    id_resu_ruta = response.trim();
                    gpsEnaDis();
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
                paramentros.put("id", id_ruta);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    private void cargarWebServiceRegistroCoordenada() {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"_ws_registro-coordenada_.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("000")){
                    //mensajeAlertaTextViewError("No registro ocurrio un error vuelva a intentarlo. ", 3000);
                    Toast.makeText(getApplicationContext(), "Error en el sql PDO Coordenada",Toast.LENGTH_SHORT).show();
                }else if(response.trim().equals("00")){
                    //mensajeAlertaTextViewVerdadero("Obra construida registrada con Exito.", 2000);
                    Toast.makeText(getApplicationContext(), "Ocurrio un error no se pudo insertar coordenada",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Coordenada registrada!..",Toast.LENGTH_SHORT).show();
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
                paramentros.put("id", id_resu_ruta);
                paramentros.put("lat", lat);
                paramentros.put("lng", lng);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    private void cargarWebServiceRegistroRepote(String des) {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"_ws_registro-varada_.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), response ,Toast.LENGTH_SHORT).show();
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
                paramentros.put("lat", lat);
                paramentros.put("lng", lng);
                paramentros.put("des", des);
                paramentros.put("id_resu_ruta", id_resu_ruta);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    private void cargarWebServiceUpdateCantidad() {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"_ws_update-resu-ruta_.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("000")){
                    //mensajeAlertaTextViewError("No registro ocurrio un error vuelva a intentarlo. ", 3000);
                    Toast.makeText(getApplicationContext(), "Error en el sql PDO UPDATE",Toast.LENGTH_SHORT).show();
                }else if(response.trim().equals("00")){
                    //mensajeAlertaTextViewVerdadero("Obra construida registrada con Exito.", 2000);
                    Toast.makeText(getApplicationContext(), "Ocurrio un error no se pudo Update resultadoruta",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Teminaste..",Toast.LENGTH_SHORT).show();
                    destruirProcesos();
                    finish();
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
                paramentros.put("id", id_resu_ruta);
                paramentros.put("can", cantidad.getText().toString());
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    /*--------------------------HABILITAR PERMISOS Y GPS---------------------------*/

    private void gpsEnaDis() {
        //
        gpsEnable = 1;
        //
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertGPS("ACTIVAR", "GPS");
        } else {
            enableLocationComponent();
            //
            manager.removeUpdates(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        }
    }

    private void showAlertGPS(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        builder.setPositiveButton("Activar gps", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /*--------------------------CAPTURADORES DE COORDENADA MAPBOX---------------------------*/

    /*HABILITANDO COMPONENTES*/
    private void enableLocationComponent() {
        // Check if permissions are enabled and if not request ELIMINA LA LOCALIZACION
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            if (locationEngine != null) {
                locationEngine.removeLocationUpdates(callback);
            }
            //
            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /*INICIO DEL LA LOCALIZACION*/
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);


        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    /*CLASE CAPTURA COORDENADAS */
    private class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MainConductor> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(MainConductor activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        //        /*Capturador de coordenadas*/
        @Override
        public void onSuccess(LocationEngineResult result) {
            MainConductor activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                // Create a Toast which displays the new location's coordinates
                lat = String.valueOf(result.getLastLocation().getLatitude());
                lng = String.valueOf(result.getLastLocation().getLongitude());

                //Toast.makeText(activity, "Ubicacion: \n" + "Latitud: " + lat + "\n" + "Longitud: " + lng,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            Toast.makeText(getApplicationContext(), "FALLANDO GPS = " + exception.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
            if (locationEngine != null) {
                locationEngine.removeLocationUpdates(callback);
                manager.removeUpdates(MainConductor.this);
                lat=null;
            }
        }
    }

    /*-------------------------PERMISOS Y LOCALIZACIONES------------------------------------*/

    /*PERMISION MANAGER*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent();
        } else {
            Toast.makeText(this, "Activa Permisos..", Toast.LENGTH_LONG).show();
            gpsEnaDis();
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        //Toast.makeText(this, "Permiison Expain", Toast.LENGTH_LONG).show();

    }

    /*LOCATION LISTENER*/
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(), "GPS INACTIVO", Toast.LENGTH_SHORT).show();
        gpsEnaDis();
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


    /*---------TIME SEND-----------------*/

    public void time(){
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                MainConductor.this.runOnUiThread(new Runnable() {
                    public void run() {
                        // update UI here
                        if(id_resu_ruta != null && lat != null && lng != null){

                            cargarWebServiceRegistroCoordenada();
                            System.out.println("SEND_COOORDENADA");
                        }else {
                            System.out.println("fallido");
                            if (gpsEnable == 1){
                                gpsEnaDis();
                                System.out.println("GPS ENABLE");
                            }
                        }
                    }
                });
            }
        },0, 30000);
    }

    /*-------------------CLASES FINALS DESTRUCCION GPS ACTIVO----------------------*/

    private void destruirProcesos() {
        if (locationEngine != null) {
            timer.cancel();
            locationEngine.removeLocationUpdates(callback);
            manager.removeUpdates(this);
        }
        if (manager != null){
            manager.removeUpdates(this);
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destruirProcesos();

    }

}
