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
import android.text.InputFilter;
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
import com.cebancpizza.database.Cliente;

import java.util.ArrayList;

public class AdminClientes extends Fragment {

    public Context context;
    private static final String SECTION_NUMBER = "section_number";
    private ArrayList<Cliente> clientes = new ArrayList<>();
    private ListView lvListaClientes;
    private AdaptadorClientes adaptador;
    private int selectedItemIndex, menuItemIndex;
    private CebancPizzaSQLiteHelper sqLiteHelper;
    private View alertView;

    public AdminClientes() {
    }

    public static AdminClientes newInstance(int sectionNumber){
        AdminClientes fragment = new AdminClientes();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Al llamar a este metodo iniciara ClienteClienteCliente
     * @param accion La operacion que se realizara: 1 Insertar. 2 Editar. 3 Eliminar.
     */
    public void iniciarAccion(int accion) {
        Log.wtf(getClass().getSimpleName(), "[accion -> " + accion + "]");
        if (accion == 1) {
            showAlertNewCliente();
        } else if (accion == 2) {
            showAlertUpdateCliente();
        } else if (accion == 3) {
            showAlertDeleteCliente();
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
            getClientesBBDD();
            initListView();
        }
    }

    public void initListView() {
        adaptador = new AdaptadorClientes(getActivity());
        lvListaClientes = (ListView) getActivity().findViewById(R.id.list);
        lvListaClientes.setAdapter(adaptador);
        registerForContextMenu(lvListaClientes);
    }

    public void getClientesBBDD() {
        Cursor c = sqLiteHelper.select("clientes", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                clientes.add(new Cliente(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4)));
            } while(c.moveToNext());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(Integer.toString(clientes.get(info.position).getCliente()));
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
        // Mensaje de que accion se va realizar y sobre que clienteCliente se va a realizar
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = arrayTipo[clienteCliente.get(selectedItemIndex).getTipo()];
        muestraAviso("Accion elegida:", menuItemName + "\n" + listItemName);
        */
        Log.wtf("clientes.get(selectedItemIndex)", clientes.get(selectedItemIndex) + "");
        if(menuItemIndex == 0) {
            iniciarAccion(2);
        } else if(menuItemIndex == 1) {
            iniciarAccion(3);
        }
        return true;
    }

    public void showAlertNewCliente() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        alertView = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(alertView);

