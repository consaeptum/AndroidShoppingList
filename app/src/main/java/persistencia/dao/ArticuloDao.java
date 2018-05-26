package persistencia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import persistencia.jb.Articulo;

/**
 * Dao Articulo
 */

public class ArticuloDao {

    /**
     * DBHelper se instancia sólo una vez porque getWritableDatabase es costoso si
     * DBhelper está cerrado.
     * Deberá cerrarse en onDestroy():
     *  protected void onDestroy() {
     *      ArticuloDao.close();
     *      super.onDestroy();
     *  }
     */
    static DBHelper mDBHelper = null;

    public ArticuloDao(Context context) {
        if (mDBHelper == null) mDBHelper = new DBHelper(ArticuloContract.getInstance(), context);
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    /**
     * Añadir un registro de la tabla Articulo.  Si la operación tiene éxito cambia el ID en articulo.
     * @param articulo El java bean Articulo.
     * @return True o False según si la operación se realizó correctamente.
     */

    public Boolean insert(Articulo articulo) {

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
        long newRowId = -1;
        try {
            newRowId = db.insertOrThrow(ArticuloContract.ArticuloEntry.TABLE_NAME, null, values);
        } catch (Exception sqlce) {
            if (sqlce instanceof SQLiteConstraintException) throw sqlce;
        }
        if (newRowId > -1) articulo.setId(newRowId);

        return newRowId > -1;
    }

    /**
     * Leer un registro de la tabla Articulo a partir del ID.
     * @param id El identificador o código de la articulo.
     * @return El javabean articulo o null si no se encontró el ID.
     */
    public Articulo read(Long id) {

        if (id == null) return null;

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

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
     * @param articulo El identificador o código de la articulo.
     * @return El javabean articulo o null si no se encontró el ID.
     */
    public Articulo read(String articulo) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

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
     * @param id El identificador o código de la articulo.
     * @return True o False según si la operación se realizó correctamente.
     */
    public Boolean delete(Long id) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

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
     * @param articulo El java bean que contiene los datos a actualizar.
     */
    public Boolean update(Articulo articulo) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

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

    /**
     * Obtiene una lista de artículos filtrado por los campos de los parametros.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     * @param nombre El nombre deberá comenzar por el contenido de este parámetro para cumplir la
     *               condición de filtrado.  Si es null, no se filtrará por este campo.
     * @param descrip Una parte de la descripción que deberá contener los artículos seleccionados
     *                en cualquier parte del texto. Si es null, no se filtrará por este campo.
     * @param medida Un carácter que especifica la media (U, L, K).  Si es null o no corresponde
     *               a U L K, no se filtrará por este campo.
     * @param id_familia El código de una familia.  Si es null no se filtra por este campo.
     * @param sortBy null si no debe estar ordenado y el nombre del campo por el que deba estar
     *               ordenado en caso contrario.
     * @return an ArrayList of Articulo
     *
     */
    public ArrayList<Articulo> listado(String nombre, String descrip, Character medida, Long id_familia, String sortBy) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

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
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (nombre != null) {
            selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE + " LIKE ?");
            selectionArgs.add(nombre + "%");
        }
        if (descrip != null) {
            if (selection.length() > 0) selection = selection.concat(" AND ");
            selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_DESCRIPCION + " LIKE ?");
            selectionArgs.add("%" + descrip + "%");
        }
        if (medida != null) {
            if ("ULK".indexOf(Character.toUpperCase(medida)) >= 0) {
                if (selection.length() > 0) selection = selection.concat(" AND ");
                selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_MEDIDA + " =?");
                selectionArgs.add(medida.toString().toUpperCase());
            }
        }
        if (id_familia != null) {
            if (selection.length() > 0) selection = selection.concat(" AND ");
            selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_ID_FAMILIA + " =?");
            selectionArgs.add(id_familia.toString());
        }

        String[] S = { "" };
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT * FROM " + ArticuloContract.ArticuloEntry.TABLE_NAME + ((sortBy != null)? (" ORDER BY " + sortBy):"") , null);
        else
            c = db.query(
                ArticuloContract.ArticuloEntry.TABLE_NAME,  // The table to query
                projection,                                 // The columns to return
                selection,                                  // The columns for the WHERE clause
                selectionArgs.toArray(S),                   // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                sortBy                                      // The sort order
        );

        ArrayList<Articulo> listArticulo = new ArrayList<Articulo>();
        if (c.getCount() > 0) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                Articulo articulo = new Articulo();
                articulo.setId(c.getLong(c.getColumnIndex(ArticuloContract.ArticuloEntry._ID)));
                articulo.setNombre(
                        c.getString(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE))
                );
                articulo.setDescripcion(
                        c.getString(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_DESCRIPCION))
                );
                articulo.setMedida(
                        c.getString(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_MEDIDA)).charAt(0)
                );
                articulo.setId_familia(
                        c.getLong(c.getColumnIndex(ArticuloContract.ArticuloEntry.COLUMN_NAME_ID_FAMILIA))
                );
                listArticulo.add(articulo);
                c.moveToNext();
            }
        }
        c.close();
        return listArticulo;
    }

    /**
     * Obtiene un cusor de una lista de artículos filtrado por los campos de los parametros.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     * @param nombre El nombre deberá comenzar por el contenido de este parámetro para cumplir la
     *               condición de filtrado.  Si es null, no se filtrará por este campo.
     * @param descrip Una parte de la descripción que deberá contener los artículos seleccionados
     *                en cualquier parte del texto. Si es null, no se filtrará por este campo.
     * @param medida Un carácter que especifica la media (U, L, K).  Si es null o no corresponde
     *               a U L K, no se filtrará por este campo.
     * @param id_familia El código de una familia.  Si es null no se filtra por este campo.
     * @param sortBy null si no debe estar ordenado y el nombre del campo por el que deba estar
     *               ordenado en caso contrario.
     * @return un cursor de Articulo
     *
     */
    public Cursor getCursor(String nombre, String descrip, Character medida, Long id_familia, String sortBy) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

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
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (nombre != null) {
            selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE + " LIKE ?");
            selectionArgs.add(nombre + "%");
        }
        if (descrip != null) {
            if (selection.length() > 0) selection = selection.concat(" AND ");
            selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_DESCRIPCION + " LIKE ?");
            selectionArgs.add("%" + descrip + "%");
        }

        if (medida != null) {
            if ("ULK".indexOf(Character.toUpperCase(medida)) >= 0) {
                if (selection.length() > 0) selection = selection.concat(" AND ");
                selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_MEDIDA + " =?");
                selectionArgs.add(medida.toString().toUpperCase());
            }
        }
        if (id_familia != null) {
            if (selection.length() > 0) selection = selection.concat(" AND ");
            selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_ID_FAMILIA + " =?");
            selectionArgs.add(id_familia.toString());
        }

        // debido a utilizar spinners, ponemos siempre el artículo con ID 0 como "   artículo   "
        // así que lo omitimos para los listados, solo servirá para los spinners.
        if (selection.length() > 0) selection = selection.concat(" AND ");
        selection = selection.concat(ArticuloContract.ArticuloEntry._ID + " >?");
        selectionArgs.add("0");

        String[] S = { "" };
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT * FROM " + ArticuloContract.ArticuloEntry.TABLE_NAME + " WHERE " +
                    ArticuloContract.ArticuloEntry._ID + " > 0 " + ((sortBy != null)? (" ORDER BY " + sortBy):"") , null);
        else
            c = db.query(
                    ArticuloContract.ArticuloEntry.TABLE_NAME,  // The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    selectionArgs.toArray(S),                   // The values for the WHERE clause
                    null,                                       // don't group the rows
                    null,                                       // don't filter by row groups
                    sortBy                                      // The sort order
            );

        return c;
    }

    /**
     * Obtiene un cusor de una lista de artículos filtrado por los campos de los parametros, pero
     * mostrando únicamente el nombre del artículo.  Esta específicamente pensado para mostrar
     * la lista de artículos cuando hay que seleccionar uno de ellos.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     * @param nombre El nombre deberá comenzar por el contenido de este parámetro para cumplir la
     *               condición de filtrado.  Si es null, no se filtrará por este campo.
     * @param descrip Una parte de la descripción que deberá contener los artículos seleccionados
     *                en cualquier parte del texto. Si es null, no se filtrará por este campo.
     * @param medida Un carácter que especifica la media (U, L, K).  Si es null o no corresponde
     *               a U L K, no se filtrará por este campo.
     * @param id_familia El código de una familia.  Si es null no se filtra por este campo.
     * @return un cursor de Articulo
     *
     */
    public Cursor getCursorReducido(String nombre, String descrip, Character medida, Long id_familia) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ArticuloContract.ArticuloEntry._ID,
                ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE,
        };

        // Filter results WHERE
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (nombre != null) {
            selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE + " LIKE ?");
            selectionArgs.add(nombre + "%");
        }
        if (descrip != null) {
            if (selection.length() > 0) selection = selection.concat(" AND ");
            selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_DESCRIPCION + " LIKE ?");
            selectionArgs.add("%" + descrip + "%");
        }

        if (medida != null) {
            if ("ULK".indexOf(Character.toUpperCase(medida)) >= 0) {
                if (selection.length() > 0) selection = selection.concat(" AND ");
                selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_MEDIDA + " =?");
                selectionArgs.add(medida.toString().toUpperCase());
            }
        }
        if (id_familia != null) {
            if (selection.length() > 0) selection = selection.concat(" AND ");
            selection = selection.concat(ArticuloContract.ArticuloEntry.COLUMN_NAME_ID_FAMILIA + " =?");
            selectionArgs.add(id_familia.toString());
        }

        String[] S = { "" };
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT " + ArticuloContract.ArticuloEntry._ID +
                    "," +ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE +
                    " FROM " + ArticuloContract.ArticuloEntry.TABLE_NAME +
                    " ORDER BY " + ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE , null);
        else
            c = db.query(
                    ArticuloContract.ArticuloEntry.TABLE_NAME,  // The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    selectionArgs.toArray(S),                   // The values for the WHERE clause
                    null,                                       // don't group the rows
                    null,                                       // don't filter by row groups
                    ArticuloContract.ArticuloEntry.COLUMN_NAME_NOMBRE   // The sort order
            );

        return c;
    }

}
