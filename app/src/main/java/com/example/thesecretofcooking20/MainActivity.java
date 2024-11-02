package com.example.thesecretofcooking20;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listaRecetasView;
    private DatabaseHelper baseDatosHelper;
    private ArrayList<String> listaRecetas;
    private ArrayAdapter<String> adapter;
    private Button btnAddRecipe, btnDeleteRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar la base de datos y la lista de recetas
        baseDatosHelper = new DatabaseHelper(this);
        listaRecetas = new ArrayList<>();

        // Configurar el ListView y el adaptador
        listaRecetasView = findViewById(R.id.ListaRecetasView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaRecetas);
        listaRecetasView.setAdapter(adapter);

        // Configurar botones
        btnAddRecipe = findViewById(R.id.btnAddRecipe);
        btnDeleteRecipe = findViewById(R.id.btnDeleteRecipe);

        // Cargar recetas desde la base de datos
        cargarRecetasDesdeBaseDeDatos();

        // Configurar acción del botón "Agregar Receta"
        btnAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoAgregarReceta();
            }
        });

        // Configurar acción del botón "Eliminar Receta"
        btnDeleteRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoEliminarReceta();
            }
        });
    }

    private void mostrarDialogoAgregarReceta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Receta");

        // Inflar el layout del diálogo
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_recipe, null, false);
        final EditText inputRecipeName = viewInflated.findViewById(R.id.inputRecipeName);
        final EditText inputIngredients = viewInflated.findViewById(R.id.inputIngredients);
        final EditText inputRating = viewInflated.findViewById(R.id.inputRating);
        builder.setView(viewInflated);

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = inputRecipeName.getText().toString();
                String ingredientes = inputIngredients.getText().toString();
                int calificacion;

                try {
                    calificacion = Integer.parseInt(inputRating.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Calificación no válida", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Agregar receta a la base de datos
                baseDatosHelper.agregarReceta(nombre, ingredientes, calificacion);
                cargarRecetasDesdeBaseDeDatos();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void mostrarDialogoEliminarReceta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Receta");

        // Configura el layout con un campo de texto para el nombre de la receta
        final EditText input = new EditText(this);
        input.setHint("Nombre de la receta");
        builder.setView(input);

        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = input.getText().toString();
                int filasEliminadas = baseDatosHelper.eliminarReceta(nombre);

                if (filasEliminadas > 0) {
                    cargarRecetasDesdeBaseDeDatos();
                    Toast.makeText(MainActivity.this, "Receta eliminada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No se encontró la receta", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void cargarRecetasDesdeBaseDeDatos() {
        listaRecetas.clear(); // Limpiar lista antes de cargar nuevos datos
        Cursor cursor = baseDatosHelper.obtenerTodasLasRecetas();

        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                String ingredientes = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_INGREDIENTS));
                int calificacion = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RATING));

                String receta = "Nombre: " + nombre + "\nIngredientes: " + ingredientes + "\nCalificación: " + calificacion;
                listaRecetas.add(receta); // Agregar cada receta a la lista
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged(); // Notificar al adaptador de cambios
    }
}




