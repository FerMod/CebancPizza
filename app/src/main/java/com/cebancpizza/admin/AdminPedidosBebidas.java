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
import com.cebancpizza.database.PedidoBebida;

import java.util.ArrayList;

public class AdminPedidosBebidas extends Fragment implements View.OnFocusChangeListener {

    public Context context;
    private static final String SECTION_NUMBER = "section_number";
    private ArrayList<PedidoBebida> pedidosBebidas = new ArrayList<>();
    private ListView lvListaPedidoBebidas;
    private AdaptadorPedidoBebidas adaptador;
    private int selectedItemIndex, menuItemIndex;
    private CebancPizzaSQLiteHelper sqLiteHelper;
    private View alertView;
    private double prVent;

    public AdminPedidosBebidas() {
    }

    public static AdminPedidosBebidas newInstance(int sectionNumber){
        AdminPedidosBebidas fragment = new AdminPedidosBebidas();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Al llamar a este metodo iniciara PedidoPedidoBebida
     * @param accion La operacion que se realizara: 1 Insertar. 2 Editar. 3 Eliminar.
     */
    public void iniciarAccion(int accion) {
        Log.wtf(getClass().getSimpleName(), "[accion -> " + accion + "]");
        if (accion == 1) {
            showAlertNewPedidoBebida();
        } else if (accion == 2) {
            showAlertUpdatePedidoBebida();
        } else if (accion == 3) {
            showAlertDeletePedidoBebida();
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
            getPedidoBebidasBBDD();
            initListView();
        }
    }

    public void initListView() {
        adaptador = new AdaptadorPedidoBebidas(getActivity());
        lvListaPedidoBebidas = (ListView) getActivity().findViewById(R.id.list);
        lvListaPedidoBebidas.setAdapter(adaptador);
        registerForContextMenu(lvListaPedidoBebidas);
    }

    public void getPedidoBebidasBBDD() {
        Cursor c = sqLiteHelper.select("pedidos_bebidas", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                pedidosBebidas.add(new PedidoBebida(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getDouble(4)));
            } while(c.moveToNext());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(Integer.toString(pedidosBebidas.get(info.position).getPedidoBebida()));
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
        // Mensaje de que accion se va realizar y sobre que pedidosBebidas se va a realizar
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = arrayTipo[pedidosBebidas.get(selectedItemIndex).getTipo()];
        muestraAviso("Accion elegida:", menuItemName + "\n" + listItemName);
        */
        Log.wtf("pedidosBebidas.get(selectedItemIndex)", pedidosBebidas.get(selectedItemIndex) + "");
        if(menuItemIndex == 0) {
            iniciarAccion(2);
        } else if(menuItemIndex == 1) {
            iniciarAccion(3);
        }
        return true;
    }

    public void showAlertNewPedidoBebida() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        alertView = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(alertView);

        alert.setTitle("Añadir PedidoBebida");
        alert.setMessage("Se va a añadir un pedido_bebida a la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) alertView.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) alertView.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) alertView.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) alertView.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) alertView.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) alertView.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) alertView.findViewById(R.id.etTexto6);

        etTexto2.setOnFocusChangeListener(this);

        etCod.setHint("pedidosBebidas (Integer)");
        etTexto1.setHint("pedido (Integer)");
        etTexto2.setHint("bebida (Integer)");
        etTexto3.setHint("cantidad (Integer)");
        etTexto4.setHint("precio (Double)");

        etCod.setVisibility(View.GONE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.VISIBLE);
        etTexto3.setVisibility(View.VISIBLE);
        etTexto4.setVisibility(View.VISIBLE);
        etTexto4.setEnabled(false);
        etTexto5.setVisibility(View.GONE);
        etTexto6.setVisibility(View.GONE);

        alert.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String pedido = etTexto1.getText().toString();
                String bebida = etTexto2.getText().toString();
                String cantidad = etTexto3.getText().toString();

                updatePrecioFrom(bebida);

                if (noErrors(pedido, bebida, cantidad)) {

                    ContentValues values = new ContentValues();
                    values.put("pedido", Integer.parseInt(pedido));
                    values.put("bebida", Integer.parseInt(bebida));
                    values.put("cantidad", Integer.parseInt(cantidad));
                    values.put("precio", prVent);

                    sqLiteHelper.insert("pedidos_bebidas", null, values);

                    String[] columns = new String[] {"MAX(pedido_bebida)"};
                    Cursor c = sqLiteHelper.select("pedidos_bebidas", columns, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        Log.wtf("(pedido_bebida) c.getInt(0)", "[c.getInt(0) -> " + c.getInt(0) + "]");
                        pedidosBebidas.add(new PedidoBebida(c.getInt(0), values.getAsInteger("pedido"), values.getAsInteger("bebida"), values.getAsInteger("cantidad"), values.getAsDouble("precio")));
                    }

                    adaptador.notifyDataSetChanged();
                    muestraToast("PedidoBebida añadida.");

                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Añadir pedidosBebidas cancelado.");
            }
        });

