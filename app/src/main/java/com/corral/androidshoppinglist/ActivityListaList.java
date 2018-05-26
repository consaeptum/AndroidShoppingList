package com.corral.androidshoppinglist;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import persistencia.dao.ListaContract;
import persistencia.dao.ListaDao;
import persistencia.jb.Lista;
import util.BottomNavigationViewHelper;


public class ActivityListaList extends AppCompatActivity {

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
                    overridePendingTransition(R.anim.aderecha, R.anim.adefuera);
                    finish();
                    return true;
                case R.id.navigation_familia:
                    intent = new Intent(getApplicationContext(),ActivityFamiliaList.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.aderecha, R.anim.adefuera);
                    finish();
                    return true;
                case R.id.navigation_articulo:
                    intent = new Intent(getApplicationContext(),ActivityArticuloList.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.aderecha, R.anim.adefuera);
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
        setContentView(R.layout.activity_lista_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Context este = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(este, ActivityListaDetalle.class);
                startActivity(intent);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().setGroupCheckable(0, true, true);
        navigation.getMenu().getItem(3).setChecked(true);

        CursorAdapterLista caf = new CursorAdapterLista(this, new ListaDao(this).
                getCursor(null, null, null, ListaContract.ListaEntry.COLUMN_NAME_FECHA));


        lv = ((ListView)findViewById(R.id.listaLista));
        lv.setAdapter(caf);

        // Al hacer click en un elemento de la lista
        final Context contexto = this;
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Lista f = new Lista();
                Cursor cur = (Cursor) lv.getAdapter().getItem(position);
                cur.moveToPosition(position);
                f.setId(cur.getLong(cur.getColumnIndex(
                        ListaContract.ListaEntry._ID)));
                f.setFechaFormatYMD(cur.getString(cur.getColumnIndex(
                        ListaContract.ListaEntry.COLUMN_NAME_FECHA)));
                f.setId_super(Long.parseLong(cur.getString(cur.getColumnIndex(
                        ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER))));
                f.setImporte(cur.getFloat(cur.getColumnIndex(
                        ListaContract.ListaEntry.COLUMN_NAME_IMPORTE)));

                Intent intent = new Intent(contexto,ActivityListaDetalle.class);
                intent.putExtra("Lista", f);
                startActivity(intent);
                overridePendingTransition(R.anim.arriba, R.anim.arribaba);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((CursorAdapter)lv.getAdapter()).changeCursor(new ListaDao(this).
                getCursor(null, null, null, ListaContract.ListaEntry.COLUMN_NAME_FECHA));
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
            Intent intent = new Intent(this,ActivityListaDetalle.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}


