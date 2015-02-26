package com.cebancpizza.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Tamano implements Parcelable{

    int tamano;
    String descripcion;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(tamano);
        out.writeString(descripcion);
    }

    public static final Creator<Tamano> CREATOR = new Creator<Tamano>() {
        public Tamano createFromParcel(Parcel in) {
            return new Tamano(in);
        }

        public Tamano[] newArray(int size) {
            return new Tamano[size];
        }
    };

    private Tamano(Parcel in) {
        tamano = in.readInt();
        descripcion = in.readString();
    }

    /**
     * Campos de tamano:
     *
     * @param tamano id
     * @param descripcion nombre
     */

    public Tamano(int tamano, String descripcion) {
        this.tamano = tamano;
        this.descripcion = descripcion;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
