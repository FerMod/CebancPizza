package com.cebancpizza.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Tipo implements Parcelable{

    int tipo;
    String descripcion;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(tipo);
        out.writeString(descripcion);
    }

    public static final Creator<Tipo> CREATOR = new Creator<Tipo>() {
        public Tipo createFromParcel(Parcel in) {
            return new Tipo(in);
        }

        public Tipo[] newArray(int size) {
            return new Tipo[size];
        }
    };

    private Tipo(Parcel in) {
        tipo = in.readInt();
        descripcion = in.readString();
    }

    /**
     * Campos de tipo:
     *
     * @param tipo id
     * @param descripcion tipo
     */

    public Tipo(int tipo, String descripcion) {
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
