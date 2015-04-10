package com.cebancpizza.cliente;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cebancpizza.R;
import com.cebancpizza.admin.AdminMainActivity;
import com.cebancpizza.database.CebancPizzaSQLiteHelper;
import com.cebancpizza.database.Cliente;

public class DatosCliente extends ActionBarActivity {

    private EditText etNombre, etDni, etDireccion, etTelefono;
    private boolean doubleBackToExitPressedOnce;
    private CebancPizzaSQLiteHelper sqLiteHelper;
    private String ADMIN_PASSWORD = "admin";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_cliente);

        Button button = (Button) this.findViewById(R.id.btnSiguiente);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSiguientePressed(v);
            }
        });
        this.overridePendingTransition(R.anim.enter_anim_vertical, R.anim.exit_anim_vertical);

        etNombre = (EditText) this.findViewById(R.id.etNombre);
        etDni = (EditText) this.findViewById(R.id.etDni);
        etDni.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //When focus is lost check that the text field has valid values.
                if (!hasFocus) {
                    buscarCliente(v);
                }
            }
        });
        etDireccion = (EditText) this.findViewById(R.id.etDireccion);
        etTelefono = (EditText) this.findViewById(R.id.etTelefono);
    }

    /*
        ContentValues values = new ContentValues();
        values.put("descripcion", "PizzaNuevaConContentValues");
        values.put("pr_vent", 999.5);
        sqLiteHelper.insert("pizzas", null, values);

        ContentValues values = new ContentValues();
        values.put("descripcion", "PizzaEditada");
        values.put("pr_vent", 23.3);
        sqLiteHelper.update("pizzas", values, "pizza = 8", null);

        sqLiteHelper.delete("pizzas", "pizza = 6", null);

        String[] columns = new String[] {"pizza", "descripcion", "pr_vent"};
        Cursor c = sqLiteHelper.select("pizzas", columns, null, null, null, null, null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {

            //Recorremos el cursor hasta que no haya más registros
            do {
                muestraToast(c.getInt(0) + " | " + c.getString(1) + " | " + c.getDouble(2));
            } while(c.moveToNext());

        }
        */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.wtf("boolean onOptionsItemSelected(MenuItem " + item + ")", "[R.id.menu_admin -> " + (item.getItemId() == R.id.menu_admin) + "]");
        if (item.getItemId() == R.id.menu_admin) {
            adminLogin();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("Seguro de quiere salir?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), PantallaInicio.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Vuelva a pulsar atrás para salir.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    /**
     * Comprueba que los datos introducidos son correctos y en caso de que asi lo sea, llamara al metodo que abre el segundo activity.
     */
    public void onSiguientePressed(View view) {

        boolean correcto = true;
        if (etNombre.getText().toString().equals("")) {
            correcto = false;
            muestraToast("No ha introducido ningun valor en el campo \"nombre\".");
        }

        if (etDni.getText().toString().equals("")) {
            correcto = false;
            muestraToast("No ha introducido ningun valor en el campo \"dni\".");
        }

        if (etDireccion.getText().toString().equals("")) {
            correcto = false;
            muestraToast("No ha introducido ningun valor en el campo \"direccion\".");
        }

        if (etTelefono.getText().toString().equals("")) {
            correcto = false;
            muestraToast("No ha introducido ningun valor en el campo \"telefono\".");
        }

        if (correcto) {
            irSiguiente();
        }

    }

    /**
     * Muestra un "Toast" con el texto que se le pasa como parametro
     *
     * @param text texto a aparecer
     */
    private void muestraToast(String text) {
        Context context = this.getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Metodo para pasar a la siguiente pantalla
     */
    public void irSiguiente() {

        Cliente cliente;
        Log.wtf("DATOS PEDIDO( exist cliente) ",  "" + sqLiteHelper.exists("clientes", "dni", etDni.getText().toString()));
        if( sqLiteHelper.exists("clientes", "dni", etDni.getText().toString()) ) {

            String[] dniCliente = new String[]{etDni.getText().toString()};
            String[] columns = new String[]{"cliente"};
            Cursor cursor = sqLiteHelper.select("clientes", columns, "dni = ?", dniCliente, null, null, null);
            int idCliente = -1;
            if (cursor.moveToFirst()) {
                idCliente = cursor.getInt(0);
            }

            Log.wtf("DATOS PEDIDO( id cliente) ", idCliente + "");

            cliente = new Cliente(idCliente, etDni.getText().toString(), etNombre.getText().toString(), etDireccion.getText().toString(), etTelefono.getText().toString());

        } else {

            cliente = new Cliente(etDni.getText().toString(), etNombre.getText().toString(), etDireccion.getText().toString(), etTelefono.getText().toString());

        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("cliente", cliente);
        startActivity(intent);
    }

    public void buscarCliente(View view) {
        sqLiteHelper = new CebancPizzaSQLiteHelper(this, "CebancPizza", null, 1);
        String[] dni = new String[]{etDni.getText().toString()};
        Cursor c = sqLiteHelper.select("clientes", null, "dni = ?", dni, null, null, null);
        if (c.moveToFirst()) {
            etDni.setText(c.getString(1));
            etNombre.setText(c.getString(2));
            etDireccion.setText(c.getString(3));
            etTelefono.setText(c.getString(4));
            muestraToast("Cliente encontrado");
        } else {
            muestraToast("Cliente nuevo introducido");
        }
        sqLiteHelper.close();
    }

    public void adminLogin() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Modo Admin");
        alert.setMessage("Introduce la contraseña de Administrador:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(input);

        final Intent intent = new Intent(this, AdminMainActivity.class);

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (value.equals(ADMIN_PASSWORD)) {
                    startActivity(intent);
                } else {
                    muestraAviso("Acceso Denegado", "La contraseña introducida no es válida.");
                }
            }
        });

        alert.setNegativeButton("Cancelar", null);
        alert.show();
    }

    private void muestraAviso(String title, String message) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(message);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

}
