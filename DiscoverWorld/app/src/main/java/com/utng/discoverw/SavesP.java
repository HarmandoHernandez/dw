package com.utng.discoverw;

public class SavesP {
    //Definición de las propiedades de la Clase, en este caso los datos a mostrar en el RecyclerView
    private String nombre;
    private String urlPhoto;
    private float valoracion;
    private String descripcion;

    public SavesP(String nombre, String urlPhoto, float valoracion, String descripcion) {
        this.nombre = nombre;
        this.urlPhoto = urlPhoto;
        this.valoracion = valoracion;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
