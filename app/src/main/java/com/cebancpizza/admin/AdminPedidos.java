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
import com.cebancpizza.database.Pedido;

import java.util.ArrayList;

public class AdminPedidos extends Fragment {

    public Context context;
    private static final String SECTION_NUMBER = "section_number";
    private ArrayList<Pedido> pedidos = new ArrayList<>();
    private ListView lvListaPedidos;
    private AdaptadorPedidos adaptador;
    private int selectedItemIndex, menuItemIndex;
    private CebancPizzaSQLiteHelper sqLiteHelper;
    private View alertView;

    public AdminPedidos() {
    }

    public static AdminPedidos newInstance(int sectionNumber){
        AdminPedidos fragment = new AdminPedidos();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Al llamar a este metodo iniciara PedidoPedidoPedido
     * @param accion La operacion que se realizara: 1 Insertar. 2 Editar. 3 Eliminar.
     */
    public void iniciarAccion(int accion) {
        Log.wtf(getClass().getSimpleName(), "[accion -> " + accion + "]");
        if (accion == 1) {
            showAlertNewPedido();
        } else if (accion == 2) {
            showAlertUpdatePedido();
        } else if (accion == 3) {
            showAlertDeletePedido();
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
            getPedidosBBDD();
            initListView();
        }
    }

    public void initListView() {
        adaptador = new AdaptadorPedidos(getActivity());
        lvListaPedidos = (ListView) getActivity().findViewById(R.id.list);
        lvListaPedidos.setAdapter(adaptador);
        registerForContextMenu(lvListaPedidos);
    }

    public void getPedidosBBDD() {
        Cursor c = sqLiteHelper.select("pedidos", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                pedidos.add(new Pedido(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3)));
            } while(c.moveToNext());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(Integer.toString(pedidos.get(info.position).getPedido()));
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
        // Mensaje de que accion se va realizar y sobre que pedidoPedido se va a realizar
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = arrayTipo[pedidoPedido.get(selectedItemIndex).getTipo()];
        muestraAviso("Accion elegida:", menuItemName + "\n" + listItemName);
        */
        Log.wtf("pedidoPedido.get(selectedItemIndex)", pedidos.get(selectedItemIndex) + "");
        if(menuItemIndex == 0) {
            iniciarAccion(2);
        } else if(menuItemIndex == 1) {
            iniciarAccion(3);
        }
        return true;
    }

    public void showAlertNewPedido() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        alertView = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(alertView);

