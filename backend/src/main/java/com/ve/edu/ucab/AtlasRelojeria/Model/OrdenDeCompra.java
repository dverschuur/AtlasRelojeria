package com.ve.edu.ucab.AtlasRelojeria.Model;

import java.time.LocalDate;

public class OrdenDeCompra {
    String numeroDeCompra, proveedor, monto, producto, cantidadDeProducto;
    LocalDate fechaCompra;

    /**  CONSTRUCTOR **/
    public OrdenDeCompra(String numeroDeCompra, String proveedor, String monto, String producto, String cantidadDeProducto, LocalDate fechaCompra) {
        this.numeroDeCompra = numeroDeCompra;
        this.proveedor = proveedor;
        this.monto = monto;
        this.producto = producto;
        this.cantidadDeProducto = cantidadDeProducto;
        this.fechaCompra = fechaCompra;
    }

    public OrdenDeCompra(){}

    /**GETTERS AND SETTERS**/
    public String getNumeroDeCompra() {
        return numeroDeCompra;
    }

    public void setNumeroDeCompra(String numeroDeCompra) {
        this.numeroDeCompra = numeroDeCompra;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCantidadDeProducto() {
        return cantidadDeProducto;
    }

    public void setCantidadDeProducto(String cantidadDeProducto) {
        this.cantidadDeProducto = cantidadDeProducto;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
}
