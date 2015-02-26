package com.cebancpizza.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Pizza implements Parcelable{

    int pizza;
    String descripcion;
    double prVent;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(pizza);
        out.writeString(descripcion);
        out.writeDouble(prVent);
    }

    public static final Creator<Pizza> CREATOR = new Creator<Pizza>() {
        public Pizza createFromParcel(Parcel in) {
            return new Pizza(in);
        }

        public Pizza[] newArray(int size) {
            return new Pizza[size];
        }
    };

    private Pizza(Parcel in) {
        pizza = in.readInt();
        descripcion = in.readString();
        prVent = in.readDouble();
    }

    /**
     * Campos de pizza:
     *
     * @param pizza id
     * @param descripcion nombre
     * @param prVent precio
     */

    public  Pizza(int pizza, String descripcion, double prVent) {
        this.pizza = pizza;
        this.descripcion = descripcion;
        this.prVent = prVent;
    }

    public int getPizza() {
        return pizza;
    }

    public void setPizza(int pizza) {
        this.pizza = pizza;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion1) {
        this.descripcion = descripcion;
    }

    public double getPrVent() {
        return prVent;
    }

    public void setPrVent(double precio) {
        this.prVent = prVent;
    }

}