        alert.setTitle("Añadir PedidoPedido");
        alert.setMessage("Se va a añadir un pedido a la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) alertView.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) alertView.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) alertView.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) alertView.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) alertView.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) alertView.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) alertView.findViewById(R.id.etTexto6);

        etCod.setHint("pedido (Integer)");
        etTexto1.setHint("articulo (Integer)");
        etTexto2.setHint("tipo (Integer)");
        etTexto3.setHint("albaran (Integer)");

        etCod.setVisibility(View.GONE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.VISIBLE);
        etTexto3.setVisibility(View.VISIBLE);
        etTexto4.setVisibility(View.GONE);
        etTexto5.setVisibility(View.GONE);
        etTexto6.setVisibility(View.GONE);

        alert.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String articulo = etTexto1.getText().toString();
                String tipo = etTexto2.getText().toString();
                String albaran = etTexto3.getText().toString();
                Log.wtf("etTexto1.getText().toString()", "[articulo -> " + etTexto1.getText().toString() + "]");
                Log.wtf("etTexto2.getText().toString()", "[tipo -> " + etTexto2.getText().toString() + "]");
                Log.wtf("etTexto3.getText().toString()", "[albaran -> " + etTexto3.getText().toString() + "]");
                if (noErrors(articulo, tipo, albaran)) {
                    ContentValues values = new ContentValues();
                    values.put("articulo", Integer.parseInt(articulo));
                    values.put("tipo", Integer.parseInt(tipo));
                    values.put("albaran", Integer.parseInt(albaran));

                    sqLiteHelper.insert("pedidos", null, values);
                    String[] columns = new String[] {"MAX(pedido)"};
                    Cursor c = sqLiteHelper.select("pedidos", columns, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        Log.wtf("(pedido) c.getInt(0)", "[c.getInt(0) -> " + c.getInt(0) + "]");
                        pedidos.add(new Pedido(c.getInt(0), values.getAsInteger("articulo"), values.getAsInteger("tipo"), values.getAsInteger("albaran")));
                    }
                    adaptador.notifyDataSetChanged();
                    muestraToast("Pedido añadido.");
//                    } else {
//                        muestraAviso("PedidoPedido Repetida", "Ya hay una pedidoPedido con esa id!");
//                    }

                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Añadir pedido cancelado.");
            }
        });

        alert.show();
    }

    public void showAlertUpdatePedido() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        alertView = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(alertView);

        alert.setTitle("Editar Pedido");
        alert.setMessage("Se va a añadir un pedido a la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) alertView.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) alertView.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) alertView.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) alertView.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) alertView.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) alertView.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) alertView.findViewById(R.id.etTexto6);

        etCod.setHint("pedido (Integer)");
        etTexto1.setHint("articulo (Integer)");
        etTexto2.setHint("tipo (Integer)");
        etTexto3.setHint("albaran (Integer)");

        String[] selectionArgs = new String[] {Integer.toString(pedidos.get(selectedItemIndex).getPedido())};
        Cursor c = sqLiteHelper.select("pedidos", null, "pedido = ?", selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            etCod.setText(Integer.toString(c.getInt(0)));
            etTexto1.setText(Integer.toString(c.getInt(1)));
            etTexto2.setText(Integer.toString(c.getInt(2)));
            etTexto3.setText(Integer.toString(c.getInt(3)));
        }

        etCod.setVisibility(View.VISIBLE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.VISIBLE);
        etTexto3.setVisibility(View.VISIBLE);
        etTexto4.setVisibility(View.GONE);
        etTexto5.setVisibility(View.GONE);
        etTexto6.setVisibility(View.GONE);


        alert.setPositiveButton("Guardar Cambios", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String pedido = etCod.getText().toString();
                String articulo = etTexto1.getText().toString();
                String tipo = etTexto2.getText().toString();
                String albaran = etTexto3.getText().toString();

                if (noErrors(articulo, tipo, albaran)) {
                    ContentValues values = new ContentValues();
                    values.put("pedido", Integer.parseInt(pedido));
                    values.put("articulo", Integer.parseInt(articulo));
                    values.put("tipo", Integer.parseInt(tipo));
                    values.put("albaran", Integer.parseInt(albaran));

                    String[] whereArgs = new String[]{Integer.toString(pedidos.get(selectedItemIndex).getPedido())};
                    sqLiteHelper.update("pedidos", values, "pedido = ?", whereArgs);
                    pedidos.set(selectedItemIndex, new Pedido(values.getAsInteger("pedido"), values.getAsInteger("articulo"), values.getAsInteger("tipo"), values.getAsInteger("albaran")));
                    adaptador.notifyDataSetChanged();
                    muestraToast("Cambios Guardados.");
                }
            }
        });

        alert.setNegativeButton("Descartar Cambios", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Cambios Descartados.");
            }
        });

        alert.show();
    }

    public boolean noErrors(String articulo, String tipo, String albaran) {

        Log.wtf("noErrors(articulo, tipo, albaran)", "[articulo -> " + articulo + "] [tipo -> " + tipo + "] [albaran -> " + albaran + "]");

        boolean correct = true;

        if(tipo.equals("") || onlyInteger(tipo)) {

            if (articulo.equals("") || !onlyInteger(articulo)) {
                correct = false;
                muestraToast("Valor en el campo \"articulo\" erroneo.");
            } else {

                switch (Integer.parseInt(tipo)) {

                    case 1:
                        if (!sqLiteHelper.exists("pedidos_pizzas", "pedido_pizza", articulo)) {
                            correct = false;
                            muestraToast("No existe el pedido_pizza con id \"" + articulo + "\".");
                        }
                        break;

                    case 2:
                        if (!sqLiteHelper.exists("pedidos_bebidas", "pedido_bebida", articulo)) {
                            correct = false;
                            muestraToast("No existe el pedido_bebida con id \"" + articulo + "\".");
                        }
                        break;

                    default:
                        correct = false;
                        muestraToast("No existe el tipo con id \"" + tipo + "\".");
                        break;

                }

            }

        } else {
            correct = false;
            muestraToast("Valor en el campo \"tipo\" erroneo.");
        }

        if(albaran.equals("") || !onlyInteger(albaran)) {
            correct = false;
            muestraToast("Valor en el campo \"albaran\" erroneo.");
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

    public void showAlertDeletePedido(){
        Builder dlgAlert = new Builder(getActivity());
        dlgAlert.setTitle("Borrar Pedido");
        dlgAlert.setMessage("Va ha borrar el Pedido de la base de datos.\nSeguro que desea eliminarlo?");
        dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] whereArgs = new String[]{Integer.toString(pedidos.get(selectedItemIndex).getPedido()), Integer.toString(pedidos.get(selectedItemIndex).getArticulo())};
                        sqLiteHelper.delete("pedidos", "pedido = ? AND articulo = ?", whereArgs);
                        adaptador.remove(pedidos.get(selectedItemIndex));
                        adaptador.notifyDataSetChanged();
                        muestraToast("Pedido eliminado.");
                    }
                }
        );

        dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Eliminar Pedido cancelado.");
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

    class AdaptadorPedidos extends ArrayAdapter<Pedido> {

        Activity context;

        AdaptadorPedidos(Activity context) {
            super(context, R.layout.lista_tabla_bbdd, pedidos);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitem_lista_bbdd, null);

            TextView tvCod = (TextView) item.findViewById(R.id.tvCod);
            tvCod.setText(Integer.toString(pedidos.get(position).getPedido()));

            TextView tvTexto1 = (TextView) item.findViewById(R.id.tvTexto1);
            tvTexto1.setText(Integer.toString(pedidos.get(position).getArticulo()));

            TextView tvTexto2 = (TextView) item.findViewById(R.id.tvTexto2);
            tvTexto2.setText(Integer.toString(pedidos.get(position).getTipo()));

            TextView tvTexto3 = (TextView)item.findViewById(R.id.tvTexto3);
            tvTexto3.setText(Integer.toString(pedidos.get(position).getAlbaran()));

            TextView tvTexto4 = (TextView)item.findViewById(R.id.tvTexto4);
            tvTexto4.setVisibility(View.GONE);

            TextView tvTexto5 = (TextView)item.findViewById(R.id.tvTexto5);
            tvTexto5.setVisibility(View.GONE);

            TextView tvTexto6 = (TextView)item.findViewById(R.id.tvTexto6);
            tvTexto6.setVisibility(View.GONE);

            return(item);
        }
    }

}
