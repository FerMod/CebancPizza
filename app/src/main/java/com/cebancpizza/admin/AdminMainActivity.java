package com.cebancpizza.admin;

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
import com.cebancpizza.cliente.PantallaInicio;
import com.cebancpizza.cliente.RealizarPedido;
import com.cebancpizza.cliente.ResumenPedidoBebidas;
import com.cebancpizza.cliente.ResumenPedidoPizzas;
import com.cebancpizza.database.CebancPizzaSQLiteHelper;

public class AdminMainActivity extends ActionBarActivity implements AdminNavigationDrawerFragment.AdminNavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private AdminNavigationDrawerFragment mNavigationDrawerFragment;
    public CharSequence mTitle, mAddTitle;
    private boolean doubleBackToExitPressedOnce;
    ResumenPedidoPizzas resumenPedidoPizzas;
    ResumenPedidoBebidas resumenPedidoBebidas;
    RealizarPedido realizarPedido;
    AdminPizzas adminPizzas;
    AdminTamanos adminTamanos;
    AdminMasas adminMasas;
    AdminPedidosPizzas adminPedidosPizzas;
    AdminBebidas adminBebidas;
    AdminPedidosBebidas adminPedidosBebidas;
    AdminPedidos adminPedidos;
    AdminTipos adminTipos;
    AdminAlbaranes adminAlbaranes;
    AdminClientes adminClientes;
    AdminFormpagos adminFormpagos;
    private CebancPizzaSQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main);

        mNavigationDrawerFragment = (AdminNavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
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
        Log.wtf("onAddPressed(num)", "[num -> " + num + "]");
        switch (num) {

            case 0:
                if(adminPizzas != null) {
                    adminPizzas.iniciarAccion(1);
                }
                break;

            case 1:
                if(adminTamanos != null) {
                    adminTamanos.iniciarAccion(1);
                }
                break;

            case 2:
                if(adminMasas != null) {
                    adminMasas.iniciarAccion(1);
                }
                break;

            case 3:
                if(adminPedidosPizzas!= null) {
                    adminPedidosPizzas.iniciarAccion(1);
                }
                break;

            case 4:
                if(adminBebidas != null) {
                    adminBebidas.iniciarAccion(1);
                }
                break;

            case 5:
                if(adminPedidosBebidas != null) {
                    adminPedidosBebidas.iniciarAccion(1);
                }
                break;

            case 6:
                if(adminPedidos != null) {
                    adminPedidos.iniciarAccion(1);
                }
                break;

            case 7:
                if(adminTipos != null) {
                    adminTipos.iniciarAccion(1);
                }
                break;

            case 8:
                if(adminAlbaranes != null) {
                    adminAlbaranes.iniciarAccion(1);
                }
                break;

            case 9:
                if(adminClientes != null) {
                    adminClientes.iniciarAccion(1);
                }
                break;

            case 10:
                if(adminFormpagos != null) {
                    adminFormpagos.iniciarAccion(1);
                }

                break;
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

    //TODO Arreglar la app de cliente, con los cambios realizados a la estructura de la aplicacion.
    public void cambiarFragment(int posicion) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (posicion) {
            case 0:
                mTitle = getString(R.string.menu_admin_pizzas);
                mAddTitle = getString(R.string.anadir_pizza);
                adminPizzas = new AdminPizzas().newInstance(posicion);
                fragmentManager.beginTransaction().replace(R.id.container, adminPizzas, mTitle.toString()).commit();
                break;
            case 1:
                mTitle = getString(R.string.menu_admin_tamanos);
                mAddTitle = getString(R.string.anadir_tamano);
                adminTamanos = new AdminTamanos().newInstance(posicion);
                fragmentManager.beginTransaction().replace(R.id.container, adminTamanos, mTitle.toString()).commit();
                break;
            case 2:
                mTitle = getString(R.string.menu_admin_masas);
                mAddTitle = getString(R.string.anadir_masa);
                adminMasas = new AdminMasas().newInstance(posicion);
                fragmentManager.beginTransaction().replace(R.id.container, adminMasas, mTitle.toString()).commit();
                break;
            case 3:
                mTitle = getString(R.string.menu_admin_pedidospizzas);
                mAddTitle = getString(R.string.anadir_pedidopizza);
                adminPedidosPizzas = new AdminPedidosPizzas().newInstance(posicion);
                fragmentManager.beginTransaction().replace(R.id.container, adminPedidosPizzas, mTitle.toString()).commit();
                break;
            case 4:
                mTitle = getString(R.string.menu_admin_bebidas);
                mAddTitle = getString(R.string.anadir_bebida);
                adminBebidas = new AdminBebidas().newInstance(posicion);
                fragmentManager.beginTransaction().replace(R.id.container, adminBebidas, mTitle.toString()).commit();
                break;
            case 5:
                mTitle = getString(R.string.menu_admin_pedidosbebidas);
                mAddTitle = getString(R.string.anadir_pedidobebida);
                adminPedidosBebidas = new AdminPedidosBebidas().newInstance(posicion);
                fragmentManager.beginTransaction().replace(R.id.container, adminPedidosBebidas, mTitle.toString()).commit();
                break;
            case 6:
                mTitle = getString(R.string.menu_admin_pedidos);
                mAddTitle = getString(R.string.anadir_pedido);
                adminPedidos = new AdminPedidos().newInstance(posicion);
                fragmentManager.beginTransaction().replace(R.id.container, adminPedidos, mTitle.toString()).commit();
                break;
            case 7:
                mTitle = getString(R.string.menu_admin_tipos);
                mAddTitle = getString(R.string.anadir_tipo);
                adminTipos = new AdminTipos().newInstance(posicion);
                fragmentManager.beginTransaction().replace(R.id.container, adminTipos, mTitle.toString()).commit();
                break;
            case 8:
                mTitle = getString(R.string.menu_admin_albaranes);
                mAddTitle = getString(R.string.anadir_albaran);
                adminAlbaranes = new AdminAlbaranes().newInstance(posicion);
                fragmentManager.beginTransaction().replace(R.id.container, adminAlbaranes, mTitle.toString()).commit();
                break;
            case 9:
                mTitle = getString(R.string.menu_admin_clientes);
                mAddTitle = getString(R.string.anadir_cliente);
                adminClientes = new AdminClientes().newInstance(posicion);
                fragmentManager.beginTransaction().replace(R.id.container, adminClientes, mTitle.toString()).commit();
                break;
            case 10:
                mTitle = getString(R.string.menu_admin_formpagos);
                mAddTitle = getString(R.string.anadir_formpago);
                adminFormpagos = new AdminFormpagos().newInstance(posicion);
                fragmentManager.beginTransaction().replace(R.id.container, adminFormpagos, mTitle.toString()).commit();
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

}
