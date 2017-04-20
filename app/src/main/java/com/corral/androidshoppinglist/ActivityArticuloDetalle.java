package com.corral.androidshoppinglist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import persistencia.dao.ArticuloDao;
import persistencia.dao.FamiliaContract;
import persistencia.dao.FamiliaDao;
import persistencia.jb.Articulo;
import persistencia.jb.Familia;
import util.BottomNavigationViewHelper;

import static util.Constantes.TIPO_MEDIDA_KILOGRAMOS;
import static util.Constantes.TIPO_MEDIDA_LITROS;
import static util.Constantes.TIPO_MEDIDA_UNIDADES;

public class ActivityArticuloDetalle extends AppCompatActivity {

    Context contexto;
    Articulo articulo;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_supermercado:
                    intent = new Intent(getApplicationContext(),ActivitySuperMercadoList.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_familia:
                    intent = new Intent(getApplicationContext(),ActivityFamiliaList.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_articulo:
                    intent = new Intent(getApplicationContext(),ActivityArticuloList.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_lista:
                    //intent = new Intent(getApplicationContext(),ActivityListaList.class);
                    //startActivity(intent);
                    // finish();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulo_detalle);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().setGroupCheckable(0, true, true);
        navigation.getMenu().getItem(2).setChecked(true);

        contexto = this;
        articulo = (Articulo) getIntent().getSerializableExtra("Articulo");

        // definir el spinner de familias
        Spinner spinner = (Spinner) findViewById(R.id.spinner_familia);

        CursorAdapterFamilia caf = new CursorAdapterFamilia(this, new FamiliaDao(this).
                getCursor(null, FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE));
        spinner.setAdapter(caf);

        // si articulo null indica que no se va a modificar o eliminar porque no se envía una referencia.
        definirBotones(articulo == null);

        if (articulo != null) {
            rellenaCampos(articulo);
        }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                builder
                        .setTitle("Eliminar Articulo")
                        .setMessage("Confirme la operación, por favor.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (articulo != null) {
                                    ArticuloDao fd = new ArticuloDao(contexto);
                                    fd.delete(articulo.getId());
                                    fd.close();
                                }
                                finish();
                            }
                        })
                        .setNegativeButton("Cancelar", null)    //Do nothing on no
                        .show();
            }
        });

        // Botón Modificar
        ((Button) findViewById(R.id.buttonModificar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aparienciaGuardar();
            }
        });

        // Botón Guardar
        ((Button) findViewById(R.id.buttonGuardar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticuloDao fd = new ArticuloDao(contexto);
                // si articulo == null venimos de añadir un registro nuevo.
                if (articulo == null) {
                    try {
                        articulo = obtenCampos();
                        fd.insert(articulo);
                    } catch (SQLiteException sqlce) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        builder
                                .setTitle("Añádir articulo")
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
                        articulo = obtenCampos();
                        fd.update(articulo);
                    } catch (SQLiteException sqlce) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        builder
                                .setTitle("Modificar articulo")
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

    // Devuelve Articulo leyéndolo de los EditText.
    // Lanza SQLiteConstraintException si algún campo UNIQUE, está vacío.
    private Articulo obtenCampos() throws SQLiteConstraintException {

        EditText et_codigo = (EditText) findViewById(R.id.et_articulo_codigo);
        EditText et_nombre = (EditText) findViewById(R.id.et_articulo_nombre);
        EditText et_descripcion = (EditText) findViewById(R.id.et_articulo_descripcion);
        RadioGroup et_medida = (RadioGroup) findViewById(R.id.radiogroup_medida);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_familia);

        Articulo f = new Articulo();
        if (et_nombre.getText().toString().length() == 0) throw new SQLiteConstraintException();
        if (spinner.getSelectedItem() == null) throw new SQLiteConstraintException();

        try {
            f.setId(Long.parseLong(et_codigo.getText().toString()));
        } catch (Exception e) {
            f.setId(0L);
        }
        f.setNombre(et_nombre.getText().toString());
        f.setDescripcion(et_descripcion.getText().toString());

        switch (et_medida.getCheckedRadioButtonId()) {
            case R.id.medida_u: f.setMedida(TIPO_MEDIDA_UNIDADES);
                break;
            case R.id.medida_l: f.setMedida(TIPO_MEDIDA_LITROS);
                break;
            case R.id.medida_k: f.setMedida(TIPO_MEDIDA_KILOGRAMOS);
                break;
            default:    f.setMedida(TIPO_MEDIDA_UNIDADES);
        }

        try {
            Cursor c = ((Cursor) spinner.getSelectedItem());
            String seleccionado = c.getString(0).toString();
            FamiliaDao fd = new FamiliaDao(this);
            Familia familia = fd.read(Long.parseLong(seleccionado));
            if (familia != null) {
                f.setId_familia(familia.getId());
            }
            fd.close();

        } catch (Exception e) {
            throw new SQLiteConstraintException();
        }
        return f;
    }

    // Rellena los EditText leyéndolo del parámetro Articulo
    private void rellenaCampos(Articulo f) {
        EditText et_codigo = (EditText) findViewById(R.id.et_articulo_codigo);
        EditText et_nombre = (EditText) findViewById(R.id.et_articulo_nombre);
        EditText et_descripcion = (EditText) findViewById(R.id.et_articulo_descripcion);
        RadioGroup et_medida = (RadioGroup) findViewById(R.id.radiogroup_medida);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_familia);

        et_codigo.setText(f.getId().toString(), TextView.BufferType.EDITABLE);
        et_nombre.setText(f.getNombre(), TextView.BufferType.EDITABLE);
        et_descripcion.setText(f.getDescripcion(), TextView.BufferType.EDITABLE);

        ((RadioButton) findViewById(R.id.medida_u)).setChecked(true);
        if (TIPO_MEDIDA_UNIDADES.compareTo(f.getMedida()) == 0) {
            ((RadioButton) findViewById(R.id.medida_u)).setChecked(true);
        }
        if (TIPO_MEDIDA_LITROS.compareTo(f.getMedida()) == 0) {
            ((RadioButton) findViewById(R.id.medida_l)).setChecked(true);
        }
        if (TIPO_MEDIDA_KILOGRAMOS.compareTo(f.getMedida()) == 0) {
            ((RadioButton) findViewById(R.id.medida_k)).setChecked(true);
        }

        FamiliaDao fd = new FamiliaDao(this);
        Familia familia = fd.read(f.getId_familia());
        if (familia != null) {

            for (int i = 0; i < spinner.getCount(); i++) {
                Cursor c = (Cursor) spinner.getItemAtPosition(i);
                long id2 = c.getLong(c.getColumnIndex(FamiliaContract.FamiliaEntry._ID));
                if (id2 == familia.getId()) {
                    spinner.setSelection(i);
                }

            }
        }
        fd.close();
    }

    // Cambiar aparicencia de botones para mostrar Botón Guardar
    private void aparienciaGuardar() {
        ((Button) findViewById(R.id.buttonEliminar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonModificar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonGuardar)).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.et_articulo_codigo)).setEnabled(false);
        ((EditText) findViewById(R.id.et_articulo_nombre)).setEnabled(true);
        ((EditText) findViewById(R.id.et_articulo_descripcion)).setEnabled(true);
        ((Spinner) findViewById(R.id.spinner_familia)).setEnabled(true);
        ((RadioButton) findViewById(R.id.medida_u)).setEnabled(true);
        ((RadioButton) findViewById(R.id.medida_k)).setEnabled(true);
        ((RadioButton) findViewById(R.id.medida_l)).setEnabled(true);
    }

    // Cambiar apariencia de botones para mostrar Botones Modificar y Eliminar.
    private void aparienciaModificar() {
        ((Button) findViewById(R.id.buttonEliminar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonModificar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonGuardar)).setVisibility(View.GONE);
        ((Spinner) findViewById(R.id.spinner_familia)).setEnabled(false);
        ((RadioButton) findViewById(R.id.medida_u)).setEnabled(false);
        ((RadioButton) findViewById(R.id.medida_k)).setEnabled(false);
        ((RadioButton) findViewById(R.id.medida_l)).setEnabled(false);

    }

}
