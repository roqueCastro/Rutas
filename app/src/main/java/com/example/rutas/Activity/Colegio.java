package com.example.rutas.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rutas.R;
import com.example.rutas.Utilidades.Utilidades_Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Colegio extends AppCompatActivity {

    private EditText _emailText,_passwordText;
    private Button _loginButton;
    String passCorrect, id;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colegio);

        request = Volley.newRequestQueue(getApplicationContext());

        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);

        passCorrect = "0";

        _loginButton =(Button) findViewById(R.id.btn_login);

        SharedPreferences prefe=getSharedPreferences("datos", Context.MODE_PRIVATE);
        String id = prefe.getString("Sid","");
        String user = prefe.getString("Suser","");
        String pass = prefe.getString("Spass","");

        if((user != "") && (pass != "")){
            _emailText.setText(user);
            _passwordText.setText(pass);
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               login();
            }
        });
    }

    private void login() {
        cargarWebServiceUser(_emailText.getText().toString());
    }

    private void cargarWebServiceUser(final String email) {
        _loginButton.setEnabled(false);
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + Utilidades_Request.ARCHIVO + email;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                JSONArray json = response.optJSONArray("usuarios");

                try {
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        id = (String) jsonObject.get("id_colegio");
                        passCorrect= (String) jsonObject.get("pass");
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
//
                if(!validate()){
                    _loginButton.setEnabled(true);
                    return;
                }else{
                    SharedPreferences preferencias=getSharedPreferences("datos",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferencias.edit();
                    editor.putString("Sid", id);
                    editor.putString("Suser", email);
                    editor.putString("Spass", passCorrect);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Bienvenido.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainColegio.class));
                    finish();
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

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if(email.isEmpty() || this.id.equals("0")) {
            _emailText.setError("Introduzca una dirección de correo electrónico válida");
            valid=false;
        }else {
            _emailText.setError(null);
        }

        if(password.isEmpty() || password.length() == 0) {
            _passwordText.setError("Ingresa caracteres.");
            valid=false;
        }else  {
            _passwordText.setError(null);

        }

        if(this.passCorrect.equals(password)) {
            _passwordText.setError(null);
        }else  {
            _passwordText.setError("Contraseña incorrecta.");
            valid=false;
        }

        return valid;
    }
}