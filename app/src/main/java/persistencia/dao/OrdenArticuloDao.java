package persistencia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import persistencia.jb.Linea;
import persistencia.jb.OrdenArticulo;

/**
 * Dao OrdenArticulo
 */

public class OrdenArticuloDao {

    /**
     * DBHelper se instancia sólo una vez porque getWritableDatabase es costoso si
     * DBhelper está cerrado.
     * Deberá cerrarse en onDestroy():
     * protected void onDestroy() {
     * OrdenArticuloDao.close();
     * super.onDestroy();
     * }
     */
    static DBHelper mDBHelper = null;

    public OrdenArticuloDao(Context context) {
        if (mDBHelper == null)
            mDBHelper = new DBHelper(OrdenArticuloContract.getInstance(), context);
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    /**
     * Añadir un registro de la tabla OrdenArticulo.  Si la operación tiene éxito cambia el ID en ordenArticulo.
     *
     * @param ordenArticulo El java bean OrdenArticulo.
     * @return True o False según si la operación se realizó correctamente.
     */

    public Boolean insert(OrdenArticulo ordenArticulo) {

        // Gets the data repository in write mode
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER, ordenArticulo.getId_super());
        values.put(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO, ordenArticulo.getId_articulo());
        values.put(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN, ordenArticulo.getOrden());

        // Insert the new row, returning the primary key value of the new row
        // null indica no añadir la fila si values está vacío.
        long newRowId = -1;
        try {
            newRowId = db.insertOrThrow(OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME, null, values);
        } catch (Exception sqlce) {
            if (sqlce instanceof SQLiteConstraintException) throw sqlce;
        }
        if (newRowId > -1) ordenArticulo.setId(newRowId);

        return newRowId > -1;
    }

    /**
     * Leer un registro de la tabla OrdenArticulo a partir del ID.
     *
     * @param id El identificador o código de la ordenArticulo.
     * @return El javabean ordenArticulo o null si no se encontró el ID.
     */
    public OrdenArticulo read(Long id) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                OrdenArticuloContract.OrdenArticuloEntry._ID,
                OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER,
                OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO,
                OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = OrdenArticuloContract.OrdenArticuloEntry._ID + " = ?";
        String[] selectionArgs = {id.toString()};

        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";


