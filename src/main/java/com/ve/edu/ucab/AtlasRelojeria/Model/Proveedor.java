package com.ve.edu.ucab.AtlasRelojeria.Model;

public class Proveedor {
    String nombre, correo, telefono, rif;

    // constructor
    public Proveedor(String nombre, String correo, String telefono, String rif) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.rif = rif;
    }

    public Proveedor() {
    }

    /**GETTER AND SETTERS**/

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRif() {
        return rif;
    }

    public void setRif(String rif) {
        this.rif = rif;
    }
}
