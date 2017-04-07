package persistencia.dao;

import android.provider.BaseColumns;

/**
 * Tabla Articulo
 * campos id, id_familia, nombre, descripcion,
 */

public final class ArticuloContract implements Contract {
    
    private static ArticuloContract articuloContract;
    
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ArticuloContract() {}

    public static ArticuloContract getInstance() {
        if (articuloContract == null) {
            articuloContract = new ArticuloContract();
        }
        return articuloContract;
    }
    
    @Override
    public String get_SQL_CREATE_ENTRIES() {
        return SQL_CREATE_ENTRIES;
    }

    @Override
    public String get_SQL_DELETE_ENTRIES() {
        return SQL_DELETE_ENTRIES;
    }

    /* Inner class that defines the table contents */
    public static class ArticuloEntry implements BaseColumns {
        public static final String TABLE_NAME = "articulo";
        public static final String COLUMN_NAME_ID_FAMILIA = "id_familia";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
        public static final String COLUMN_NAME_DESCRIPCION = "descripcion";
        public static final String COLUMN_NAME_MEDIDA = "medida";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String UNIQUE_NOTNULL = " UNIQUE NOT NULL";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ArticuloEntry.TABLE_NAME + " (" +
                    ArticuloEntry._ID + " INTEGER PRIMARY KEY," +
                    ArticuloEntry.COLUMN_NAME_ID_FAMILIA + INTEGER_TYPE + COMMA_SEP +
                    ArticuloEntry.COLUMN_NAME_NOMBRE + TEXT_TYPE + UNIQUE_NOTNULL + COMMA_SEP +
                    ArticuloEntry.COLUMN_NAME_DESCRIPCION + TEXT_TYPE + COMMA_SEP +
                    ArticuloEntry.COLUMN_NAME_MEDIDA + TEXT_TYPE + COMMA_SEP +
                        " FOREIGN KEY ("+ ArticuloEntry.COLUMN_NAME_ID_FAMILIA+") REFERENCES "+
                            FamiliaContract.FamiliaEntry.TABLE_NAME+
                            "("+FamiliaContract.FamiliaEntry._ID+")" +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ArticuloEntry.TABLE_NAME;

}
