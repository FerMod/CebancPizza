package com.cebancpizza.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Masa implements Parcelable{

    int masa;
    String descripcion;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(masa);
        out.writeString(descripcion);
    }

    public static final Creator<Masa> CREATOR = new Creator<Masa>() {
        public Masa createFromParcel(Parcel in) {
            return new Masa(in);
        }

        public Masa[] newArray(int size) {
            return new Masa[size];
        }
    };

    private Masa(Parcel in) {
        masa = in.readInt();
        descripcion = in.readString();
    }

    /**
     * Campos de masa:
     *
     * @param masa id
     * @param descripcion nombre
     */

    public Masa(int masa, String descripcion) {
        this.masa = masa;
        this.descripcion = descripcion;
    }

    public int getMasa() {
        return masa;
    }

    public void setMasa(int masa) {
        this.masa = masa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
