package com.corral.androidshoppinglist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import persistencia.dao.FamiliaContract;

public class CursorAdapterFamilia extends CursorAdapter {

    public CursorAdapterFamilia(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.cursor_item_familia, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvFamilia = (TextView) view.findViewById(R.id.familia);
        // Extract properties from cursor
        String familia = cursor.getString(cursor.getColumnIndexOrThrow(
                FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE));
        // Populate fields with extracted properties
        tvFamilia.setText(familia);
    }
}