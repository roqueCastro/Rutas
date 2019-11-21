package com.example.rutas.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rutas.R;
import com.example.rutas.Utilidades.Utilidades_Request;
import com.example.rutas.entidades.Coordenada;
import com.example.rutas.entidades.Rutasa;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textOffset;

public class NavigationActivityRoute extends AppCompatActivity implements OnMapReadyCallback , View.OnClickListener {

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    ArrayList<Coordenada> coordenadas;

    String id_resu_ruta, distancia, duraccion;
    int code_get_route = 0;

    Button btnDur, btnDis, btnInicio;

    private MapboxMap mapboxMap;
    private MapView mapView;
    private Context context;

    private static final String PROFILE_NAME = "PROFILE_NAME";
    private static final String ORIGEN = "ORIGIN";
    private static final String DESTINO = "DESTINATION";

    private static final String SOURCE_ID = "source-id";
    private  GeoJsonSource jsonSource;

    private Point carro,colegio;

    private static final String DRIVING_ROUTE_POLYLINE_LINE_LAYER_ID = "DRIVING_ROUTE_POLYLINE_LINE_LAYER_ID";
    private static final String DRIVING_ROUTE_POLYLINE_SOURCE_ID = "DRIVING_ROUTE_POLYLINE_SOURCE_ID";

    private static final float NAVIGATION_LINE_WIDTH = 6;
    private static final float NAVIGATION_LINE_OPACITY = .8f;

    private NavigationMapRoute navigationMapRoute;

    //
   Timer timer;

