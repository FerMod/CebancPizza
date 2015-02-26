package com.cebancpizza.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Bebida implements Parcelable{

    int bebida;
    String descripcion;
    double prVent;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(bebida);
        out.writeString(descripcion);
        out.writeDouble(prVent);
    }

    public static final Creator<Bebida> CREATOR = new Creator<Bebida>() {
        public Bebida createFromParcel(Parcel in) {
            return new Bebida(in);
        }

        public Bebida[] newArray(int size) {
            return new Bebida[size];
        }
    };

    private Bebida(Parcel in) {
        bebida = in.readInt();
        descripcion = in.readString();
        prVent = in.readDouble();
    }

    /**
     * Campos de bebida:
     *
     * @param bebida id
     * @param descripcion nombre
     * @param prVent precio
     */

    public Bebida(int bebida, String descripcion, double prVent) {
        this.bebida = bebida;
        this.descripcion = descripcion;
        this.prVent = prVent;
    }

    public int getBebida() {
        return bebida;
    }

    public void setBebida(int bebida) {
        this.bebida = bebida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrVent() {
        return prVent;
    }

    public void setPrVent(double prVent) {
        this.prVent = prVent;
    }

}
