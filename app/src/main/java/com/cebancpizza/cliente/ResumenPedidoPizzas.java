package com.cebancpizza.cliente;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cebancpizza.R;
import com.cebancpizza.database.CebancPizzaSQLiteHelper;
import com.cebancpizza.database.PedidoPizza;

import java.util.ArrayList;

public class ResumenPedidoPizzas extends Fragment {

    public Context context;
    private static final String SECTION_NUMBER = "section_number";
    private CebancPizzaSQLiteHelper sqLiteHelper;
    private ArrayList<String> pizzas, masas, tamanos;
    private ArrayList<PedidoPizza> pedidoPizzas;
    private ListView lvListaPedidosPizzas;
    private TextView tvTotal;
    private AdaptadorPedidoPizza adaptador;
    private OnResumenPedidoPizzasListener onResumenPedidoPizzasListener;
    private int selectedItemIndex, menuItemIndex;

    /**
     * Al llamar a este metodo iniciara PedidoPizza
     *
     * @param accion La operacion que se realizara: 1 Insertar una nueva pizza. 2 Editar la pizza.
     */
    public void iniciarPedidoPizza(int accion) {
        Intent intent = new Intent(getActivity(), NuevaPizza.class);
        intent.putExtra("accion", accion);
        if (accion == 2) {
            intent.putExtra("pizza", pedidoPizzas.get(selectedItemIndex));
        }
        Log.wtf(getClass().getSimpleName(), "[accion -> " + accion + "]");
        startActivityForResult(intent, accion);
    }

    public void setArrayPizzas(ArrayList<PedidoPizza> arrayPedidoPizzas) {
        this.pedidoPizzas = arrayPedidoPizzas;
    }

    public interface OnResumenPedidoPizzasListener {
        public void passPizzaData(ArrayList<PedidoPizza> arrayList);

        public void passPizzaData(double totalPizzas);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onResumenPedidoPizzasListener = (OnResumenPedidoPizzasListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnResumenPedidoPizzasListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onResumenPedidoPizzasListener.passPizzaData(pedidoPizzas);
        onResumenPedidoPizzasListener.passPizzaData(getTotal());
        onResumenPedidoPizzasListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (adaptador == null) {
            initListView();
        }

        Log.wtf("onActivityResult()", "[" + requestCode + "]" + "[" + resultCode + "] " + data);

        switch (resultCode) {
            case 1: // Añadir
                pedidoPizzas.add(data.getExtras().<PedidoPizza>getParcelable("pizza"));
                break;
            case 2: // Modificar
                pedidoPizzas.set(selectedItemIndex, data.getExtras().<PedidoPizza>getParcelable("pizza"));
                break;
        }

        tvTotal.setText("Total: " + String.format("%.2f", getTotal()) + "€");
        adaptador.notifyDataSetChanged();

    }

    public double getTotal() {
        double total = 0;
        for (PedidoPizza pedidoPizza : pedidoPizzas) {
            total += pedidoPizza.getPrecio();
        }
        Log.wtf("double getTotal()", "[" + total + "]");
        return total;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.resumen_pedido_pizzas, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sqLiteHelper = new CebancPizzaSQLiteHelper(getActivity(), "CebancPizza", null, 1);

        pizzas = new ArrayList<>();
        pizzas.add(""); //To make the first item of the spinner blank
        pizzas.addAll(sqLiteHelper.fillArrayList("pizzas", "descripcion"));

        masas = new ArrayList<>();
        masas.add("");
        masas.addAll(sqLiteHelper.fillArrayList("masas", "descripcion"));

        tamanos = new ArrayList<>();
        tamanos.add("");
        tamanos.addAll(sqLiteHelper.fillArrayList("tamanos", "descripcion"));

        if (pedidoPizzas != null) {
            initListView();
        }
    }

    public void initListView() {
        adaptador = new AdaptadorPedidoPizza(getActivity());
        lvListaPedidosPizzas = (ListView) getActivity().findViewById(R.id.list);
        lvListaPedidosPizzas.setAdapter(adaptador);
        registerForContextMenu(lvListaPedidosPizzas);
        tvTotal = (TextView) getActivity().findViewById(R.id.tvTotal);
        tvTotal.setText("Total: " + String.format("%.2f", getTotal()) + "€");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(pizzas.get(pedidoPizzas.get(info.position).getPizza()));
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        for (int i = 0; i < menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        menuItemIndex = item.getItemId();
        selectedItemIndex = info.position;
        Log.wtf("boolean onContextItemSelected(" + item + ")", "[" + menuItemIndex + "]" + "[" + selectedItemIndex + "]");

        /*
        // Mensaje de que accion se va realizar y sobre que pizza se va a realizar
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = arrayTipo[pizzas.get(selectedItemIndex).getTipo()];
        muestraAviso("Accion elegida:", menuItemName + "\n" + listItemName);
        */

        if (menuItemIndex == 0) {
            Log.wtf("pizzas.get(selectedItemIndex)", pedidoPizzas.get(selectedItemIndex) + "");
            iniciarPedidoPizza(2);
        } else if (menuItemIndex == 1) {
            adaptador.remove(pedidoPizzas.get(selectedItemIndex));
            // pizzas.remove(selectedItemIndex);
            adaptador.notifyDataSetChanged();
            tvTotal.setText("Total: " + String.format("%.2f", getTotal()) + "€");
        }

        return true;
    }

//    private void muestraAviso(String title, String message) {
//        Builder dlgAlert  = new Builder(getActivity());
//        dlgAlert.setTitle(title);
//        dlgAlert.setMessage(message);
//        dlgAlert.setPositiveButton("OK", null);
//        dlgAlert.setCancelable(true);
//        dlgAlert.create().show();
//    }

    class AdaptadorPedidoPizza extends ArrayAdapter<PedidoPizza> {

        Activity context;

        AdaptadorPedidoPizza(Activity context) {
            super(context, R.layout.listitem_pedido_pizza, pedidoPizzas);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitem_pedido_pizza, null);

            TextView lblTitulo = (TextView) item.findViewById(R.id.tvTitulo);
            lblTitulo.setText(pizzas.get(pedidoPizzas.get(position).getPizza()));

            TextView lblPrecio = (TextView) item.findViewById(R.id.tvPrecio);
            lblPrecio.setText(String.format("%.2f", pedidoPizzas.get(position).getPrecio()) + "€");

            TextView lblSubtitulo2 = (TextView) item.findViewById(R.id.tvCod);
            lblSubtitulo2.setText("Masa: " + masas.get(pedidoPizzas.get(position).getMasa()));

            TextView lblSubtitulo3 = (TextView) item.findViewById(R.id.tvTexto1);
            lblSubtitulo3.setText("Tamaño: " + tamanos.get(pedidoPizzas.get(position).getTamano()));

            TextView lblSubtitulo4 = (TextView) item.findViewById(R.id.tvTexto2);
            lblSubtitulo4.setText("Cantidad: " + pedidoPizzas.get(position).getCantidad());

            return (item);
        }
    }

}
