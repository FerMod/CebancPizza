package com.cebancpizza.cliente;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cebancpizza.R;
import com.cebancpizza.database.Cliente;
import com.cebancpizza.database.PedidoBebida;
import com.cebancpizza.database.PedidoPizza;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, ResumenPedidoPizzas.OnResumenPedidoPizzasListener, ResumenPedidoBebidas.OnResumenPedidoBebidasListener {

//TODO redo the data save when orientation is changed. Te right way to do it is RETAINING AN OBJECT in runtime changes. (Handling Runtime Changes - http://developer.android.com/guide/topics/resources/runtime-changes.html) 

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ArrayList<PedidoPizza> pedidoPizzas = new ArrayList<>();
    private ArrayList<PedidoBebida> pedidoBebida = new ArrayList<>();
    public CharSequence mTitle, mAddTitle;
    private boolean doubleBackToExitPressedOnce;
    ResumenPedidoPizzas resumenPedidoPizzas;
    ResumenPedidoBebidas resumenPedidoBebidas;
    RealizarPedido realizarPedido;
    private double totalPizzas, totalBebidas;

    public interface OnMainActivityListener {
        public void passPizzaData(ArrayList<PedidoPizza> arrayList);
        public void passBebidaData(ArrayList<PedidoBebida> arrayList);
        public void passClienteData(Cliente cliente);
        public void passTotalData(double totalPizzas, double totalBebidas);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getString(R.string.menu_pizzas);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        this.overridePendingTransition(R.anim.enter_anim_horizontal, R.anim.exit_anim_horizontal);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        cambiarFragment(position);
    }

    @Override
    public void onActionBarButtonPressed(int num) {
        Log.wtf("onAddPressed("+num+")", "[resumenPedidoPizzas -> " + (resumenPedidoPizzas != null) + "]" + "[resumenPedidoBebidas -> " + (resumenPedidoBebidas != null) + "]"+ "[realizarPedido -> " + (realizarPedido != null) + "]");
        if(num == 0 && resumenPedidoPizzas != null) {
            resumenPedidoPizzas.iniciarPedidoPizza(1);
        } else if(num == 1 && resumenPedidoBebidas != null) {
            resumenPedidoBebidas.iniciarPedidoBebida(1);
        } else if(num == 2 && realizarPedido != null) {
            realizarPedido.finalizarPedido();
        }
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
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void cambiarFragment(int posicion) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (posicion) {
            case 0:
                mTitle = getString(R.string.menu_pizzas);
                mAddTitle = getString(R.string.anadir_pizza);
                resumenPedidoPizzas = new ResumenPedidoPizzas().newInstance(posicion);
                resumenPedidoPizzas.setArrayPizzas(pedidoPizzas);
                fragmentManager.beginTransaction().replace(R.id.container, resumenPedidoPizzas, mTitle.toString()).commit();
                break;
            case 1:
                mTitle = getString(R.string.menu_bebidas);
                mAddTitle = getString(R.string.anadir_bebida);
                resumenPedidoBebidas = new ResumenPedidoBebidas();
                resumenPedidoBebidas.setArrayBebidas(pedidoBebida);
                fragmentManager.beginTransaction().replace(R.id.container, resumenPedidoBebidas, mTitle.toString()).commit();
                break;
            case 2:
                mTitle = getString(R.string.menu_finalizar);
                mAddTitle = getString(R.string.finalizar);
                realizarPedido = new RealizarPedido();
                realizarPedido.passTotalData(totalPizzas, totalBebidas);
                realizarPedido.passPizzaData(pedidoPizzas);
                realizarPedido.passBebidaData(pedidoBebida);
                realizarPedido.passClienteData(getIntent().getExtras().<Cliente>getParcelable("cliente"));
                fragmentManager.beginTransaction().replace(R.id.container, realizarPedido, mTitle.toString()).commit();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void disbleActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu); //<-------------
            MenuItem item = menu.findItem(R.id.action_menu);
            item.setTitle(mAddTitle);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_admin) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void muestraToast(String text) {
        Context context = this.getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void passBebidaData(double totalBebidas) {
        this.totalBebidas = totalBebidas;
    }

    @Override
    public void passPizzaData(double totalPizzas) {
        this.totalPizzas = totalPizzas;
    }

    @Override
    public void passPizzaData(ArrayList<PedidoPizza> arrayList) {
        Log.wtf("passData(ArrayList<Pizza> arrayList)", "["+ arrayList +"]");
        pedidoPizzas = arrayList;
    }

    @Override
    public void passBebidaData(PedidoBebida pedidoBebida) {

    }

    @Override
    public void passBebidaData(ArrayList<PedidoBebida> arrayList) {
        Log.wtf("passData(ArrayList<Bebida> arrayList)", "["+ arrayList +"]");
        pedidoBebida = arrayList;
    }


}
