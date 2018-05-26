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

import persistencia.dao.ArticuloDao;
import persistencia.dao.FamiliaDao;
import persistencia.jb.Familia;
import util.BottomNavigationViewHelper;

public class ActivityFamiliaDetalle extends AppCompatActivity {

    Context contexto;
    Familia familia;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_supermercado:
                    intent = new Intent(getApplicationContext(),ActivitySuperMercadoList.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.aderecha, R.anim.aderecha);
                    finish();
                    return true;
                case R.id.navigation_familia:
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
        setContentView(R.layout.activity_familia_detalle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().setGroupCheckable(0, true, true);
        navigation.getMenu().getItem(1).setChecked(true);

        contexto = this;
        familia = (Familia) getIntent().getSerializableExtra("Familia");

        // si familia null indica que no se va a modificar o eliminar porque no se envía una referencia.
        definirBotones(familia == null);

        if (familia != null) {
            rellenaCampos(familia);
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
                if (familiaLibre(familia.getId())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                    builder
                            .setTitle("Eliminar Familia")
                            .setMessage("Confirme la operación, por favor.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (familia != null) {
                                        FamiliaDao fd = new FamiliaDao(contexto);
                                        fd.delete(familia.getId());
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
                            .setTitle("Eliminar Familia ERROR")
                            .setMessage("No es posible eliminar una familia de artículos " +
                                    "mientras haya artículos pertenecientes a esta.")
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
                EditText et = ((EditText) findViewById(R.id.et_familia_nombre));
                et.requestFocus();
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                aparienciaGuardar();
            }
        });

        // Botón Guardar
        ((Button) findViewById(R.id.buttonGuardar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FamiliaDao fd = new FamiliaDao(contexto);
                // si familia == null venimos de añadir un registro nuevo.
                if (familia == null) {
                    try {
                        familia = obtenCampos();
                        fd.insert(familia);

                        // En caso de haber creado una nueva familia desde ActivityArticuloDetalle
                        // retornamos el resultado del Id de la familia con setResult().
                        // Si no se pasó por ActivityArticuloDetalle simplemente se ignora Result.
                        setResult(familia.getId().intValue());

                    } catch (SQLiteException sqlce) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        builder
                                .setTitle("Añádir familia")
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
                        familia = obtenCampos();
                        fd.update(familia);
                    } catch (SQLiteException sqlce) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        builder
                                .setTitle("Modificar familia")
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

    // Devuelve Familia leyéndolo de los EditText.
    // Lanza SQLiteConstraintException si algún campo UNIQUE, está vacío.
    private Familia obtenCampos() throws SQLiteConstraintException {

        EditText et_codigo = (EditText) findViewById(R.id.et_familia_codigo);
        EditText et_nombre = (EditText) findViewById(R.id.et_familia_nombre);

        Familia f = new Familia();
        if (et_nombre.getText().toString().length() == 0) throw new SQLiteConstraintException();

        try {
            f.setId(Long.parseLong(et_codigo.getText().toString()));
        } catch (Exception e) {
            f.setId(0L);
        }
        f.setNombre(et_nombre.getText().toString());
        return f;
    }

    // Rellena los EditText leyéndolo del parámetro Familia
    private void rellenaCampos(Familia f) {
        EditText et_codigo = (EditText) findViewById(R.id.et_familia_codigo);
        EditText et_nombre = (EditText) findViewById(R.id.et_familia_nombre);
        et_codigo.setText(f.getId().toString(), TextView.BufferType.EDITABLE);
        et_nombre.setText(f.getNombre(), TextView.BufferType.EDITABLE);
    }

    // Cambiar aparicencia de botones para mostrar Botón Guardar
    private void aparienciaGuardar() {
        ((Button) findViewById(R.id.buttonEliminar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonModificar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonGuardar)).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.et_familia_codigo)).setEnabled(false);
        ((EditText) findViewById(R.id.et_familia_nombre)).setEnabled(true);
    }

    // Cambiar apariencia de botones para mostrar Botones Modificar y Eliminar.
    private void aparienciaModificar() {
        ((Button) findViewById(R.id.buttonEliminar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonModificar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonGuardar)).setVisibility(View.GONE);
    }

    // indica si una familia tiene artículos que apunten a esta (true) o no (false).
    private Boolean familiaLibre(Long id_familia) {
        ArticuloDao ad = new ArticuloDao(this);
        List articulos_ = ad.listado(null, null, null, id_familia, null);
        return (articulos_.size() == 0);
    }
}
