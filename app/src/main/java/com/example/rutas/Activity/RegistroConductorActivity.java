package com.example.rutas.Activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import butterknife.BindView;

public class RegistroConductorActivity extends AppCompatActivity {

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    ArrayList<Conductore> conductors = new ArrayList<>();;

    EditText _nombre, _apellido, _telefono, _direccion, _emailText, _passwordText, _reEnterPasswordText;
    ImageButton _signupButton;

    String name, apellido, direccion, email, mobile, password, reEnterPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_conductor);

        //titulo action bar
        this.setTitle("Registro Conductor");

        request = Volley.newRequestQueue(getApplicationContext());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        _nombre = (EditText) findViewById(R.id.input_nombre);
        _apellido = (EditText) findViewById(R.id.input_apellido);
        _telefono = (EditText) findViewById(R.id.input_telefono);
        _direccion = (EditText) findViewById(R.id.input_direccion);
        _emailText = (EditText) findViewById(R.id.input_usuario);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);

        _signupButton = (ImageButton) findViewById(R.id.btn_signup);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        cargarWebServiceConductor();
    }

    /*CLIC CREAR*/
    public void signUp() {
        _signupButton.setEnabled(false);
        if (!validate()) {
            _signupButton.setEnabled(true);
            return;
        }
        cargarWebServiceRegistroConductor(name,apellido,mobile,direccion,email,password);
    }


    /*WS*/
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
                        conductore.setUsuario(jsonObject.optString("userr"));

                        conductors.add(conductore);
                    }
                    //

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

    private void cargarWebServiceRegistroConductor(final String nom, final String ape, final String tel, final String dir, final String usu, final String pas) {

        _signupButton.setEnabled(false);

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"_ws_registro-conductor_.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equals("registra")){
                    //mensajeAlertaTextViewError("No registro ocurrio un error vuelva a intentarlo. ", 3000);
                    Toast.makeText(getApplicationContext(), "registro exitoso",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    //mensajeAlertaTextViewVerdadero("Obra construida registrada con Exito.", 2000);
                    //cargarWebServiceActivid(id);
                    Toast.makeText(getApplicationContext(), "Ocurrio un error, no registro.",Toast.LENGTH_SHORT).show();
                    _signupButton.setEnabled(true);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mensajeAlertaTextViewError("Ocurrio un error en el servidor ", 3000);
                _signupButton.setEnabled(true);
                Log.i("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> paramentros = new HashMap<>();
                paramentros.put("nombre", nom);
                paramentros.put("apellido", ape);
                paramentros.put("telefono", tel);
                paramentros.put("direccion", dir);
                paramentros.put("usuario", usu);
                paramentros.put("pass", pas);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    /*VALIDATION FORMS*/
    public boolean validate() {
        boolean valid = true;

        name = _nombre.getText().toString();
        apellido = _apellido.getText().toString();
        direccion = _direccion.getText().toString();
        email = _emailText.getText().toString();
        mobile = _telefono.getText().toString();
        password = _passwordText.getText().toString();
        reEnterPassword = _reEnterPasswordText.getText().toString();

        int user_vali=0;
        for(int i=0; i<conductors.size(); i++){
            if(conductors.get(i).getUsuario().equals(email)){
                user_vali=1;
            }
        }

        if (name.isEmpty() || name.length() < 3) {
            _nombre.setError("Minimo 3 letras");
            valid = false;
        } else {
            _nombre.setError(null);
        }

        if (apellido.isEmpty() || apellido.length() < 3) {
            _apellido.setError("Minimo 3 letras");
            valid = false;
        } else {
            _apellido.setError(null);
        }
        if (direccion.isEmpty() || direccion.length() < 3) {
            _direccion.setError("Minimo 3 letras");
            valid = false;
        } else {
            _direccion.setError(null);
        }


        if (email.isEmpty() || user_vali == 1 ) {
            if(email.isEmpty()){
                _emailText.setError("Usuario vacio.");
            }else {
                _emailText.setError("Usuario ya existe!");
            }
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=10) {
            _telefono.setError("Ingrese un numero de móvil valido");
            valid = false;
        } else {
            _telefono.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("LLenar campo");
            valid = false;
        } else {
            _passwordText.setError(null);
        }


        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("La contraseña no coincide");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }
        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
