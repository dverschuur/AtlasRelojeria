package com.ve.edu.ucab.AtlasRelojeria.Model;

public class Perfil {
    String nombre;
    String id;
    int edad;

    /** Constructores */
    public Perfil(String nombre, String id, int edad) {
        this.nombre = nombre;
        this.id = id;
        this.edad = edad;
    }

    public Perfil() {}

    /** Getters and Setters */

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public String toString() {
        return nombre + ";" + id +";" + edad;
    }
}
