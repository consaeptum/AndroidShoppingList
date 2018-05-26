package persistencia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import persistencia.jb.Lista;

/**
 * Dao Lista
 */

public class ListaDao {

    /**
     * DBHelper se instancia sólo una vez porque getWritableDatabase es costoso si
     * DBhelper está cerrado.
     * Deberá cerrarse en onDestroy():
     *  protected void onDestroy() {
     *      ListaDao.close();
     *      super.onDestroy();
     *  }
     */
    static DBHelper mDBHelper = null;

    public ListaDao(Context context) {
        if (mDBHelper == null) mDBHelper = new DBHelper(ListaContract.getInstance(), context);
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    /**
     * Añadir un registro de la tabla Lista.    Si la operación tiene éxito cambia el ID en lista.
     * @param lista El java bean Lista.
     * @return True o False según si la operación se realizó correctamente.
     */

    public Boolean insert(Lista lista) {

        // Gets the data repository in write mode
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ListaContract.ListaEntry.COLUMN_NAME_FECHA, lista.getFechaFormatYMD());
        values.put(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER, lista.getId_super());
        values.put(ListaContract.ListaEntry.COLUMN_NAME_IMPORTE, lista.getImporte());

        // Insert the new row, returning the primary key value of the new row
        // null indica no añadir la fila si values está vacío.
        long newRowId = -1;
        try {
            newRowId = db.insertOrThrow(ListaContract.ListaEntry.TABLE_NAME, null, values);
        } catch (Exception sqlce) {
            if (sqlce instanceof SQLiteConstraintException) throw sqlce;
        }
        if (newRowId > -1) lista.setId(newRowId);

        return newRowId > -1;
    }