   int btnActication = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.key_Token));
        setContentView(R.layout.activity_navigation_route);

        //titulo action bar
        this.setTitle("Ruta en linea");

        context = NavigationActivityRoute.this;

        request = Volley.newRequestQueue(getApplicationContext());

        coordenadas = new ArrayList<>();

        id_resu_ruta = getIntent().getStringExtra("ids");

        btnDis = (Button) findViewById(R.id.btnDistancia);
        btnDur = (Button) findViewById(R.id.btnDuracion);
        btnInicio = (Button) findViewById(R.id.btninicioColegio);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mapView = findViewById(R.id.mapViewNavi);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    /*---------------------MAP---------------------------*/
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.TRAFFIC_DAY,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        time();
                    }
                });
    }

    private void obtenerPoint() {
        Style style = mapboxMap.getStyle();

        if (btnActication == 0){
            btnInicio.setEnabled(true);
            btnInicio.setBackgroundResource(R.color.mapbox_navigation_view_color_list_background);
            btnInicio.setOnClickListener(this);
        }

        Double carLat = Double.parseDouble(coordenadas.get(0).getLat());
        Double carLng = Double.parseDouble(coordenadas.get(0).getLng());

        Double colLat = Double.parseDouble(coordenadas.get(0).getCol_lat());
        Double colLng = Double.parseDouble(coordenadas.get(0).getCol_lng());

        carro = Point.fromLngLat(carLng, carLat);
        colegio = Point.fromLngLat(colLng, colLat);

        Feature origFeature = Feature.fromGeometry(Point.fromLngLat(carro.longitude(),
                carro.latitude()));
        origFeature.addStringProperty(PROFILE_NAME, ORIGEN);

        Feature destiFeature = Feature.fromGeometry(Point.fromLngLat(colegio.longitude(),
                colegio.latitude()));
        destiFeature.addStringProperty(PROFILE_NAME, DESTINO);





        if (jsonSource != null) {
            jsonSource = mapboxMap.getStyle().getSourceAs(SOURCE_ID);

            jsonSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{origFeature,destiFeature}
            ));

        }else {
            /*--------------*/
            jsonSource = new GeoJsonSource(SOURCE_ID,
                    FeatureCollection.fromFeatures(new Feature[] {
                            origFeature,
                            destiFeature,
                    }));

            style.addSource(jsonSource);
            // Add a source and LineLayer for the snaking directions route line
            style.addSource(new GeoJsonSource(DRIVING_ROUTE_POLYLINE_SOURCE_ID));

            style.addLayerBelow(new LineLayer(DRIVING_ROUTE_POLYLINE_LINE_LAYER_ID,
                    DRIVING_ROUTE_POLYLINE_SOURCE_ID)
                    .withProperties(
                            lineWidth(NAVIGATION_LINE_WIDTH),
                            lineOpacity(NAVIGATION_LINE_OPACITY),
                            lineCap(LINE_CAP_ROUND),
                            lineJoin(LINE_JOIN_ROUND),
                            lineColor(Color.parseColor("#d742f4"))
                    ), "layer-id");

            style.addLayer(new SymbolLayer("layer_id", SOURCE_ID).withProperties(
                    iconImage(get(PROFILE_NAME)),
                    iconIgnorePlacement(true),
                    iconAllowOverlap(true),
//                    textField(get(PROFILE_NAME)),
                    textIgnorePlacement(true),
                    textAllowOverlap(true),
                    textOffset(new Float[] {0f, 2f})
            ));
            mapView.addOnStyleImageMissingListener(new MapView.OnStyleImageMissingListener() {
                @Override
                public void onStyleImageMissing(@NonNull String id) {
                    switch (id) {
                        case ORIGEN:
                            addImage(id, R.drawable.ic_directions_bus);
                            break;
                        case DESTINO:
                            addImage(id, R.drawable.ic_school);
                            break;
                    }
                }
            });
            /*-------------*/

        }

        if (code_get_route == 1){
            getRoute(carro,colegio);
        }

        LatLng posisionMarkerCamera = new LatLng(carLat, carLng);
        animarCamara(posisionMarkerCamera);
    }

    private void animarCamara(LatLng posisionMarkerCamera) {
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(posisionMarkerCamera)) // Sets the new camera position
                .zoom(14) // Sets the zoom
                .bearing(0) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 2000);
    }

    private void addImage(String id, int drawableImage) {
        Style style = mapboxMap.getStyle();
        if (style != null) {
            style.addImageAsync(id, BitmapUtils.getBitmapFromDrawable(
                    getResources().getDrawable(drawableImage)));
        }
    }

    private void getRoute(Point originPosition, Point destinationPosition) {
        NavigationRoute.builder(context)
                .accessToken(Mapbox.getAccessToken())
                .origin(originPosition)
                .destination(destinationPosition)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, retrofit2.Response<DirectionsResponse> response) {
                        if (response.body() == null){
                            System.out.println("No routes found, cheked user tokens");
                            return;
                        }else if(response.body().routes().size() == 0){
                            System.out.println("No routes found");
                            return;
                        }

                        if (navigationMapRoute != null){
                            navigationMapRoute.removeRoute();
                        }else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap);
                        }

                        DirectionsRoute currentRoute = response.body().routes().get(0);
                        distancia = currentRoute.distance().toString();
                        duraccion = currentRoute.duration().toString();
                        navigationMapRoute.addRoute(currentRoute);
                        viewDistanceMetricas();

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        System.out.println("Error " + t.getMessage());
                    }
                });
    }

    @SuppressLint("ResourceAsColor")
    private void viewDistanceMetricas() {
        Double dura = ((Double.parseDouble(duraccion)) / 60);

        DecimalFormat formato2 = new DecimalFormat("#.#");
        String numDuracion = formato2.format(dura);

        String msjDistancia, msjDuracion;

        Double EnDistancia = Double.parseDouble(distancia);
        Double EnDuracion = Double.parseDouble(numDuracion);

        if(EnDistancia > 1000){
            Double d = EnDistancia/1000;

            DecimalFormat format = new DecimalFormat("#.#");
            String distance = format.format(d);

            msjDistancia = "  " + distance  + " Km";
        }else {
            msjDistancia = "  " + distancia  + " Mtros";
        }

        if(EnDuracion > 60){
            Double d_u = EnDuracion / 60;

            DecimalFormat format = new DecimalFormat("#.#");
            String time = format.format(d_u);

            msjDuracion = time  + " H  ";
        }else{
            msjDuracion = numDuracion  + " MIN  ";
        }

        //distancia
        btnDis.setEnabled(true);
        btnDis.setBackgroundColor(R.color.design_default_color_primary);
        btnDis.setTextColor(Color.parseColor("#ffffff"));
        btnDis.setText(msjDistancia);
        //duracion
        btnDur.setEnabled(true);
        btnDur.setBackgroundColor(R.color.design_default_color_primary);
        btnDur.setTextColor(Color.parseColor("#ffffff"));
        btnDur.setText(msjDuracion);
    }

    /*------------WS WEB-------------------------------------*/

    private void cargarWebServiceUser(final String id) {

        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "_ws_rutas-coor_.php?id=" + id;


        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                JSONArray json = response.optJSONArray("coor");
                Coordenada coordenada = null;
                coordenadas.clear();

                try {
                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        if (jsonObject.optInt("coordenada_id") != 0){
                            coordenada = new Coordenada();

                            coordenada.setRuta_name(jsonObject.optString("ruta_name"));
                            coordenada.setLat(jsonObject.optString("lat"));
                            coordenada.setLng(jsonObject.optString("lng"));
                            coordenada.setConductor_name(jsonObject.optString("conductor_name"));
                            coordenada.setCol_lat(jsonObject.optString("col_latitud"));
                            coordenada.setCol_lng(jsonObject.optString("col_longitud"));
                            coordenada.setCol_tel(jsonObject.optString("col_telefono"));

                            coordenadas.add(coordenada);
                        }

                      //  Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                        obtenerPoint();

                    }

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

    /*------------------------------------------------*/

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btninicioColegio:
                getRoute(carro,colegio);
                btnInicio.setEnabled(false);
                btnInicio.setBackgroundResource(Color.parseColor("#00000000"));
                code_get_route = 1;
                btnActication = 1;
                break;
        }
    }

    public void time(){
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                NavigationActivityRoute.this.runOnUiThread(new Runnable() {
                    public void run() {
                        // update UI here
                        System.out.println("CARGANDO SERVICIO WS USER");
                        cargarWebServiceUser(id_resu_ruta);
                    }
                });
            }
        },0, 30000);
    }

    /*------------------------------------------------*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
// Prevent leaks
        mapView.onDestroy();
        timer.cancel();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
