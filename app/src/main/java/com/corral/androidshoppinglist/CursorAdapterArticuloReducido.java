package com.corral.androidshoppinglist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import persistencia.dao.ArticuloContract;

public class CursorAdapterArticuloReducido extends CursorAdapter {

    Context contexto;

    public CursorAdapterArticuloReducido(Context context, Cursor cursor) {
        super(context, cursor, 0);
        contexto = context;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);

        final TextView tv_list_item_articulo = (TextView) view.findViewById(R.id.list_item_articulo_reducido);

        Cursor c = getCursor();

        tv_list_item_articulo.setText(c.getString(
                c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE)));

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Animate the background color of clicked Item
                ColorDrawable[] color = {
                        new ColorDrawable(contexto.getResources().getColor(R.color.listview_color_4)),
                        new ColorDrawable(contexto.getResources().getColor(R.color.listview_color_1))
                };
                TransitionDrawable trans = new TransitionDrawable(color);
                v.setBackground(trans);
                trans.startTransition(500); // duration 2 seconds

                // Go back to the default background color of Item
                ColorDrawable[] color2 = {
                        new ColorDrawable(contexto.getResources().getColor(R.color.listview_color_3)),
                        new ColorDrawable(contexto.getResources().getColor(R.color.listview_color_4))
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