package com.cebancpizza.admin;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.cebancpizza.database.Bebida;
import com.cebancpizza.database.CebancPizzaSQLiteHelper;

import java.util.ArrayList;

public class AdminBebidas extends Fragment {

    public Context context;
    private static final String SECTION_NUMBER = "section_number";
    private ArrayList<Bebida> bebidas = new ArrayList<>();
    private ListView lvListaBebidas;
    private AdaptadorBebidas adaptador;
    private int selectedItemIndex, menuItemIndex;
    private CebancPizzaSQLiteHelper sqLiteHelper;

    public AdminBebidas() {
    }

    public static AdminBebidas newInstance(int sectionNumber){
        AdminBebidas fragment = new AdminBebidas();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Al llamar a este metodo iniciara PedidoBebida
     * @param accion La operacion que se realizara: 1 Insertar. 2 Editar. 3 Eliminar.
     */
    public void iniciarAccion(int accion) {
        Log.wtf(getClass().getSimpleName(), "[accion -> " + accion + "]");
        if (accion == 1) {
            showAlertNewBebida();
        } else if (accion == 2) {
            showAlertUpdateBebida();
        } else if (accion == 3) {
            showAlertDeleteBebida();
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
            getBebidasBBDD();
            initListView();
        }
    }

    public void initListView() {
        adaptador = new AdaptadorBebidas(getActivity());
        lvListaBebidas = (ListView) getActivity().findViewById(R.id.list);
        lvListaBebidas.setAdapter(adaptador);
        registerForContextMenu(lvListaBebidas);
    }

    public void getBebidasBBDD() {
        Cursor c = sqLiteHelper.select("bebidas", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                bebidas.add( new Bebida(c.getInt(0), c.getString(1), c.getDouble(2)) );
            } while(c.moveToNext());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(bebidas.get(info.position).getDescripcion());
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
        // Mensaje de que accion se va realizar y sobre que bebida se va a realizar
        String[] menuItems = getResources().getStringArray(R.array.longclick_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = arrayTipo[bebidas.get(selectedItemIndex).getTipo()];
        muestraAviso("Accion elegida:", menuItemName + "\n" + listItemName);
        */
        Log.wtf("bebida.get(selectedItemIndex)", bebidas.get(selectedItemIndex) + "");
        if(menuItemIndex == 0) {
            iniciarAccion(2);
        } else if(menuItemIndex == 1) {
            iniciarAccion(3);
        }
        return true;
    }

    public void showAlertNewBebida() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(view);

        alert.setTitle("Añadir Bebida");
        alert.setMessage("Se va a añadir una bebida a la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) view.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) view.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) view.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) view.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) view.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) view.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) view.findViewById(R.id.etTexto6);
        etTexto6.setVisibility(View.GONE);

        etCod.setHint("bebida (Integer)");
        etTexto1.setHint("descripcion (String)");
        etTexto2.setHint("prVent (Double)");

        etCod.setVisibility(View.VISIBLE);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.VISIBLE);
        etTexto3.setVisibility(View.GONE);
        etTexto4.setVisibility(View.GONE);
        etTexto5.setVisibility(View.GONE);

        alert.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String bebida = etCod.getText().toString();
                String descripcion = etTexto1.getText().toString();
                String prVent = etTexto2.getText().toString();
                prVent = prVent.replace(",", ".");

                if (noInsertErrors(bebida, descripcion, prVent)) {
                    Log.wtf("BEBIDA", "[bebida -> " + bebida + "] [descripcion -> " + descripcion + "] [prVent -> " + prVent + "]");
                    if (!sqLiteHelper.exists("bebidas", "bebida", bebida)) {
                        ContentValues values = new ContentValues();
                        values.put("bebida", bebida);
                        values.put("descripcion", descripcion);
                        values.put("pr_vent", prVent);
                        sqLiteHelper.insert("bebidas", null, values);
                        bebidas.add(new Bebida(values.getAsInteger("bebida"), values.getAsString("descripcion"), values.getAsDouble("pr_vent")));
                        adaptador.notifyDataSetChanged();
                        muestraToast("Bebida añadida.");
                    } else {
                        muestraAviso("Bebida Repetida", "Ya hay una bebida con esa id!");
                    }

                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Añadir bebida cancelado.");
            }
        });

