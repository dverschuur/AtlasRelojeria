package com.ve.edu.ucab.AtlasRelojeria.Model;

import java.time.LocalDate;

public class Ventas {

    /** Atributos */
    LocalDate fecha;
    double monto;
    String idVenta, idProducto, idUsuario, direccion;

    /** Constructor */
    public Ventas(LocalDate fecha, String idVenta, double monto, String idProducto, String idUsuario, String direccion) {
        this.fecha = fecha;
        this.idVenta = idVenta;
        this.monto = monto;
        this.idProducto = idProducto;
        this.idUsuario = idUsuario;
        this.direccion = direccion;
    }

    public Ventas(){}

    /** Getters and Setters */
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return fecha + ";" + idVenta + ";" + monto + ";" + idProducto + ";" + idUsuario;
    }
}
