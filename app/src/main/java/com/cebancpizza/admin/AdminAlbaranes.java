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
import com.cebancpizza.database.Albaran;
import com.cebancpizza.database.CebancPizzaSQLiteHelper;

import java.util.ArrayList;

public class AdminAlbaranes extends Fragment {

    public Context context;
    private static final String SECTION_NUMBER = "section_number";
    private ArrayList<Albaran> albaranes = new ArrayList<>();
    private ListView lvListaAlbaranes;
    private AdaptadorAlbaranes adaptador;
    private int selectedItemIndex, menuItemIndex;
    private CebancPizzaSQLiteHelper sqLiteHelper;

    public AdminAlbaranes() {
    }

    public static AdminAlbaranes newInstance(int sectionNumber){
        AdminAlbaranes fragment = new AdminAlbaranes();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Al llamar a este metodo realizara una accion.
     * @param accion La operacion que se realizara: 1 Insertar. 2 Editar. 3 Eliminar.
     */
    public void iniciarAccion(int accion) {
        Log.wtf(getClass().getSimpleName(), "[accion -> " + accion + "]");
        if (accion == 1) {
            showAlertNewAlbaran();
        } else if (accion == 2) {
            showAlertUpdateAlbaran();
        } else if (accion == 3) {
            showAlertDeleteAlbaran();
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
            getAlbaranesBBDD();
            initListView();
        }
    }

    public void initListView() {
        adaptador = new AdaptadorAlbaranes(getActivity());
        lvListaAlbaranes = (ListView) getActivity().findViewById(R.id.list);
        lvListaAlbaranes.setAdapter(adaptador);
        registerForContextMenu(lvListaAlbaranes);
    }

    public void getAlbaranesBBDD() {
        Cursor c = sqLiteHelper.select("albaranes", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                albaranes.add( new Albaran(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3)) );
            } while(c.moveToNext());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(Integer.toString(albaranes.get(info.position).getAlbaran()));
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
        // Mensaje de que accion se va realizar y sobre que albaran se va a realizar
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = arrayTipo[albarans.get(selectedItemIndex).getTipo()];
        muestraAviso("Accion elegida:", menuItemName + "\n" + listItemName);
        */
        Log.wtf("albaranes.get(selectedItemIndex)", albaranes.get(selectedItemIndex) + "");
        if(menuItemIndex == 0) {
            iniciarAccion(2);
        } else if(menuItemIndex == 1) {
            iniciarAccion(3);
        }
        return true;
    }

    public void showAlertNewAlbaran() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(view);

        alert.setTitle("Añadir Albaran");
        alert.setMessage("Se va a añadir un albaran a la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) view.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) view.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) view.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) view.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) view.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) view.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) view.findViewById(R.id.etTexto6);
        etTexto6.setVisibility(View.GONE);

        etCod.setHint("albaran (Integer)");
        etTexto1.setHint("cliente (Integer)");
        etTexto2.setHint("fecha_albaran (String)");
        etTexto3.setHint("formpago (String)");

        etCod.setVisibility(View.GONE);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.VISIBLE);
        etTexto2.setEnabled(false);
        etTexto3.setVisibility(View.VISIBLE);
        etTexto4.setVisibility(View.GONE);
        etTexto5.setVisibility(View.GONE);

        etTexto2.setText(sqLiteHelper.getCurrentDate());

        alert.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String cliente = etTexto1.getText().toString();
                String fechaAlbaran = etTexto2.getText().toString();
                String formpago = etTexto3.getText().toString();

