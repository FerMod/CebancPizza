package com.cebancpizza.cliente;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.cebancpizza.R;
import com.cebancpizza.database.CebancPizzaSQLiteHelper;
import com.cebancpizza.database.PedidoPizza;

import java.util.ArrayList;

public class NuevaPizza extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private Spinner sTipos, sMasas, sTamanos;
    private Button bAnadir, bGuardarCambios, bDescartarCambios, bAceptar;
    private int posicionTipos, posicionMasas, posicionTamanos;
    private NumberPicker npCantidad;
    private int accion;
    private PedidoPizza pedidoPizza;
    private CebancPizzaSQLiteHelper sqLiteHelper;
    private ArrayList<String> pizzas, masas, tamanos, precios;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedido_pizza);

        sqLiteHelper = new CebancPizzaSQLiteHelper(this, "CebancPizza", null, 1);

        Intent intent = getIntent();
        pedidoPizza = intent.getExtras().getParcelable("pizza");
        accion = getIntent().getExtras().getInt("accion");

        precios = new ArrayList<>();
        precios.add("");
        precios.addAll(sqLiteHelper.fillArrayList("pizzas", "pr_vent"));

        pizzas = new ArrayList<>();
        pizzas.add(""); //To make the first item of the spinner blank
        pizzas.addAll(sqLiteHelper.fillArrayList("pizzas", "descripcion"));

        ArrayAdapter<String> arrayTipos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pizzas);
        arrayTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sTipos = (Spinner) this.findViewById(R.id.sTipos);
        sTipos.setAdapter(arrayTipos);
        sTipos.setOnItemSelectedListener(this);

        masas = new ArrayList<>();
        masas.add("");
        masas.addAll(sqLiteHelper.fillArrayList("masas", "descripcion"));

        ArrayAdapter<String> arrayMasas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, masas);
        arrayMasas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sMasas = (Spinner) this.findViewById(R.id.sMasas);
        sMasas.setAdapter(arrayMasas);
        sMasas.setOnItemSelectedListener(this);

        tamanos = new ArrayList<>();
        tamanos.add("");
        tamanos.addAll(sqLiteHelper.fillArrayList("tamanos", "descripcion"));

        ArrayAdapter<String> arrayTamanos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tamanos);
        arrayTamanos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sTamanos = (Spinner) this.findViewById(R.id.sTamanos);
        sTamanos.setAdapter(arrayTamanos);
        sTamanos.setOnItemSelectedListener(this);

        npCantidad = (NumberPicker) this.findViewById(R.id.npCantidad);
        npCantidad.setMaxValue(20);
        npCantidad.setMinValue(1);
        npCantidad.setFocusable(true);
        npCantidad.setFocusableInTouchMode(true);

        bAnadir = (Button) this.findViewById(R.id.bAnadir);
        bAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accionPizza(accion);
            }
        });

        bGuardarCambios = (Button) this.findViewById(R.id.bGuardarCambios);
        bGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accionPizza(accion);
            }
        });

        bDescartarCambios = (Button) this.findViewById(R.id.bDescartarCambios);
        bDescartarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descartarCambios();
            }
        });

        Log.wtf(getClass().getSimpleName(), "[accion -> " + accion + "]");

        if (accion == 2) { //Modo Editar
            int tipo, masa, tamano, cantidad;
            tipo = pedidoPizza.getPizza();
            masa = pedidoPizza.getMasa();
            tamano = pedidoPizza.getTamano();
            cantidad = pedidoPizza.getCantidad();
            Log.wtf(getClass().getSimpleName(), "[tipo -> " + tipo + "]" + "[masa -> " + masa + "]" + "[tamano -> " + tamano + "]" + "[cantidad -> " + cantidad + "]");
            sTipos.setSelection(tipo);
            sMasas.setSelection(masa);
            sTamanos.setSelection(tamano);
            npCantidad.setValue(cantidad);

            bGuardarCambios.setVisibility(View.VISIBLE);

            bDescartarCambios.setVisibility(View.VISIBLE);

            bAnadir.setVisibility(View.GONE);
        }

    }

    private void muestraToast(String text) {
        Context context = this.getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void muestraAviso(String title, String message) {
        Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(message);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        switch (parent.getId()) {

            case R.id.sTipos:
                posicionTipos = pos;
                break;
            case R.id.sMasas:
                posicionMasas = pos;
                break;
            case R.id.sTamanos:
                posicionTamanos = pos;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    public void accionPizza(int accion) {
        if (posicionTipos != 0 && posicionMasas !=0 && posicionTamanos !=0) {
            pedidoPizza = new PedidoPizza(sqLiteHelper.getMaxId("pedidos", "pedido") + 1, posicionTipos, posicionMasas, posicionTamanos, npCantidad.getValue(), getPrecioPizza());
            muestraToast("Pizza añadida.");

            Intent returnIntent = new Intent();
            returnIntent.putExtra("pizza", pedidoPizza);
            returnIntent.putExtra("precioPizza", getPrecioPizza());
            setResult(accion, returnIntent);
            finish();

        } else {
            muestraAviso("Campos incompletos", "Uno o varios campos estan vacios.\nEs necesario que sean rellenados.");
        }
    }

    public void descartarCambios() {
        finish();
    }

    public double getPrecioPizza(){

        double precio = 0;

        precio += Double.parseDouble(precios.get(posicionTipos));

        switch (posicionTamanos) {
            case 2:
                precio += 3;
                break;

            case 3:
                precio += 7;
                break;
        }

        precio *= npCantidad.getValue();

        Log.wtf("double getPrecioPizza()", "[" + precio + "]");
        return precio;
    }

    public void resetLayout(){
        sTipos.setSelection(0);
        sMasas.setSelection(0);
        sTamanos.setSelection(0);
        npCantidad.setValue(1);
    }
    private void onAceptarPressed(View view){
        finish();
    }

}
