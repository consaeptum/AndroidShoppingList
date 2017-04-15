package com.corral.androidshoppinglist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import persistencia.dao.FamiliaContract;
import persistencia.dao.FamiliaDao;
import persistencia.jb.Familia;

public class ActivityFamiliaList extends AppCompatActivity {

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familia_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CursorAdapterFamilia caf = new CursorAdapterFamilia(this, new FamiliaDao(this).
                getCursor(null, FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE));


        lv = ((ListView)findViewById(R.id.listaFamilia));
        lv.setAdapter(caf);

        // Al hacer click en un elemento de la lista
        final Context contexto = this;
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Familia f = new Familia();
                Cursor cur = (Cursor) lv.getAdapter().getItem(position);
                cur.moveToPosition(position);
                f.setId(cur.getLong(cur.getColumnIndex(FamiliaContract.FamiliaEntry._ID)));
                f.setNombre(cur.getString(cur.getColumnIndex(FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE)));

                Intent intent = new Intent(contexto,ActivityFamiliaDetalle.class);
                intent.putExtra("Familia", f);
                startActivity(intent);
            }
        });

        // Al hacer click en el botón flotante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(contexto,ActivityFamiliaDetalle.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((CursorAdapter)lv.getAdapter()).changeCursor(new FamiliaDao(this).
                getCursor(null, FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE));
    }

}
