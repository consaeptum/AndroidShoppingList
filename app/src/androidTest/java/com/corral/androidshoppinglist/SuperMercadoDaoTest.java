package com.corral.androidshoppinglist;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import persistencia.dao.DBHelper;
import persistencia.dao.SuperMercadoContract;
import persistencia.dao.SuperMercadoDao;
import persistencia.jb.SuperMercado;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertTrue;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class SuperMercadoDaoTest {

    private DBHelper mDBHelper;

    @Before
    public void setUp() throws Exception {
        mDBHelper = new DBHelper(SuperMercadoContract.getInstance(), getTargetContext());
        getTargetContext().deleteDatabase(mDBHelper.getDatabaseName());
    }

    @After
    public void tearDown() throws Exception {
        mDBHelper.close();
    }

    @Test
    public void crudTest() throws Exception {

        Context appContext = getTargetContext();

        SuperMercado f = new SuperMercado();
        SuperMercadoDao fd = new SuperMercadoDao(appContext);

        f.setNombre("Nombre de SuperMercado de prueba UPDATER");
        assertTrue(fd.insert(f));

        f = fd.read("Nombre de SuperMercado de prueba UPDATER");
        assertTrue(f != null);

        f.setNombre("Nombre de SuperMercado de prueba UPDATE");
        fd.update(f);

        f = fd.read("Nombre de SuperMercado de prueba UPDATE");
        assertTrue(f != null);

        assertTrue(fd.delete(f.getId()));

        f.setNombre("superPrueba1");
        assertTrue(fd.insert(f));

        f.setNombre("superPrueba2");
        assertTrue(fd.insert(f));

        f.setNombre("superPrueba3");
        assertTrue(fd.insert(f));

        ArrayList<SuperMercado> listaSuperMercado =  fd.listado("super", "nombre");

        for(SuperMercado a: listaSuperMercado) {
            System.out.println("###### SuperMercado: " + a.getId() + "-" + a.getNombre());
        }

        assertTrue(listaSuperMercado.size() == 3);
        assertTrue(listaSuperMercado.get(0).getId() == 1L);


    }

}
