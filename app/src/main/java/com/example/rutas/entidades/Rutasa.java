package com.example.rutas.entidades;

import android.content.Intent;

public class Rutasa {

    private Integer id;
    private String nombre_ruta;
    private String nombre_c;
    private String apellido;
    Integer conductor_id;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre_ruta() {
        return nombre_ruta;
    }

    public void setNombre_ruta(String nombre_ruta) {
        this.nombre_ruta = nombre_ruta;
    }

    public String getNombre_c() {
        return nombre_c;
    }

    public void setNombre_c(String nombre_c) {
        this.nombre_c = nombre_c;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getConductor_id() {
        return conductor_id;
    }

    public void setConductor_id(Integer conductor_id) {
        this.conductor_id = conductor_id;
    }
}
