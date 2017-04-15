package com.corral.androidshoppinglist;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import persistencia.dao.DBHelper;
import persistencia.dao.LineaContract;
import persistencia.dao.LineaDao;
import persistencia.jb.Linea;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertTrue;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class LineaDaoTest {

    private DBHelper mDBHelper;

    @Before
    public void setUp() throws Exception {
        mDBHelper = new DBHelper(LineaContract.getInstance(), getTargetContext());
        getTargetContext().deleteDatabase(mDBHelper.getDatabaseName());
    }

    @After
    public void tearDown() throws Exception {
        mDBHelper.close();
    }

    @Test
    public void crudTest() throws Exception {

        Context appContext = getTargetContext();

        Linea f = new Linea();
        LineaDao fd = new LineaDao(appContext);

        f.setCantidad(44f);
        assertTrue(fd.insert(f));

        f = fd.read(f.getId());
        assertTrue(f != null);

        f.setCantidad(88f);
        fd.update(f);

        f = fd.read(f.getId());
        assertTrue(f.getCantidad() == 88f);

        assertTrue(fd.delete(f.getId()));

        f.setPvp(1.24f); f.setCantidad(1f); f.setId_articulo(1L); f.setId_lista(4L);
        assertTrue(fd.insert(f));

        f.setPvp(1.25f); f.setCantidad(2f); f.setId_articulo(2L); f.setId_lista(5L);
        assertTrue(fd.insert(f));

        f.setPvp(1.26f); f.setCantidad(3f); f.setId_articulo(3L); f.setId_lista(5L);
        assertTrue(fd.insert(f));

        ArrayList<Linea> listaLinea =  fd.listado(5L,3L, null);

        for(Linea a: listaLinea) {
            System.out.println("###### Linea: " + a.getId() + "-" + a.getPvp() + "-" + a.getCantidad() +
                    "-" + a.getId_articulo() + "-" + a.getId_lista());
        }

        assertTrue(listaLinea.size() == 1);
        assertTrue(listaLinea.get(0).getId() == 3L);

    }

}
