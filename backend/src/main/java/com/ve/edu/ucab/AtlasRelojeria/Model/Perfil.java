package com.ve.edu.ucab.AtlasRelojeria.Model;

import java.util.ArrayList;
import java.util.List;

public class Perfil {
    private String nombre;
    private String id;
    private int edad;
    private String contrasena;
    private boolean esAdministrador;
    private List<String> listaDeseos;

    public Perfil() {
        this.listaDeseos = new ArrayList<>();
    }

    public Perfil(String nombre, String id, int edad, String contrasena, boolean esAdministrador) {
        this.nombre = nombre;
        this.id = id;
        this.edad = edad;
        this.contrasena = contrasena;
        this.esAdministrador = esAdministrador;
        this.listaDeseos = new ArrayList<>();
    }

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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isEsAdministrador() {
        return esAdministrador;
    }

    public void setEsAdministrador(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }

    public List<String> getListaDeseos() {
        return listaDeseos;
    }

    public void setListaDeseos(List<String> listaDeseos) {
        this.listaDeseos = listaDeseos;
    }

    public void agregarAListaDeseos(String productoId) {
        if (!this.listaDeseos.contains(productoId)) {
            this.listaDeseos.add(productoId);
        }
    }

    public void quitarDeListaDeseos(String productoId) {
        this.listaDeseos.remove(productoId);
    }

    public boolean estaEnListaDeseos(String productoId) {
        return this.listaDeseos.contains(productoId);
    }
}
