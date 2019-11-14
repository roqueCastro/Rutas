package com.example.rutas.entidades;

import android.content.Intent;

public class SubCole {
    Integer id_resu_ruta;
    String nom_ruta;
    String estado;
    String fecha_inicio;
    String fecha_fin;
    String pasajeros;
    String name_conductor;
    String telefono_conductor;

    public Integer getId_resu_ruta() {
        return id_resu_ruta;
    }

    public void setId_resu_ruta(Integer id_resu_ruta) {
        this.id_resu_ruta = id_resu_ruta;
    }

    public String getNom_ruta() {
        return nom_ruta;
    }

    public void setNom_ruta(String nom_ruta) {
        this.nom_ruta = nom_ruta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(String pasajeros) {
        this.pasajeros = pasajeros;
    }

    public String getName_conductor() {
        return name_conductor;
    }

    public void setName_conductor(String name_conductor) {
        this.name_conductor = name_conductor;
    }

    public String getTelefono_conductor() {
        return telefono_conductor;
    }

    public void setTelefono_conductor(String telefono_conductor) {
        this.telefono_conductor = telefono_conductor;
    }
}
