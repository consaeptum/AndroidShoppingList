package com.corral.androidshoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import persistencia.dao.DBHelper;
import persistencia.dao.FamiliaContract;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper mDBHelper = new DBHelper(FamiliaContract.getInstance(), this);

/*
        Familia f = new Familia();
        FamiliaDao fd = new FamiliaDao(this);

        f.setNombre("Nombre de Familia 1");
        fd.insert(f);

        f.setNombre("Nombre de Familia 2");
        fd.insert(f);
*/
        Intent i = new Intent(this, ActivityFamiliaList.class );
        startActivity(i);
    }
}