        Cursor c = db.query(
                OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        OrdenArticulo f = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            f = new OrdenArticulo();
            f.setId(id);
            f.setId_super(
                    c.getLong(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER))
            );
            f.setId_articulo(
                    c.getLong(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO))
            );
            f.setOrden(
                    c.getInt(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN))
            );
        }
        c.close();

        return f;
    }

    /**
     * Leer un registro de la tabla OrdenArticulo a partir del super y el articulo.
     *
     * @param id_super    El identificador del super.
     * @param id_articulo El identificador del articulo.
     * @return El javabean ordenArticulo o null si no se encontró el ID.
     */
    public OrdenArticulo read(Long id_super, Long id_articulo) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                OrdenArticuloContract.OrdenArticuloEntry._ID,
                OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER,
                OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO,
                OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER + " = ? "
                + "AND " + OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO + " = ? ";
        String[] selectionArgs = {id_super.toString(), id_articulo.toString()};

        Cursor c = db.query(
                OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        OrdenArticulo f = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            f = new OrdenArticulo();
            f.setId(c.getLong(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry._ID)));
            f.setId_super(
                    c.getLong(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER))
            );
            f.setId_articulo(
                    c.getLong(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO))
            );
            f.setOrden(
                    c.getInt(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN))
            );
        }
        c.close();

        return f;
    }

    /**
     * Elimina un registro de la tabla OrdenArticulo a partir del ID.
     *
     * @param id El identificador o código de la ordenArticulo.
     * @return True o False según si la operación se realizó correctamente.
     */
    public Boolean delete(Long id) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = OrdenArticuloContract.OrdenArticuloEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {id.toString()};
        // Issue SQL statement.
        int count = db.delete(OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME, selection, selectionArgs);

        return count > 0;
    }

    /**
     * Modifica un registro de la tabla OrdenArticulo a partir del ID.
     *
     * @param ordenArticulo El java bean que contiene los datos a actualizar.
     */
    public Boolean update(OrdenArticulo ordenArticulo) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER, ordenArticulo.getId_super());
        values.put(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO, ordenArticulo.getId_articulo());
        values.put(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN, ordenArticulo.getOrden());

        // Which row to update, based on the title
        String selection = OrdenArticuloContract.OrdenArticuloEntry._ID + " = ?";
        String[] selectionArgs = {ordenArticulo.getId().toString()};

        int count = db.update(
                OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count > 0;
    }

    /**
     * Obtiene un mapa de (id_articulo, OrdenArticulo) filtrado por el supermercado indicado.
     * id_super ha de ser un número >0 si no devuelve map vacío.
     *
     * @param id_super El código de un Supermercado.  Si es null no se filtra por este campo.
     * @return Map<id_articulo, articulo> ordenado según 'orden'.
     */
    public Map<Long, OrdenArticulo> listado(Long id_super) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                OrdenArticuloContract.OrdenArticuloEntry._ID,
                OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER,
                OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO,
                OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN
        };

        // Filter results WHERE
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (id_super != null) {
            selection = selection.concat(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER + " =?");
            selectionArgs.add(id_super.toString());
        } else {
            return new HashMap<Long, OrdenArticulo>();
        }

        String[] S = {""};
        Cursor c;
        if (selection.length() == 0)
            c = db.rawQuery("SELECT * FROM " + OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME, null);
        else
            c = db.query(
                    OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME,  // The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    selectionArgs.toArray(S),                   // The values for the WHERE clause
                    null,                                       // don't group the rows
                    null,                                       // don't filter by row groups
                    null                                        // The sort order
            );

        Map<Long, OrdenArticulo> listOrdenArticulo = new HashMap<Long, OrdenArticulo>();
        if (c.getCount() > 0) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                OrdenArticulo ordenArticulo = new OrdenArticulo();
                ordenArticulo.setId(c.getLong(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry._ID)));
                ordenArticulo.setId_articulo(
                        c.getLong(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO))
                );
                ordenArticulo.setId_super(
                        c.getLong(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER))
                );
                ordenArticulo.setOrden(
                        c.getInt(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN))
                );
                listOrdenArticulo.put(ordenArticulo.getId_articulo(), ordenArticulo);
                c.moveToNext();
            }
        }
        c.close();
        return listOrdenArticulo;
    }

    /**
     * Obtiene una lista de OrdenArticulos filtrado por el supermercado indicado y en orden según
     * el atributo 'orden'.
     * Si todos los parametros son null o 0, el resultado es el listado de todos los registros.
     *
     * @param lineas   Una lista con las líneas que tenemos que poner en orden.
     * @param ordenado Un boolean que indica si queremos el resultado ordenado o no.
     * @return un ArrayList de líneas en el orden correcto.
     */
    public ArrayList<Linea> getLineasEnOrden(ArrayList<Linea> lineas, Long id_super, Boolean ordenado) {
        if (ordenado) {
            Map<Long, OrdenArticulo> loa = listado(id_super);

            int sinOrden = lineas.size();
            // ponemos en lio cada linea con su número de orden para el super id_super.
            for (Linea l : lineas) {

                // si es una linea vacia ponemos orden 0.
                if (loa.get(l.getId_articulo()) != null) {
                    l.setOrden(loa.get(l.getId_articulo()).getOrden());
                } else {
                    l.setOrden(sinOrden--);
                }
            }

            // Ordenamos lio y devolvermos la lista de lineas ya ordenada.
            Collections.sort(lineas, new Comparator<Linea>() {
                @Override
                public int compare(Linea l1, Linea l2) {
                    return l1.getOrden().compareTo(l2.getOrden());
                }
            });

        } else {
            if (lineas.size() > 1) {
                ArrayList<Linea> lista = new ArrayList<Linea>(lineas);
                Collections.reverse(lista);
                return lista;
            }

        }
        return lineas;
    }

    /**
     * Recibe una lista de Lineas de artículos que no se han marcado como comprados y están en
     * orden habitual antes que el artículo determinado.  Retornará el OrdenArtículo que
     * aparece en lineas y esté en OrdenArtículo antes que los demás.
     *
     * @param lineas Una lista con las líneas np marcadas.
     * @param oa     El objeto OrdenArticulo a posicionar.
     * @return el OrdenArticulo del artículo cuya posición debe ocupar el artículo actual.  Devuelve
     * null si ningún artículo cumple la condición.
     */
    public OrdenArticulo posicionAOcupar(ArrayList<Linea> lineas, OrdenArticulo oa) {

        String listart = "";
        for (Linea l : lineas) {
            listart += (((listart.isEmpty()) ? "" : " , ") + l.getId_articulo().toString());
        }

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor c;
        c = db.rawQuery(
                "SELECT * FROM " + OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME +
                        " WHERE " + OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER + " = " + oa.getId_super() +
                        " AND " + OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN + " < " + oa.getOrden() +
                        " AND " + OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO + " IN (" + listart + ")" +
                        " ORDER BY " + OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN
                , null);

        OrdenArticulo oart = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            oart = new OrdenArticulo();
            oart.setId(c.getLong(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry._ID)));
            oart.setId_super(
                    c.getLong(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER))
            );
            oart.setId_articulo(
                    c.getLong(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO))
            );
            oart.setOrden(
                    c.getInt(c.getColumnIndex(OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN))
            );

        }
        c.close();

        return oart;
    }

    /**
     * Se asigna a oa el orden de oa2 y todos los artículos siguiente a oa2 se les incrementa
     * en uno el orden.
     *
     * @param oa    El objeto OrdenArticulo a posicionar.
     * @param oa2   El objeto a partir del cual hay que incrementar el orden en 1.
     */
    public void ganarPosiciones(OrdenArticulo oa, OrdenArticulo oa2) {

        Integer rango1 = oa2.getOrden();
        Integer rango2 = oa.getOrden();

        // nos aseguramos que el orden sea correcto.
        if (rango1 > rango2) return;


        String where;

        where = OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN + " BETWEEN " +
                    rango1 + " AND " + rango2;

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor c;
        c = db.rawQuery(
                "UPDATE " + OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME +
                        " SET " + OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN + " = " +
                        OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN + " + 1 " +
                        " WHERE " + OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER + " = " + oa.getId_super() +
                        " AND " + where
                , null);

        c.moveToFirst();
        c.close();

        oa.setOrden(rango1);
        update(oa);

    }


    /**
     * Se borra el registro oa y se cambian los valores de orden desde oa hasta el final en -1
     * posición.  Quedarán el número de elementos con uno menos.
     *
     * @param oa    El objeto OrdenArticulo a posicionar.
     */
    public void cederPosiciones(OrdenArticulo oa) {

        String where;
        where = OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN + " > " + oa.getOrden();

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor c;
        c = db.rawQuery(
                "DELETE FROM " + OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME +
                        " WHERE " + OrdenArticuloContract.OrdenArticuloEntry._ID + " = " + oa.getId()
                , null);

        c = db.rawQuery(
                "UPDATE " + OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME +
                        " SET " + OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN + " = " +
                        OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ORDEN + " - 1 " +
                        " WHERE " + OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER + " = " + oa.getId_super() +
                        " AND " + where
                , null);

        c.moveToFirst();
        c.close();
    }


    /**
     * Devuelve el número de elementos para un super (+1 para evitar el 0).
     * @param id_super El super del cual se necesita saber el número de artículos.
     */
    public Integer elemsSuper(Long id_super) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor c;
        c = db.rawQuery("SELECT * FROM " + OrdenArticuloContract.OrdenArticuloEntry.TABLE_NAME +
                " WHERE " + OrdenArticuloContract.OrdenArticuloEntry.COLUMN_NAME_ID_SUPER +
                " = " + id_super, null);

        return c.getCount() + 1;
    }
}