        alert.show();
    }

    public void showAlertUpdatePedidoBebida() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        alertView = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(alertView);

        alert.setTitle("Editar PedidoBebida");
        alert.setMessage("Se va a modificar una pedidosBebidas de la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) alertView.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) alertView.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) alertView.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) alertView.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) alertView.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) alertView.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) alertView.findViewById(R.id.etTexto6);

        etTexto2.setOnFocusChangeListener(this);

        etCod.setHint("pedido_bebida (Integer)");
        etTexto1.setHint("pedido (Integer)");
        etTexto2.setHint("bebida (Integer)");
        etTexto5.setHint("cantidad (Integer)");
        etTexto6.setHint("precio (Double)");
        String[] selectionArgs = new String[] {Integer.toString(pedidosBebidas.get(selectedItemIndex).getPedidoBebida())};
        Cursor c = sqLiteHelper.select("pedidos_bebidas", null, "pedido_bebida = ?", selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            etCod.setText(Integer.toString(c.getInt(0)));
            etTexto1.setText(Integer.toString(c.getInt(1)));
            etTexto2.setText(Integer.toString(c.getInt(2)));
            etTexto3.setText(Integer.toString(c.getInt(3)));
            etTexto4.setText(Double.toString(c.getDouble(4)));
        }

        etCod.setVisibility(View.VISIBLE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.VISIBLE);
        etTexto3.setVisibility(View.VISIBLE);
        etTexto4.setVisibility(View.VISIBLE);
        etTexto4.setEnabled(false);
        etTexto5.setVisibility(View.GONE);
        etTexto6.setVisibility(View.GONE);


        alert.setPositiveButton("Guardar Cambios", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String pedidoBebida = etCod.getText().toString();
                String pedido = etTexto1.getText().toString();
                String bebida = etTexto2.getText().toString();
                String cantidad = etTexto3.getText().toString();

                updatePrecioFrom(bebida);

                if (noErrors(pedido, bebida, cantidad)) {
                    ContentValues values = new ContentValues();
                    values.put("pedido_bebida", Integer.parseInt(pedidoBebida));
                    values.put("pedido", Integer.parseInt(pedido));
                    values.put("bebida", Integer.parseInt(bebida));
                    values.put("cantidad", Integer.parseInt(cantidad));
                    values.put("precio", prVent);
                    String[] whereArgs = new String[]{Integer.toString(pedidosBebidas.get(selectedItemIndex).getPedidoBebida())};
                    sqLiteHelper.update("pedidos_bebidas", values, "pedido_bebida = ?", whereArgs);
                    pedidosBebidas.set(selectedItemIndex, new PedidoBebida(values.getAsInteger("pedido_bebida"), values.getAsInteger("pedido"), values.getAsInteger("bebida"), values.getAsInteger("cantidad"), values.getAsDouble("precio")));
                    adaptador.notifyDataSetChanged();
                    muestraToast("Cambios Guardados añadida.");
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

    public boolean noErrors(String pedido, String bebida, String cantidad) {

        boolean correct = true;

        if(pedido.equals("") || !onlyInteger(pedido)) {
            correct = false;
            muestraToast("Valor en el campo \"pedido\" erroneo.");
        }

        if(bebida.equals("") || !onlyInteger(bebida)) {
            correct = false;
            muestraToast("Valor en el campo \"bebida\" erroneo.");
        } else if(!sqLiteHelper.exists("bebidas", "bebida", bebida)) {
            correct = false;
            muestraToast("No existe la bebida con id \""+ bebida +"\".");
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

    public void showAlertDeletePedidoBebida(){
        Builder dlgAlert = new Builder(getActivity());
        dlgAlert.setTitle("Borrar PedidoBebida");
        dlgAlert.setMessage("Va ha borrar la pedidosBebidas de la base de datos.\nSeguro que desea eliminarla?");
        dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] whereArgs = new String[]{Integer.toString(pedidosBebidas.get(selectedItemIndex).getPedidoBebida())};
                        sqLiteHelper.delete("pedidos_bebidas", "pedido_bebida = ?", whereArgs);
                        adaptador.remove(pedidosBebidas.get(selectedItemIndex));
                        adaptador.notifyDataSetChanged();
                        muestraToast("PedidoBebida eliminada.");
                    }
                }
        );

        dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Eliminar pedidosBebidas cancelada.");
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
            EditText etText4 = (EditText) alertView.findViewById(R.id.etTexto4); //"view" only contains the view of the EditText which has launched the listener, therefore I need the view of the alert layout.
            if(sqLiteHelper.exists("bebidas", "bebida", etText2.getText().toString())) {
                prVent = sqLiteHelper.getPrecioVenta("bebidas", "bebida", etText2.getText().toString());
                updatePrecioFrom(etText2.getText().toString());
                etText4.setText(Double.toString(prVent));
            } else {
                prVent = 0.0;
                etText4.setText(Double.toString(prVent));
            }
        }
    }

    private void updatePrecioFrom(String bebida) {
        prVent = sqLiteHelper.getPrecioVenta("bebidas", "bebida", bebida);
    }

    class AdaptadorPedidoBebidas extends ArrayAdapter<PedidoBebida> {

        Activity context;

        AdaptadorPedidoBebidas(Activity context) {
            super(context, R.layout.lista_tabla_bbdd, pedidosBebidas);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitem_lista_bbdd, null);

            TextView tvCod = (TextView) item.findViewById(R.id.tvCod);
            tvCod.setText(Integer.toString(pedidosBebidas.get(position).getPedidoBebida()));

            TextView tvTexto1 = (TextView) item.findViewById(R.id.tvTexto1);
            tvTexto1.setText(Integer.toString(pedidosBebidas.get(position).getPedido()));

            TextView tvTexto2 = (TextView) item.findViewById(R.id.tvTexto2);
            tvTexto2.setText(Integer.toString(pedidosBebidas.get(position).getBebida()));

            TextView tvTexto3 = (TextView)item.findViewById(R.id.tvTexto3);
            tvTexto3.setText(Integer.toString(pedidosBebidas.get(position).getCantidad()));

            TextView tvTexto4 = (TextView)item.findViewById(R.id.tvTexto4);
            tvTexto4.setText(Double.toString(pedidosBebidas.get(position).getPrecio()));

            TextView tvTexto5 = (TextView)item.findViewById(R.id.tvTexto5);
            tvTexto5.setVisibility(View.GONE);

            TextView tvTexto6 = (TextView)item.findViewById(R.id.tvTexto6);
            tvTexto6.setVisibility(View.GONE);

            return(item);
        }
    }

}
