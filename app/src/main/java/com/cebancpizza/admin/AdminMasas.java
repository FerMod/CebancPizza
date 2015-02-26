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
import com.cebancpizza.database.Masa;

import java.util.ArrayList;

public class AdminMasas extends Fragment {

    public Context context;
    private static final String SECTION_NUMBER = "section_number";
    private ArrayList<Masa> masas = new ArrayList<>();
    private ListView lvListaMasas;
    private AdaptadorMasas adaptador;
    private int selectedItemIndex, menuItemIndex;
    private CebancPizzaSQLiteHelper sqLiteHelper;

    public AdminMasas() {
    }

    public static AdminMasas newInstance(int sectionNumber){
        AdminMasas fragment = new AdminMasas();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Al llamar a este metodo iniciara PedidoMasa
     * @param accion La operacion que se realizara: 1 Insertar. 2 Editar. 3 Eliminar.
     */
    public void iniciarAccion(int accion) {
        Log.wtf(getClass().getSimpleName(), "[accion -> " + accion + "]");
        if (accion == 1) {
            showAlertNewMasa();
        } else if (accion == 2) {
            showAlertUpdateMasa();
        } else if (accion == 3) {
            showAlertDeleteMasa();
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
        sqLiteHelper = new CebancPizzaSQLiteHelper(getActivity(), "CebancMasa", null, 1);
        if (sqLiteHelper != null) {
            getMasasBBDD();
            initListView();
        }
    }

    public void initListView() {
        adaptador = new AdaptadorMasas(getActivity());
        lvListaMasas = (ListView) getActivity().findViewById(R.id.list);
        lvListaMasas.setAdapter(adaptador);
        registerForContextMenu(lvListaMasas);
    }

    public void getMasasBBDD() {
        Cursor c = sqLiteHelper.select("masas", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                masas.add( new Masa(c.getInt(0), c.getString(1)) );
            } while(c.moveToNext());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(masas.get(info.position).getDescripcion());
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
        // Mensaje de que accion se va realizar y sobre que masa se va a realizar
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = arrayTipo[masas.get(selectedItemIndex).getTipo()];
        muestraAviso("Accion elegida:", menuItemName + "\n" + listItemName);
        */
        Log.wtf("masa.get(selectedItemIndex)", masas.get(selectedItemIndex) + "");
        if(menuItemIndex == 0) {
            iniciarAccion(2);
        } else if(menuItemIndex == 1) {
            iniciarAccion(3);
        }
        return true;
    }

    public void showAlertNewMasa() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(view);

        alert.setTitle("Añadir Masa");
        alert.setMessage("Se va a añadir una masa a la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) view.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) view.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) view.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) view.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) view.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) view.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) view.findViewById(R.id.etTexto6);

        etCod.setHint("masa (Integer)");
        etTexto1.setHint("descripcion (String)");

        etCod.setVisibility(View.VISIBLE);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.GONE);
        etTexto3.setVisibility(View.GONE);
        etTexto4.setVisibility(View.GONE);
        etTexto5.setVisibility(View.GONE);
        etTexto6.setVisibility(View.GONE);

        alert.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String masa = etCod.getText().toString();
                String descripcion = etTexto1.getText().toString();

                if (noInsertErrors(masa, descripcion)) {
                    Log.wtf("PIZZA", "[masa -> " + masa + "] [descripcion -> " + descripcion + "]");
                    if (!sqLiteHelper.exists("masas", "masa", masa)) {
                        ContentValues values = new ContentValues();
                        values.put("masa", masa);
                        values.put("descripcion", descripcion);
                        sqLiteHelper.insert("masas", null, values);
                        masas.add(new Masa(values.getAsInteger("masa"), values.getAsString("descripcion")));
                        adaptador.notifyDataSetChanged();
                        muestraToast("Masa añadida.");
                    } else {
                        muestraAviso("Masa Repetida", "Ya hay una masa con esa id!");
                    }

                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Añadir masa cancelado.");
            }
        });

