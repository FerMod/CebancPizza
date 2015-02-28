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
import com.cebancpizza.database.PedidoBebida;

import java.util.ArrayList;

public class NuevaBebida extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private Spinner sTipos;
    private NumberPicker npCantidad;
    private int accion, posicionTipos;
    private Button bAnadir, bGuardarCambios, bDescartarCambios;
    private PedidoBebida pedidoBebida;
    private CebancPizzaSQLiteHelper sqLiteHelper;
    private ArrayList<String> bebidas, precios;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedido_bebida);

        sqLiteHelper = new CebancPizzaSQLiteHelper(this, "CebancPizza", null, 1);

        precios = new ArrayList<>();
        precios.add("");
        precios.addAll(sqLiteHelper.fillArrayList("bebidas", "pr_vent"));

        bebidas = new ArrayList<>();
        bebidas.add(""); //To make the first item of the spinner blank
        bebidas.addAll(sqLiteHelper.fillArrayList("bebidas", "descripcion"));

        ArrayAdapter<String> arrayTipos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bebidas);
        arrayTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sTipos = (Spinner) this.findViewById(R.id.sTipos);
        sTipos.setAdapter(arrayTipos);
        sTipos.setOnItemSelectedListener(this);

        npCantidad = (NumberPicker) this.findViewById(R.id.npCantidad);
        npCantidad.setMaxValue(20);
        npCantidad.setMinValue(1);
        npCantidad.setFocusable(true);
        npCantidad.setFocusableInTouchMode(true);

        Intent intent = getIntent();
        pedidoBebida = intent.getExtras().getParcelable("bebida");
        accion = getIntent().getExtras().getInt("accion");

        bAnadir = (Button) this.findViewById(R.id.bAnadir);
        bAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accionBebida(accion);
            }
        });

        bGuardarCambios = (Button) this.findViewById(R.id.bGuardarCambios);
        bGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accionBebida(accion);
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
            int tipo, cantidad;
            tipo = pedidoBebida.getBebida();
            cantidad = pedidoBebida.getCantidad();
            Log.wtf(getClass().getSimpleName(), "[tipo -> " + tipo + "]" + "[cantidad -> " + cantidad + "]");
            sTipos.setSelection(tipo);
            npCantidad.setValue(cantidad);

            bGuardarCambios.setVisibility(View.VISIBLE);

            bDescartarCambios.setVisibility(View.VISIBLE);

            bAnadir.setVisibility(View.GONE);
        }

    }

    private void muestraAviso(String title, String message) {
        Builder dlgAlert = new AlertDialog.Builder(this);
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
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    public void accionBebida(int accion) {
        if (posicionTipos != 0) {
            pedidoBebida = new PedidoBebida(sqLiteHelper.getMaxId("pedidos", "pedido") + 1, posicionTipos, npCantidad.getValue(), getPrecioBebida());
            muestraToast("Bebida añadida.");

            Intent returnIntent = new Intent();
            returnIntent.putExtra("bebida", pedidoBebida);
            returnIntent.putExtra("precioBebida", getPrecioBebida());
            setResult(accion, returnIntent);
            finish();

        } else {
            muestraAviso("Campos incompletos", "Uno o varios campos estan vacios.\nEs necesario que sean rellenados.");
        }
    }

    public void descartarCambios() {
        finish();
    }

    public double getPrecioBebida() {

        double precio = 0;

        precio += Double.parseDouble(precios.get(posicionTipos));

        precio *= npCantidad.getValue();

        Log.wtf("double getPrecioBebida()", "[" + precio + "]");

        return precio;
    }

    private void muestraToast(String text) {
        Context context = this.getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
