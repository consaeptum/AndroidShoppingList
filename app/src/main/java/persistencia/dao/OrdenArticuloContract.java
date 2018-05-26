package persistencia.dao;

import android.provider.BaseColumns;

/**
 * Tabla OrdenArticulo
 * campos id, id_super, id_articulo, orden
 */

public final class OrdenArticuloContract implements Contract {

    private static OrdenArticuloContract ordenArticuloContract;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private OrdenArticuloContract() {}

    public static OrdenArticuloContract getInstance() {
        if (ordenArticuloContract == null) {
            ordenArticuloContract = new OrdenArticuloContract();
        }
        return ordenArticuloContract;
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
        return false;
    }

    @Override
    public String get_SQL_CREATE_INDEX() {
        return "";
    }


    /* Inner class that defines the table contents */
    public static class OrdenArticuloEntry implements BaseColumns {
        public static final String TABLE_NAME = "OrdenArticulo";
        public static final String COLUMN_NAME_ID_SUPER = "id_super";
        public static final String COLUMN_NAME_ID_ARTICULO = "id_articulo";
        public static final String COLUMN_NAME_ORDEN = "orden";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String NOTNULL = " NOT NULL";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + OrdenArticuloEntry.TABLE_NAME + " (" +
                    OrdenArticuloEntry._ID + " INTEGER PRIMARY KEY," +
                    OrdenArticuloEntry.COLUMN_NAME_ID_SUPER + INTEGER_TYPE + NOTNULL + COMMA_SEP +
                    OrdenArticuloEntry.COLUMN_NAME_ID_ARTICULO + INTEGER_TYPE + NOTNULL + COMMA_SEP +
                    OrdenArticuloEntry.COLUMN_NAME_ORDEN + INTEGER_TYPE + NOTNULL + " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + OrdenArticuloEntry.TABLE_NAME;
}