        alert.show();
    }

    public void showAlertUpdateMasa() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(view);

        alert.setTitle("Editar Masa");
        alert.setMessage("Se va a modificar una masa de la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) view.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) view.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) view.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) view.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) view.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) view.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) view.findViewById(R.id.etTexto6);

        etTexto1.setHint("descripcion (String)");

        String[] selectionArgs = new String[] {Integer.toString(masas.get(selectedItemIndex).getMasa())};
        Cursor c = sqLiteHelper.select("masas", null, "masa = ?", selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            etCod.setText(Integer.toString(c.getInt(0)));
            etTexto1.setText(c.getString((1)));
        }

        etCod.setVisibility(View.VISIBLE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.GONE);
        etTexto3.setVisibility(View.GONE);
        etTexto4.setVisibility(View.GONE);
        etTexto5.setVisibility(View.GONE);
        etTexto6.setVisibility(View.GONE);

        alert.setPositiveButton("Guardar Cambios", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String descripcion = etTexto1.getText().toString();

                if (noEditErrors(descripcion)) {
                    ContentValues values = new ContentValues();
                    values.put("descripcion", descripcion);
                    String[] whereArgs = new String[]{Integer.toString( masas.get(selectedItemIndex).getMasa() )};
                    sqLiteHelper.update("masas", values, "masa = ?", whereArgs);
                    masas.set(selectedItemIndex, new Masa(masas.get(selectedItemIndex).getMasa(), values.getAsString("descripcion")));
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

    public boolean noInsertErrors(String masa, String descripcion) {

        boolean correct = true;

        if(masa.equals("") || !onlyInteger(masa)) {
            correct = false;
            muestraToast("Valor en el campo \"masa\" erroneo.");
        }

        if(descripcion.equals("")) {
            correct = false;
            muestraToast("Valor en el campo \"descripcion\" erroneo.");
        }

        return correct;
    }

    public boolean noEditErrors(String descripcion) {

        boolean correct = true;

        if(descripcion.equals("")) {
            correct = false;
            muestraToast("Valor en el campo \"descripcion\" erroneo.");
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

    public void showAlertDeleteMasa(){
        Builder dlgAlert = new Builder(getActivity());
        dlgAlert.setTitle("Borrar Masa");
        dlgAlert.setMessage("Va ha borrar la masa de la base de datos.\nSeguro que desea eliminarla?");
        dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] whereArgs = new String[]{Integer.toString( masas.get(selectedItemIndex).getMasa() )};
                        sqLiteHelper.delete("masas", "masa = ?", whereArgs);
                        adaptador.remove(masas.get(selectedItemIndex));
                        adaptador.notifyDataSetChanged();
                        muestraToast("Masa eliminada.");
                    }
                }
        );

        dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Eliminar masa cancelada.");
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

    class AdaptadorMasas extends ArrayAdapter<Masa> {

        Activity context;

        AdaptadorMasas(Activity context) {
            super(context, R.layout.lista_tabla_bbdd, masas);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitem_lista_bbdd, null);

            TextView tvCod = (TextView) item.findViewById(R.id.tvCod);
            tvCod.setText(Integer.toString(masas.get(position).getMasa()));

            TextView tvTexto1 = (TextView) item.findViewById(R.id.tvTexto1);
            tvTexto1.setText(masas.get(position).getDescripcion());

            TextView tvTexto2 = (TextView) item.findViewById(R.id.tvTexto2);
            tvTexto2.setVisibility(item.GONE);

            TextView tvTexto3 = (TextView) item.findViewById(R.id.tvTexto3);
            tvTexto3.setVisibility(item.GONE);

            TextView tvTexto4 = (TextView)item.findViewById(R.id.tvTexto4);
            tvTexto4.setVisibility(item.GONE);

            TextView tvTexto5 = (TextView)item.findViewById(R.id.tvTexto5);
            tvTexto5.setVisibility(item.GONE);

            TextView tvTexto6 = (TextView)item.findViewById(R.id.tvTexto6);
            tvTexto6.setVisibility(item.GONE);

            return(item);
        }
    }

}
