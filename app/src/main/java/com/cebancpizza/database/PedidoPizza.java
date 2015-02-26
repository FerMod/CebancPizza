package com.cebancpizza.database;

import android.os.Parcel;
import android.os.Parcelable;

public class PedidoPizza implements Parcelable{

	int pedidoPizza, pedido, pizza, masa, tamano, cantidad;
	double precio;

	@Override
	public int describeContents() {
	        return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
        out.writeInt(pedidoPizza);
        out.writeInt(pedido);
        out.writeInt(pizza);
		out.writeInt(masa);
		out.writeInt(tamano);
		out.writeInt(cantidad);
		out.writeDouble(precio);
	}

	public static final Parcelable.Creator<PedidoPizza> CREATOR = new Parcelable.Creator<PedidoPizza>() {
		public PedidoPizza createFromParcel(Parcel in) {
			return new PedidoPizza(in);
		}

		public PedidoPizza[] newArray(int size) {
			return new PedidoPizza[size];
		}
	};

	private PedidoPizza(Parcel in) {
        pedidoPizza = in.readInt();
        pedido = in.readInt();
        pizza = in.readInt();
		masa = in.readInt();
		tamano = in.readInt();
		cantidad = in.readInt();
		precio = in.readDouble();
	}

	/**
	 * Valores de un pedidoPizza:
	 * @param pedidoPizza id
     * @param pedido id pedido
     * @param pizza id pizza
	 * @param masa id masa
	 * @param tamano id tamano
	 * @param cantidad numero de pizzas
	 * @param precio pr_vent
	 */

	public PedidoPizza(int pedidoPizza, int pedido, int pizza,  int masa, int tamano, int cantidad, double precio) {
        this.pedidoPizza = pedidoPizza;
        this.pedido = pedido;
        this.pizza = pizza;
		this.masa = masa;
		this.tamano = tamano;
		this.cantidad = cantidad;
		this.precio = precio;
	}

    /**
     * Valores de un pedidoPizza con id autogenerado:
     * @param pedido id pedido
     * @param pizza id pizza
     * @param masa id masa
     * @param tamano id tamano
     * @param cantidad numero de pizzas
     * @param precio pr_vent
     */

    public PedidoPizza(int pedido, int pizza,  int masa, int tamano, int cantidad, double precio) {
        this.pedido = pedido;
        this.pizza = pizza;
        this.masa = masa;
        this.tamano = tamano;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public int getPedidoPizza() {
        return pedidoPizza;
    }

    public void setPedidoPizza(int pedidoPizza) {
        this.pedidoPizza = pedidoPizza;
    }

    public int getPedido() {
        return pedido;
    }

    public void setPedido(int pedido) {
        this.pedido = pedido;
    }

    public int getPizza() {
        return pizza;
    }

    public void setPizza(int pizza) {
        this.pizza = pizza;
    }

    public int getMasa() {
        return masa;
    }

    public void setMasa(int masa) {
        this.masa = masa;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
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
