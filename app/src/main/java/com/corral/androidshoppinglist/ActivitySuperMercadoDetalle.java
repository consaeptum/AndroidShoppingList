package com.corral.androidshoppinglist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import persistencia.dao.ListaDao;
import persistencia.dao.SuperMercadoDao;
import persistencia.jb.SuperMercado;
import util.BottomNavigationViewHelper;

public class ActivitySuperMercadoDetalle extends AppCompatActivity {

    Context contexto;
    SuperMercado supermercado;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_supermercado:
                    return true;
                case R.id.navigation_familia:
                    intent = new Intent(getApplicationContext(),ActivityFamiliaList.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.aizquierda, R.anim.aizfuera);
                    finish();
                    return true;
                case R.id.navigation_articulo:
                    intent = new Intent(getApplicationContext(),ActivityArticuloList.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.aizquierda, R.anim.aizfuera);
                    finish();
                    return true;
                case R.id.navigation_lista:
                    intent = new Intent(getApplicationContext(),ActivityListaList.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.aizquierda, R.anim.aizfuera);
                    finish();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermercado_detalle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().setGroupCheckable(0, true, true);
        navigation.getMenu().getItem(0).setChecked(true);

        contexto = this;
        supermercado = (SuperMercado) getIntent().getSerializableExtra("SuperMercado");

        // si supermercado null indica que no se va a modificar o eliminar porque no se envía una referencia.
        definirBotones(supermercado == null);

        if (supermercado != null) {
            rellenaCampos(supermercado);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.abajo, R.anim.abajo);
    }

    private void definirBotones(Boolean opcionGuardar) {

        // el botón guardar se muestra si no aparece el botón modificar y eliminar.
        if (opcionGuardar) {
            aparienciaGuardar();
        } else {
            aparienciaModificar();
        }

        // Botón Volver
        ((Button) findViewById(R.id.buttonVolver)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Botón Eliminar
        ((Button) findViewById(R.id.buttonEliminar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (superMercadoLibre(supermercado.getId())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                    builder
                            .setTitle("Eliminar SuperMercado")
                            .setMessage("Confirme la operación, por favor.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (supermercado != null) {
                                        SuperMercadoDao fd = new SuperMercadoDao(contexto);
                                        fd.delete(supermercado.getId());
                                        fd.close();
                                    }
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancelar", null)    //Do nothing on no
                            .show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                    builder
                            .setTitle("Eliminar Supermercado ERROR")
                            .setMessage("No es posible eliminar un supermercado " +
                                    "mientras haya listas que lo contengan.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Aceptar", null)
                            .show();

                }
            }
        });

        // Botón Modificar
        ((Button) findViewById(R.id.buttonModificar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                EditText et = ((EditText) findViewById(R.id.et_supermercado_nombre));
                et.requestFocus();
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                aparienciaGuardar();
            }
        });

        // Botón Guardar
        ((Button) findViewById(R.id.buttonGuardar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuperMercadoDao fd = new SuperMercadoDao(contexto);
                // si supermercado == null venimos de añadir un registro nuevo.
                if (supermercado == null) {
                    try {
                        supermercado = obtenCampos();
                        fd.insert(supermercado);

                        // En caso de haber creado un nuevo supermercado desde ActivityListaDetalle
                        // retornamos el resultado del Id del supermercado con setResult().
                        // Si no se pasó por ActivityArticuloDetalle simplemente se ignora Result.
                        setResult(supermercado.getId().intValue());

                    } catch (SQLiteException sqlce) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        builder
                                .setTitle("Añadir supermercado_")
                                .setMessage("Algún campo está vacío o se intenta guardar un nombre repetido.")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Ok", null)
                                .show();
                        fd.close();
                        return;
                    }

                // en caso contrario venimos de modificar y queremos guardar los cambios.
                } else {
                    try {
                        supermercado = obtenCampos();
                        fd.update(supermercado);
                    } catch (SQLiteException sqlce) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        builder
                                .setTitle("Modificar supermercado_")
                                .setMessage("Algún campo está vacío o se intenta guardar un nombre repetido.")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Ok", null)
                                .show();
                        fd.close();
                        return;
                    }
                }
                finish();
            }
        });
    }

    // Devuelve SuperMercado leyéndolo de los EditText.
    // Lanza SQLiteConstraintException si algún campo UNIQUE, está vacío.
    private SuperMercado obtenCampos() throws SQLiteConstraintException {

        EditText et_codigo = (EditText) findViewById(R.id.et_supermercado_codigo);
        EditText et_nombre = (EditText) findViewById(R.id.et_supermercado_nombre);

        SuperMercado f = new SuperMercado();
        if (et_nombre.getText().toString().length() == 0) throw new SQLiteConstraintException();

        try {
            f.setId(Long.parseLong(et_codigo.getText().toString()));
        } catch (Exception e) {
            f.setId(0L);
        }
        f.setNombre(et_nombre.getText().toString());
        return f;
    }

    // Rellena los EditText leyéndolo del parámetro SuperMercado
    private void rellenaCampos(SuperMercado f) {
        EditText et_codigo = (EditText) findViewById(R.id.et_supermercado_codigo);
        EditText et_nombre = (EditText) findViewById(R.id.et_supermercado_nombre);
        et_codigo.setText(f.getId().toString(), TextView.BufferType.EDITABLE);
        et_nombre.setText(f.getNombre(), TextView.BufferType.EDITABLE);
    }

    // Cambiar aparicencia de botones para mostrar Botón Guardar
    private void aparienciaGuardar() {
        ((Button) findViewById(R.id.buttonEliminar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonModificar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonGuardar)).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.et_supermercado_codigo)).setEnabled(false);
        ((EditText) findViewById(R.id.et_supermercado_nombre)).setEnabled(true);
    }

    // Cambiar apariencia de botones para mostrar Botones Modificar y Eliminar.
    private void aparienciaModificar() {
        ((Button) findViewById(R.id.buttonEliminar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonModificar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonGuardar)).setVisibility(View.GONE);
    }

    // indica si una supermercado tiene listas que apunten este supermercado (true) o no (false).
    private Boolean superMercadoLibre(Long id_super) {
        ListaDao ad = new ListaDao(this);
        List supermercados_ = ad.listado(null, null, id_super, null);
        return (supermercados_.size() == 0);
    }

}