        alert.setTitle("Añadir Cliente");
        alert.setMessage("Se va a añadir un cliente a la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) alertView.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) alertView.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) alertView.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) alertView.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) alertView.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) alertView.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) alertView.findViewById(R.id.etTexto6);

        etCod.setHint("cliente (Integer)");
        etTexto1.setHint("dni (String)");
        etTexto2.setHint("nombre (String)");
        etTexto3.setHint("direccion (String)");
        etTexto4.setHint("telefono (String)");

        //Filter for EditText that limits the number of characters to 9
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(9);

        etCod.setVisibility(View.GONE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);

        etTexto1.setFilters(filterArray);

        etTexto2.setVisibility(View.VISIBLE);
        etTexto3.setVisibility(View.VISIBLE);
        etTexto4.setVisibility(View.VISIBLE);
        etTexto4.setFilters(filterArray);
        etTexto5.setVisibility(View.GONE);
        etTexto6.setVisibility(View.GONE);

        alert.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String dni = etTexto1.getText().toString();
                String nombre = etTexto2.getText().toString();
                String direccion = etTexto3.getText().toString();
                String telefono = etTexto4.getText().toString();

                if (noErrors(dni, nombre, direccion, telefono)) {

                    ContentValues values = new ContentValues();
                    values.put("dni", dni);
                    values.put("nombre", nombre);
                    values.put("direccion", direccion);
                    values.put("telefono", telefono);

                    sqLiteHelper.insert("clientes", null, values);

                    String[] columns = new String[] {"MAX(cliente)"};
                    Cursor c = sqLiteHelper.select("clientes", columns, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        Log.wtf("(cliente) c.getInt(0)", "[c.getInt(0) -> " + c.getInt(0) + "]");
                        clientes.add( new Cliente(c.getInt(0), values.getAsString("dni"), values.getAsString("nombre"), values.getAsString("direccion"), values.getAsString("telefono")) );
                    }

                    adaptador.notifyDataSetChanged();
                    muestraToast("Cliente añadido.");

                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Añadir Cliente cancelado.");
            }
        });

        alert.show();
    }

    public void showAlertUpdateCliente() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        alertView = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(alertView);

        alert.setTitle("Editar Cliente");
        alert.setMessage("Se va a añadir un cliente a la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) alertView.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) alertView.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) alertView.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) alertView.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) alertView.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) alertView.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) alertView.findViewById(R.id.etTexto6);

        etCod.setHint("cliente (Integer)");
        etTexto1.setHint("dni (String)");
        etTexto2.setHint("nombre (String)");
        etTexto3.setHint("direccion (String)");
        etTexto4.setHint("telefono (String)");

        String[] selectionArgs = new String[] {Integer.toString(clientes.get(selectedItemIndex).getCliente())};
        Cursor c = sqLiteHelper.select("clientes", null, "cliente = ?", selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            etCod.setText(Integer.toString(c.getInt(0)));
            etTexto1.setText(c.getString(1));
            etTexto2.setText(c.getString(2));
            etTexto3.setText(c.getString(3));
            etTexto4.setText(c.getString(4));
        }

        //Filter for EditText that limits the number of characters to 9
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(9);

        etCod.setVisibility(View.VISIBLE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto1.setFilters(filterArray);
        etTexto2.setVisibility(View.VISIBLE);
        etTexto3.setVisibility(View.VISIBLE);
        etTexto4.setVisibility(View.VISIBLE);
        etTexto4.setFilters(filterArray);
        etTexto5.setVisibility(View.GONE);
        etTexto6.setVisibility(View.GONE);


        alert.setPositiveButton("Guardar Cambios", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String cliente = etCod.getText().toString();
                String dni = etTexto1.getText().toString();
                String nombre = etTexto2.getText().toString();
                String direccion = etTexto3.getText().toString();
                String telefono = etTexto4.getText().toString();

                if (noErrors(dni, nombre, direccion, telefono)) {

                    ContentValues values = new ContentValues();
                    values.put("cliente", Integer.parseInt(cliente));
                    values.put("dni", dni);
                    values.put("nombre", nombre);
                    values.put("direccion", direccion);
                    values.put("telefono", telefono);

                    String[] whereArgs = new String[]{Integer.toString(clientes.get(selectedItemIndex).getCliente())};
                    sqLiteHelper.update("clientes", values, "cliente = ?", whereArgs);
                    clientes.set(selectedItemIndex,new Cliente(values.getAsInteger("cliente"), values.getAsString("dni"), values.getAsString("nombre"), values.getAsString("direccion"), values.getAsString("telefono")) );
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

    public boolean noErrors(String dni, String nombre, String direccion, String telefono) {

        Log.wtf("noErrors(dni, nombre, direccion, formpago)", "[dni -> " + dni + "] [nombre -> " + nombre + "] [direccion -> " + direccion + "] [telefono -> " + telefono + "]");

        boolean correct = true;

        if(dni.equals("")) {
            correct = false;
            muestraToast("Valor en el campo \"dni\" erroneo.");
        }

        if(nombre.equals("")) {
            correct = false;
            muestraToast("Valor en el campo \"nombre\" erroneo.");
        }

        if(direccion.equals("")) {
            correct = false;
            muestraToast("Valor en el campo \"direccion\" erroneo.");
        }

        if(telefono.equals("") || !onlyInteger(telefono)) {
            correct = false;
            muestraToast("Valor en el campo \"telefono\" erroneo.");
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

    public void showAlertDeleteCliente(){
        Builder dlgAlert = new Builder(getActivity());
        dlgAlert.setTitle("Borrar ClienteCliente");
        dlgAlert.setMessage("Va ha borrar la clienteCliente de la base de datos.\nSeguro que desea eliminarla?");
        dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] whereArgs = new String[]{Integer.toString(clientes.get(selectedItemIndex).getCliente())};
                        sqLiteHelper.delete("clientes", "cliente = ?", whereArgs);
                        adaptador.remove(clientes.get(selectedItemIndex));
                        adaptador.notifyDataSetChanged();
                        muestraToast("Cliente eliminado.");
                    }
                }
        );

        dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Eliminar Cliente cancelado.");
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

    class AdaptadorClientes extends ArrayAdapter<Cliente> {

        Activity context;

        AdaptadorClientes(Activity context) {
            super(context, R.layout.lista_tabla_bbdd, clientes);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitem_lista_bbdd, null);

            TextView tvCod = (TextView) item.findViewById(R.id.tvCod);
            tvCod.setText(Integer.toString(clientes.get(position).getCliente()));

            TextView tvTexto1 = (TextView) item.findViewById(R.id.tvTexto1);
            tvTexto1.setText(clientes.get(position).getDni());

            TextView tvTexto2 = (TextView) item.findViewById(R.id.tvTexto2);
            tvTexto2.setText(clientes.get(position).getNombre());

            TextView tvTexto3 = (TextView)item.findViewById(R.id.tvTexto3);
            tvTexto3.setText(clientes.get(position).getDireccion());

            TextView tvTexto4 = (TextView)item.findViewById(R.id.tvTexto4);
            tvTexto4.setText(clientes.get(position).getTelefono());

            TextView tvTexto5 = (TextView)item.findViewById(R.id.tvTexto5);
            tvTexto5.setVisibility(View.GONE);

            TextView tvTexto6 = (TextView)item.findViewById(R.id.tvTexto6);
            tvTexto6.setVisibility(View.GONE);

            return(item);
        }
    }

}
