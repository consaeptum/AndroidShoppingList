package persistencia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import persistencia.jb.Linea;

/**
 * Dao Linea
 */

public class LineaDao {

    /**
     * DBHelper se instancia sólo una vez porque getWritableDatabase es costoso si
     * DBhelper está cerrado.
     * Deberá cerrarse en onDestroy():
     *  protected void onDestroy() {
     *      LineaDao.close();
     *      super.onDestroy();
     *  }
     */
    static DBHelper mDBHelper = null;

    public LineaDao(Context context) {
        if (mDBHelper == null) mDBHelper = new DBHelper(LineaContract.getInstance(), context);
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    /**
     * Añadir un registro de la tabla Linea.  Si la operación tiene éxito cambia el ID en linea.
     * @param linea El java bean Linea.
     * @return True o False según si la operación se realizó correctamente.
     */

    public Boolean insert(Linea linea) {

        // Gets the data repository in write mode
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LineaContract.LineaEntry.COLUMN_NAME_CANTIDAD, linea.getCantidad());
        values.put(LineaContract.LineaEntry.COLUMN_NAME_PVP, linea.getPvp());
        values.put(LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO, linea.getId_articulo());
        values.put(LineaContract.LineaEntry.COLUMN_NAME_ID_LISTA, linea.getId_lista());

        // Insert the new row, returning the primary key value of the new row
        // null indica no añadir la fila si values está vacío.
        long newRowId = db.insert(LineaContract.LineaEntry.TABLE_NAME, null, values);
        if (newRowId > -1) linea.setId(newRowId);

        return newRowId > -1;
    }

    /**
     * Leer un registro de la tabla Linea a partir del ID.
     * @param id El identificador o código de la linea.
     * @return El javabean linea o null si no se encontró el ID.
     */
    public Linea read(Long id) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

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
     * @param id El identificador o código de la linea.
     * @return True o False según si la operación se realizó correctamente.
     */
    public Boolean delete(Long id) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

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
     * @param linea El java bean que contiene los datos a actualizar.
     */
    public Boolean update(Linea linea) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

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

    /**
     * Obtiene una lista de Lineas filtrado por los campos de los parametros.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     * @param id_lista El código de una lista.  Si es 0 no se filtra por este campo.
     * @param id_articulo El código de un artículo.  Si es 0 no se filtra por este campo.
     * @return an ArrayList of Linea
     *
     */
    public ArrayList<Linea> listado(Long id_lista, Long id_articulo) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LineaContract.LineaEntry._ID,
                LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO,
                LineaContract.LineaEntry.COLUMN_NAME_ID_LISTA,
                LineaContract.LineaEntry.COLUMN_NAME_PVP,
                LineaContract.LineaEntry.COLUMN_NAME_CANTIDAD
        };

        // Filter results WHERE
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (id_articulo > 0) {
            selection = selection.concat(LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO + " =?");
            selectionArgs.add(id_articulo.toString());
        }
        if (id_lista > 0) {
            if (selection.length() > 0) selection = selection.concat(" AND ");
            selection = selection.concat(LineaContract.LineaEntry.COLUMN_NAME_ID_LISTA + " =?");
            selectionArgs.add(id_lista.toString());
        }

        String[] S = { "" };
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT * FROM " + LineaContract.LineaEntry.TABLE_NAME, null);
        else
            c = db.query(
                LineaContract.LineaEntry.TABLE_NAME,  // The table to query
                projection,                                 // The columns to return
                selection,                                  // The columns for the WHERE clause
                selectionArgs.toArray(S),                   // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null                                        // The sort order
        );

        ArrayList<Linea> listLinea = new ArrayList<Linea>();
        if (c.getCount() > 0) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                Linea linea = new Linea();
                linea.setId(c.getLong(c.getColumnIndex(LineaContract.LineaEntry._ID)));
                linea.setId_articulo(
                        c.getLong(c.getColumnIndex(LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO))
                );
                linea.setId_lista(
                        c.getLong(c.getColumnIndex(LineaContract.LineaEntry.COLUMN_NAME_ID_LISTA))
                );
                linea.setCantidad(
                        c.getFloat(c.getColumnIndex(LineaContract.LineaEntry.COLUMN_NAME_CANTIDAD))
                );
                linea.setPvp(
                        c.getFloat(c.getColumnIndex(LineaContract.LineaEntry.COLUMN_NAME_PVP))
                );
                listLinea.add(linea);
                c.moveToNext();
            }
        }
        c.close();
        return listLinea;
    }

    /**
     * Obtiene un cursor de una lista de Lineas filtrado por los campos de los parametros.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     * @param id_lista El código de una lista.  Si es 0 no se filtra por este campo.
     * @param id_articulo El código de un artículo.  Si es 0 no se filtra por este campo.
     * @return un cursor de  Linea
     *
     */
    public Cursor getCursor(Long id_lista, Long id_articulo) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LineaContract.LineaEntry._ID,
                LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO,
                LineaContract.LineaEntry.COLUMN_NAME_ID_LISTA,
                LineaContract.LineaEntry.COLUMN_NAME_PVP,
                LineaContract.LineaEntry.COLUMN_NAME_CANTIDAD
        };

        // Filter results WHERE
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (id_articulo > 0) {
            selection = selection.concat(LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO + " =?");
            selectionArgs.add(id_articulo.toString());
        }
        if (id_lista > 0) {
            if (selection.length() > 0) selection = selection.concat(" AND ");
            selection = selection.concat(LineaContract.LineaEntry.COLUMN_NAME_ID_LISTA + " =?");
            selectionArgs.add(id_lista.toString());
        }

        String[] S = { "" };
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT * FROM " + LineaContract.LineaEntry.TABLE_NAME, null);
        else
            c = db.query(
                    LineaContract.LineaEntry.TABLE_NAME,  // The table to query
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
