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

import persistencia.dao.SuperMercadoContract;

public class CursorAdapterSuperMercado extends CursorAdapter {

    public CursorAdapterSuperMercado(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundColor(Color.WHITE);

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
        return LayoutInflater.from(context).inflate(R.layout.cursor_item_supermercado, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvSuperMercado = (TextView) view.findViewById(R.id.supermercado);
        // Extract properties from cursor
        String superMercado = cursor.getString(cursor.getColumnIndexOrThrow(
                SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE));
        // Populate fields with extracted properties
        tvSuperMercado.setText(superMercado);
    }
}