package com.ve.edu.ucab.AtlasRelojeria.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Producto {

/** Atributos de la clase Producto*/

    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;
    private String imagen;
    private String proveedor;

    public Producto(String nombre, String descripcion, double precio, String id, int cantidad, String imagen, String proveedor) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.id = id;
        this.cantidad = cantidad;
        this.imagen = imagen;
        this.proveedor = proveedor;
    }

    public Producto() {}

    /** Getters y Setters */

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return nombre +";"+ descripcion +";" + precio + ";" + id + ";" + cantidad + ";" + proveedor;
    }
}
