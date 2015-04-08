package com.cebancpizza.cliente;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cebancpizza.R;
import com.cebancpizza.database.CebancPizzaSQLiteHelper;
import com.cebancpizza.database.Cliente;
import com.cebancpizza.database.PedidoBebida;
import com.cebancpizza.database.PedidoPizza;

import java.util.ArrayList;

import static android.widget.AdapterView.OnItemSelectedListener;

public class RealizarPedido extends Fragment implements MainActivity.OnMainActivityListener, OnItemSelectedListener {

    private TextView tvTotalPizzas, tvTotalBebidas, tvSumaTotales, tvNombre, tvDni, tvDireccion, tvTelefono;
    private String SECTION_NUMBER = "section_number";
    private double totalPizzas, totalBebidas, total;
    private Cliente cliente;
    private ArrayList<PedidoBebida> arrayPedidosBebidas;
    private ArrayList<PedidoPizza> arrayPedidosPizzas;
    private int posicionFormpagos;
    private ArrayList<String> formpagos;
    private CebancPizzaSQLiteHelper sqLiteHelper;


    public RealizarPedido() {
    }

    public RealizarPedido newInstance(int sectionNumber) {
        RealizarPedido fragment = new RealizarPedido();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.realizar_pedido, container, false);

        tvTotalPizzas = (TextView) view.findViewById(R.id.tvTotalPizzas);
        tvTotalPizzas.setText(" " + String.format("%.2f", totalPizzas) + "€");

        tvTotalBebidas = (TextView) view.findViewById(R.id.tvTotalBebidas);
        tvTotalBebidas.setText(" " + String.format("%.2f", totalBebidas) + "€");

        total = totalPizzas + totalBebidas;
        tvSumaTotales = (TextView) view.findViewById(R.id.tvSumaTotales);
        tvSumaTotales.setText(" " + String.format("%.2f", total) + "€");

        setRetainInstance(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.wtf("onActivityCreated()", "" + cliente);
        Log.wtf("REALIZAR PEDIDO( id cliente) ", "" + cliente.getCliente());
        if (cliente != null) {
            initElements();
        }
    }

    private void initElements() {
        tvNombre = (TextView) getActivity().findViewById(R.id.textViewNombre);
        tvNombre.setText(cliente.getNombre());

        tvDni = (TextView) getActivity().findViewById(R.id.textViewDni);
        tvDni.setText(cliente.getDni());

        tvDireccion = (TextView) getActivity().findViewById(R.id.textViewDireccion);
        tvDireccion.setText(cliente.getDireccion());

        tvTelefono = (TextView) getActivity().findViewById(R.id.textViewTelefono);
        tvTelefono.setText(cliente.getTelefono());
    }

    public void finalizarPedido() {
        if (totalPizzas > 0) {
            showAlertFormpago();
        } else {
            muestraAviso("Pizza no añadida", "Debe hacer almenos la compra de una pizza para realizar el pedido!", false);
        }
    }

