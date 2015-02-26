package com.cebancpizza.admin;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cebancpizza.R;
import com.cebancpizza.database.CebancPizzaSQLiteHelper;
import com.cebancpizza.database.PedidoPizza;

import java.util.ArrayList;

public class AdminPedidosPizzas extends Fragment implements View.OnFocusChangeListener {

    public Context context;
    private static final String SECTION_NUMBER = "section_number";
    private ArrayList<PedidoPizza> pedidosPizzas = new ArrayList<>();
    private ListView lvListaPedidoPizzas;
    private AdaptadorPedidoPizzas adaptador;
    private int selectedItemIndex, menuItemIndex;
    private CebancPizzaSQLiteHelper sqLiteHelper;
    private View alertView;
    private double prVent;

    public AdminPedidosPizzas() {
    }

    public static AdminPedidosPizzas newInstance(int sectionNumber){
        AdminPedidosPizzas fragment = new AdminPedidosPizzas();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Al llamar a este metodo iniciara PedidoPedidoPizza
     * @param accion La operacion que se realizara: 1 Insertar. 2 Editar. 3 Eliminar.
     */
    public void iniciarAccion(int accion) {
        Log.wtf(getClass().getSimpleName(), "[accion -> " + accion + "]");
        if (accion == 1) {
            showAlertNewPedidoPizza();
        } else if (accion == 2) {
            showAlertUpdatePedidoPizza();
        } else if (accion == 3) {
            showAlertDeletePedidoPizza();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lista_tabla_bbdd, container, false);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sqLiteHelper = new CebancPizzaSQLiteHelper(getActivity(), "CebancPizza", null, 1);
        if (sqLiteHelper != null) {
            getPedidoPizzasBBDD();
            initListView();
        }
    }

    public void initListView() {
        adaptador = new AdaptadorPedidoPizzas(getActivity());
        lvListaPedidoPizzas = (ListView) getActivity().findViewById(R.id.list);
        lvListaPedidoPizzas.setAdapter(adaptador);
        registerForContextMenu(lvListaPedidoPizzas);
    }

    public void getPedidoPizzasBBDD() {
        Cursor c = sqLiteHelper.select("pedidos_pizzas", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                pedidosPizzas.add(new PedidoPizza(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), c.getInt(5), c.getDouble(6)));
            } while(c.moveToNext());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(Integer.toString(pedidosPizzas.get(info.position).getPedidoPizza()));
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        for (int i = 0; i<menuItems.length; i++) {
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
        // Mensaje de que accion se va realizar y sobre que pedidosPizzas se va a realizar
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = arrayTipo[pedidosPizzas.get(selectedItemIndex).getTipo()];
        muestraAviso("Accion elegida:", menuItemName + "\n" + listItemName);
        */
        Log.wtf("pedidosPizzas.get(selectedItemIndex)", pedidosPizzas.get(selectedItemIndex) + "");
        if(menuItemIndex == 0) {
            iniciarAccion(2);
        } else if(menuItemIndex == 1) {
            iniciarAccion(3);
        }
        return true;
    }

    public void showAlertNewPedidoPizza() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        alertView = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(alertView);

        alert.setTitle("Añadir PedidoPizza");
        alert.setMessage("Se va a añadir un pedido_pizza a la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) alertView.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) alertView.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) alertView.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) alertView.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) alertView.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) alertView.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) alertView.findViewById(R.id.etTexto6);

        etTexto2.setOnFocusChangeListener(this);

        etCod.setHint("pedidosPizzas (Integer)");
        etTexto1.setHint("pedido (Integer)");
        etTexto2.setHint("pizza (Integer)");
        etTexto3.setHint("masa (Integer)");
        etTexto4.setHint("tamano (Integer)");
        etTexto5.setHint("cantidad (Integer)");
        etTexto6.setHint("precio (Double)");

        etCod.setVisibility(View.GONE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.VISIBLE);
        etTexto3.setVisibility(View.VISIBLE);
        etTexto4.setVisibility(View.VISIBLE);
        etTexto5.setVisibility(View.VISIBLE);
        etTexto6.setVisibility(View.VISIBLE);
        etTexto6.setEnabled(false);

        alert.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String pedido = etTexto1.getText().toString();
                String pizza = etTexto2.getText().toString();
                String masa = etTexto3.getText().toString();
                String tamano = etTexto4.getText().toString();
                String cantidad = etTexto5.getText().toString();

                updatePrecioFrom(pizza);

                if (noErrors(pedido, pizza, masa, tamano, cantidad)) {
                    ContentValues values = new ContentValues();
                    values.put("pedido", Integer.parseInt(pedido));
                    values.put("pizza", Integer.parseInt(pizza));
                    values.put("masa", Integer.parseInt(masa));
                    values.put("tamano", Integer.parseInt(tamano));
                    values.put("cantidad", Integer.parseInt(cantidad));
                    values.put("precio", prVent);

                    sqLiteHelper.insert("pedidos_pizzas", null, values);
                    String[] columns = new String[] {"MAX(pedido_pizza)"};
                    Cursor c = sqLiteHelper.select("pedidos_pizzas", columns, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        Log.wtf("(pedido_pizza) c.getInt(0)", "[c.getInt(0) -> " + c.getInt(0) + "]");
                        AdminPedidosPizzas.this.pedidosPizzas.add(new PedidoPizza(c.getInt(0), values.getAsInteger("pedido"), values.getAsInteger("pizza"), values.getAsInteger("masa"), values.getAsInteger("tamano"), values.getAsInteger("cantidad"), values.getAsDouble("precio")));
                    }
                    adaptador.notifyDataSetChanged();
                    muestraToast("PedidoPizza añadida.");
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Añadir pedidosPizzas cancelado.");
            }
        });

