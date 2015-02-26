package com.cebancpizza.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Albaran implements Parcelable{

    int albaran, cliente;
    String fechaAlbaran, formpago;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(albaran);
        out.writeInt(cliente);
        out.writeString(fechaAlbaran);
        out.writeString(formpago);
    }

    public static final Creator<Albaran> CREATOR = new Creator<Albaran>() {
        public Albaran createFromParcel(Parcel in) {
            return new Albaran(in);
        }

        public Albaran[] newArray(int size) {
            return new Albaran[size];
        }
    };

    private Albaran(Parcel in) {
        albaran = in.readInt();
        cliente = in.readInt();
        fechaAlbaran = in.readString();
        formpago = in.readString();
    }

    /**
     * Campos de albaran:
     *
     * @param albaran id
     * @param cliente id cliente
     * @param fechaAlbaran fecha de la creacion del albaran
     * @param formpago id formpago
     */

    public Albaran(int albaran, int cliente, String fechaAlbaran, String formpago) {
        this.albaran = albaran;
        this.cliente = cliente;
        this.fechaAlbaran = fechaAlbaran;
        this.formpago = formpago;
    }

    public int getAlbaran() {
        return albaran;
    }

    public void setAlbaran(int albaran) {
        this.albaran = albaran;
    }

    public int getCliente() {
        return cliente;
    }

    public void setCliente(int cliente) {
        this.cliente = cliente;
    }

    public String getFechaAlbaran() {
        return fechaAlbaran;
    }

    public void setFechaAlbaran(String fechaAlbaran) {
        this.fechaAlbaran = fechaAlbaran;
    }

    public String getFormpago() {
        return formpago;
    }

    public void setFormpago(String formpago) {
        this.formpago = formpago;
    }

}
