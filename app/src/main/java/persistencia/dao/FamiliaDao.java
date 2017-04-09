package persistencia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import persistencia.jb.Familia;

/**
 * Dao Familia
 */

public class FamiliaDao {

    /**
     * DBHelper se instancia sólo una vez porque getWritableDatabase es costoso si
     * DBhelper está cerrado.
     * Deberá cerrarse en onDestroy():
     *  protected void onDestroy() {
     *      FamiliaDao.close();
     *      super.onDestroy();
     *  }
     */
    static DBHelper mDBHelper = null;

    public FamiliaDao(Context context) {
        if (mDBHelper == null) mDBHelper = new DBHelper(FamiliaContract.getInstance(), context);
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    /**
     * Añadir un registro de la tabla Familia.  Si la operación tiene éxito cambia el ID en familia.
     * @param familia El java bean Familia.
     * @return True o False según si la operación se realizó correctamente.
     */

    public Boolean insert(Familia familia) {

        // Gets the data repository in write mode
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE, familia.getNombre());

        // Insert the new row, returning the primary key value of the new row
        // null indica no añadir la fila si values está vacío.
        long newRowId = db.insertOrThrow(FamiliaContract.FamiliaEntry.TABLE_NAME, null, values);
        if (newRowId > -1) familia.setId(newRowId);

        return newRowId > -1;
    }

    /**
     * Leer un registro de la tabla Familia a partir del ID.
     * @param id El identificador o código de la familia.
     * @return El javabean familia o null si no se encontró el ID.
     */
    public Familia read(Long id) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                FamiliaContract.FamiliaEntry._ID,
                FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = FamiliaContract.FamiliaEntry._ID + " = ?";
        String[] selectionArgs = { id.toString() };

        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";


        Cursor c = db.query(
                FamiliaContract.FamiliaEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        Familia f = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            f = new Familia();
            f.setId(id);
            f.setNombre(
                    c.getString(c.getColumnIndex(FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE))
            );
        }
        c.close();

        return f;
    }

    /**
     * Leer un registro de la tabla Familia a partir del ID.
     * @param nombre El nombre que identifica la familia.
     * @return El javabean familia o null si no se encontró el ID.
     */
    public Familia read(String nombre) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                FamiliaContract.FamiliaEntry._ID,
                FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE + " = ?";
        String[] selectionArgs = { nombre };

        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";


        Cursor c = db.query(
                FamiliaContract.FamiliaEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        Familia f = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            f = new Familia();
            f.setId(c.getLong(c.getColumnIndex(FamiliaContract.FamiliaEntry._ID)));
            f.setNombre(nombre);
        }
        c.close();

        return f;
    }

    /**
     * Elimina un registro de la tabla Familia a partir del ID.
     * @param id El identificador o código de la familia.
     * @return True o False según si la operación se realizó correctamente.
     */
    public Boolean delete(Long id) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = FamiliaContract.FamiliaEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id.toString() };
        // Issue SQL statement.
        int count = db.delete(FamiliaContract.FamiliaEntry.TABLE_NAME, selection, selectionArgs);

        return count > 0;
    }

    /**
     * Modifica un registro de la tabla Familia a partir del ID.
     * @param familia El java bean que contiene los datos a actualizar.
     */
    public Boolean update(Familia familia) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE, familia.getNombre());

        // Which row to update, based on the title
        String selection = FamiliaContract.FamiliaEntry._ID + " = ?";
        String[] selectionArgs = { familia.getId().toString() };

        int count = db.update(
                FamiliaContract.FamiliaEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count > 0;
    }

    /**
     * Obtiene una lista de familias filtrado por los campos de los parametros.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     * @param nombre El nombre deberá comenzar por el contenido de este parámetro para cumplir la
     *               condición de filtrado.  Si es null, no se filtrará por este campo.
     * @return an ArrayList of Familia
     *
     */
    public ArrayList<Familia> listado(String nombre) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                FamiliaContract.FamiliaEntry._ID,
                FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE,
        };

        // Filter results WHERE
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (nombre != null) {
            selection = selection.concat(FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE + " LIKE ?");
            selectionArgs.add(nombre + "%");
        }

        String[] S = { "" };
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT * FROM " + FamiliaContract.FamiliaEntry.TABLE_NAME, null);
        else
            c = db.query(
                FamiliaContract.FamiliaEntry.TABLE_NAME,  // The table to query
                projection,                                 // The columns to return
                selection,                                  // The columns for the WHERE clause
                selectionArgs.toArray(S),                   // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null                                        // The sort order
        );

        ArrayList<Familia> listFamilia = new ArrayList<Familia>();
        if (c.getCount() > 0) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                Familia familia = new Familia();
                familia.setId(c.getLong(c.getColumnIndex(FamiliaContract.FamiliaEntry._ID)));
                familia.setNombre(
                        c.getString(c.getColumnIndex(FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE))
                );
                listFamilia.add(familia);
                c.moveToNext();
            }
        }
        c.close();
        return listFamilia;
    }

    /**
     * Obtiene el cursor de una lista de familias filtrado por los campos de los parametros.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     * @param nombre El nombre deberá comenzar por el contenido de este parámetro para cumplir la
     *               condición de filtrado.  Si es null, no se filtrará por este campo.
     * @return un cursor de Familia
     *
     */
    public Cursor getCursor(String nombre) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                FamiliaContract.FamiliaEntry._ID,
                FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE,
        };

        // Filter results WHERE
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (nombre != null) {
            selection = selection.concat(FamiliaContract.FamiliaEntry.COLUMN_NAME_NOMBRE + " LIKE ?");
            selectionArgs.add(nombre + "%");
        }

        String[] S = { "" };
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT * FROM " + FamiliaContract.FamiliaEntry.TABLE_NAME, null);
        else
            c = db.query(
                    FamiliaContract.FamiliaEntry.TABLE_NAME,  // The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    selectionArgs.toArray(S),                   // The values for the WHERE clause
                    null,                                       // don't group the rows
                    null,                                       // don't filter by row groups
                    null                                        // The sort order
            );
        return c;
    }

}