                if (noErrors(cliente, formpago)) {
                    Log.wtf("ALBARAN", "[cliente -> " + cliente + "] [fecha_albaran -> " + fechaAlbaran + "] [formpago -> " + formpago + "]");
//                    if (!sqLiteHelper.exists("albaranes", "albaran", albaran)) {

                    ContentValues values = new ContentValues();
                    values.put("cliente", Integer.parseInt(cliente));
                    values.put("fecha_albaran", fechaAlbaran);
                    values.put("formpago", formpago);

                    sqLiteHelper.insert("albaranes", null, values);

                    String[] columns = new String[] {"MAX(albaran)"};
                    Cursor c = sqLiteHelper.select("albaranes", columns, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        Log.wtf("(albaran) c.getInt(0)", "[c.getInt(0) -> " + c.getInt(0) + "]");
                        albaranes.add(new Albaran(c.getInt(0), values.getAsInteger("cliente"), values.getAsString("fecha_albaran"), values.getAsString("formpago")));
                    }

                    adaptador.notifyDataSetChanged();
                    muestraToast("Albaran añadido.");
//                    } else {
//                        muestraAviso("Albaran Repetido", "Ya hay un albaran con esa id!");
//                    }

                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Añadir albaran cancelado.");
            }
        });

        alert.show();
    }

    public void showAlertUpdateAlbaran() {

        Builder alert = new Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(view);

        alert.setTitle("Editar Albaran");
        alert.setMessage("Se va a modificar un albaran de la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) view.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) view.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) view.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) view.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) view.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) view.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) view.findViewById(R.id.etTexto6);

        etCod.setHint("albaran (Integer)");
        etTexto1.setHint("cliente (Integer)");
        etTexto2.setHint("fecha_albaran (String)");
        etTexto3.setHint("formpago (String)");

        String[] selectionArgs = new String[] {Integer.toString(albaranes.get(selectedItemIndex).getAlbaran())};
        Cursor c = sqLiteHelper.select("albaranes", null, "albaran = ?", selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            etCod.setText(Integer.toString(c.getInt(0)));
            etTexto1.setText(Integer.toString(c.getInt(1)));
            etTexto2.setText(c.getString(2));
            etTexto3.setText(c.getString(3));
        }


        etCod.setVisibility(View.VISIBLE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.VISIBLE);
        etTexto2.setEnabled(false);
        etTexto3.setVisibility(View.VISIBLE);
        etTexto4.setVisibility(View.GONE);
        etTexto5.setVisibility(View.GONE);
        etTexto6.setVisibility(View.GONE);

        alert.setPositiveButton("Guardar Cambios", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String cliente = etTexto1.getText().toString();
                String fechaAlbaran = etTexto2.getText().toString();
                String formpago = etTexto3.getText().toString();

                if (noErrors(cliente, formpago)) {
                    ContentValues values = new ContentValues();
                    values.put("cliente", Integer.parseInt(cliente));
                    values.put("fecha_albaran", fechaAlbaran);
                    values.put("formpago", formpago);
                    String[] whereArgs = new String[]{Integer.toString( albaranes.get(selectedItemIndex).getAlbaran() )};
                    sqLiteHelper.update("albaranes", values, "albaran = ?", whereArgs);
                    albaranes.set(selectedItemIndex, new Albaran(albaranes.get(selectedItemIndex).getAlbaran(), values.getAsInteger("cliente"), values.getAsString("fecha_albaran"), values.getAsString("formpago")));
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

    public boolean noErrors(String cliente, String formpago) {

        boolean correct = true;

        if(cliente.equals("") || !onlyInteger(cliente)) {
            correct = false;
            muestraToast("Valor en el campo \"cliente\" erroneo.");
        }

        if(formpago.equals("")) {
            correct = false;
            muestraToast("Valor en el campo \"formpago\" erroneo.");
        } else if(!sqLiteHelper.exists("formpagos", "formpago", formpago)) {
            correct = false;
            muestraToast("No existe el formpago con id \""+ formpago +"\".");
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

    public void showAlertDeleteAlbaran(){
        Builder dlgAlert = new Builder(getActivity());
        dlgAlert.setTitle("Borrar Albaran");
        dlgAlert.setMessage("Va ha borrar el albaran de la base de datos.\nSeguro que desea eliminarlo?");
        dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] whereArgs = new String[]{Integer.toString( albaranes.get(selectedItemIndex).getAlbaran() )};
                        sqLiteHelper.delete("albaranes", "albaran = ?", whereArgs);
                        adaptador.remove(albaranes.get(selectedItemIndex));
                        adaptador.notifyDataSetChanged();
                        muestraToast("Albaran eliminado.");
                    }
                }
        );

        dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Eliminar albaran cancelado.");
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

    class AdaptadorAlbaranes extends ArrayAdapter<Albaran> {

        Activity context;

        AdaptadorAlbaranes(Activity context) {
            super(context, R.layout.lista_tabla_bbdd, albaranes);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitem_lista_bbdd, null);

            TextView tvCod = (TextView) item.findViewById(R.id.tvCod);
            tvCod.setText(Integer.toString(albaranes.get(position).getAlbaran()));

            TextView tvTexto1 = (TextView) item.findViewById(R.id.tvTexto1);
            tvTexto1.setText(Integer.toString(albaranes.get(position).getCliente()));

            TextView tvTexto2 = (TextView) item.findViewById(R.id.tvTexto2);
            tvTexto2.setText(albaranes.get(position).getFechaAlbaran());

            TextView tvTexto3 = (TextView) item.findViewById(R.id.tvTexto3);
            tvTexto3.setText(albaranes.get(position).getFormpago());

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
