package com.corral.androidshoppinglist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CursorAdapter;
import android.widget.TextView;

import persistencia.dao.ArticuloContract;
import util.Constantes;

public class CursorAdapterArticuloReducido extends CursorAdapter {

    Context contexto;

    public CursorAdapterArticuloReducido(Context context, Cursor cursor) {
        super(context, cursor, 0);
        contexto = context;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView tv_list_item_articulo = (TextView) view.findViewById(R.id.list_item_articulo_reducido);

        Cursor c = getCursor();

        tv_list_item_articulo.setText(c.getString(
                c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE)));

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Animation animation1 = new AlphaAnimation(1.0f, 0.3f);
                animation1.setDuration(500);
                Drawable colorAnterior = v.getBackground();
                v.setBackgroundColor(Constantes.COLOR_LISTA_SELECCIONADA);
                v.startAnimation(animation1);
                v.setBackground(colorAnterior);
                return false;
            }
        });

        return view;
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.cursor_item_articulo_reducido, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find fields to populate in inflated template
        TextView tvArticulo = (TextView) view.findViewById(R.id.list_item_articulo_reducido);
        // Extract properties from cursor
        String articulo = cursor.getString(cursor.getColumnIndexOrThrow(
                ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE));
        // Populate fields with extracted properties
        tvArticulo.setText(articulo);

    }
}