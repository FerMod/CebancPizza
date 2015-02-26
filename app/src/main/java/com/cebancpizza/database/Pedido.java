package com.cebancpizza.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Pedido implements Parcelable{

    int pedido, articulo, tipo, albaran;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(pedido);
        out.writeInt(articulo);
        out.writeInt(tipo);
        out.writeInt(albaran);
    }

    public static final Creator<Pedido> CREATOR = new Creator<Pedido>() {
        public Pedido createFromParcel(Parcel in) {
            return new Pedido(in);
        }

        public Pedido[] newArray(int size) {
            return new Pedido[size];
        }
    };

    private Pedido(Parcel in) {
        pedido = in.readInt();
        articulo = in.readInt();
        tipo = in.readInt();
        albaran = in.readInt();
    }

    /**
     * Valores de un pedido:
     * @param pedido id
     * @param articulo id articulo
     * @param tipo id tipo
     * @param albaran id albaran
     */

    public Pedido(int pedido, int articulo, int tipo, int albaran) {
        this.pedido = pedido;
        this.articulo = articulo;
        this.tipo = tipo;
        this.albaran = albaran;
    }

    /**
     * Valores de un pedido con id autogenerado:
     * @param articulo id articulo
     * @param tipo id tipo
     * @param albaran id albaran
     */

    public Pedido(int articulo, int tipo, int albaran) {
        this.articulo = articulo;
        this.tipo = tipo;
        this.albaran = albaran;
    }

    public int getPedido() {
        return pedido;
    }

    public void setPedido(int pedido) {
        this.pedido = pedido;
    }

    public int getArticulo() {
        return articulo;
    }

    public void setArticulo(int articulo) {
        this.articulo = articulo;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getAlbaran() {
        return albaran;
    }

    public void setAlbaran(int albaran) {
        this.albaran = albaran;
    }
}
