package com.cebancpizza.cliente;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cebancpizza.R;
import com.cebancpizza.database.CebancPizzaSQLiteHelper;
import com.cebancpizza.database.PedidoBebida;

import java.util.ArrayList;

public class ResumenPedidoBebidas extends Fragment{

    private ArrayList<String> bebidas;
    private ArrayList<PedidoBebida> pedidoBebida;
    private ListView lvListaPedidosBebidas;
    private OnResumenPedidoBebidasListener onResumenPedidoBebidasListener;
    private AdaptadorPedidoBebida adaptador;
    private CebancPizzaSQLiteHelper sqLiteHelper;
    private static final String SECTION_NUMBER = "section_number";
    private TextView tvTotal;
    private String ARRAY_VALUE = "array_avalue";
    private int menuItemIndex;
    private int selectedItemIndex;

    public ResumenPedidoBebidas() {
    }

    public ResumenPedidoBebidas newInstance(int sectionNumber) {
        ResumenPedidoBebidas fragment = new ResumenPedidoBebidas();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Al llamar a este metodo iniciara PedidoBebida
     * @param accion La operacion que se realizara: 1 Insertar una nueva bebida. 2 Editar la bebida.
     */
    public void iniciarPedidoBebida(int accion) {
        Intent intent = new Intent(getActivity(), NuevaBebida.class);
        intent.putExtra("accion", accion);
        if (accion == 2) {
            intent.putExtra("bebida", pedidoBebida.get(selectedItemIndex));
        }
        Log.wtf(getClass().getSimpleName(), "[accion -> " + accion + "]");
        startActivityForResult(intent, accion);
    }

    public void setArrayBebidas(ArrayList<PedidoBebida> arrayPedidoBebida) {
        this.pedidoBebida = arrayPedidoBebida;
    }

    public interface OnResumenPedidoBebidasListener {
        public void passBebidaData(double totalBebidas);
        public void passBebidaData(PedidoBebida pedidoBebida);
        public void passBebidaData(ArrayList<PedidoBebida> arrayList);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            onResumenPedidoBebidasListener = (OnResumenPedidoBebidasListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnResumenPedidoBebidasListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onResumenPedidoBebidasListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (adaptador == null) {
            initListView();
        }

        Log.wtf("onActivityResult()", "[" + requestCode + "]" + "[" + resultCode + "] " + data);

        switch (resultCode) {
            case 1: // Añadir
                pedidoBebida.add(data.getExtras().<PedidoBebida>getParcelable("bebida"));
                onResumenPedidoBebidasListener.passBebidaData(pedidoBebida);
                break;
            case 2: // Modificar
                pedidoBebida.set(selectedItemIndex, data.getExtras().<PedidoBebida>getParcelable("bebida"));
                break;
        }

        tvTotal.setText("Total: " + String.format("%.2f", getTotal())  + "€");
        onResumenPedidoBebidasListener.passBebidaData(getTotal());
        adaptador.notifyDataSetChanged();

    }

    public double getTotal(){
        double total = 0;
        for (PedidoBebida pedidoBebida : this.pedidoBebida) {
            total += pedidoBebida.getPrecio();
        }
        Log.wtf("double getTotal()", "[" + total + "]");
        return total;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            pedidoBebida = savedInstanceState.getParcelableArrayList(ARRAY_VALUE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARRAY_VALUE, pedidoBebida);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resumen_pedido_bebidas, container, false);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sqLiteHelper = new CebancPizzaSQLiteHelper(getActivity(), "CebancPizza", null, 1);

        bebidas = new ArrayList<>();
        bebidas.add(""); //To make the first item of the spinner blank
        bebidas.addAll(sqLiteHelper.fillArrayList("bebidas", "descripcion"));

        if (pedidoBebida != null) {
            initListView();
        }
    }

    public void initListView() {
        adaptador = new AdaptadorPedidoBebida(getActivity());
        lvListaPedidosBebidas = (ListView) getActivity().findViewById(R.id.list);
        lvListaPedidosBebidas.setAdapter(adaptador);
        registerForContextMenu(lvListaPedidosBebidas);
        tvTotal = (TextView) getActivity().findViewById(R.id.tvTotal);
        tvTotal.setText("Total: " + String.format("%.2f", getTotal()) + "€");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(bebidas.get(pedidoBebida.get(info.position).getBebida()));
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        menuItemIndex = item.getItemId();
        selectedItemIndex = info.position;
        Log.wtf("boolean onContextItemSelected(" + item + ")", "[" + menuItemIndex + "]" + "[" + selectedItemIndex + "]");

        /*
        // Mensaje de que accion se va realizar y sobre que bebida se va a realizar
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = arrayTipo[bebidas.get(selectedItemIndex).getTipo()];
        muestraAviso("Accion elegida:", menuItemName + "\n" + listItemName);
        */

        if(menuItemIndex == 0) {
            Log.wtf("bebidas.get(selectedItemIndex)", pedidoBebida.get(selectedItemIndex)+"");
            iniciarPedidoBebida(2);
        } else if(menuItemIndex == 1) {
            adaptador.remove(pedidoBebida.get(selectedItemIndex));
            // bebidas.remove(selectedItemIndex);
            adaptador.notifyDataSetChanged();
            tvTotal.setText("Total: " + String.format("%.2f", getTotal())  + "€");
        }

        return true;
    }

    private void muestraAviso(String title, String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getActivity());
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(message);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    class AdaptadorPedidoBebida extends ArrayAdapter<PedidoBebida> {

        Activity context;

        AdaptadorPedidoBebida(Activity context) {
            super(context, R.layout.listitem_pedido_bebida, pedidoBebida);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitem_pedido_bebida, null);

            TextView tvPrecio = (TextView) item.findViewById(R.id.tvPrecio);
            tvPrecio.setText(String.format("%.2f", pedidoBebida.get(position).getPrecio()) + "€");

            TextView tvTitulo = (TextView) item.findViewById(R.id.tvTitulo);
            tvTitulo.setText(bebidas.get(pedidoBebida.get(position).getBebida()));

            TextView tvSubtitulo = (TextView) item.findViewById(R.id.tvCod);
            tvSubtitulo.setText("Cantidad: " + pedidoBebida.get(position).getCantidad());

            return(item);
        }
    }
}
