package com.cebancpizza.cliente;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cebancpizza.R;

public class PantallaInicio extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_inicio);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Metodo para pasar al siguiente "Activity"
     * @param view view
     */
    public void irSiguiente(View view){
        Intent intent = new Intent(this, DatosCliente.class);
        startActivity(intent);
    }
}
