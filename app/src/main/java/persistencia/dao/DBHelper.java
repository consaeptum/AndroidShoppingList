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
        db.execSQL(contract.get_SQL_CREATE_ENTRIES());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(contract.get_SQL_DELETE_ENTRIES());
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
