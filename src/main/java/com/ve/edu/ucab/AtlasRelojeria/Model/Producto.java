package com.ve.edu.ucab.AtlasRelojeria.Model;

public class Producto {

/** Atributos de la clase Producto*/

    String nombre, descripcion;
    double precio;
    String id;
    int cantidad;

    public Producto(String nombre, String descripcion, double precio, String id, int cantidad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.id = id;
        this.cantidad = cantidad;
    }

    public Producto() {}

    /** Getters y Setters */

    public void AgregarProducto(int numero) {
        this.cantidad += numero;
    }

    public boolean RemoverProducto(int numero) {
        if(this.cantidad-numero >= 0){
            this.cantidad -= numero;
            return true;
        }
        else{
            return false;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return nombre +";"+ descripcion +";" + precio + ";" + id + ";" + cantidad;
    }
}
