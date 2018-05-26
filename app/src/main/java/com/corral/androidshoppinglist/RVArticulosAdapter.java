package com.corral.androidshoppinglist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import persistencia.consultas.DatosDesplegables;
import persistencia.dao.ArticuloContract;
import persistencia.dao.ArticuloDao;
import persistencia.dao.LineaContract;
import persistencia.dao.LineaDao;
import persistencia.dao.ListaDao;
import persistencia.dao.OrdenArticuloDao;
import persistencia.jb.Articulo;
import persistencia.jb.Linea;
import persistencia.jb.Lista;
import persistencia.jb.OrdenArticulo;

import static com.corral.androidshoppinglist.ActivityListaDetalle.REQUESTCODE_MAS_ARTICULO;
import static util.Constantes.COLOR_CARDVIEW_COMPRADO;
import static util.Constantes.COLOR_CARDVIEW_NO_COMPRADO;
import static util.Constantes.COLOR_CARDVIEW_NUEVO;
import static util.Constantes.TIPO_MEDIDA_KILOGRAMOS;
import static util.Constantes.TIPO_MEDIDA_UNIDADES;

public class RVArticulosAdapter extends RecyclerView.Adapter<RVArticulosAdapter.LineaViewHolder> {

    // La lista se inyecta para poder acceder al Id_supermercado.
    private static Lista lista;

    // La lista de artículos como objeto Linea que coincide con los artículos en el Recycler.
    private static List<Linea> lineas;

    // Lista para recordar que CardView están desplegadas y cuales no.
    private static List<Boolean> lDesplegadas;

    // La activity puede estar en estado de introducción de artículos o en estado de marcando
    // los articulos (o sea comprando).
    private static Boolean comprando;

    // diferenciamos el estado de un artículo que estamos introduciendo del de un articulo que
    // ya está completado.
    public static final Integer ELEMENTO_MAS_RECIENTE = -1;
    public static final Integer NO_ELEMENTO_MAS_RECIENTE = 0;

    // los distintos estados posibles de los articulos con sus cardviews.
    public static final int BOTONES_ESTADO_NUEVO_ARTICULO = 0;
    public static final int BOTONES_ESTADO_COMPRANDO_SIN_MARCAR = 10;
    public static final int BOTONES_ESTADO_COMPRANDO_MARCADO = 20;
    public static final int BOTONES_ESTADO_NOCOMPRANDO_SIN_MARCAR = 30;
    public static final int BOTONES_ESTADO_NOCOMPRANDO_MARCADO = 40;

    // como usamos startActivityOnResult en lugar de recibir Context recibimos Activity.
    private static Context contexto;

