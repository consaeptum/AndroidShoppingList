package persistencia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import persistencia.jb.SuperMercado;

/**
 * Dao SuperMercado
 */

public class SuperMercadoDao {

    /**
     * DBHelper se instancia sólo una vez porque getWritableDatabase es costoso si
     * DBhelper está cerrado.
     * Deberá cerrarse en onDestroy():
     *  protected void onDestroy() {
     *      SuperMercadoDao.close();
     *      super.onDestroy();
     *  }
     */
    static DBHelper mDBHelper = null;

    public SuperMercadoDao(Context context) {
        if (mDBHelper == null) mDBHelper = new DBHelper(SuperMercadoContract.getInstance(), context);
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    /**
     * Añadir un registro de la tabla SuperMercado.  Si la operación tiene éxito cambia el ID en
     * superMercado.
     * @param superMercado El java bean SuperMercado.
     * @return True o False según si la operación se realizó correctamente.
     */

    public Boolean insert(SuperMercado superMercado) {

        // Gets the data repository in write mode
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE, superMercado.getNombre());

        // Insert the new row, returning the primary key value of the new row
        // null indica no añadir la fila si values está vacío.
        long newRowId = -1;
        try {
            newRowId = db.insertOrThrow(SuperMercadoContract.SuperMercadoEntry.TABLE_NAME, null, values);
        } catch (Exception sqlce) {
            if (sqlce instanceof SQLiteConstraintException) throw sqlce;
        }
        if (newRowId > -1) superMercado.setId(newRowId);

        return newRowId > -1;
    }

    /**
     * Leer un registro de la tabla SuperMercado a partir del ID.
     * @param id El identificador o código de la SuperMercado.
     * @return El javabean SuperMercado o null si no se encontró el ID.
     */
    public SuperMercado read(Long id) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                SuperMercadoContract.SuperMercadoEntry._ID,
                SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = SuperMercadoContract.SuperMercadoEntry._ID + " = ?";
        String[] selectionArgs = { id.toString() };

        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";


        Cursor c = db.query(
                SuperMercadoContract.SuperMercadoEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        SuperMercado f = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            f = new SuperMercado();
            f.setId(id);
            f.setNombre(
                    c.getString(c.getColumnIndex(SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE))
            );
        }
        c.close();

        return f;
    }

    /**
     * Leer un registro de la tabla SuperMercado a partir del ID.
     * @param superMercado El identificador del SuperMercado.
     * @return El javabean SuperMercado o null si no se encontró el ID.
     */
    public SuperMercado read(String superMercado) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                SuperMercadoContract.SuperMercadoEntry._ID,
                SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE + " = ?";
        String[] selectionArgs = { superMercado };

        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";


        Cursor c = db.query(
                SuperMercadoContract.SuperMercadoEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        SuperMercado f = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            f = new SuperMercado();
            f.setId(c.getLong(c.getColumnIndex(SuperMercadoContract.SuperMercadoEntry._ID)));
            f.setNombre(
                    c.getString(c.getColumnIndex(SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE))
            );
        }
        c.close();

        return f;
    }


    /**
     * Elimina un registro de la tabla SuperMercado a partir del ID.
     * @param id El identificador o código de la SuperMercado.
     * @return True o False según si la operación se realizó correctamente.
     */
    public Boolean delete(Long id) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = SuperMercadoContract.SuperMercadoEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id.toString() };
        // Issue SQL statement.
        int count = db.delete(SuperMercadoContract.SuperMercadoEntry.TABLE_NAME, selection, selectionArgs);

        return count > 0;
    }

    /**
     * Modifica un registro de la tabla SuperMercado a partir del ID.
     * @param SuperMercado El java bean que contiene los datos a actualizar.
     */
    public Boolean update(SuperMercado SuperMercado) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE, SuperMercado.getNombre());

        // Which row to update, based on the title
        String selection = SuperMercadoContract.SuperMercadoEntry._ID + " = ?";
        String[] selectionArgs = { SuperMercado.getId().toString() };

        int count = db.update(
                SuperMercadoContract.SuperMercadoEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count > 0;
    }

    /**
     * Obtiene una lista de superMercados filtrado por los campos de los parametros.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     * @param nombre El nombre deberá comenzar por el contenido de este parámetro para cumplir la
     *               condición de filtrado.  Si es null, no se filtrará por este campo.
     * @param sortBy null si no debe estar ordenado y el nombre del campo por el que deba estar
     *               ordenado en caso contrario.
     * @return an ArrayList of SuperMercado
     *
     */
    public ArrayList<SuperMercado> listado(String nombre, String sortBy) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                SuperMercadoContract.SuperMercadoEntry._ID,
                SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE,
        };

        // Filter results WHERE
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (nombre != null) {
            selection = selection.concat(SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE + " LIKE ?");
            selectionArgs.add(nombre + "%");
        }

        String[] S = { "" };
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT * FROM " + SuperMercadoContract.SuperMercadoEntry.TABLE_NAME + ((sortBy != null)? (" ORDER BY " + sortBy):"") , null);
        else
            c = db.query(
                SuperMercadoContract.SuperMercadoEntry.TABLE_NAME,  // The table to query
                projection,                                 // The columns to return
                selection,                                  // The columns for the WHERE clause
                selectionArgs.toArray(S),                   // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                sortBy                                      // The sort order
        );

        ArrayList<SuperMercado> listSuperMercado = new ArrayList<SuperMercado>();
        if (c.getCount() > 0) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                SuperMercado superMercado = new SuperMercado();
                superMercado.setId(c.getLong(c.getColumnIndex(SuperMercadoContract.SuperMercadoEntry._ID)));
                superMercado.setNombre(
                        c.getString(c.getColumnIndex(SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE))
                );
                listSuperMercado.add(superMercado);
                c.moveToNext();
            }
        }
        c.close();
        return listSuperMercado;
    }

    /**
     * Obtiene un cursor de una lista de superMercados filtrado por los campos de los parametros.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     * @param nombre El nombre deberá comenzar por el contenido de este parámetro para cumplir la
     *               condición de filtrado.  Si es null, no se filtrará por este campo.
     * @param sortBy null si no debe estar ordenado y el nombre del campo por el que deba estar
     *               ordenado en caso contrario.
     * @return un cursor de Supermercado
     *
     */
    public Cursor getCursor(String nombre, String sortBy) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                SuperMercadoContract.SuperMercadoEntry._ID,
                SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE,
        };

        // Filter results WHERE
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (nombre != null) {
            selection = selection.concat(SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE + " LIKE ?");
            selectionArgs.add(nombre + "%");
        }

        String[] S = { "" };
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT * FROM " + SuperMercadoContract.SuperMercadoEntry.TABLE_NAME + ((sortBy != null)? (" ORDER BY " + sortBy):"") , null);
        else
            c = db.query(
                    SuperMercadoContract.SuperMercadoEntry.TABLE_NAME,  // The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    selectionArgs.toArray(S),                   // The values for the WHERE clause
                    null,                                       // don't group the rows
                    null,                                       // don't filter by row groups
                    sortBy                                      // The sort order
            );

        return c;
    }

}
