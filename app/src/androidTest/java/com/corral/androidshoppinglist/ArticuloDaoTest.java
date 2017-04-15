package com.corral.androidshoppinglist;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import persistencia.dao.ArticuloContract;
import persistencia.dao.ArticuloDao;
import persistencia.dao.DBHelper;
import persistencia.jb.Articulo;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertTrue;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class ArticuloDaoTest {

    private DBHelper mDBHelper;

    @Before
    public void setUp() throws Exception {
        mDBHelper = new DBHelper(ArticuloContract.getInstance(), getTargetContext());
        getTargetContext().deleteDatabase(mDBHelper.getDatabaseName());
    }

    @After
    public void tearDown() throws Exception {
        mDBHelper.close();
    }

    @Test
    public void crudTest() throws Exception {

        Context appContext = getTargetContext();

        Articulo f = new Articulo();
        ArticuloDao fd = new ArticuloDao(appContext);

        f.setNombre("Nombre de Articulo de prueba UPDATER");
        assertTrue(fd.insert(f));

        f = fd.read("Nombre de Articulo de prueba UPDATER");
        assertTrue(f != null);

        f.setNombre("Nombre de Articulo de prueba UPDATE");
        fd.update(f);

        f = fd.read("Nombre de Articulo de prueba UPDATE");
        assertTrue(f != null);

        assertTrue(fd.delete(f.getId()));

        f.setNombre("articuloPrueba1"); f.setDescripcion("desc1"); f.setMedida('u'); f.setId_familia(1L);
        assertTrue(fd.insert(f));

        f.setNombre("articuloPrueba2"); f.setDescripcion("desc2"); f.setMedida('k'); f.setId_familia(1L);
        assertTrue(fd.insert(f));

        f.setNombre("articuloPrueba3"); f.setDescripcion("desc3"); f.setMedida('l'); f.setId_familia(1L);
        assertTrue(fd.insert(f));

        ArrayList<Articulo> listaArticulo =  fd.listado("articulo",null,'k',0L, "nombre");

        for(Articulo a: listaArticulo) {
            System.out.println("###### Articulo: " + a.getId() + "-" + a.getNombre() + "-" + a.getDescripcion() +
            "-" + a.getMedida() + "-" + a.getId_familia());
        }

        assertTrue(listaArticulo.size() == 1);
        assertTrue(listaArticulo.get(0).getId() == 2L);
    }

}
