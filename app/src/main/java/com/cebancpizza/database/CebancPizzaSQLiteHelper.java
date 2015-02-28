package com.cebancpizza.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CebancPizzaSQLiteHelper extends SQLiteOpenHelper {

    String sqlCreatePizzas ="CREATE TABLE pizzas " +
            "( " +
            "pizza INTEGER PRIMARY KEY, " +
            "descripcion TEXT, " +
            "pr_vent INTEGER " +
            ")";

    String sqlCreateTamanos ="CREATE TABLE tamanos " +
            "( " +
            "tamano INTEGER PRIMARY KEY, " +
            "descripcion TEXT " +
            ")";

    String sqlCreateMasas ="CREATE TABLE masas " +
            "( " +
            "masa INTEGER PRIMARY KEY, " +
            "descripcion TEXT " +
            ")";

    String sqlCreateBebidas ="CREATE TABLE bebidas " +
            "( " +
            "bebida INTEGER PRIMARY KEY, " +
            "descripcion TEXT, " +
            "pr_vent INTEGER " +
            ")";

    String sqlCreateClientes ="CREATE TABLE clientes " +
            "( " +
            "cliente INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "dni TEXT, " +
            "nombre TEXT, " +
            "direccion TEXT, " +
            "telefono TEXT " +
            ")";

    String sqlCreatePedidosPizzas ="CREATE TABLE pedidos_pizzas " +
            "( " +
            "pedido_pizza INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "pedido INTEGER, " +
            "pizza INTEGER, " +
            "masa INTEGER, " +
            "tamano INTEGER, " +
            "cantidad INTEGER, " +
            "precio DECIMAL(6,2), " +
            "FOREIGN KEY(pedido) REFERENCES pedidos(pedido), " +
            "FOREIGN KEY(pizza) REFERENCES pizzas(pizza), " +
            "FOREIGN KEY(masa) REFERENCES masas(masa), " +
            "FOREIGN KEY(tamano) REFERENCES tamanos(tamano) " +
            ") ";

    String sqlCreatePedidosBebidas ="CREATE TABLE pedidos_bebidas " +
            "( " +
            "pedido_bebida INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "pedido INTEGER, " +
            "bebida INTEGER, " +
            "cantidad INTEGER, " +
            "precio DECIMAL(6,2), " +
            "FOREIGN KEY(pedido) REFERENCES pedidos(pedido), " +
            "FOREIGN KEY(bebida) REFERENCES bebidas(bebida) " +
            ") ";

    String sqlCreatePedidos ="CREATE TABLE pedidos " +
            "( " +
            "pedido INTEGER, " +
            "articulo INTEGER, " +
            "tipo INTEGER, " +
            "albaran INTEGER, " +
            "PRIMARY KEY (pedido, articulo, tipo), " +
            "FOREIGN KEY(articulo) REFERENCES pedidos_bebidas(pedido_bebida), " +
            "FOREIGN KEY(articulo) REFERENCES pedidos_pizzas(pedido_pizza), " +
            "FOREIGN KEY(tipo) REFERENCES tipos(tipo), " +
            "FOREIGN KEY(albaran) REFERENCES albaranes(albaran) " +
            ") ";

    String sqlCreateTipos ="CREATE TABLE tipos " +
            "( " +
            "tipo INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "descripcion TEXT " +
            ") ";

    String sqlCreateAlbaranes ="CREATE TABLE albaranes " +
            "( " +
            "albaran INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cliente INTEGER, " +
            "fecha_albaran DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "formpago TEXT, " +
            "FOREIGN KEY(cliente) REFERENCES clientes(cliente), " +
            "FOREIGN KEY(formpago) REFERENCES formpagos(formpago) " +
            ") ";

    String sqlCreateFormpagos ="CREATE TABLE formpagos " +
            "( " +
            "formpago TEXT, " +
            "descripcion TEXT" +
            ")";

    String sqlInitPizzas ="INSERT INTO pizzas (descripcion, pr_vent) " +
            "VALUES ('Carbonara', 14.20), ('Barbacoa', 15.50), ('Cuatro Quesos', 13.20), ('Vegetal', 15.40), ('Tropical', 16.40)";

    String sqlInitTamanos ="INSERT INTO tamanos (descripcion) " +
            "VALUES ('Individual'), ('Mediana'), ('Familiar')";

    String sqlInitMasas ="INSERT INTO masas (descripcion) " +
            "VALUES ('Fina'), ('Normal')";

    String sqlInitBebidas ="INSERT INTO bebidas (descripcion, pr_vent) " +
            "VALUES ('CocaCola', 1.50), ('Fanta Limon', 1.35), ('Fanta Naranja', 1.55), ('Nestea', 1.43), ('San Miguel', 1.10), ('Agua', 1.20)";

    String sqlInitTipos ="INSERT INTO tipos (descripcion) " +
            "VALUES ('Pizza'), ('Bebida')";

    String sqlInitFormpagos ="INSERT INTO formpagos (formpago, descripcion) " +
            "VALUES ('C', 'Contado'), ('L1', '30'), ('L2', '30/60'), ('L3', '30/60/90'), ('L4', '30/60/90/120')";


    public CebancPizzaSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sqlCreatePizzas);
        db.execSQL(sqlCreateTamanos);
        db.execSQL(sqlCreateMasas);
        db.execSQL(sqlCreateBebidas);
        db.execSQL(sqlCreateClientes);
        db.execSQL(sqlCreatePedidosPizzas);
        db.execSQL(sqlCreatePedidosBebidas);
        db.execSQL(sqlCreatePedidos);
        db.execSQL(sqlCreateTipos);
        db.execSQL(sqlCreateAlbaranes);
        db.execSQL(sqlCreateFormpagos);

        db.execSQL(sqlInitPizzas);
        db.execSQL(sqlInitTamanos);
        db.execSQL(sqlInitMasas);
        db.execSQL(sqlInitBebidas);
        db.execSQL(sqlInitTipos);
        db.execSQL(sqlInitFormpagos);

        //  insert into clientes (dni, nombre, direccion, formpago, telefono) values (123, 'Paco', 'CasaPaco', 'C', 123456789);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS pizzas");
        db.execSQL("DROP TABLE IF EXISTS tamanos");
        db.execSQL("DROP TABLE IF EXISTS masas");
        db.execSQL("DROP TABLE IF EXISTS bebidas");
        db.execSQL("DROP TABLE IF EXISTS clientes");
        db.execSQL("DROP TABLE IF EXISTS pedidos_pizzas");
        db.execSQL("DROP TABLE IF EXISTS pedidos_bebidas");
        db.execSQL("DROP TABLE IF EXISTS pedidos");
        db.execSQL("DROP TABLE IF EXISTS tipos");
        db.execSQL("DROP TABLE IF EXISTS albaranes");
        db.execSQL("DROP TABLE IF EXISTS formpagos");

        onCreate(db);

    }

    public void insert(String table, String nullColumnHack, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(table, nullColumnHack, values);
    }

    public Cursor select(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public void update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(table, values, whereClause, whereArgs);
    }

    public void delete(String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, whereClause, whereArgs);
    }

    public boolean exists(String table, String selection, String value){
        String[] columns = new String[] {selection};
        String[] selectionArgs = new String[] {value};
        Cursor c = this.select(table, columns, selection + " = ?", selectionArgs, null, null, null);
        return c.moveToFirst();
    }

    public double getPrecioVenta(String table, String primaryKey, String value){
        String[] columns = new String[] {"pr_vent"};
        String[] selectionArgs = new String[] {value};
        Cursor c = this.select(table, columns, primaryKey + " = ?", selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Log.wtf("getPrecioVenta(String table, String primaryKey, String value)", "[table -> " + table + "] [primaryKey -> " + primaryKey + "] [value -> " + value + "] {Returns(double) - > " + c.getDouble(0) + " }");
            return c.getDouble(0);
        } else {
            return 0;
        }
    }

    public String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    public ArrayList<String> fillArrayList(String table, String column) {

        String[] columns = new String[] {column};
        Cursor c = this.select(table, columns, null, null, null, null, null);

        ArrayList<String> arrayList = new ArrayList<>();

        String debudMessage = "";

        if (c.moveToFirst()) {
            do {
                debudMessage += " [" + c.getString(0) + "]";

                arrayList.add(c.getString(0));
            } while(c.moveToNext());
        }

        Log.wtf("fillArrayList(" + table + ", " + column + ")", debudMessage);

        return arrayList;

    }

    public int getMaxId(String table, String column) {
        String[] columns = new String[] {"MAX(" + column + ")"};
        Cursor c = this.select(table, columns, null, null, null, null, null);
        if (c.moveToFirst()) {
            Log.wtf("getMaxId(" + table + ", " + column + ")", "[c.getInt(0) -> " + c.getInt(0) + "]");
            return c.getInt(0);
        }
        return -1;
    }


}
