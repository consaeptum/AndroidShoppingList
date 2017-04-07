package persistencia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import persistencia.jb.SuperMercado;

/**
 * Dao SuperMercado
 */

public class SuperMercadoDao {

    /**
     * Añadir un registro de la tabla SuperMercado.  Si la operación tiene éxito cambia el ID en
     * superMercado.
     * @param context El contexto de la aplicación.
     * @param superMercado El java bean SuperMercado.
     * @return True o False según si la operación se realizó correctamente.
     */

    public Boolean insert(Context context, SuperMercado superMercado) {

        DBHelper mDBHelper = new DBHelper(SuperMercadoContract.getInstance(), context);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(SuperMercadoContract.SuperMercadoEntry.COLUMN_NAME_NOMBRE, superMercado.getNombre());

        // Insert the new row, returning the primary key value of the new row
        // null indica no añadir la fila si values está vacío.
        long newRowId = db.insert(SuperMercadoContract.SuperMercadoEntry.TABLE_NAME, null, values);
        if (newRowId > -1) superMercado.setId(newRowId);

        return newRowId > -1;
    }

    /**
     * Leer un registro de la tabla SuperMercado a partir del ID.
     * @param context El contexto de la aplicación.
     * @param id El identificador o código de la SuperMercado.
     * @return El javabean SuperMercado o null si no se encontró el ID.
     */
    public SuperMercado read(Context context, Long id) {
        DBHelper mDbHelper = new DBHelper(SuperMercadoContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
     * @param context El contexto de la aplicación.
     * @param superMercado El identificador del SuperMercado.
     * @return El javabean SuperMercado o null si no se encontró el ID.
     */
    public SuperMercado read(Context context, String superMercado) {
        DBHelper mDbHelper = new DBHelper(SuperMercadoContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
     * @param context El contexto de la aplicación.
     * @param id El identificador o código de la SuperMercado.
     * @return True o False según si la operación se realizó correctamente.
     */
    public Boolean delete(Context context, Long id) {

        DBHelper mDbHelper = new DBHelper(SuperMercadoContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
     * @param context El contexto de la aplicación.
     * @param SuperMercado El java bean que contiene los datos a actualizar.
     */
    public Boolean update(Context context, SuperMercado SuperMercado) {

        DBHelper mDbHelper = new DBHelper(SuperMercadoContract.getInstance(), context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
}
