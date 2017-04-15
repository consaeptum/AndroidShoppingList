package persistencia.dao;

import android.provider.BaseColumns;

/**
 * Tabla Lista
 * campos id, id_super, fecha
 */

public final class ListaContract implements Contract {
    
    private static ListaContract listaContract;
    
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ListaContract() {}

    public static ListaContract getInstance() {
        if (listaContract == null) {
            listaContract = new ListaContract();
        }
        return listaContract;
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
    public static class ListaEntry implements BaseColumns {
        public static final String TABLE_NAME = "Lista";
        public static final String COLUMN_NAME_ID_SUPER = "id_super";
        public static final String COLUMN_NAME_FECHA = "fecha";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String NOTNULL = " NOT NULL";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ListaEntry.TABLE_NAME + " (" +
                    ListaEntry._ID + " INTEGER PRIMARY KEY," +
                    ListaEntry.COLUMN_NAME_ID_SUPER + INTEGER_TYPE + COMMA_SEP +
                    ListaEntry.COLUMN_NAME_FECHA + INTEGER_TYPE + NOTNULL + COMMA_SEP +
                        " FOREIGN KEY ("+ListaEntry.COLUMN_NAME_ID_SUPER+") REFERENCES "+
                            SuperMercadoContract.SuperMercadoEntry.TABLE_NAME+
                            "("+SuperMercadoContract.SuperMercadoEntry._ID+")" +
                    " )";

    public static final String SQL_CREATE_INDEX =
            "CREATE UNIQUE INDEX idx_lista_fecha ON lista(fecha);";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ListaEntry.TABLE_NAME;
}
