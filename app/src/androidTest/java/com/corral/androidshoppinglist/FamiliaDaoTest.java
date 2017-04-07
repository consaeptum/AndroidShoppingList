package com.corral.androidshoppinglist;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import persistencia.dao.DBHelper;
import persistencia.dao.FamiliaContract;
import persistencia.dao.FamiliaDao;
import persistencia.jb.Familia;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertTrue;

/**
 * 
 */
@RunWith(AndroidJUnit4.class)
public class FamiliaDaoTest {

    private DBHelper mDBHelper;

    @Before
    public void setUp() throws Exception {
        mDBHelper = new DBHelper(FamiliaContract.getInstance(), getTargetContext());
        getTargetContext().deleteDatabase(mDBHelper.getDatabaseName());
    }

    @After
    public void tearDown() throws Exception {
        mDBHelper.close();
    }

    @Test
    public void crudTest() throws Exception {
        
        Context appContext = getTargetContext();

        Familia f = new Familia();
        FamiliaDao fd = new FamiliaDao();

        f.setNombre("Nombre de Familia de prueba UPDATER");
        assertTrue(fd.insert(appContext, f));

        f = fd.read(appContext, "Nombre de Familia de prueba UPDATER");
        assertTrue(f != null);

        f.setNombre("Nombre de Familia de prueba UPDATE");
        fd.update(appContext, f);

        f = fd.read(appContext, "Nombre de Familia de prueba UPDATE");
        assertTrue(f != null);

        assertTrue(fd.delete(appContext, f.getId()));

    }

}
