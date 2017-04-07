package persistencia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import persistencia.jb.Lista;

/**
 * Dao Lista
 */

public class ListaDao {

    /**
     * Añadir un registro de la tabla Lista.    Si la operación tiene éxito cambia el ID en lista.
     * @param context El contexto de la aplicación.
     * @param lista El java bean Lista.
     * @return True o False según si la operación se realizó correctamente.
     */

    public Boolean insert(Context context, Lista lista) {

        DBHelper mDBHelper = new DBHelper(ListaContract.getInstance(), context);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ListaContract.ListaEntry.COLUMN_NAME_FECHA, lista.getFechaFormat());
        values.put(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER, lista.getId_super());

        // Insert the new row, returning the primary key value of the new row
        // null indica no añadir la fila si values está vacío.
        long newRowId = db.insert(ListaContract.ListaEntry.TABLE_NAME, null, values);
        if (newRowId > -1) lista.setId(newRowId);

        return newRowId > -1;
    }

    /**
     * Leer un registro de la tabla Lista a partir del ID.
     * @param context El contexto de la aplicación.
     * @param id El identificador o código de la lista.
     * @return El javabean lista o null si no se encontró el ID.
     */
    public Lista read(Context context, Long id) {
        DBHelper mDbHelper = new DBHelper(ListaContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ListaContract.ListaEntry._ID,
                ListaContract.ListaEntry.COLUMN_NAME_FECHA,
                ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = ListaContract.ListaEntry._ID + " = ?";
        String[] selectionArgs = { id.toString() };

        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";


        Cursor c = db.query(
                ListaContract.ListaEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        Lista f = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            f = new Lista();
            f.setId(id);
            f.setFechaFormat(
                    c.getString(c.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_FECHA))
            );
            f.setId_super(
                    c.getLong(c.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER))
            );
        }
        c.close();

        return f;
    }

    /**
     * Elimina un registro de la tabla Lista a partir del ID.
     * @param context El contexto de la aplicación.
     * @param id El identificador o código de la lista.
     * @return True o False según si la operación se realizó correctamente.
     */
    public Boolean delete(Context context, Long id) {

        DBHelper mDbHelper = new DBHelper(ListaContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = ListaContract.ListaEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id.toString() };
        // Issue SQL statement.
        int count = db.delete(ListaContract.ListaEntry.TABLE_NAME, selection, selectionArgs);

        return count > 0;
    }

    /**
     * Modifica un registro de la tabla Lista a partir del ID.
     * @param context El contexto de la aplicación.
     * @param lista El java bean que contiene los datos a actualizar.
     */
    public Boolean update(Context context, Lista lista) {

        DBHelper mDbHelper = new DBHelper(ListaContract.getInstance(), context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(ListaContract.ListaEntry.COLUMN_NAME_FECHA, lista.getFechaFormat());
        values.put(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER, lista.getId_super());

        // Which row to update, based on the title
        String selection = ListaContract.ListaEntry._ID + " = ?";
        String[] selectionArgs = { lista.getId().toString() };

        int count = db.update(
                ListaContract.ListaEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count > 0;
    }
}
