package persistencia.dao;

import android.provider.BaseColumns;

/**
 * Tabla SuperMercado
 * campos id, nombre
 */

public final class SuperMercadoContract implements Contract {

    private static SuperMercadoContract superMercadoContract;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private SuperMercadoContract() {}

    public static SuperMercadoContract getInstance() {
        if (superMercadoContract == null) {
            superMercadoContract = new SuperMercadoContract();
        }
        return superMercadoContract;
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
    public static class SuperMercadoEntry implements BaseColumns {
        public static final String TABLE_NAME = "supermercado";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String UNIQUE_NOTNULL = " UNIQUE NOT NULL";
    public static final String COMMA_SEP = ",";
    public static final String COLLATENOCASE = " COLLATE NOCASE";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + SuperMercadoEntry.TABLE_NAME + " (" +
                    SuperMercadoEntry._ID + " INTEGER PRIMARY KEY," +
                    SuperMercadoEntry.COLUMN_NAME_NOMBRE + TEXT_TYPE + UNIQUE_NOTNULL + COLLATENOCASE + " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SuperMercadoEntry.TABLE_NAME;

}
