package persistencia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import persistencia.jb.Familia;

/**
 * Dao Familia
 */

public class FamiliaDao {

    /**
     * Añadir un registro de la tabla Familia.  Si la operación tiene éxito cambia el ID en familia.
     * @param context El contexto de la aplicación.
     * @param familia El java bean Familia.
     * @return True o False según si la operación se realizó correctamente.
     */

    public Boolean insert(Context context, Familia familia) {

        DBHelper mDBHelper = new DBHelper(FamiliaContract.getInstance(), context);

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
     * @param context El contexto de la aplicación.
     * @param id El identificador o código de la familia.
     * @return El javabean familia o null si no se encontró el ID.
     */
    public Familia read(Context context, Long id) {
        DBHelper mDbHelper = new DBHelper(FamiliaContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
     * @param context El contexto de la aplicación.
     * @param nombre El nombre que identifica la familia.
     * @return El javabean familia o null si no se encontró el ID.
     */
    public Familia read(Context context, String nombre) {
        DBHelper mDbHelper = new DBHelper(FamiliaContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
     * @param context El contexto de la aplicación.
     * @param id El identificador o código de la familia.
     * @return True o False según si la operación se realizó correctamente.
     */
    public Boolean delete(Context context, Long id) {

        DBHelper mDbHelper = new DBHelper(FamiliaContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
     * @param context El contexto de la aplicación.
     * @param familia El java bean que contiene los datos a actualizar.
     */
    public Boolean update(Context context, Familia familia) {

        DBHelper mDbHelper = new DBHelper(FamiliaContract.getInstance(), context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
}
