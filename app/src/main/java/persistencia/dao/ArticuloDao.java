package persistencia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import persistencia.jb.Articulo;

/**
 * Dao Articulo
 */

public class ArticuloDao {

    /**
     * Añadir un registro de la tabla Articulo.  Si la operación tiene éxito cambia el ID en articulo.
     * @param context El contexto de la aplicación.
     * @param articulo El java bean Articulo.
     * @return True o False según si la operación se realizó correctamente.
     */

    public Boolean insert(Context context, Articulo articulo) {

        DBHelper mDBHelper = new DBHelper(ArticuloContract.getInstance(), context);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE, articulo.getNombre());
        values.put(ArticuloContract.ArticuloEntry.COLUMN_NAME_DESCRIPCION, articulo.getDescripcion());
        values.put(ArticuloContract.ArticuloEntry.COLUMN_NAME_MEDIDA, articulo.getMedida().toString());
        values.put(ArticuloContract.ArticuloEntry.COLUMN_NAME_ID_FAMILIA, articulo.getId_familia());

        // Insert the new row, returning the primary key value of the new row
        // null indica no añadir la fila si values está vacío.
        long newRowId = db.insert(ArticuloContract.ArticuloEntry.TABLE_NAME, null, values);
        if (newRowId > -1) articulo.setId(newRowId);

        return newRowId > -1;
    }

    /**
     * Leer un registro de la tabla Articulo a partir del ID.
     * @param context El contexto de la aplicación.
     * @param id El identificador o código de la articulo.
     * @return El javabean articulo o null si no se encontró el ID.
     */
    public Articulo read(Context context, Long id) {
        DBHelper mDbHelper = new DBHelper(ArticuloContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ArticuloContract.ArticuloEntry._ID,
                ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE,
                ArticuloContract.ArticuloEntry.COLUMN_NAME_DESCRIPCION,
                ArticuloContract.ArticuloEntry.COLUMN_NAME_MEDIDA,
                ArticuloContract.ArticuloEntry.COLUMN_NAME_ID_FAMILIA
        };

        // Filter results WHERE
        String selection = ArticuloContract.ArticuloEntry._ID + " = ?";
        String[] selectionArgs = { id.toString() };

        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";


        Cursor c = db.query(
                ArticuloContract.ArticuloEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        Articulo f = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            f = new Articulo();
            f.setId(id);
            f.setNombre(
                    c.getString(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE))
            );
            f.setDescripcion(
                    c.getString(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_DESCRIPCION))
            );
            f.setMedida(
                    c.getString(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_MEDIDA)).charAt(0)
            );
            f.setId_familia(
                    c.getLong(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_ID_FAMILIA))
            );
        }
        c.close();

        return f;
    }

    /**
     * Leer un registro de la tabla Articulo a partir del ID.
     * @param context El contexto de la aplicación.
     * @param articulo El identificador o código de la articulo.
     * @return El javabean articulo o null si no se encontró el ID.
     */
    public Articulo read(Context context, String articulo) {
        DBHelper mDbHelper = new DBHelper(ArticuloContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ArticuloContract.ArticuloEntry._ID,
                ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE,
                ArticuloContract.ArticuloEntry.COLUMN_NAME_DESCRIPCION,
                ArticuloContract.ArticuloEntry.COLUMN_NAME_MEDIDA,
                ArticuloContract.ArticuloEntry.COLUMN_NAME_ID_FAMILIA
        };

        // Filter results WHERE
        String selection = ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE + " = ?";
        String[] selectionArgs = { articulo };

        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";


        Cursor c = db.query(
                ArticuloContract.ArticuloEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        Articulo f = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            f = new Articulo();
            f.setId(c.getLong(c.getColumnIndex(ArticuloContract.ArticuloEntry._ID)));
            f.setNombre(
                    c.getString(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE))
            );
            f.setDescripcion(
                    c.getString(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_DESCRIPCION))
            );
            f.setMedida(
                    c.getString(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_MEDIDA)).charAt(0)
            );
            f.setId_familia(
                    c.getLong(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_ID_FAMILIA))
            );
        }
        c.close();

        return f;
    }


    /**
     * Elimina un registro de la tabla Articulo a partir del ID.
     * @param context El contexto de la aplicación.
     * @param id El identificador o código de la articulo.
     * @return True o False según si la operación se realizó correctamente.
     */
    public Boolean delete(Context context, Long id) {

        DBHelper mDbHelper = new DBHelper(ArticuloContract.getInstance(), context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = ArticuloContract.ArticuloEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id.toString() };
        // Issue SQL statement.
        int count = db.delete(ArticuloContract.ArticuloEntry.TABLE_NAME, selection, selectionArgs);

        return count > 0;
    }

    /**
     * Modifica un registro de la tabla Articulo a partir del ID.
     * @param context El contexto de la aplicación.
     * @param articulo El java bean que contiene los datos a actualizar.
     */
    public Boolean update(Context context, Articulo articulo) {

        DBHelper mDbHelper = new DBHelper(ArticuloContract.getInstance(), context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE, articulo.getNombre());
        values.put(ArticuloContract.ArticuloEntry.COLUMN_NAME_DESCRIPCION, articulo.getDescripcion());
        values.put(ArticuloContract.ArticuloEntry.COLUMN_NAME_MEDIDA, articulo.getMedida().toString());
        values.put(ArticuloContract.ArticuloEntry.COLUMN_NAME_ID_FAMILIA, articulo.getId_familia());

        // Which row to update, based on the title
        String selection = ArticuloContract.ArticuloEntry._ID + " = ?";
        String[] selectionArgs = { articulo.getId().toString() };

        int count = db.update(
                ArticuloContract.ArticuloEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count > 0;
    }
}
