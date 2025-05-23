package com.ve.edu.ucab.AtlasRelojeria.Model;

import java.time.LocalDateTime;

public class Ventas {

    /** Atributos */
    LocalDateTime fecha;
    int idVenta;
    String monto, idProducto, idUsuario;

    /** Constructor */
    public Ventas(LocalDateTime fecha, int idVenta, String monto, String idProducto, String idUsuario) {
        this.fecha = fecha;
        this.idVenta = idVenta;
        this.monto = monto;
        this.idProducto = idProducto;
        this.idUsuario = idUsuario;
    }

    public Ventas(){}

    /** Getters and Setters */
    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
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

    @Override
    public String toString() {
        return fecha + ";" + idVenta + ";" + monto + ";" + idProducto + ";" + idUsuario;
    }
}
