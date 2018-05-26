package com.corral.androidshoppinglist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import persistencia.dao.ListaContract;
import persistencia.dao.SuperMercadoDao;
import persistencia.jb.Lista;
import persistencia.jb.SuperMercado;


public class CursorAdapterLista extends CursorAdapter {

    Context contexto;

    public CursorAdapterLista(Context context, Cursor cursor) {
        super(context, cursor, 0);
        contexto = context;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundColor(Color.WHITE);

        TextView tv_list_item_fecha = (TextView) view.findViewById(R.id.list_item_fecha);
        TextView tv_list_item_supermercado = (TextView) view.findViewById(R.id.list_item_supermercado);

        Cursor c = getCursor();
        tv_list_item_fecha.setText(Lista.getFechaFormatDMY(c.getString(
                c.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_FECHA))));

        SuperMercadoDao fd = new SuperMercadoDao(contexto);
        String id_super = c.getString(c.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER));
        SuperMercado superMercado = fd.read(Long.parseLong(id_super));
        if (superMercado != null)
            tv_list_item_supermercado.setText((superMercado.getNombre()));

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Animate the background color of clicked Item
                ColorDrawable[] color = {
                        new ColorDrawable(v.getResources().getColor(R.color.listview_color_4)),
                        new ColorDrawable(v.getResources().getColor(R.color.listview_color_1))
                };
                TransitionDrawable trans = new TransitionDrawable(color);
                v.setBackground(trans);
                trans.startTransition(500); // duration 2 seconds

                // Go back to the default background color of Item
                ColorDrawable[] color2 = {
                        new ColorDrawable(v.getResources().getColor(R.color.listview_color_3)),
                        new ColorDrawable(v.getResources().getColor(R.color.listview_color_4))
                };
                TransitionDrawable trans2 = new TransitionDrawable(color2);
                v.setBackground(trans2);
                trans2.startTransition(500); // duration 2 seconds

                return false;
            }
        });


        return view;
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.cursor_item_lista, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find fields to populate in inflated template
        TextView tvFecha = (TextView) view.findViewById(R.id.list_item_fecha);
        // Extract properties from cursor
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow(
                ListaContract.ListaEntry.COLUMN_NAME_FECHA));
        // Populate fields with extracted properties
        tvFecha.setText(Lista.getFechaFormatDMY(fecha));

        TextView tvSuper = (TextView) view.findViewById(R.id.list_item_supermercado);

        TextView tvImporte = (TextView) view.findViewById(R.id.list_item_importe);

        // Extract properties from cursor
        SuperMercadoDao fd = new SuperMercadoDao(contexto);
        String id_super = cursor.getString(
                cursor.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER));
        SuperMercado superjb = fd.read(Long.parseLong(id_super));
        if (superjb != null) {
            tvSuper.setText(superjb.getNombre());
            Float i = cursor.getFloat(cursor.getColumnIndexOrThrow(ListaContract.ListaEntry.COLUMN_NAME_IMPORTE));
            if (i > 0) {
                String n = String.format("%1$,.2f", i);
                tvImporte.setText(n + " €");
            }
        }
        fd.close();
    }
}