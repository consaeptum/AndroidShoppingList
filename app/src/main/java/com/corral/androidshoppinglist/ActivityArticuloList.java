package com.corral.androidshoppinglist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import persistencia.dao.ArticuloContract;
import persistencia.dao.ArticuloDao;
import persistencia.jb.Articulo;
import util.BottomNavigationViewHelper;


public class ActivityArticuloList extends AppCompatActivity {

    ListView lv;

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
        setContentView(R.layout.activity_articulo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().setGroupCheckable(0, true, true);
        navigation.getMenu().getItem(2).setChecked(true);

        CursorAdapterArticulo caf = new CursorAdapterArticulo(this, new ArticuloDao(this).
                getCursor(null, null, null, null, ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE));


        lv = ((ListView)findViewById(R.id.listaArticulo));
        lv.setAdapter(caf);

        // Al hacer click en un elemento de la lista
        final Context contexto = this;
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Articulo f = new Articulo();
                Cursor cur = (Cursor) lv.getAdapter().getItem(position);
                cur.moveToPosition(position);
                f.setId(cur.getLong(cur.getColumnIndex(
                        ArticuloContract.ArticuloEntry._ID)));
                f.setNombre(cur.getString(cur.getColumnIndex(
                        ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE)));
                f.setDescripcion(cur.getString(cur.getColumnIndex(
                        ArticuloContract.ArticuloEntry.COLUMN_NAME_DESCRIPCION)));
                f.setMedida(cur.getString(cur.getColumnIndex(
                        ArticuloContract.ArticuloEntry.COLUMN_NAME_MEDIDA
                )).charAt(0));
                f.setId_familia(Long.parseLong(cur.getString(cur.getColumnIndex(
                        ArticuloContract.ArticuloEntry.COLUMN_NAME_ID_FAMILIA))));

                Intent intent = new Intent(contexto,ActivityArticuloDetalle.class);
                intent.putExtra("Articulo", f);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((CursorAdapter)lv.getAdapter()).changeCursor(new ArticuloDao(this).
                getCursor(null, null, null, null, ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tool_bar_menu_articulo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this,ActivityArticuloDetalle.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}