    // Al escribir en el EditText cantidad la BottomNavigationView se sube por encima del teclado
    // virtual.  Para evitarlo la ocultaremos al escribir y para eso necesitamos una referencia
    // al navigationView.
    public static BottomNavigationView bottomNavigationView;
    public static RecyclerView recyclerviewOrigen;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class LineaViewHolder extends RecyclerView.ViewHolder {

        // Referencias a objetos para acceder posteriormente, los guardamos para cada ViewHolder.
        CardView cv;
        LinearLayout ll;
        EditText cantidad;
        EditText descripcion;

        AutoCompleteTextView articuloACTV;
        Boolean articuloACTVOk; // indica si se ha seleccionado algo de la lista o está a medias

        EditText pvp;
        Spinner cantidadNPicker;
        FloatingActionButton botonCheckCompraOff;
        FloatingActionButton botonCheckCompraOn;
        FloatingActionButton botonMasArticulo;
        FloatingActionButton botonBorrarArticulo;
        FloatingActionButton botonDesplegarArticulo;
        TextView tvCantidad;

        // Si mantenemos para cada ViewHolder los JavaBeans Linea y Articulo conseguimos ahorrar
        // tiempo de proceso al no tener que buscar tantos datos en la base de datos.
        Linea linea;
        Articulo articulo;
        
        LineaViewHolder thisHolder;

        // Al iniciar el spinner se selecciona automáticamente una vez.  Por tanto necesitamos
        // descartar la primera selección.
        Boolean inicio_setSelection_cantidad = false;

        public LineaViewHolder(View itemView) {
            super(itemView);
            thisHolder = this;

            cv = (CardView)itemView.findViewById(R.id.cv);
            ll = (LinearLayout)cv.findViewById(R.id.cv_informacion_extra);
            tvCantidad = (TextView) cv.findViewById(R.id.lista_tv_cantidad);
            cantidad = (EditText) cv.findViewById(R.id.lista_item_cantidad);
            descripcion = (EditText) cv.findViewById(R.id.cv_descripcion);
            articuloACTV = (AutoCompleteTextView) cv.findViewById(R.id.autocompletetextview_item_lista_articulo);
            articuloACTVOk = false;
            pvp = (EditText) cv.findViewById(R.id.cv_edit_text_pvp);
            botonCheckCompraOff = (FloatingActionButton) cv.findViewById(R.id.botonCheckCompraOff);
            botonCheckCompraOn = (FloatingActionButton) cv.findViewById(R.id.botonCheckCompraOn);


            if (comprando) {
                articuloACTV.setEnabled(false);
                LinearLayout layoutBotonCheck = (LinearLayout) cv.findViewById(R.id.layout_boton_check);
                layoutBotonCheck.setVisibility(View.VISIBLE);

            } else {
                articuloACTV.setEnabled(true);
                LinearLayout layoutBotonCheck = (LinearLayout) cv.findViewById(R.id.layout_boton_check);
                layoutBotonCheck.setVisibility(View.GONE);

            }

            // Al hacer click en botonMasArticulo...
            botonMasArticulo = definirBotonMasArticulo(
                    (FloatingActionButton)cv.findViewById(R.id.boton_articulo_nueva_articulo));

            // Al hacer click en botonBorrarArticulo...
            botonBorrarArticulo = definirBotonBorrarArticulo(
                    (FloatingActionButton)cv.findViewById(R.id.boton_articulo_borrar_articulo));

            // Al hacer click en botonDesplegarArticulo...
            botonDesplegarArticulo = definirBotonDesplegarArticulo(
                    (FloatingActionButton)cv.findViewById(R.id.boton_articulo_desplegar_articulo));

            // Al hacer click en botonCheckCompraOff...
            definirBotonCheckCompraOff(botonCheckCompraOff);

            // Al hacer click en botonCheckCompraOn...
            definirBotonCheckCompraOn(botonCheckCompraOn);

            // definir El Spinner
            definirArticuloACTV(articuloACTV);

            // definir Notas
            descripcion = definirEdittextDescripcion(descripcion);

            // definir el Edittext cantidad
            definirEdittextCantidad(cantidad);

            // definir el NumberPicker
            cantidadNPicker = definirNumberPicker(
                    (Spinner)cv.findViewById(R.id.lista_item_numberPicker));

            // definir el EditText pvp
            pvp = definirEdittextPvp((EditText)cv.findViewById(R.id.cv_edit_text_pvp));

            escogerEtONp();
        }

        /**
         *  Rellenamos los datos desplegables del HolderView
         */
        public void rellenarDatosDesplegables() {

            if (articulo != null) {
                EditText tv_dd_notas = (EditText) cv.findViewById(R.id.cv_descripcion);
                if (!articulo.getDescripcion().isEmpty()) {
                    tv_dd_notas.setText(articulo.getDescripcion());
                    tv_dd_notas.setSingleLine();
                }
            }

            pvp.setHint(linea.getPvp().toString());

            DatosDesplegables dd = new DatosDesplegables(contexto);

            dd.cargaDatos(linea.getId_articulo());
            TableLayout tl=(TableLayout)cv.findViewById(R.id.tablelayout);
            tl.removeAllViews();
            TableRow tr1 = new TableRow(contexto);
            tr1.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            for (DatosDesplegables.PrecioComparativo pc : dd.getListaPrecios()) {

                if (pc.getPvp().compareTo(0.0f) == 0) continue;

                tr1 = new TableRow(contexto);
                TextView nom_super = new TextView(contexto);
                nom_super.setText(pc.getNombre_super());
                nom_super.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 4f));
                nom_super.setGravity(Gravity.LEFT);
                nom_super.setTypeface(null, Typeface.BOLD);
                tr1.addView(nom_super);

                TextView pvp = new TextView(contexto);
                pvp.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 4f));
                pvp.setText(pc.getPvp().toString());
                pvp.setGravity(Gravity.RIGHT);
                pvp.setTypeface(null, Typeface.BOLD);
                tr1.addView(pvp);

                TextView fecha = new TextView(contexto);
                fecha.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 4f));
                String f = pc.getUltima_fecha();
                Lista l = new Lista();
                l.setFechaFormatYMD(f);
                fecha.setText(l.getFechaFormatDMY());
                fecha.setGravity(Gravity.RIGHT);
                fecha.setTypeface(null, Typeface.BOLD);
                tr1.addView(fecha);

                tl.addView(tr1);
            }

        }
        
        
        /**
         * definimos el botón R.id.boton_articulo_nueva_articulo
         * @param v El botón cuyos eventos hay que definir
         * @return el botón
         */
        FloatingActionButton definirBotonMasArticulo(FloatingActionButton v) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(contexto, ActivityArticuloDetalle.class);
                    intent.putExtra(cv.getContext().getString(R.string.articuloRVnuevo), articuloACTV.getText().toString());
                    ((Activity)contexto).startActivityForResult(intent, REQUESTCODE_MAS_ARTICULO);
                }
            });
            return v;
        }

        /**
         * definimos el botón R.id.boton_articulo_borrar_articulo
         * Al borrar un artículo de la lista, suponemos que el usuario finalmente no lo compró o
         * decidió no ponerlo en la lista.  En ambos casos no repercute en OrdenArticuloDao, porque
         * aunque hubiese sido marcado como comprado para después borrarlo, el orden podría haber
         * sido el correcto, y si no posteriormente se iría corrigiendo el orden en las siguientes
         * compras.
         * @param v El botón cuyos eventos hay que definir
         * @return el botón
         */
        FloatingActionButton definirBotonBorrarArticulo(FloatingActionButton v) {

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // eliminar linea de LineaDao
                    LineaDao ld = new LineaDao(contexto);
                    ld.delete(linea.getId());

                    // mostramos con animación como se elimina de la lista.
                    LinearLayout ll = (LinearLayout)cv.getParent();
                    RecyclerView rv = (RecyclerView)ll.getParent();
                    rv.getAdapter().notifyItemRemoved(posicion(linea));

                    // eliminar la linea de la lista
                    lineas.remove(linea);
                }
            });
            return v;
        }

        /**
         * definimos el botón R.id.boton_articulo_desplegar_articuloACTV
         * @param v El botón cuyos eventos hay que definir
         * @return el botón
         */
        FloatingActionButton definirBotonDesplegarArticulo(FloatingActionButton v) {
/*            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Boolean estado = lDesplegadas.get(position);
                    lDesplegadas.set(position, !b);
                    android.support.transition.TransitionManager.beginDelayedTransition(rv);
                    ll.setVisibility( !b? View.VISIBLE: View.GONE);

                    if (ll.getVisibility() == View.VISIBLE) {

                        RecyclerView rv = ((RecyclerView)cv.getParent().getParent());
                        android.support.transition.TransitionManager.beginDelayedTransition(rv);
                        ll.setVisibility(View.GONE);

                    } else {

                        RecyclerView rv = ((RecyclerView)cv.getParent().getParent());
                        android.support.transition.TransitionManager.beginDelayedTransition(rv);
                        ll.setVisibility(View.VISIBLE);

                    }

                }
            });
*/            return v;
        }

        /**
         * definimos el botón R.id.botonCheckCompraOff
         * @param v El botón cuyos eventos hay que definir
         * @return el botón
         */
        FloatingActionButton definirBotonCheckCompraOff(FloatingActionButton v) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LineaDao ld = new LineaDao(contexto);
                    linea.setComprado(!linea.getComprado());
                    ld.update(linea);

                    // evitamos procesar un artículo todavía no completado
                    if (linea.getOrden() != ELEMENTO_MAS_RECIENTE) {
                        if (linea.getComprado()) {

                            ((CardView) (cv)).setCardBackgroundColor(COLOR_CARDVIEW_COMPRADO);
                            estadoBotones(BOTONES_ESTADO_COMPRANDO_MARCADO, thisHolder);

                            // damos posición en OrdenArticulo a este artículo
                            OrdenArticuloDao oad = new OrdenArticuloDao(contexto);
                            OrdenArticulo oa = oad.read(lista.getId_super(), linea.getId_articulo());

                            // si no tenía orden anteriormente, lo ponemos al final.
                            if (oa == null) {
                                oa = nuevoOrdenArticulo(oad, lista.getId_super(), linea.getId_articulo());
                            } else {

                                // Cuál es el artículo más arriba de la lista de OrdenArtículo que debe
                                // pasar a un orden mayor que oa.
                                OrdenArticulo pos2Swap = oad.posicionAOcupar(fragmentoNoMarcadoLineas(), oa);

                                // Asignar a oa.orden el orden de pos2Swap e incrementar en uno los siguientes.
                                if (pos2Swap != null) oad.ganarPosiciones(oa, pos2Swap);
                            }
                        }
                    }
                }
            });
            return v;
        }


        /**
         * definimos el botón R.id.botonCheckCompraOn
         * @param v El botón cuyos eventos hay que definir
         * @return el botón
         */
        FloatingActionButton definirBotonCheckCompraOn(FloatingActionButton v) {
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    LineaDao ld = new LineaDao(contexto);
                    linea.setComprado(!linea.getComprado());
                    ld.update(linea);

                    // evitamos procesar un artículo todavía no completado
                    if (linea.getOrden() != ELEMENTO_MAS_RECIENTE) {
                        if (!linea.getComprado()) {
                            // Si desmarcamos un artículo lo ponemos al final de la lista y ya
                            // se reajustará según lo marquemos posteriormente.

                            ((CardView) (cv)).setCardBackgroundColor(COLOR_CARDVIEW_NO_COMPRADO);
                            estadoBotones(BOTONES_ESTADO_COMPRANDO_SIN_MARCAR, thisHolder);

                            OrdenArticuloDao oad = new OrdenArticuloDao(contexto);
                            OrdenArticulo oa = oad.read(lista.getId_super(), linea.getId_articulo());

                            // Lo ponemos al final comprobando que realmente existiera en OrdenArticulo
                            if (oa != null) {
                                // movemos todos los artículo una posición menos para tapar el hueco
                                // dejado por el artículo, ya que este se fue al último de la lista.
                                oad.cederPosiciones(oa);

                                oa.setOrden(oad.elemsSuper(lista.getId_super()) - 1);
                                oad.update(oa);
                            } else {
                                nuevoOrdenArticulo(oad, lista.getId_super(), linea.getId_articulo());
                            }
                        }
                    }
                }
            });
            return v;
        }


        /**
         * definimos el spinner R.id.spinner_item_lista_articulo
         * @param v El botón cuyos eventos hay que definir
         * @return el botón
         */
        AutoCompleteTextView definirArticuloACTV(final AutoCompleteTextView v) {

            // Poblar el ACTV.
            ArticuloDao ad = new ArticuloDao(contexto);
            CursorAdapterArticuloReducido ca = new CursorAdapterArticuloReducido(contexto,
                    ad.getCursorReducido(null, null, null, null));
            v.setThreshold(1);
            v.setAdapter(ca);

            v.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View vv, boolean hasFocus) {
                    if (hasFocus) {
                        //v.setSingleLine(true);
                        InputMethodManager mgr = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.showSoftInput(v, 0);
                    } else {
                        //v.setSingleLine(false);
                    }
                }
            });

            v.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                    Cursor cursor = ((Cursor)parent.getItemAtPosition(position));
                    String selection = cursor.getString(cursor.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE));
                    v.setText(selection);

                    Long anterior = linea.getId_articulo();
                    Integer anteriorReciente = linea.getOrden();
                    try {
                        ArticuloDao ad = new ArticuloDao(contexto);
                        Articulo a = ad.read(selection);
                        if (a != null) {
                            linea.setId_articulo(a.getId());
                            linea.setOrden(NO_ELEMENTO_MAS_RECIENTE);
                            LineaDao ld = new LineaDao(contexto);
                            ld.update(linea);

                            articuloACTVOk = true;
                            if (cantidadNoCero(thisHolder)) {
                                marcarTarjetaCompletada();
                            }
                            mostrarCorrespondienteCantidad(thisHolder, linea);

                            InputMethodManager mgr = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }

                    } catch (SQLiteException sqle) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        builder
                                .setTitle("Lista de Artículos")
                                .setMessage("No se permite repetir artículos en la lista.")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Ok", null)
                                .show();

                        // restaurar operación
                        linea.setId_articulo(anterior);
                        linea.setOrden(anteriorReciente);
                        LineaDao ld = new LineaDao(contexto);
                        ld.update(linea);
                    }
                }
            });

            // Al terminar de escribir algo en cantidad
            v.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    articuloACTVOk = false;
                    articuloACTV.setHint("");
                    ListAdapter c = v.getAdapter();
                    for (int i = 0; i < c.getCount(); i++) {
                        Cursor cursor = (Cursor)c.getItem(i);
                        String valor = cursor.getString(cursor.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE));
                        if (valor.startsWith(s.toString())) {
                            v.setListSelection(i);
                            break;
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            return v;
        }


        /**
         * definimos el numberPicker R.id.lista_item_numberPicker
         * @param v El numberPicker cuyos eventos hay que definir
         * @return el numberPicker
         */
        Spinner definirNumberPicker(Spinner v) {

            Integer[] items = new Integer[100];
            for (int i = 0; i < 100; i++) { items[i] = i; }
            ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(contexto,
                    R.layout.cursor_item_cantidad,
                    R.id.spiner_list_cantidad, items);
            v.setAdapter(adapter);

            v.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (inicio_setSelection_cantidad == false) {
                        inicio_setSelection_cantidad = true;
                    } else {
                        linea.setCantidad(Float.valueOf(position));
                        linea.setOrden(NO_ELEMENTO_MAS_RECIENTE);
                        LineaDao ld = new LineaDao(contexto);
                        ld.update(linea);

                        // mostrar el importe de la compra en el botón fin de compra
                        importeTotalABotonFin();

                        if (articuloACTVOk) { //getSelectedItemPosition() != 0) {
                            marcarTarjetaCompletada();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            return v;
        }

        /**
         * definimos el Edittext R.id.cv_descripcion
         * @param vv El Edittext cuyos eventos hay que definir
         * @return el Edittext
         */
        EditText definirEdittextDescripcion(final EditText vv) {

            vv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId== EditorInfo.IME_ACTION_DONE){
                        //Clear focus here from edittext
                        ArticuloDao ad = new ArticuloDao(contexto);
                        articulo.setDescripcion(vv.getText().toString());
                        ad.update(articulo);
                    }
                    return false;
                }
            });

            // Al abandonar el campo
            vv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        //Clear focus here from edittext
                        ArticuloDao ad = new ArticuloDao(contexto);
                        articulo.setDescripcion(vv.getText().toString());
                        ad.update(articulo);
                    }
                }
            });

            vv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        vv.setSingleLine();
                        vv.setHint("");
                        vv.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        vv.setMaxLines(1);
                    }
                    return false;
                }
            });
            return vv;
        }


        /**
         * definimos el Edittext R.id.lista_item_cantidad
         * @param vv El Edittext cuyos eventos hay que definir
         * @return el Edittext
         */
        EditText definirEdittextCantidad(final EditText vv) {

            vv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId== EditorInfo.IME_ACTION_DONE){
                        // mostrar el importe de la compra en el botón fin de compra
                        importeTotalABotonFin();
                        marcarTarjetaCompletada();
                    }
                    return false;
                }
            });

            // Al terminar de escribir algo en cantidad
            vv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        linea.setCantidad(Float.parseFloat(cantidad.getText().toString()));
                    } catch (NumberFormatException nfe) {
                        linea.setCantidad(0.0f);
                    }
                    linea.setOrden(NO_ELEMENTO_MAS_RECIENTE);
                    LineaDao ld = new LineaDao(contexto);
                    ld.update(linea);

                    if (articuloACTVOk) { //getSelectedItemPosition()!=0) {
                        marcarTarjetaCompletada();
                    }
                }
            });

            return vv;
        }


        /**
         * definimos el Edittext R.id.cv_edit_text_pvp
         * @param vv El Edittext cuyos eventos hay que definir
         * @return el Edittext
         */
        EditText definirEdittextPvp(final EditText vv) {

            vv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_DONE){

                        LineaDao ld = new LineaDao(contexto);
                        try {
                            linea.setPvp(Float.parseFloat(pvp.getText().toString()));
                            ld.update(linea);
                        } catch (NumberFormatException pe) {
                            linea.setPvp(0f);
                        }
                        // mostrar el importe de la compra en el botón fin de compra
                        importeTotalABotonFin();
                    }
                    return false;
                }
            });

            // Al terminar de escribir algo en cantidad
            vv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            // Al abandonar el campo
            vv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        LineaDao ld = new LineaDao(contexto);
                        try {
                            linea.setPvp(Float.parseFloat(pvp.getText().toString()));
                            ld.update(linea);
                        } catch (NumberFormatException pe) {
                            linea.setPvp(0f);
                        }
                        // mostrar el importe de la compra en el botón fin de compra
                        importeTotalABotonFin();
                    }
                }
            });

            return vv;
        }

        /**
         * Calculamos el importe total de la compra. Recorriendo todas las líneas
         */
        public void importeTotalABotonFin() {
            Button b = (Button) ((LinearLayout)cv.getParent().getParent().getParent())
                    .findViewById(R.id.buttonFinComprar);
            if (b != null) {
                Float total = 0.0f;
                for (Linea l : lineas) {
                    total += (l.getPvp() * l.getCantidad());
                }
                String n = String.format("%1$,.2f", total);
                b.setText(n + " €");
                b.setTextColor(Color.WHITE);
                b.setTypeface(null, Typeface.BOLD);
                b.setTextSize(TypedValue.COMPLEX_UNIT_DIP, cv.getResources().getDimension(R.dimen.importe_total));

                // guardar datos importe total
                ListaDao ld = new ListaDao(contexto);
                lista.setImporte(total);
                ld.update(lista);
            }
        }


        /**
         * Hace visible el Edittext Cantidad o el numberPicker según si Articulo.medida
         * es unidades o kilogramos.
         * @return
         */
        void escogerEtONp() {

            if (linea == null) {
                cantidadNPicker.setVisibility(View.VISIBLE);
                cantidad.setVisibility(View.GONE);
                return;
            }

            Articulo a = (new ArticuloDao(contexto)).read(linea.getId_articulo());
            if (a.getMedida().compareTo(TIPO_MEDIDA_UNIDADES) == 0) {
                cantidadNPicker.setVisibility(View.VISIBLE);
                cantidad.setVisibility(View.GONE);
            } else {
                cantidadNPicker.setVisibility(View.GONE);
                cantidad.setVisibility(View.VISIBLE);
            }
        }


        // Como lo artículos marcados ya han sido posicionados en OrdenArticuloDao solo tenemos que
        // preocuparnos de los artículos no marcados.
        // Una vez tenemos la lista de los no marcados necesitaremos saber si hay alguno que tenga
        // un orden menor que el artículo actual y entonces cambiar el orden del artículo actual
        // por el orden de menor valor en la lista de los no marcados.  Después habrá que incrementar
        // en uno todos los siguientes en OrdenArtículos.
        private ArrayList<Linea> fragmentoNoMarcadoLineas() {

            ArrayList<Linea> ll = new ArrayList<Linea>();
            Boolean inicio = false;
            for (Linea l : lineas) {
                if (!l.getComprado()) ll.add(l);
            }
            return ll;
        }


        public void marcarTarjetaCompletada() {
            //botonMasArticulo.setVisibility(View.GONE);
            if ((botonDesplegarArticulo.getVisibility() != View.VISIBLE)
                    && (linea.getOrden() != ELEMENTO_MAS_RECIENTE))
            {
                estadoBotones(BOTONES_ESTADO_NOCOMPRANDO_SIN_MARCAR, thisHolder);
            }
            if (linea.getComprado()) {
                estadoBotones(BOTONES_ESTADO_COMPRANDO_MARCADO, thisHolder);
                cv.setCardBackgroundColor(COLOR_CARDVIEW_COMPRADO);
            } else {
                estadoBotones(BOTONES_ESTADO_COMPRANDO_SIN_MARCAR, thisHolder);
                cv.setCardBackgroundColor(COLOR_CARDVIEW_NO_COMPRADO);
            }
        }

        public OrdenArticulo nuevoOrdenArticulo(OrdenArticuloDao oad, Long id_super, Long id_articuloSpinner) {
            OrdenArticulo oa = new OrdenArticulo();
            oa.setOrden(oad.elemsSuper(id_super));
            oa.setId_super(id_super);
            oa.setId_articulo(id_articuloSpinner);
            oad.insert(oa);
            return oa;
        }

        /**
         * Hacemos visible EditText Cantidad en lugar de Spinner Cantidad
         */
        public void mostrarCantidadEditText() {
            cantidad.setVisibility(View.VISIBLE);
            cantidadNPicker.setVisibility(View.GONE);
            tvCantidad.setText("peso kg");
        }

        /**
         * Hacemos visible Spinner Cantidad en lugar de EditText cantidad
         */
        public void mostrarCantidadSpinner() {
            cantidad.setVisibility(View.GONE);
            cantidadNPicker.setVisibility(View.VISIBLE);
            tvCantidad.setText("unidades");
        }
    }


    /***************************************************************************************
     * Recibe la Lista de la que hace referencia las lineas y el contexto.
     * ordenado indica si queremos el RecyclerView en orden de venta habitual o sólo el orden
     * de introducción en la lista.
     * comprando_ indica si estamos introduciéndo artículos y realizando la compra.
     * @param lista_
     * @param c
     * @param ordenado
     * @param comprando_
     */
    public RVArticulosAdapter(Lista lista_, Context c, Boolean ordenado, Boolean comprando_, ConstraintLayout cl) {

        contexto = c;
        bottomNavigationView = (BottomNavigationView) cl.findViewById(R.id.navigation);
        recyclerviewOrigen = (RecyclerView) cl.findViewById(R.id.recycler_view_articulos);
        comprando = comprando_;
        LineaDao ld = new LineaDao(contexto);
        OrdenArticuloDao oad = new OrdenArticuloDao(contexto);

        // Si cambiamos de lista, también reiniciamos lDesplegadas
        if ((lista != null) && (lista.getId() != lista_.getId())) {
            lDesplegadas = null;
        }
        lista = lista_;

        // obtenemos las lineas en el orden de compra a través del objeto OrdenArticuloDao
        lineas = oad.getLineasEnOrden(ld.listado(lista.getId(), null, LineaContract.LineaEntry._ID),
                lista.getId_super(), ordenado);

        // iniciamos la lista de recordatorio de estado de CardView, estado desplegado si/no.
        if (lDesplegadas == null) { // cuando iniciamos la app o cambiamos de lista.
            lDesplegadas = new ArrayList<Boolean>();
            for (Linea l: lineas) {
                lDesplegadas.add(Boolean.FALSE);
            }
        } else {
            if (lDesplegadas.size() < lineas.size()) {  // cuando añadimos un CardView a la lista
                for (Linea l: lineas) {
                    lDesplegadas.add(Boolean.FALSE);
                }
            }
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public RVArticulosAdapter.LineaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cursor_item_lista_cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        LineaViewHolder vh = new LineaViewHolder((LinearLayout) v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LineaViewHolder holder, final int position) {

        // evitamos comportamientos extraños al reutilizar los ViewHolder
        holder.setIsRecyclable(false);

        final LineaViewHolder holder_ = holder;
        holder.linea = lineas.get(position);
        holder.articulo = (new ArticuloDao(contexto)).read(holder.linea.getId_articulo());

        // Si el CardView estaba desplegado, lo desplegamos o replegamos según lDesplegadas
        Boolean estado = (lDesplegadas.size() > position)? lDesplegadas.get(position) : false;
        android.support.transition.TransitionManager.beginDelayedTransition(holder_.cv);
        holder_.ll.setVisibility( (estado)? View.VISIBLE: View.GONE);


        mostrarCorrespondienteCantidad(holder, holder.linea);

        // si holder.linea.articulo_id = -1 ponemos orden a -1 para indicar que es el nuevo artículo
        if (holder.linea.getId_articulo() == ELEMENTO_MAS_RECIENTE.longValue()) {
            ((CardView)(holder.cv)).setCardBackgroundColor(COLOR_CARDVIEW_NUEVO);
            estadoBotones(BOTONES_ESTADO_NUEVO_ARTICULO, holder);
            holder.linea.setOrden(ELEMENTO_MAS_RECIENTE);
        } else {

            // actualizamos checkbox y hacemos visible botonDesplegar si está marcado como comprado
            //holder.checkBox.setChecked(holder.linea.getComprado());
            if (comprando) {
                if (holder.linea.getComprado()) {
                    ((CardView) (holder.cv)).setCardBackgroundColor(COLOR_CARDVIEW_COMPRADO);
                    estadoBotones(BOTONES_ESTADO_COMPRANDO_MARCADO, holder);
                } else {
                    ((CardView)(holder.cv)).setCardBackgroundColor(COLOR_CARDVIEW_NO_COMPRADO);
                    estadoBotones(BOTONES_ESTADO_COMPRANDO_SIN_MARCAR, holder);
                }
            } else {
                if (holder.linea.getComprado()) {
                    ((CardView) (holder.cv)).setCardBackgroundColor(COLOR_CARDVIEW_COMPRADO);
                    estadoBotones(BOTONES_ESTADO_NOCOMPRANDO_MARCADO, holder);
                } else {
                    ((CardView) (holder.cv)).setCardBackgroundColor(COLOR_CARDVIEW_NO_COMPRADO);
                    estadoBotones(BOTONES_ESTADO_NOCOMPRANDO_SIN_MARCAR, holder);
                }
            }

        }

        // poner el spinner en el articulo correspondiente
        ArticuloDao ad = new ArticuloDao(contexto);
        CursorAdapterArticuloReducido ca = new CursorAdapterArticuloReducido(contexto,
                ad.getCursorReducido(null, null, null, null));
        holder.articuloACTV.setAdapter(ca);
        if (holder.articulo != null) {
            holder.articuloACTV.setText(holder.articulo.getNombre());
            holder.articuloACTV.setSingleLine(false);
        }
        //setSpinnerPos(holder.articuloACTV, lineas.get(position).getId_articulo());
        
        // rellenar datos desplegables.
        holder.rellenarDatosDesplegables();

        // ponemos el estado correcto de despliegue del CardView.
        holder.botonDesplegarArticulo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean estado = lDesplegadas.get(position);
                    lDesplegadas.set(position, !estado);
                    android.support.transition.TransitionManager.beginDelayedTransition(holder_.cv);
                    holder_.ll.setVisibility( (!estado)? View.VISIBLE: View.GONE);

                }
        });

    }

    /**
     *  Ocultar BottomNavigationView
     */
    public static void ocultarBNV() {

        if ((recyclerviewOrigen != null) && (bottomNavigationView != null)) {
            recyclerviewOrigen.setPadding(0, 0, 0, 0);
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    /**
     *  Mostrar BottomNavigationView
     */
    public static void mostrarBNV() {

        if ((recyclerviewOrigen != null) && (bottomNavigationView != null)) {
            recyclerviewOrigen.setPadding(0, 0, 0, bottomNavigationView.getHeight());
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    //
    // muestra los botones correspondientes según el estado
    //
    public static void estadoBotones(int tipoEstado, LineaViewHolder holder) {

        holder.botonCheckCompraOff.setVisibility(View.GONE);
        holder.botonCheckCompraOn.setVisibility(View.GONE);
        holder.botonDesplegarArticulo.setVisibility(View.GONE);
        holder.botonBorrarArticulo.setVisibility(View.GONE);
        holder.botonMasArticulo.setVisibility(View.GONE);
        switch (tipoEstado) {
            case BOTONES_ESTADO_NUEVO_ARTICULO:
                holder.botonMasArticulo.setVisibility(View.VISIBLE);
                break;
            case BOTONES_ESTADO_NOCOMPRANDO_MARCADO:
                holder.botonDesplegarArticulo.setVisibility(View.VISIBLE);
                break;
            case BOTONES_ESTADO_NOCOMPRANDO_SIN_MARCAR:
                holder.botonBorrarArticulo.setVisibility(View.VISIBLE);
            case BOTONES_ESTADO_COMPRANDO_SIN_MARCAR:
                holder.botonBorrarArticulo.setVisibility(View.VISIBLE);
                holder.botonCheckCompraOff.setVisibility(View.VISIBLE);
                break;
            case BOTONES_ESTADO_COMPRANDO_MARCADO:
                holder.botonDesplegarArticulo.setVisibility(View.VISIBLE);
                holder.botonCheckCompraOn.setVisibility(View.VISIBLE);
                break;
        }
    }

    //
    // mostramos el tipo de input para la cantidad según corresponda (EditText o Spinner)
    //
    static public void mostrarCorrespondienteCantidad(LineaViewHolder lvh, Linea l) {

        Articulo a = (new LineaDao(contexto)).readArticulo(l);
        if (a != null) {
            if (a.getMedida().charValue() == TIPO_MEDIDA_KILOGRAMOS) {
                lvh.mostrarCantidadEditText();
                if (l.getCantidad() > 0) lvh.cantidad.setHint(l.getCantidad().toString());
            } else {
                lvh.mostrarCantidadSpinner();
                if (l.getCantidad() > 0) lvh.cantidadNPicker.setSelection(l.getCantidad().intValue());
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lineas.size();
    }


    // Devuelve el id de la linea que hay que actualizarle articuloId.
    public Long getElemActual() {
        for (Linea l: lineas) {
            if (l.getOrden() == ELEMENTO_MAS_RECIENTE) {
                return l.getId();
            }
        }
        return 0L;
    }

    // Devuelve la posición de la línea que le indiquemos.
    public static Integer posicion(Linea l) {
        Integer i;
        for (i = 0; i < lineas.size(); i++) {
            if (lineas.get(i).getId() == l.getId()) return i;
        }
        return i;
    }

    /**
     * Devuelve Verdadero si en EditText cantidad o NumberPicker hay algún valor.
     * Falso en otro caso.
     * @return
     */
    static Boolean cantidadNoCero(LineaViewHolder vh) {

        if (vh.cantidad.getVisibility() == View.VISIBLE) {
            if (!vh.cantidad.getText().toString().isEmpty()) return true;
        }
        if (vh.cantidadNPicker.getVisibility() == View.VISIBLE) {
            if (vh.cantidadNPicker.getSelectedItemPosition() != 0) return true;
        }
        return false;
    }

}

