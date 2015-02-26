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
import com.cebancpizza.database.Formpago;

import java.util.ArrayList;

public class AdminFormpagos extends Fragment {

    public Context context;
    private static final String SECTION_NUMBER = "section_number";
    private ArrayList<Formpago> formpagos = new ArrayList<>();
    private ListView lvListaFormpagos;
    private AdaptadorFormpagos adaptador;
    private int selectedItemIndex, menuItemIndex;
    private CebancPizzaSQLiteHelper sqLiteHelper;

    public AdminFormpagos() {
    }

    public static AdminFormpagos newInstance(int sectionNumber){
        AdminFormpagos fragment = new AdminFormpagos();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Al llamar a este metodo iniciara PedidoFormpago
     * @param accion La operacion que se realizara: 1 Insertar. 2 Editar. 3 Eliminar.
     */
    public void iniciarAccion(int accion) {
        Log.wtf(getClass().getSimpleName(), "[accion -> " + accion + "]");
        if (accion == 1) {
            showAlertNewFormpago();
        } else if (accion == 2) {
            showAlertUpdateFormpago();
        } else if (accion == 3) {
            showAlertDeleteFormpago();
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
        sqLiteHelper = new CebancPizzaSQLiteHelper(getActivity(), "CebancFormpago", null, 1);
        if (sqLiteHelper != null) {
            getFormpagosBBDD();
            initListView();
        }
    }

    public void initListView() {
        adaptador = new AdaptadorFormpagos(getActivity());
        lvListaFormpagos = (ListView) getActivity().findViewById(R.id.list);
        lvListaFormpagos.setAdapter(adaptador);
        registerForContextMenu(lvListaFormpagos);
    }

    public void getFormpagosBBDD() {
        Cursor c = sqLiteHelper.select("formpagos", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                formpagos.add( new Formpago(c.getString(0), c.getString(1)) );
            } while(c.moveToNext());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(formpagos.get(info.position).getDescripcion());
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
        // Mensaje de que accion se va realizar y sobre que formpago se va a realizar
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = arrayTipo[formpagos.get(selectedItemIndex).getTipo()];
        muestraAviso("Accion elegida:", menuItemName + "\n" + listItemName);
        */
        Log.wtf("formpago.get(selectedItemIndex)", formpagos.get(selectedItemIndex) + "");
        if(menuItemIndex == 0) {
            iniciarAccion(2);
        } else if(menuItemIndex == 1) {
            iniciarAccion(3);
        }
        return true;
    }

    public void showAlertNewFormpago() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(view);

        alert.setTitle("Añadir Formpago");
        alert.setMessage("Se va a añadir una formpago a la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) view.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) view.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) view.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) view.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) view.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) view.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) view.findViewById(R.id.etTexto6);
        etTexto6.setVisibility(View.GONE);

        etCod.setHint("formpago (Integer)");
        etTexto1.setHint("descripcion (String)");

        etCod.setVisibility(View.VISIBLE);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.GONE);
        etTexto3.setVisibility(View.GONE);
        etTexto4.setVisibility(View.GONE);
        etTexto5.setVisibility(View.GONE);

        alert.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String formpago = etCod.getText().toString();
                String descripcion = etTexto1.getText().toString();

                if (noInsertErrors(formpago, descripcion)) {
                    Log.wtf("BEBIDA", "[formpago -> " + formpago + "] [descripcion -> " + descripcion + "]");
                    if (!sqLiteHelper.exists("formpagos", "formpago", formpago)) {
                        ContentValues values = new ContentValues();
                        values.put("formpago", formpago);
                        values.put("descripcion", descripcion);
                        sqLiteHelper.insert("formpagos", null, values);
                        formpagos.add(new Formpago(values.getAsString("formpago"), values.getAsString("descripcion")));
                        adaptador.notifyDataSetChanged();
                        muestraToast("Formpago añadida.");
                    } else {
                        muestraAviso("Formpago Repetida", "Ya hay una formpago con esa id!");
                    }

                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Añadir formpago cancelado.");
            }
        });

        alert.show();
    }

    public void showAlertUpdateFormpago() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(view);

        alert.setTitle("Editar Formpago");
        alert.setMessage("Se va a modificar una formpago de la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) view.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) view.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) view.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) view.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) view.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) view.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) view.findViewById(R.id.etTexto6);
        etTexto6.setVisibility(View.GONE);

        etTexto1.setHint("descripcion (String)");

        String[] selectionArgs = new String[] {formpagos.get(selectedItemIndex).getFormpago()};
        Cursor c = sqLiteHelper.select("formpagos", null, "formpago = ?", selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            etCod.setText(c.getString(0));
            etTexto1.setText(c.getString((1)));
        }


        etCod.setVisibility(View.VISIBLE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.GONE);
        etTexto3.setVisibility(View.GONE);
        etTexto4.setVisibility(View.GONE);
        etTexto5.setVisibility(View.GONE);

        alert.setPositiveButton("Guardar Cambios", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String descripcion = etTexto1.getText().toString();

                if (noEditErrors(descripcion)) {
                    ContentValues values = new ContentValues();
                    values.put("descripcion", descripcion);
                    String[] whereArgs = new String[]{formpagos.get(selectedItemIndex).getFormpago()};
                    sqLiteHelper.update("formpagos", values, "formpago = ?", whereArgs);
                    formpagos.set(selectedItemIndex, new Formpago(formpagos.get(selectedItemIndex).getFormpago(), values.getAsString("descripcion")));
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

    public boolean noInsertErrors(String formpago, String descripcion) {

        boolean correct = true;

        if(formpago.equals("")) {
            correct = false;
            muestraToast("Valor en el campo \"formpago\" erroneo.");
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

    public void showAlertDeleteFormpago(){
        Builder dlgAlert = new Builder(getActivity());
        dlgAlert.setTitle("Borrar Formpago");
        dlgAlert.setMessage("Va ha borrar la formpago de la base de datos.\nSeguro que desea eliminarla?");
        dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] whereArgs = new String[]{formpagos.get(selectedItemIndex).getFormpago()};
                        sqLiteHelper.delete("formpagos", "formpago = ?", whereArgs);
                        adaptador.remove(formpagos.get(selectedItemIndex));
                        adaptador.notifyDataSetChanged();
                        muestraToast("Formpago eliminada.");
                    }
                }
        );

        dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Eliminar formpago cancelada.");
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

    class AdaptadorFormpagos extends ArrayAdapter<Formpago> {

        Activity context;

        AdaptadorFormpagos(Activity context) {
            super(context, R.layout.lista_tabla_bbdd, formpagos);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitem_lista_bbdd, null);

            TextView tvCod = (TextView) item.findViewById(R.id.tvCod);
            tvCod.setText(formpagos.get(position).getFormpago());

            TextView tvTexto1 = (TextView) item.findViewById(R.id.tvTexto1);
            tvTexto1.setText(formpagos.get(position).getDescripcion());

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
