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

import persistencia.dao.FamiliaContract;
import persistencia.dao.FamiliaDao;
import persistencia.jb.Familia;
import util.BottomNavigationViewHelper;

public class ActivityFamiliaList extends AppCompatActivity {

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
                    return true;
                case R.id.navigation_articulo:
                    intent = new Intent(getApplicationContext(),ActivityArticuloList.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_lista:
                    intent = new Intent(getApplicationContext(),ActivityListaList.class);
                    startActivity(intent);
                    finish();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familia_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Context este = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(este, ActivityFamiliaDetalle.class);
                startActivity(intent);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().setGroupCheckable(0, true, true);
        navigation.getMenu().getItem(1).setChecked(true);

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((CursorAdapter)lv.getAdapter()).changeCursor(new FamiliaDao(this).
                getCursor(null, FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE));
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
            Intent intent = new Intent(this,ActivityFamiliaDetalle.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
