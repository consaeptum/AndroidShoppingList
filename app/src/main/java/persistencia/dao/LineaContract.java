package persistencia.dao;

import android.provider.BaseColumns;

/**
 * Tabla Linea
 * campos id, id_articulo, id_lista, cantiddd, pvp
 */

public final class LineaContract implements Contract {
    
    private static LineaContract lineaContract;
    
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private LineaContract() {}

    public static LineaContract getInstance() {
        if (lineaContract == null) {
            lineaContract = new LineaContract();
        }
        return lineaContract;
    }
    
    @Override
    public String get_SQL_CREATE_ENTRIES() {
        return SQL_CREATE_ENTRIES;
    }

    @Override
    public String get_SQL_DELETE_ENTRIES() {
        return SQL_DELETE_ENTRIES;
    }

    @Override
    public Boolean hasIndex() {
        return true;
    }

    @Override
    public String get_SQL_CREATE_INDEX() {
        return SQL_CREATE_INDEX;
    }


    /* Inner class that defines the table contents */
    public static class LineaEntry implements BaseColumns {
        public static final String TABLE_NAME = "linea";
        public static final String COLUMN_NAME_ID_ARTICULO = "id_articulo";
        public static final String COLUMN_NAME_ID_LISTA = "id_lista";
        public static final String COLUMN_NAME_CANTIDAD = "cantidad";
        public static final String COLUMN_NAME_PVP = "pvp";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String UNIQUE_NOTNULL = " UNIQUE NOT NULL";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LineaEntry.TABLE_NAME + " (" +
                    LineaEntry._ID + " INTEGER PRIMARY KEY," +
                    LineaEntry.COLUMN_NAME_ID_ARTICULO + INTEGER_TYPE + COMMA_SEP +
                    LineaEntry.COLUMN_NAME_ID_LISTA + INTEGER_TYPE + COMMA_SEP +
                    LineaEntry.COLUMN_NAME_CANTIDAD + INTEGER_TYPE + COMMA_SEP +
                    LineaEntry.COLUMN_NAME_PVP + INTEGER_TYPE + COMMA_SEP +
                        " FOREIGN KEY ("+ LineaEntry.COLUMN_NAME_ID_LISTA+") REFERENCES "+
                            ListaContract.ListaEntry.TABLE_NAME+
                            "("+ListaContract.ListaEntry._ID+")" + COMMA_SEP +
                        " FOREIGN KEY ("+ LineaEntry.COLUMN_NAME_ID_ARTICULO+") REFERENCES "+
                            ArticuloContract.ArticuloEntry.TABLE_NAME+
                            "("+ArticuloContract.ArticuloEntry._ID+")" +
                    " )";

    public static final String SQL_CREATE_INDEX =
            "CREATE UNIQUE INDEX idx_linea_lista_articulo ON linea(id_lista, id_articulo);";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LineaEntry.TABLE_NAME;

}
