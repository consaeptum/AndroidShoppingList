package com.corral.androidshoppinglist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import persistencia.dao.ArticuloDao;
import persistencia.dao.FamiliaContract;
import persistencia.dao.FamiliaDao;
import persistencia.dao.LineaDao;
import persistencia.jb.Articulo;
import persistencia.jb.Familia;
import util.BottomNavigationViewHelper;

import static util.Constantes.TIPO_MEDIDA_KILOGRAMOS;
import static util.Constantes.TIPO_MEDIDA_UNIDADES;

public class ActivityArticuloDetalle extends AppCompatActivity {

    private static final int ERROR_INSERT = -1;
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
                    return true;
                case R.id.navigation_lista:
                    intent = new Intent(getApplicationContext(),ActivityListaList.class);
                    startActivity(intent);
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
        setContentView(R.layout.activity_articulo_detalle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
                if (articuloLibre(articulo.getId())) {
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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                    builder
                            .setTitle("Eliminar Artículo ERROR")
                            .setMessage("No es posible eliminar artículos " +
                                    "mientras haya artículos pertenecientes a una lista.")
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
                EditText et = ((EditText) findViewById(R.id.et_articulo_nombre));
                et.requestFocus();
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
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
                        Boolean res = fd.insert(articulo);

                        // En caso de haber creado una nuevo articulo desde una Card de la Lista
                        // retornamos el resultado del Id del articulo con setResult().
                        // Si no se pasó por Card simplemente se ignora Result.
                        // Aunque el startActivityForResult se llamó desde RVArticulosAdapter,
                        // como el context pertenece a ActivityListaDetalle, el resultado se
                        // recoge en ActivityListaDetalle.onActivityResult().
                        setResult((res)? articulo.getId().intValue() : ERROR_INSERT);

                    } catch (SQLiteException sqlce) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        builder
                                .setTitle("Añádir articulo")
                                .setMessage("Algún campo está vacío o se intenta guardar un nombre repetido.")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Ok", null)
                                .show();
                        fd.close();
                        setResult(ERROR_INSERT);
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

        // boton Familia nueva.  lanza la activity para crear nueva familia.
        FloatingActionButton nuevaFamilia = (FloatingActionButton) findViewById(R.id.boton_articulo_nueva_familia);
        nuevaFamilia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(),ActivityFamiliaDetalle.class);
            startActivityForResult(intent, 0);
            }
        });
    }

    // Cuando se añade una familia desde Artículo, lanzamos la activity FamiliaActivityDetalle
    // con startActivityForResult.  Aquí especificamos que hacer al recibir el resultado y el
    // objeto familia que nos envía ActivityFamiliaDetalle al pulsar el botón guardar.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 ) {
            if (resultCode >= 0) {
                Spinner spiner = (Spinner) findViewById(R.id.spinner_familia);
                CursorAdapterFamilia caf = new CursorAdapterFamilia(this, new FamiliaDao(this).
                        getCursor(null, FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE));
                spiner.setAdapter(caf);
                for (int i = 0; i < spiner.getCount(); i++) {
                    Cursor c = (Cursor) spiner.getItemAtPosition(i);
                    long id = c.getLong(c.getColumnIndex(FamiliaContract.FamiliaEntry._ID));
                    if (id == resultCode) {
                        spiner.setSelection(i);
                    }
                }
            }
        }
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
            //case R.id.medida_l: f.setMedida(TIPO_MEDIDA_LITROS);
                //break;
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
        //if (TIPO_MEDIDA_LITROS.compareTo(f.getMedida()) == 0) {
        //    ((RadioButton) findViewById(R.id.medida_l)).setChecked(true);
        //}
        if (TIPO_MEDIDA_KILOGRAMOS.compareTo(f.getMedida()) == 0) {
            ((RadioButton) findViewById(R.id.medida_k)).setChecked(true);
        }

        // ponemos como seleccionada en el spinner la familia correspondiente
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
        ((FloatingActionButton) findViewById(R.id.boton_articulo_nueva_familia)).setEnabled(true);
        ((RadioButton) findViewById(R.id.medida_u)).setEnabled(true);
        ((RadioButton) findViewById(R.id.medida_k)).setEnabled(true);
        //((RadioButton) findViewById(R.id.medida_l)).setEnabled(true);
    }

    // Cambiar apariencia de botones para mostrar Botones Modificar y Eliminar.
    private void aparienciaModificar() {
        ((Button) findViewById(R.id.buttonEliminar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonModificar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonGuardar)).setVisibility(View.GONE);
        ((Spinner) findViewById(R.id.spinner_familia)).setEnabled(false);
        ((FloatingActionButton) findViewById(R.id.boton_articulo_nueva_familia)).setEnabled(false);
        ((RadioButton) findViewById(R.id.medida_u)).setEnabled(false);
        ((RadioButton) findViewById(R.id.medida_k)).setEnabled(false);
        //((RadioButton) findViewById(R.id.medida_l)).setEnabled(false);

    }
    // indica si un Artículo tiene listas que apunten a este (true) o no (false).
    private Boolean articuloLibre(Long id_articulo) {
        LineaDao ad = new LineaDao(this);
        List articulos_ = ad.listado(null, id_articulo, null);
        return (articulos_.size() == 0);
    }

}
