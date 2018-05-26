package com.corral.androidshoppinglist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import persistencia.dao.LineaDao;
import persistencia.dao.ListaDao;
import persistencia.dao.SuperMercadoContract;
import persistencia.dao.SuperMercadoDao;
import persistencia.jb.Linea;
import persistencia.jb.Lista;
import persistencia.jb.SuperMercado;
import util.BottomNavigationViewHelper;


public class ActivityListaDetalle extends AppCompatActivity {

    public static final int REQUESTCODE_MAS_SUPER = 0;
    public static final int REQUESTCODE_MAS_ARTICULO = 10;

    Context contexto;

    // La lista que recibimos por parametros Extra de otra activity.
    Lista lista;

    Boolean comprando = false;
    Boolean modificandoTitleBar = false;

    static EditText et_fecha;

    // cada vez que usamos el teclado en el CardView el menu BottomNavView se sube
    // para evitarlo lo ocultaremos mientras el teclado esté desplegado, pero hay que pasarlo
    // cada vez que llamamos a RVArticulosAdapter.
    //static BottomNavigationView bnv;

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
                    intent = new Intent(getApplicationContext(),ActivityFamiliaList.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.aderecha, R.anim.aderecha);
                    finish();
                    return true;
                case R.id.navigation_articulo:
                    intent = new Intent(getApplicationContext(),ActivityArticuloList.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.aderecha, R.anim.aderecha);
                    finish();
                    return true;
                case R.id.navigation_lista:
                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_detalle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view_articulos);
        rv.setLayoutManager(mLayoutManager);
        rv.setVisibility(View.GONE);

        //bnv = (BottomNavigationView) findViewById(R.id.navigation);

        // Para permitir que TimePickerFragment cambie el valor de la fecha necesitamos
        // una variable static que inicializamos aquí.
        et_fecha = (EditText) findViewById(R.id.et_lista_fecha);
        et_fecha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getSupportFragmentManager(), "Fecha");
                        break;
                }
                return false;
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().setGroupCheckable(0, true, true);
        navigation.getMenu().getItem(3).setChecked(true);

        contexto = this;
        lista = (Lista) getIntent().getSerializableExtra("Lista");
        comprando = false;

        // definir el spinner de familias
        Spinner spinner = (Spinner) findViewById(R.id.spinner_supermercado);

        CursorAdapterSuperMercado caf = new CursorAdapterSuperMercado(this, new SuperMercadoDao(this).
                getCursor(null, SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE));
        spinner.setAdapter(caf);

        // si lista null indica intención de añadir una lista.
        Boolean listaVacia = (lista != null) && (lineasLibre(lista.getId()));
        definirBotones(lista == null, listaVacia);

        if (lista != null) {
            if (listaVacia) {
                rellenaCampos(lista);
            } else {
                SuperMercadoDao sd = new SuperMercadoDao(this);
                SuperMercado s = sd.read(lista.getId_super());
                aparienciaMasLineasInicioCompra(lista.getFechaFormatDMY(), s.getNombre());
                RecyclerView.Adapter mAdapter = new RVArticulosAdapter(lista, this, false, comprando, (ConstraintLayout) findViewById(R.id.origen));
                rv.setAdapter(mAdapter);
            }
        }

        // Cuando se despliega el teclado, nos sube el NavigationBottomView, así que lo ocultaremos
        // y mostraremos según se despliegue/repliegue el teclado.
        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                TypedValue tv = new TypedValue();
                Integer actionBarHeight = 0;
                if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
                }
                if (heightDiff > 200) { // > 200 pixels, probablemente al desplegar teclado
                    RVArticulosAdapter.ocultarBNV();
                }
                if ((heightDiff < 200) && (heightDiff > actionBarHeight)) { // al replegar
                    RVArticulosAdapter.mostrarBNV();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.abajo, R.anim.abajo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tool_bar_menu_lista_editar, menu);

        // mostramos el icono de modificar en toolbar la fecha y super sólo si la lista tiene artículos.
        if ((lista != null) && (!lineasLibre(lista.getId()))) {
            menu.findItem(R.id.action_edit).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            if (modificandoTitleBar) {
                // grabar datos
                Lista mlista = obtenCampos();
                ListaDao ld = new ListaDao(contexto);
                if (ld.update(mlista)) {
                    lista = mlista;
                }
                vaciarPantalla(View.GONE);
                item.setIcon(R.drawable.ic_action_edit);
                setTitle(lista.getFechaFormatDMY() + "   "  + (new SuperMercadoDao(contexto)).read(lista.getId_super()).getNombre());
            } else {
                vaciarPantalla(View.VISIBLE);
                rellenaCampos(lista);
                item.setIcon(R.drawable.ic_action_guardar);
            }
            modificandoTitleBar = !modificandoTitleBar;
        }
        return super.onOptionsItemSelected(item);
    }

    private void definirBotones(Boolean opcionGuardar, Boolean listaVacia) {

        // el botón guardar se muestra si no aparece el botón modificar y eliminar.
        if (opcionGuardar) {
            aparienciaGuardar();
        } else {
            if (listaVacia) {
                aparienciaModificar();
            } else {
                aparienciaMasLineasInicioCompra("", "");
            }
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

                if (lineasLibre(lista.getId())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                    builder
                            .setTitle("Eliminar Lista")
                            .setMessage("Confirme la operación, por favor.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (lista != null) {
                                        ListaDao fd = new ListaDao(contexto);
                                        fd.delete(lista.getId());
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
                            .setTitle("Eliminar Lista ERROR")
                            .setMessage("No es posible eliminar una lista de la compra " +
                                    "mientras haya elementos pertenecientes a esta.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Aceptar", null)
                            .show();
                }
            }
        });

        // Botón Añadir Artículos
        ((Button) findViewById(R.id.buttonModificar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ListaDao fd = new ListaDao(contexto);
                try {
                    lista = obtenCampos();
                    fd.update(lista);
                } catch (SQLiteException sqlce) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                    builder
                            .setTitle("Modificar lista")
                            .setMessage("Algún campo está vacío o se intenta guardar un nombre repetido.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    fd.close();
                                    finish();
                                }
                                }).show();
                    return;
                }
                // Una vez está creada la lista comenzamos a añadir artículos
                aparienciaMasLineasInicioCompra(((EditText) findViewById(R.id.et_lista_fecha)).getText().toString(),
                        ((Spinner) findViewById(R.id.spinner_supermercado)).getSelectedItem().toString());

            }
        });

        // Botón Guardar
        ((Button) findViewById(R.id.buttonGuardar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListaDao fd = new ListaDao(contexto);
                // si lista == null venimos de añadir un registro nuevo.
                if (lista == null) {
                    try {
                        lista = obtenCampos();
                        fd.insert(lista);
                    } catch (SQLiteException sqlce) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        builder
                                .setTitle("Añádir lista")
                                .setMessage("Algún campo está vacío o se intenta guardar un nombre repetido.")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        fd.close();
                                        finish();
                                    }
                                }).show();
                        return;
                    }

                    // en caso contrario venimos de modificar y queremos guardar los cambios.
                } else {
                    try {
                        lista = obtenCampos();
                        fd.update(lista);
                    } catch (SQLiteException sqlce) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        builder
                                .setTitle("Modificar lista")
                                .setMessage("Algún campo está vacío o se intenta guardar un nombre repetido.")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        fd.close();
                                        finish();
                                    }
                                }).show();
                        return;
                    }
                }
                // Una vez está creada la lista comenzamos a añadir artículos
                aparienciaMasLineasInicioCompra(((EditText) findViewById(R.id.et_lista_fecha)).getText().toString(),
                        ((Spinner) findViewById(R.id.spinner_supermercado)).getSelectedItem().toString());
            }
        });

        // Botón Más Artículos
        ((Button) findViewById(R.id.buttonNuevaLinea)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view_articulos);
                rv.setItemAnimator(new SlideInDownAnimator());
                rv.getItemAnimator().setAddDuration(200);
                rv.setVisibility(View.VISIBLE);

                LineaDao ld = new LineaDao(contexto);
                Linea linea = new Linea();
                linea.setId_articulo(RVArticulosAdapter.ELEMENTO_MAS_RECIENTE.longValue());
                linea.setId_lista(lista.getId());
                try {
                    if (ld.insert(linea)) {
                        RecyclerView.Adapter mAdapter = new RVArticulosAdapter(lista, contexto, false, comprando, (ConstraintLayout) findViewById(R.id.origen));
                        rv.setAdapter(mAdapter);
                        rv.getAdapter().notifyItemInserted(0);
                        rv.scrollToPosition(0);
                    }
                } catch (SQLiteConstraintException sqce) {
                    // Si ya teníamos una línea vacía, no la creamos de nuevo.
                }

            }
        });

        // Botón Iniciar compra
        ((Button) findViewById(R.id.buttonInicioComprar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprando = true;

                // si hay nuevo elemento sin artículo, lo eliminamos
                LineaDao ld = new LineaDao(contexto);
                RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view_articulos);
                Long nulo = ((RVArticulosAdapter)rv.getAdapter()).getElemActual();
                if (nulo > 0) {
                    ld.delete(nulo);
                }

                aparienciaMasLineasFinCompra(lista.getFechaFormatDMY(),
                        (new SuperMercadoDao(contexto)).read(lista.getId_super()).getNombre());

                // actualizar la lista en orden habitual
                RecyclerView.Adapter mAdapter = new RVArticulosAdapter(lista, contexto, false, comprando, (ConstraintLayout) findViewById(R.id.origen));
                ((RecyclerView) findViewById(R.id.recycler_view_articulos)).setAdapter(mAdapter);

                // poner el importe de la compra en el botón comprar (si es mayor que 0)
                if ((lista != null) && (lista.getImporte() > 0)) {
                    Button b = (Button) findViewById(R.id.buttonFinComprar);
                    b.setTextColor(Color.WHITE);
                    b.setTypeface(null, Typeface.BOLD);
                    b.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.importe_total));
                    String n = String.format("%1$,.2f", lista.getImporte());
                    b.setText(n + " €");
                }

            }
        });

        // Botón Fin compra
        ((Button) findViewById(R.id.buttonFinComprar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprando = false;
                aparienciaMasLineasInicioCompra(lista.getFechaFormatDMY(),
                        (new SuperMercadoDao(contexto)).read(lista.getId_super()).getNombre());

                // actualizar la lista en orden de entrada
                RecyclerView.Adapter mAdapter = new RVArticulosAdapter(lista, contexto, false, comprando, (ConstraintLayout) findViewById(R.id.origen));
                ((RecyclerView) findViewById(R.id.recycler_view_articulos)).setAdapter(mAdapter);

            }
        });

        // boton Supermercado nuevo.  lanza la activity para crear nueva Supermercado.
        FloatingActionButton nuevoSupermercado = (FloatingActionButton) findViewById(R.id.boton_lista_nuevo_supermercado);
        nuevoSupermercado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ActivitySuperMercadoDetalle.class);
                startActivityForResult(intent, REQUESTCODE_MAS_SUPER);
            }
        });

    }

    // Cuando se añade un supermercado desde Lista, lanzamos la activity SupermercdoActivityDetalle
    // con startActivityForResult.  Aquí especificamos que hacer al recibir el resultado y el
    // objeto supermercado que nos envía ActivitySupermercadoDetalle al pulsar el botón guardar.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Añadir un Supermercado desde ActivityListaDetalle.xml
        if (requestCode == REQUESTCODE_MAS_SUPER ) {
            if (resultCode >= 0) {
                Spinner spiner = (Spinner) findViewById(R.id.spinner_supermercado);
                CursorAdapterSuperMercado caf = new CursorAdapterSuperMercado(this, new SuperMercadoDao(this).
                        getCursor(null, SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE));
                spiner.setAdapter(caf);
                for (int i = 0; i < spiner.getCount(); i++) {
                    Cursor c = (Cursor) spiner.getItemAtPosition(i);
                    long id = c.getLong(c.getColumnIndex(SuperMercadoContract.SuperMercadoEntry._ID));
                    if (id == resultCode) {
                        spiner.setSelection(i);
                    }
                }
            }
        }
        if (requestCode == REQUESTCODE_MAS_ARTICULO) {
            if (resultCode > 0) {
                // actualizamos el recyclerview
                RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view_articulos);
                rv.setVisibility(View.VISIBLE);

                // antes de actualizar Recyclerview actualizamos lineaDao para que al
                // actualizarse Recyclerview aparezca por defecto el artículo seleccionado.
                Integer articuloId = resultCode;
                LineaDao ld = new LineaDao(contexto);
                Linea l = ld.read(((RVArticulosAdapter)rv.getAdapter()).getElemActual());

                l.setId_articulo(articuloId.longValue());
                ld.update(l);
                RecyclerView.Adapter mAdapter = new RVArticulosAdapter(lista, contexto, false, comprando, (ConstraintLayout) findViewById(R.id.origen));
                rv.setAdapter(mAdapter);
            }
        }
    }

    // Devuelve Lista leyéndolo de los EditText.
    // Lanza SQLiteConstraintException si algún campo UNIQUE, está vacío.
    private Lista obtenCampos() throws SQLiteConstraintException {

        EditText et_codigo = (EditText) findViewById(R.id.et_lista_codigo);
        EditText et_fecha = (EditText) findViewById(R.id.et_lista_fecha);
        Spinner spinner_supermercado = (Spinner) findViewById(R.id.spinner_supermercado);

        Lista f = new Lista();
        if (et_fecha.getText().toString().isEmpty()) throw new SQLiteConstraintException();
        if (spinner_supermercado.getSelectedItem() == null) throw new SQLiteConstraintException();

        try {
            f.setId(Long.parseLong(et_codigo.getText().toString()));
        } catch (Exception e) {
            f.setId(0L);
        }
        f.setFechaFormatDMY(et_fecha.getText().toString());

        try {
            Cursor c = ((Cursor) spinner_supermercado.getSelectedItem());
            String seleccionado = c.getString(0).toString();
            SuperMercadoDao fd = new SuperMercadoDao(this);
            SuperMercado familia = fd.read(Long.parseLong(seleccionado));
            if (familia != null) {
                f.setId_super(familia.getId());
            }
            fd.close();

        } catch (Exception e) {
            throw new SQLiteConstraintException();
        }
        return f;
    }

    // Rellena los EditText leyéndolo del parámetro Lista
    private void rellenaCampos(Lista f) {
        EditText et_codigo = (EditText) findViewById(R.id.et_lista_codigo);
        EditText et_fecha = (EditText) findViewById(R.id.et_lista_fecha);
        Spinner spinner_supermercado = (Spinner) findViewById(R.id.spinner_supermercado);

        et_codigo.setText(f.getId().toString(), TextView.BufferType.EDITABLE);
        et_fecha.setText(f.getFechaFormatDMY(), TextView.BufferType.EDITABLE);

        // ponemos como seleccionada en el spinner el supermercado correspondiente
        SuperMercadoDao fd = new SuperMercadoDao(this);
        SuperMercado superMercado = fd.read(f.getId_super());
        if (superMercado != null) {

            for (int i = 0; i < spinner_supermercado.getCount(); i++) {
                Cursor c = (Cursor) spinner_supermercado.getItemAtPosition(i);
                long id2 = c.getLong(c.getColumnIndex(SuperMercadoContract.SuperMercadoEntry._ID));
                if (id2 == superMercado.getId()) {
                    spinner_supermercado.setSelection(i);
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
        ((Button) findViewById(R.id.buttonNuevaLinea)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonFinComprar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonInicioComprar)).setVisibility(View.GONE);

        ((EditText) findViewById(R.id.et_lista_codigo)).setEnabled(false);
        ((EditText) findViewById(R.id.et_lista_fecha)).setEnabled(true);
        ((EditText) findViewById(R.id.et_lista_fecha)).setFocusable(false);
        et_fecha.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        ((Spinner) findViewById(R.id.spinner_supermercado)).setEnabled(true);
        ((FloatingActionButton) findViewById(R.id.boton_lista_nuevo_supermercado)).setEnabled(true);
        ((RecyclerView)findViewById(R.id.recycler_view_articulos)).setVisibility(View.GONE);
    }

    // Cambiar apariencia de botones para mostrar Botones Modificar y Eliminar.
    private void aparienciaModificar() {
        ((Button) findViewById(R.id.buttonEliminar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonModificar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonGuardar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonNuevaLinea)).setVisibility(View.GONE);
        ((EditText) findViewById(R.id.et_lista_codigo)).setEnabled(false);
        ((EditText) findViewById(R.id.et_lista_fecha)).setEnabled(true);
        ((EditText) findViewById(R.id.et_lista_fecha)).setFocusable(true);
        ((Spinner) findViewById(R.id.spinner_supermercado)).setEnabled(true);
        ((FloatingActionButton) findViewById(R.id.boton_lista_nuevo_supermercado)).setEnabled(true);

    }

    // Cambiar aparicencia de botones para mostrar Botón Más Artículos e IniciarCompra
    // Pone en el TitleBar la fecha y el supermercado y elimina de la vista los TextViews.
    private void aparienciaMasLineasInicioCompra(String fecha, String superMercado) {

        ((Button) findViewById(R.id.buttonEliminar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonModificar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonGuardar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonNuevaLinea)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonInicioComprar)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.buttonFinComprar)).setVisibility(View.GONE);
        Spinner spiner = (Spinner) findViewById(R.id.spinner_supermercado);

        setTitle(lista.getFechaFormatDMY() + "   "  + (new SuperMercadoDao(contexto)).read(lista.getId_super()).getNombre());

        vaciarPantalla(View.GONE);

        // preparamos el recyclerview
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view_articulos);
        rv.setVisibility(View.VISIBLE);
        RecyclerView.Adapter mAdapter = new RVArticulosAdapter(lista, contexto, false, comprando, (ConstraintLayout) findViewById(R.id.origen));
        rv.setAdapter(mAdapter);
    }

    // Cambiar aparicencia de botones para mostrar Botón Más Artículos e FinCompra
    // Pone en el TitleBar la fecha y el supermercado y elimina de la vista los TextViews.
    private void aparienciaMasLineasFinCompra(String fecha, String superMercado) {
        ((Button) findViewById(R.id.buttonEliminar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonModificar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonGuardar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonNuevaLinea)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonInicioComprar)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.buttonFinComprar)).setVisibility(View.VISIBLE);
        Spinner spiner = (Spinner) findViewById(R.id.spinner_supermercado);

        setTitle(lista.getFechaFormatDMY() + "   "  + (new SuperMercadoDao(contexto)).read(lista.getId_super()).getNombre());

        vaciarPantalla(View.GONE);

        // preparamos el recyclerview
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view_articulos);
        rv.setVisibility(View.VISIBLE);
        RecyclerView.Adapter mAdapter = new RVArticulosAdapter(lista, contexto, false, comprando, (ConstraintLayout) findViewById(R.id.origen));
        rv.setAdapter(mAdapter);
    }

    // Hace invisibles o visibles los textviews de activity_lista_detalle según el valor
    // enviado como parámetro (View.GONE o View.VISIBLE).
    private void vaciarPantalla(int opcion) {
        ((TextView) findViewById(R.id.tv_lista_detalle_fecha)).setVisibility(opcion);
        ((TextView) findViewById(R.id.tv_lista_detalle_supermercado)).setVisibility(opcion);
        ((EditText) findViewById(R.id.et_lista_fecha)).setVisibility(opcion);
        ((Spinner) findViewById(R.id.spinner_supermercado)).setVisibility(opcion);
        ((FloatingActionButton) findViewById(R.id.boton_lista_nuevo_supermercado)).setVisibility(opcion);
        ((ScrollView) findViewById(R.id.scrollview_lista_detalle)).setVisibility(opcion);
    }

    // indica si una lista tiene lineas que apunten esta (true) o no (false).
    private Boolean lineasLibre(Long id_lista) {
        LineaDao ad = new LineaDao(this);
        List lineas_ = ad.listado(id_lista, null, null);
        return (lineas_.size() == 0);
    }


    ///////////////////////////////////////////////////////////////////////
    // Al escribir la fecha el usuario utilizará este picker por comodidad.
    //
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        // ponemos en el campo et_lista_fecha la fecha seleccionada.  Month is 0 based.
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            et_fecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }
    }
    //
    /////////////////////////////////////////////////////////////////////////

}
