package persistencia.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static util.Constantes.DATABASE_NAME;
import static util.Constantes.DATABASE_VERSION;


/**
 * DBHelper para utilizar en el los Dao.
 */

public class DBHelper extends SQLiteOpenHelper {

    private Contract contract;

    public DBHelper(Contract contract, Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.contract = contract;
    }

    public void onCreate(SQLiteDatabase db) {

        FamiliaContract fc = FamiliaContract.getInstance();
        db.execSQL(fc.get_SQL_CREATE_ENTRIES());
        if (fc.hasIndex()) {
            db.execSQL(fc.get_SQL_CREATE_INDEX());
        }

        SuperMercadoContract sc = SuperMercadoContract.getInstance();
        db.execSQL(sc.get_SQL_CREATE_ENTRIES());
        if (sc.hasIndex()) {
            db.execSQL(sc.get_SQL_CREATE_INDEX());
        }

        ArticuloContract ac = ArticuloContract.getInstance();
        db.execSQL(ac.get_SQL_CREATE_ENTRIES());
        if (ac.hasIndex()) {
            db.execSQL(ac.get_SQL_CREATE_INDEX());
        }

        LineaContract lc = LineaContract.getInstance();
        db.execSQL(lc.get_SQL_CREATE_ENTRIES());
        if (lc.hasIndex()) {
            db.execSQL(lc.get_SQL_CREATE_INDEX());
        }

        ListaContract lic = ListaContract.getInstance();
        db.execSQL(lic.get_SQL_CREATE_ENTRIES());
        if (lic.hasIndex()) {
            db.execSQL(lic.get_SQL_CREATE_INDEX());
        }

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        FamiliaContract fc = FamiliaContract.getInstance();
        db.execSQL(fc.get_SQL_DELETE_ENTRIES());

        SuperMercadoContract sc = SuperMercadoContract.getInstance();
        db.execSQL(sc.get_SQL_DELETE_ENTRIES());

        ArticuloContract ac = ArticuloContract.getInstance();
        db.execSQL(ac.get_SQL_DELETE_ENTRIES());

        LineaContract lc = LineaContract.getInstance();
        db.execSQL(lc.get_SQL_DELETE_ENTRIES());

        ListaContract lic = ListaContract.getInstance();
        db.execSQL(lic.get_SQL_DELETE_ENTRIES());

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
