package com.example.thesecretofcooking20;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipes_db";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla y sus columnas
    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_RATING = "rating";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_INGREDIENTS + " TEXT, " +
                COLUMN_RATING + " INTEGER" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // Método para agregar una receta
    public long agregarReceta(String nombre, String ingredientes, int calificacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COLUMN_NAME, nombre);
        valores.put(COLUMN_INGREDIENTS, ingredientes);
        valores.put(COLUMN_RATING, calificacion);

        long resultado = db.insert(TABLE_RECIPES, null, valores);
        db.close();
        return resultado; // Retorna el ID de la fila insertada, o -1 si hubo error
    }


    // Método para obtener todas las recetas
    public Cursor obtenerTodasLasRecetas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RECIPES, null, null, null, null, null, null);
    }

    // Método para eliminar una receta por nombre
    public int eliminarReceta(String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete(TABLE_RECIPES, COLUMN_NAME + "=?", new String[]{nombre});
        db.close();
        return resultado;
    }
}