        alert.show();
    }

    public void showAlertUpdateBebida() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_alert_fields, null);
        alert.setView(view);

        alert.setTitle("Editar Bebida");
        alert.setMessage("Se va a modificar una bebida de la base de datos.\nIntroduzca los siguientes campos:");

        final EditText etCod = (EditText) view.findViewById(R.id.etCod);
        final EditText etTexto1 = (EditText) view.findViewById(R.id.etTexto1);
        final EditText etTexto2 = (EditText) view.findViewById(R.id.etTexto2);
        final EditText etTexto3 = (EditText) view.findViewById(R.id.etTexto3);
        final EditText etTexto4 = (EditText) view.findViewById(R.id.etTexto4);
        final EditText etTexto5 = (EditText) view.findViewById(R.id.etTexto5);
        final EditText etTexto6 = (EditText) view.findViewById(R.id.etTexto6);

        etTexto1.setHint("descripcion (String)");
        etTexto2.setHint("prVent (Double)");

        String[] selectionArgs = new String[] {Integer.toString(bebidas.get(selectedItemIndex).getBebida())};
        Cursor c = sqLiteHelper.select("bebidas", null, "bebida = ?", selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            etCod.setText(Integer.toString(c.getInt(0)));
            etTexto1.setText(c.getString((1)));
            etTexto2.setText(Double.toString(c.getDouble((2))));
        }


        etCod.setVisibility(View.VISIBLE);
        etCod.setEnabled(false);
        etTexto1.setVisibility(View.VISIBLE);
        etTexto2.setVisibility(View.VISIBLE);
        etTexto3.setVisibility(View.GONE);
        etTexto4.setVisibility(View.GONE);
        etTexto5.setVisibility(View.GONE);
        etTexto6.setVisibility(View.GONE);

        alert.setPositiveButton("Guardar Cambios", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String descripcion = etTexto1.getText().toString();
                String prVent = etTexto2.getText().toString();
                prVent = prVent.replace(",", ".");

                if (noEditErrors(descripcion, prVent)) {
                    ContentValues values = new ContentValues();
                    values.put("descripcion", descripcion);
                    values.put("pr_vent", prVent);
                    String[] whereArgs = new String[]{Integer.toString( bebidas.get(selectedItemIndex).getBebida() )};
                    sqLiteHelper.update("bebidas", values, "bebida = ?", whereArgs);
                    bebidas.set(selectedItemIndex, new Bebida(bebidas.get(selectedItemIndex).getBebida(), values.getAsString("descripcion"), values.getAsDouble("pr_vent")));
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

    public boolean noInsertErrors(String bebida, String descripcion, String prVent) {

        boolean correct = true;

        if(bebida.equals("") || !onlyInteger(bebida)) {
            correct = false;
            muestraToast("Valor en el campo \"bebida\" erroneo.");
        }

        if(descripcion.equals("")) {
            correct = false;
            muestraToast("Valor en el campo \"descripcion\" erroneo.");
        }

        prVent = prVent.replace(",", ".");
        if(prVent.equals("") || !onlyDouble(prVent)) {
            correct = false;
            muestraToast("Valor en el campo \"prVent\" erroneo.");
        }
        return correct;
    }

    public boolean noEditErrors(String descripcion, String prVent) {

        boolean correct = true;

        if(descripcion.equals("")) {
            correct = false;
            muestraToast("Valor en el campo \"descripcion\" erroneo.");
        }

        if(prVent.equals("") || !onlyDouble(prVent)) {
            correct = false;
            muestraToast("Valor en el campo \"prVent\" erroneo.");
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

    public static boolean onlyDouble(String s) {
//      formatos: (##,##) | (##.##)
        if (s.matches("\\d{0,2}\\.\\d{1,2}")) {
            return true;
        } else {
            System.err.println("Caracterer no válido.");
            return false;
        }
    }

    public void showAlertDeleteBebida(){
        Builder dlgAlert = new Builder(getActivity());
        dlgAlert.setTitle("Borrar Bebida");
        dlgAlert.setMessage("Va ha borrar la bebida de la base de datos.\nSeguro que desea eliminarla?");
        dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] whereArgs = new String[]{Integer.toString( bebidas.get(selectedItemIndex).getBebida() )};
                        sqLiteHelper.delete("bebidas", "bebida = ?", whereArgs);
                        adaptador.remove(bebidas.get(selectedItemIndex));
                        adaptador.notifyDataSetChanged();
                        muestraToast("Bebida eliminada.");
                    }
                }
        );

        dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                muestraToast("Eliminar bebida cancelada.");
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

    class AdaptadorBebidas extends ArrayAdapter<Bebida> {

        Activity context;

        AdaptadorBebidas(Activity context) {
            super(context, R.layout.lista_tabla_bbdd, bebidas);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitem_lista_bbdd, null);

            TextView tvCod = (TextView) item.findViewById(R.id.tvCod);
            tvCod.setText(Integer.toString(bebidas.get(position).getBebida()));

            TextView tvTexto1 = (TextView) item.findViewById(R.id.tvTexto1);
            tvTexto1.setText(bebidas.get(position).getDescripcion());

            TextView tvTexto2 = (TextView) item.findViewById(R.id.tvTexto2);
            tvTexto2.setText(Double.toString(bebidas.get(position).getPrVent()));

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