    public void mensajeRealizarPedido() {

        Builder dlgAlert = new Builder(getActivity());
        dlgAlert.setTitle("Mensaje Confirmación");
        dlgAlert.setMessage("Va ha realizar la compra del pedido de pizzas y bebidas.\nDesea continuar?");
        dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                muestraPremio(total);
                insertarCliente();
                insertarPedidos();
                muestraAviso("Información", "Pedido realizado con exito!\nGracias por su compra!", true);
            }

        });
        dlgAlert.setNegativeButton("Cancelar", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void insertarCliente() {
        // Si no existe se introduce el nuevo cliente en la bbdd
        sqLiteHelper = new CebancPizzaSQLiteHelper(getActivity(), "CebancPizza", null, 1);
        Log.wtf("REALIZAR PEDIDO (EXIST CLIENTE)",  "" +sqLiteHelper.exists("clientes", "dni", cliente.getDni()));
        if (!sqLiteHelper.exists("clientes", "dni", cliente.getDni())) {
            ContentValues values = new ContentValues();
            values.put("dni", cliente.getDni());
            values.put("nombre", cliente.getNombre());
            values.put("direccion", cliente.getDireccion());
            values.put("telefono", cliente.getTelefono());
            sqLiteHelper.insert("clientes", null, values);
        }
        sqLiteHelper.close();
    }

    //TODO MAKE DIFERENTS FORMPAGOS TO CHOOSE
    public void showAlertFormpago() {

        sqLiteHelper = new CebancPizzaSQLiteHelper(getActivity(), "CebancPizza", null, 1);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_formpago_fields, null);

        final Spinner sFormpagos = (Spinner) view.findViewById(R.id.sFormpagos);

        formpagos = new ArrayList<>();
        formpagos.add(""); //To make the first item of the spinner blank
        formpagos.addAll(sqLiteHelper.fillArrayList("formpagos", "descripcion"));

        ArrayAdapter<String> arrayFormpagos = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, formpagos);
        arrayFormpagos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sFormpagos.setAdapter(arrayFormpagos);
        sFormpagos.setOnItemSelectedListener(this);
        sFormpagos.setVisibility(View.VISIBLE);

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Seleccione la Forma de Pago")
                .setMessage("Para proseguir con la compra, debe elegir la forma de pago y completar los campos que se pidan a continuación:")
                .setPositiveButton("Aceptar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (noErrors(posicionFormpagos)) {
                            Log.wtf("REALIZAR PEDIDO ( FORMPAGO )",  "" + formpagos.get(posicionFormpagos) );
                            mensajeRealizarPedido();
                            alertDialog.dismiss();
                        }
                    }

                });

            }

        });

        alertDialog.show();

        sqLiteHelper.close();
    }

    /**
     * Comprueba que los datos introducidos son correctos.
     */
    public boolean noErrors(int pos) {

        boolean correct = true;

        if (pos == 0) {
            correct = false;
            muestraToast("No se ha seleccionado ningun valor en forma de pago.");
        }

        return correct;

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        switch (parent.getId()) {

            case R.id.sFormpagos:
                posicionFormpagos = pos;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void insertarPedidos() {

        sqLiteHelper = new CebancPizzaSQLiteHelper(getActivity(), "CebancPizza", null, 1);

        int idPedido = sqLiteHelper.getMaxId("pedidos", "pedido") + 1;

        String[] descripcionFormpago = new String[]{formpagos.get(posicionFormpagos)};
        String[] columns = new String[]{"formpago"};
        Cursor cursor = sqLiteHelper.select("formpagos", columns, "descripcion = ?", descripcionFormpago, null, null, null);
        String formpago = "";
        if (cursor.moveToFirst()) {
            formpago = cursor.getString(0);
        }

        ContentValues values = new ContentValues();
        values.put("cliente", cliente.getCliente());
        values.put("fecha_albaran", sqLiteHelper.getCurrentDate());
        values.put("formpago", formpago);
        sqLiteHelper.insert("albaranes", null, values);

        int albaran = sqLiteHelper.getMaxId("albaranes", "albaran") + 1;

        for (PedidoPizza pedidoPizza : arrayPedidosPizzas) {
            values = new ContentValues();
            values.put("pedido", idPedido);
            values.put("articulo", pedidoPizza.getPizza());
            values.put("tipo", 1);
            values.put("albaran", albaran);
            sqLiteHelper.insert("pedidos", null, values);

            values = new ContentValues();
            values.put("pedido", idPedido);
            values.put("pizza", pedidoPizza.getPizza());
            values.put("masa", pedidoPizza.getMasa());
            values.put("tamano", pedidoPizza.getTamano());
            values.put("cantidad", pedidoPizza.getCantidad());
            values.put("precio", pedidoPizza.getPrecio());
            sqLiteHelper.insert("pedidos_pizzas", null, values);
        }

        for (PedidoBebida pedidoBebida : arrayPedidosBebidas) {
            values = new ContentValues();
            values.put("pedido", idPedido);
            values.put("articulo", pedidoBebida.getBebida());
            values.put("tipo", 2);
            values.put("albaran", albaran);
            sqLiteHelper.insert("pedidos", null, values);

            values = new ContentValues();
            values.put("pedido", idPedido);
            values.put("bebida", pedidoBebida.getBebida());
            values.put("cantidad", pedidoBebida.getCantidad());
            values.put("precio", pedidoBebida.getPrecio());
            sqLiteHelper.insert("pedidos_bebidas", null, values);
        }
        sqLiteHelper.close();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void muestraNotificacion(String ticker, String title, String message, long time) {

        Notification notification = new Notification.Builder(getActivity())
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(message)
                .setSubText("Copyright © 2013-2014 CebanPizza")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_logo)
                .setWhen(time)
                .build();
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private void muestraAviso(String title, String message, boolean close) {
        Builder dlgAlert = new Builder(getActivity());
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(message);
        if (close) {
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), PantallaInicio.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            });
        } else {
            dlgAlert.setPositiveButton("OK", null);
        }
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void muestraToast(String text) {
        Context context = getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Dependiendo del precio a pagar del pedido se hara un regalo u otro.
     *
     * @param total precio total del pedido
     * @return premio por la compra
     */
    public int muestraPremio(double total) {
        if (total >= 33) {
            muestraNotificacion("Aviso Cebanc Pizza", "Regalo Recibido!", "Muñeco Android y vale para el comedor de Cebanc", System.currentTimeMillis() + 3000);
            return 2;
        } else if (total >= 20) {
            muestraNotificacion("Aviso Cebanc Pizza", "Regalo Recibido!", "Muñeco Android", System.currentTimeMillis() + 3000);
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void passPedidoPizzasData(ArrayList<PedidoPizza> arrayList) {
        this.arrayPedidosPizzas = arrayList;
    }

    @Override
    public void passPedidoBebidasData(ArrayList<PedidoBebida> arrayList) {
        this.arrayPedidosBebidas = arrayList;
    }


    @Override
    public void passClienteData(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public void passTotalData(double totalPizzas, double totalBebidas) {
        Log.wtf("passData()", "[totalPizzas -> " + totalPizzas + "] [totalBebidas -> " + totalBebidas + "]");
        this.totalPizzas = totalPizzas;
        this.totalBebidas = totalBebidas;
    }
}
