package persistencia.dao;

import android.provider.BaseColumns;

/**
 * Tabla Familia
 * campos id, nombre
 */

public final class FamiliaContract implements Contract {

    private static FamiliaContract familiaContract;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FamiliaContract() {}

    public static FamiliaContract getInstance() {
        if (familiaContract == null) {
            familiaContract = new FamiliaContract();
        }
        return familiaContract;
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
    public static class FamiliaEntry implements BaseColumns {
        public static final String TABLE_NAME = "familia";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String UNIQUE_NOTNULL = " UNIQUE NOT NULL";
    public static final String COMMA_SEP = ",";
    public static final String COLLATENOCASE = " COLLATE NOCASE";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FamiliaEntry.TABLE_NAME + " (" +
                    FamiliaEntry._ID + " INTEGER PRIMARY KEY," +
                    FamiliaEntry.COLUMN_NAME_NOMBRE + TEXT_TYPE + UNIQUE_NOTNULL + COLLATENOCASE + ")";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FamiliaEntry.TABLE_NAME;
}