    /**
     * Leer un registro de la tabla Lista a partir del ID.
     * @param id El identificador o código de la lista.
     * @return El javabean lista o null si no se encontró el ID.
     */
    public Lista read(Long id) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ListaContract.ListaEntry._ID,
                ListaContract.ListaEntry.COLUMN_NAME_FECHA,
                ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER,
                ListaContract.ListaEntry.COLUMN_NAME_IMPORTE
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
            f.setFechaFormatYMD(
                    c.getString(c.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_FECHA))
            );
            f.setId_super(
                    c.getLong(c.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER))
            );
            f.setImporte(
                    c.getFloat(c.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_IMPORTE))
            );
        }
        c.close();

        return f;
    }

    /**
     * Elimina un registro de la tabla Lista a partir del ID.
     * @param id El identificador o código de la lista.
     * @return True o False según si la operación se realizó correctamente.
     */
    public Boolean delete(Long id) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

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
     * @param lista El java bean que contiene los datos a actualizar.
     */
    public Boolean update(Lista lista) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(ListaContract.ListaEntry.COLUMN_NAME_FECHA, lista.getFechaFormatYMD());
        values.put(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER, lista.getId_super());
        values.put(ListaContract.ListaEntry.COLUMN_NAME_IMPORTE, lista.getImporte());

        // Which row to update, based on the title
        String selection = ListaContract.ListaEntry._ID + " = ?";
        String[] selectionArgs = { lista.getId().toString() };

        int count;
        try {
            count = db.update(
                    ListaContract.ListaEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        } catch (SQLiteException sqle) {
            count = 0;
        }
        return count > 0;
    }

    /**
     * Obtiene una lista de artículos filtrado por los campos de los parametros.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     * El orden será siempre DESC.
     * @param fecha La fecha por la cual filtrar el resultado. Si es null, no se filtrará por este campo.
     *              Debe estar en formato "AAAA-MM-dd".
     * @param fechaFin La fecha tope de filtrado.  Si no es null, se filtrarán los resultados entre
     *               fecha y fecha_.  Debe estar en formato "AAAA-MM-dd".
     * @param id_super El código de un supermercado.  Si es null no se filtra por este campo.
     * @param sortBy null si no debe estar ordenado y el nombre del campo por el que deba estar
     *               ordenado en caso contrario.
     * @return an ArrayList of Lista
     *
     */
    public ArrayList<Lista> listado(String fecha, String fechaFin, Long id_super, String sortBy) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ListaContract.ListaEntry._ID,
                ListaContract.ListaEntry.COLUMN_NAME_FECHA,
                ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER,
                ListaContract.ListaEntry.COLUMN_NAME_IMPORTE
        };

        // Filter results WHERE
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (fecha != null) {
            if (fechaFin != null) {
                // filtro por rango
                selection = selection.concat(ListaContract.ListaEntry.COLUMN_NAME_FECHA + " >= ?");
                selectionArgs.add(fecha);
                selection = selection.concat(" AND ");
                selection = selection.concat(ListaContract.ListaEntry.COLUMN_NAME_FECHA + " <= ?");
                selectionArgs.add(fechaFin);
            } else {
                // filtro por una fecha exacta
                selection = selection.concat(ListaContract.ListaEntry.COLUMN_NAME_FECHA + " = ?");
                selectionArgs.add(fecha);
            }

        }
        if (id_super != null) {
            if (selection.length() > 0) selection = selection.concat(" AND ");
            selection = selection.concat(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER + " =?");
            selectionArgs.add(id_super.toString());
        }

        String[] S = { "" };
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT * FROM " + ListaContract.ListaEntry.TABLE_NAME + ((sortBy != null)? (" ORDER BY " + sortBy):"") +" DESC" , null);
        else
            c = db.query(
                ListaContract.ListaEntry.TABLE_NAME,  // The table to query
                projection,                                 // The columns to return
                selection,                                  // The columns for the WHERE clause
                selectionArgs.toArray(S),                   // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                sortBy + " DESC"                                      // The sort order
        );

        ArrayList<Lista> listLista = new ArrayList<Lista>();
        if (c.getCount() > 0) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                Lista lista = new Lista();
                lista.setId(c.getLong(c.getColumnIndex(ListaContract.ListaEntry._ID)));
                lista.setFechaFormatYMD(
                        c.getString(c.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_FECHA))
                );
                lista.setId_super(
                        c.getLong(c.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER))
                );
                lista.setImporte(
                        c.getFloat(c.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_IMPORTE))
                        );
                listLista.add(lista);
                c.moveToNext();
            }
        }
        c.close();
        return listLista;
    }

    /**
     * Obtiene un cursor de una lista de artículos filtrado por los campos de los parametros.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     * El orden será siempre DESC.
     * @param fecha La fecha por la cual filtrar el resultado. Si es null, no se filtrará por este campo.
     *              Debe estar en formato "AAAA-MM-dd".
     * @param fechaFin La fecha tope de filtrado.  Si no es null, se filtrarán los resultados entre
     *               fecha y fecha_.  Debe estar en formato "AAAA-MM-dd".
     * @param id_super El código de un supermercado.  Si es null no se filtra por este campo.
     * @param sortBy null si no debe estar ordenado y el nombre del campo por el que deba estar
     *               ordenado en caso contrario.
     * @return un cursor de Lista
     *
     */
    public Cursor getCursor(String fecha, String fechaFin, Long id_super, String sortBy) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ListaContract.ListaEntry._ID,
                ListaContract.ListaEntry.COLUMN_NAME_FECHA,
                ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER,
                ListaContract.ListaEntry.COLUMN_NAME_IMPORTE
        };

        // Filter results WHERE
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (fecha != null) {
            if (fechaFin != null) {
                // filtro por rango
                selection = selection.concat(ListaContract.ListaEntry.COLUMN_NAME_FECHA + " >= ?");
                selectionArgs.add(fecha);
                selection = selection.concat(" AND ");
                selection = selection.concat(ListaContract.ListaEntry.COLUMN_NAME_FECHA + " <= ?");
                selectionArgs.add(fechaFin);
            } else {
                // filtro por una fecha exacta
                selection = selection.concat(ListaContract.ListaEntry.COLUMN_NAME_FECHA + " = ?");
                selectionArgs.add(fecha);
            }

        }
        if (id_super != null) {
            if (selection.length() > 0) selection = selection.concat(" AND ");
            selection = selection.concat(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER + " =?");
            selectionArgs.add(id_super.toString());
        }

        String[] S = { "" };
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT * FROM " + ListaContract.ListaEntry.TABLE_NAME + ((sortBy != null)? (" ORDER BY " + sortBy):"") + " DESC", null);
        else
            c = db.query(
                    ListaContract.ListaEntry.TABLE_NAME,  // The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    selectionArgs.toArray(S),                   // The values for the WHERE clause
                    null,                                       // don't group the rows
                    null,                                       // don't filter by row groups
                    sortBy + " DESC"                                      // The sort order
            );

        return c;
    }

}
