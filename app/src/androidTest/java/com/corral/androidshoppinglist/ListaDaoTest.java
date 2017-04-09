package com.corral.androidshoppinglist;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import persistencia.dao.DBHelper;
import persistencia.dao.ListaContract;
import persistencia.dao.ListaDao;
import persistencia.jb.Lista;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertTrue;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class ListaDaoTest {

    private DBHelper mDBHelper;

    @Before
    public void setUp() throws Exception {
        mDBHelper = new DBHelper(ListaContract.getInstance(), getTargetContext());
        getTargetContext().deleteDatabase(mDBHelper.getDatabaseName());
    }

    @After
    public void tearDown() throws Exception {
        mDBHelper.close();
    }

    @Test
    public void crudTest() throws Exception {

        Context appContext = getTargetContext();

        Lista f = new Lista();
        ListaDao fd = new ListaDao(appContext);

        f.setFechaFormat("2017-04-7"); f.setId_super(1L);
        assertTrue(fd.insert(f));

        f = fd.read(f.getId()); f.setId_super(2L);
        assertTrue(f != null);

        f.setFechaFormat("2027-04-07"); f.setId_super(3L);
        fd.update(f);

        f = fd.read(f.getId());
        assertTrue(f.getFechaFormat().contains("2027-04-07"));

        assertTrue(fd.delete(f.getId()));

        f.setFechaFormat("2017-04-09"); f.setId_super(1L);
        assertTrue(fd.insert(f));

        f.setFechaFormat("2017-04-10"); f.setId_super(2L);
        assertTrue(fd.insert(f));

        f.setFechaFormat("2017-04-11"); f.setId_super(3L);
        assertTrue(fd.insert(f));

        ArrayList<Lista> listaLista =  fd.listado("2017-04-10", "2017-04-11", 3L);

        for(Lista a: listaLista) {
            System.out.println("###### Lista: " + a.getId() + "-" + a.getFechaFormat());
        }

        assertTrue(listaLista.size() == 1);
        assertTrue(listaLista.get(0).getId() == 3L);

    }

}