        alert.show();
    }

    public void showAlertUpdatePedidoPizza() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        alertView = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(alertView);

        alert.setTitle("Editar PedidoPizza");
        alert.setMessage("Se va a modificar una pedidosPizzas de la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) alertView.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) alertView.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) alertView.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) alertView.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) alertView.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) alertView.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) alertView.findViewById(R.id.etTexto6);

        etTexto2.setOnFocusChangeListener(this);

        etCod.setHint("pedido_pizza (Integer)");
        etTexto1.setHint("pedido (Integer)");
        etTexto2.setHint("pizza (Integer)");
        etTexto3.setHint("masa (Integer)");
        etTexto4.setHint("tamano (Integer)");
        etTexto5.setHint("cantidad (Integer)");
        etTexto6.setHint("precio (Double)");
        String[] selectionArgs = new String[] {Integer.toString(pedidosPizzas.get(selectedItemIndex).getPedidoPizza())};
        Cursor c = sqLiteHelper.select("pedidos_pizzas", null, "pedido_pizza = ?", selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            etCod.setText(Integer.toString(c.getInt(0)));
            etTexto1.setText(Integer.toString(c.getInt(1)));
            etTexto2.setText(Integer.toString(c.getInt(2)));
            etTexto3.setText(Integer.toString(c.getInt(3)));
            etTexto4.setText(Integer.toString(c.getInt(4)));
            etTexto5.setText(Integer.toString(c.getInt(5)));
            etTexto6.setText(Double.toString(c.getDouble(6)));
        }

        etCod.setVisibility(View.VISIBLE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.VISIBLE);
        etTexto3.setVisibility(View.VISIBLE);
        etTexto4.setVisibility(View.VISIBLE);
        etTexto5.setVisibility(View.VISIBLE);
        etTexto6.setVisibility(View.VISIBLE);
        etTexto6.setEnabled(false);

        alert.setPositiveButton("Guardar Cambios", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String pedidoPizza = etCod.getText().toString();
                String pedido = etTexto1.getText().toString();
                String pizza = etTexto2.getText().toString();
                String masa = etTexto3.getText().toString();
                String tamano = etTexto4.getText().toString();
                String cantidad = etTexto5.getText().toString();

                updatePrecioFrom(pizza);

                if (noErrors(pedido, pizza, masa, tamano, cantidad)) {
                    ContentValues values = new ContentValues();
                    values.put("pedido_pizza", Integer.parseInt(pedidoPizza));
                    values.put("pedido", Integer.parseInt(pedido));
                    values.put("pizza", Integer.parseInt(pizza));
                    values.put("masa", Integer.parseInt(masa));
                    values.put("tamano", Integer.parseInt(tamano));
                    values.put("cantidad", Integer.parseInt(cantidad));
                    values.put("precio", prVent);
                    String[] whereArgs = new String[]{Integer.toString(AdminPedidosPizzas.this.pedidosPizzas.get(selectedItemIndex).getPedidoPizza())};
                    sqLiteHelper.update("pedidos_pizzas", values, "pedido_pizza = ?", whereArgs);
                    AdminPedidosPizzas.this.pedidosPizzas.set(selectedItemIndex, new PedidoPizza(values.getAsInteger("pedido_pizza"), values.getAsInteger("pedido"), values.getAsInteger("pizza"), values.getAsInteger("masa"), values.getAsInteger("tamano"), values.getAsInteger("cantidad"), values.getAsDouble("precio")));
                    adaptador.notifyDataSetChanged();
                    muestraToast("Cambios Guardados.");
                }
            }
        });

        alert.setNegativeButton("Descartar Cambios", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Cambios descartados.");
            }
        });

        alert.show();
    }

    public boolean noErrors(String pedido, String pizza, String masa, String tamano, String cantidad) {

        boolean correct = true;

//        if(pedidosPizzas.equals("") || !onlyInteger(pedidosPizzas)) {
//            correct = false;
//            muestraToast("Valor en el campo \"pedido_pizza\" erroneo.");
//        }

        if(pedido.equals("") || !onlyInteger(pedido)) {
            correct = false;
            muestraToast("Valor en el campo \"pedido\" erroneo.");
        }

        if(pizza.equals("") || !onlyInteger(pizza)) {
            correct = false;
            muestraToast("Valor en el campo \"pizza\" erroneo.");
        } else if(!sqLiteHelper.exists("pizzas", "pizza", pizza)) {
            correct = false;
            muestraToast("No existe la pizza con id \""+ pizza +"\".");
        }

        if(masa.equals("") || !onlyInteger(masa)) {
            correct = false;
            muestraToast("Valor en el campo \"masa\" erroneo.");
        } else if (!sqLiteHelper.exists("masas", "masa", masa)) {
            correct = false;
            muestraToast("No existe la masa con id \""+ masa +"\".");
        }

        if(tamano.equals("") || !onlyInteger(tamano)) {
            correct = false;
            muestraToast("Valor en el campo \"tamano\" erroneo.");
        } else if (!sqLiteHelper.exists("tamanos", "tamano", tamano)) {
            correct = false;
            muestraToast("No existe el tamaño con id \""+ tamano +"\".");
        }

        if(cantidad.equals("") || !onlyInteger(cantidad)) {
            correct = false;
            muestraToast("Valor en el campo \"cantidad\" erroneo.");
        }

        return correct;
    }

    public static boolean onlyInteger(String s) {
//      formato: (#)
        if (s.matches("\\d*?")) {
            return true;
        } else {
            System.err.println("Caracterer no válido.");
            return false;
        }
    }

    public void showAlertDeletePedidoPizza(){
        Builder dlgAlert = new Builder(getActivity());
        dlgAlert.setTitle("Borrar PedidoPizza");
        dlgAlert.setMessage("Va ha borrar la pedidosPizzas de la base de datos.\nSeguro que desea eliminarla?");
        dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] whereArgs = new String[]{Integer.toString(pedidosPizzas.get(selectedItemIndex).getPedidoPizza())};
                        sqLiteHelper.delete("pedidos_pizzas", "pedido_pizza = ?", whereArgs);
                        adaptador.remove(pedidosPizzas.get(selectedItemIndex));
                        adaptador.notifyDataSetChanged();
                        muestraToast("PedidoPizza eliminada.");
                    }
                }
        );

        dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Eliminar pedidosPizzas cancelada.");
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void muestraToast(String text) {
        Context context = getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void muestraAviso(String title, String message) {
        Builder dlgAlert  = new Builder(getActivity());
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(message);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        Log.wtf("onFocusChange(View view, boolean hasFocus)", "[view -> " + view + "] [hasFocus -> " + hasFocus + "]");
        Log.wtf("view.getId()", "[view id -> " + view.getId() + "]");
        Log.wtf("R.id.etTexto2", "[etTexto2 id -> " + R.id.etTexto2 + "]");
        if (!hasFocus && view.getId() == R.id.etTexto2) {
            EditText etText2 = (EditText) view.findViewById(R.id.etTexto2);
            EditText etText6 = (EditText) alertView.findViewById(R.id.etTexto6); //"view" only contains the view of the EditText which has launched the listener, therefore I need the view of the alert layout.
            if(sqLiteHelper.exists("pizzas", "pizza", etText2.getText().toString())) {
                updatePrecioFrom(etText2.getText().toString());
                etText6.setText(Double.toString(prVent));
            } else {
                prVent = 0.0;
                etText6.setText(Double.toString(prVent));
            }
        }
    }

    private void updatePrecioFrom(String pizza) {
        prVent = sqLiteHelper.getPrecioVenta("pizzas", "pizza", pizza);
    }

    class AdaptadorPedidoPizzas extends ArrayAdapter<PedidoPizza> {

        Activity context;

        AdaptadorPedidoPizzas(Activity context) {
            super(context, R.layout.lista_tabla_bbdd, pedidosPizzas);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitem_lista_bbdd, null);

            TextView tvCod = (TextView) item.findViewById(R.id.tvCod);
            tvCod.setText(Integer.toString(pedidosPizzas.get(position).getPedidoPizza()));

            TextView tvTexto1 = (TextView) item.findViewById(R.id.tvTexto1);
            tvTexto1.setText(Integer.toString(pedidosPizzas.get(position).getPedido()));

            TextView tvTexto2 = (TextView) item.findViewById(R.id.tvTexto2);
            tvTexto2.setText(Integer.toString(pedidosPizzas.get(position).getPizza()));

            TextView tvTexto3 = (TextView) item.findViewById(R.id.tvTexto3);
            tvTexto3.setText(Integer.toString(pedidosPizzas.get(position).getMasa()));

            TextView tvTexto4 = (TextView)item.findViewById(R.id.tvTexto4);
            tvTexto4.setText(Integer.toString(pedidosPizzas.get(position).getTamano()));

            TextView tvTexto5 = (TextView)item.findViewById(R.id.tvTexto5);
            tvTexto5.setText(Integer.toString(pedidosPizzas.get(position).getCantidad()));

            TextView tvTexto6 = (TextView)item.findViewById(R.id.tvTexto6);
            tvTexto6.setText(Double.toString(pedidosPizzas.get(position).getPrecio()));

            return(item);
        }
    }

}
