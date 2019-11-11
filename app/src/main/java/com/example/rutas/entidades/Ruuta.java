package com.example.rutas.entidades;

public class Ruuta {
    Integer id_resu_ruta;
    String nombre_ruta;
    String nombre_conductor;
    String fecha_inicio;

    public Integer getId_resu_ruta() {
        return id_resu_ruta;
    }

    public void setId_resu_ruta(Integer id_resu_ruta) {
        this.id_resu_ruta = id_resu_ruta;
    }

    public String getNombre_ruta() {
        return nombre_ruta;
    }

    public void setNombre_ruta(String nombre_ruta) {
        this.nombre_ruta = nombre_ruta;
    }

    public String getNombre_conductor() {
        return nombre_conductor;
    }

    public void setNombre_conductor(String nombre_conductor) {
        this.nombre_conductor = nombre_conductor;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }
}
