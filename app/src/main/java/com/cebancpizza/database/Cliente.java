package com.cebancpizza.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Cliente implements Parcelable {

    int cliente;
    String nombre, dni, direccion, telefono;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(cliente);
        out.writeString(dni);
        out.writeString(nombre);
        out.writeString(direccion);
        out.writeString(telefono);
    }

    public static final Parcelable.Creator<Cliente> CREATOR = new Parcelable.Creator<Cliente>() {
        public Cliente createFromParcel(Parcel in) {
            return new Cliente(in);
        }

        public Cliente[] newArray(int size) {
            return new Cliente[size];
        }
    };

    private Cliente(Parcel in) {
        cliente = in.readInt();
        dni = in.readString();
        nombre = in.readString();
        direccion = in.readString();
        telefono = in.readString();
    }

    /**
     * Datos de un cliente:
     *
     * @param cliente id
     * @param dni dni del cliente
     * @param nombre nombre del cliente
     * @param direccion direccion del cliente
     * @param telefono numero de telefono del cliente
     **/
    public Cliente(int cliente, String dni, String nombre, String direccion, String telefono) {
        this.cliente = cliente;
        this.dni = dni;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    /**
     * Datos de un cliente con id autogenerado:
     * @param dni dni del cliente
     * @param nombre nombre del cliente
     * @param direccion direccion del cliente
     * @param telefono numero de telefono del cliente
     **/
    public Cliente(String dni, String nombre, String direccion, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public int getCliente() {
        return cliente;
    }

    public void setCliente(int cliente) {
        this.cliente = cliente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
