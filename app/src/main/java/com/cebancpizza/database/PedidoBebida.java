package com.cebancpizza.database;

import android.os.Parcel;
import android.os.Parcelable;

public class PedidoBebida implements Parcelable{

    int pedidoBebida, pedido, bebida, cantidad;
    double precio;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(pedidoBebida);
        out.writeInt(pedido);
        out.writeInt(bebida);
        out.writeInt(cantidad);
        out.writeDouble(precio);
    }

    public static final Parcelable.Creator<PedidoBebida> CREATOR = new Parcelable.Creator<PedidoBebida>() {
        public PedidoBebida createFromParcel(Parcel in) {
            return new PedidoBebida(in);
        }

        public PedidoBebida[] newArray(int size) {
            return new PedidoBebida[size];
        }
    };

    private PedidoBebida(Parcel in) {
        pedidoBebida = in.readInt();
        pedido = in.readInt();
        bebida = in.readInt();
        cantidad = in.readInt();
        precio = in.readDouble();
    }

    /**
     * Valores de un pedidoBebida:
     * @param pedidoBebida id
     * @param pedido id pedido
     * @param bebida id bebida
     * @param precio pr_vent
     */

    public PedidoBebida(int pedidoBebida, int pedido, int bebida, int cantidad, double precio) {
        this.pedidoBebida = pedidoBebida;
        this.pedido = pedido;
        this.bebida = bebida;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    /**
     * Valores de un pedidoBebida con id autogenerado:
     * @param pedido id pedido
     * @param bebida id bebida
     * @param precio pr_vent
     */

    public PedidoBebida(int pedido, int bebida, int cantidad, double precio) {
        this.pedido = pedido;
        this.bebida = bebida;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public int getPedidoBebida() {
        return pedidoBebida;
    }

    public void setPedidoBebida(int pedidoBebida) {
        this.pedidoBebida = pedidoBebida;
    }

    public int getPedido() {
        return pedido;
    }

    public void setPedido(int pedido) {
        this.pedido = pedido;
    }

    public int getBebida() {
        return bebida;
    }

    public void setBebida(int bebida) {
        this.bebida = bebida;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

}
