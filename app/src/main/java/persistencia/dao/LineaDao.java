package persistencia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import persistencia.jb.Linea;

/**
 * Dao Linea
 */

public class LineaDao {

    /**
     * Añadir un registro de la tabla Linea.  Si la operación tiene éxito cambia el ID en linea.
     * @param context El contexto de la aplicación.
     * @param linea El java bean Linea.
     * @return True o False según si la operación se realizó correctamente.
     */

    public Boolean insert(Context context, Linea linea) {

        DBHelper mDBHelper = new DBHelper(LineaContract.getInstance(), context);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LineaContract.LineaEntry.COLUMN_NAME_CANTIDAD, linea.getCantidad());
        values.put(LineaContract.LineaEntry.COLUMN_NAME_PVP, linea.getPvp());
        values.put(LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO, linea.getId_articulo());
        values.put(LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO, linea.getId_lista());

        // Insert the new row, returning the primary key value of the new row
        // null indica no añadir la fila si values está vacío.
        long newRowId = db.insert(LineaContract.LineaEntry.TABLE_NAME, null, values);
        if (newRowId > -1) linea.setId(newRowId);

        return newRowId > -1;
    }

    /**
     * Leer un registro de la tabla Linea a partir del ID.
     * @param context El contexto de la aplicación.
     * @param id El identificador o código de la linea.
     * @return El javabean linea o null si no se encontró el ID.
     */
    public Linea read(Context context, Long id) {
        DBHelper mDbHelper = new DBHelper(LineaContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LineaContract.LineaEntry._ID,
                LineaContract.LineaEntry.COLUMN_NAME_CANTIDAD,
                LineaContract.LineaEntry.COLUMN_NAME_PVP,
                LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO,
                LineaContract.LineaEntry.COLUMN_NAME_ID_LISTA
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = LineaContract.LineaEntry._ID + " = ?";
        String[] selectionArgs = { id.toString() };

        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";


        Cursor c = db.query(
                LineaContract.LineaEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        Linea f = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            f = new Linea();
            f.setId(id);
            f.setCantidad(
                    c.getFloat(c.getColumnIndex(LineaContract.LineaEntry.COLUMN_NAME_CANTIDAD))
            );
            f.setPvp(
                    c.getFloat(c.getColumnIndex(LineaContract.LineaEntry.COLUMN_NAME_PVP))
            );
            f.setId_articulo(
                    c.getLong(c.getColumnIndex(LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO))
            );
            f.setId_lista(
                    c.getLong(c.getColumnIndex(LineaContract.LineaEntry.COLUMN_NAME_ID_LISTA))
            );
        }
        c.close();

        return f;
    }

    /**
     * Elimina un registro de la tabla Linea a partir del ID.
     * @param context El contexto de la aplicación.
     * @param id El identificador o código de la linea.
     * @return True o False según si la operación se realizó correctamente.
     */
    public Boolean delete(Context context, Long id) {

        DBHelper mDbHelper = new DBHelper(LineaContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = LineaContract.LineaEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id.toString() };
        // Issue SQL statement.
        int count = db.delete(LineaContract.LineaEntry.TABLE_NAME, selection, selectionArgs);

        return count > 0;
    }

    /**
     * Modifica un registro de la tabla Linea a partir del ID.
     * @param context El contexto de la aplicación.
     * @param linea El java bean que contiene los datos a actualizar.
     */
    public Boolean update(Context context, Linea linea) {

        DBHelper mDbHelper = new DBHelper(LineaContract.getInstance(), context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(LineaContract.LineaEntry.COLUMN_NAME_CANTIDAD, linea.getCantidad());
        values.put(LineaContract.LineaEntry.COLUMN_NAME_PVP, linea.getPvp());
        values.put(LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO, linea.getId_articulo());
        values.put(LineaContract.LineaEntry.COLUMN_NAME_ID_LISTA, linea.getId_lista());

        // Which row to update, based on the title
        String selection = LineaContract.LineaEntry._ID + " = ?";
        String[] selectionArgs = { linea.getId().toString() };

        int count = db.update(
                LineaContract.LineaEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count > 0;
    }
}
