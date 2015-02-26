package com.cebancpizza.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Formpago implements Parcelable{

    String formpago, descripcion;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(formpago);
        out.writeString(descripcion);
    }

    public static final Creator<Formpago> CREATOR = new Creator<Formpago>() {
        public Formpago createFromParcel(Parcel in) {
            return new Formpago(in);
        }

        public Formpago[] newArray(int size) {
            return new Formpago[size];
        }
    };

    private Formpago(Parcel in) {
        formpago = in.readString();
        descripcion = in.readString();
    }

    /**
     * Campos de formpago:
     *
     * @param formpago id
     * @param descripcion nombre
     */

    public Formpago(String formpago, String descripcion) {
        this.formpago = formpago;
        this.descripcion = descripcion;
    }

    public String getFormpago() {
        return formpago;
    }

    public void setFormpago(String formpago) {
        this.formpago = formpago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
