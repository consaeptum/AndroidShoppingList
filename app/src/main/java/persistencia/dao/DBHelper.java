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

        // insertamos el _id 0 como "   articulo   " para el elemento 0 de los spinners
        db.execSQL(ac.get_SQL_INSERTFIRST());

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

        OrdenArticuloContract oac = OrdenArticuloContract.getInstance();
        db.execSQL(oac.get_SQL_CREATE_ENTRIES());
        if (oac.hasIndex()) {
            db.execSQL(oac.get_SQL_CREATE_INDEX());
        }

        introducirFilasIniciales(db);

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

        OrdenArticuloContract oac = OrdenArticuloContract.getInstance();
        db.execSQL(oac.get_SQL_DELETE_ENTRIES());

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void introducirFilasIniciales(SQLiteDatabase db) {
        db.rawQuery("INSERT INTO supermercado (_id, nombre) VALUES (1, 'Area de Guissona')", null).moveToFirst();
        db.rawQuery("INSERT INTO supermercado (_id, nombre) VALUES (2, 'Carrefour')", null).moveToFirst();
        db.rawQuery("INSERT INTO supermercado (_id, nombre) VALUES (3, 'Condis')", null).moveToFirst();
        db.rawQuery("INSERT INTO supermercado (_id, nombre) VALUES (4, 'Dia')", null).moveToFirst();
        db.rawQuery("INSERT INTO supermercado (_id, nombre) VALUES (5, 'Eroski')", null).moveToFirst();
        db.rawQuery("INSERT INTO supermercado (_id, nombre) VALUES (6, 'Lidl')", null).moveToFirst();
        db.rawQuery("INSERT INTO supermercado (_id, nombre) VALUES (7, 'Mercadona')", null).moveToFirst();
        db.rawQuery("INSERT INTO supermercado (_id, nombre) VALUES (8, 'Próxim')", null).moveToFirst();
        db.rawQuery("INSERT INTO supermercado (_id, nombre) VALUES (9, 'Sorli Discau')", null).moveToFirst();

        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (1, 'Bebidas')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (2, 'Bebidas alcoholicas')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (3, 'Bollería')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (4, 'Carnes')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (5, 'Charcutería')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (6, 'Comida Preparada')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (7, 'Congelados')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (8, 'Conservas')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (9, 'Droguería')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (10, 'Dulces')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (11, 'Frutas')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (12, 'Panadería')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (13, 'Productos secos')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (14, 'Pescado')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (15, 'Productos Lácteos')", null).moveToFirst();
        db.rawQuery("INSERT INTO familia (_id, nombre) VALUES (16, 'Verduras')", null).moveToFirst();


        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (1, 1, 'Agua', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (2, 1, 'Cocacola', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (3, 1, 'Fanta', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (4, 2, 'Vino', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (5, 1, 'Zumo', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (6, 15, 'Leche', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (7, 13, 'Café', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (8, 13, 'Café cápsulas', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (9, 13, 'Azúcar', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (10, 13, 'Harina', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (11, 13, 'Sal', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (12, 12, 'Pan', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (13, 5, 'Queso fresco' , '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (14, 5, 'Queso Lonchas', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (15, 5, 'Queso cuña', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (16, 5, 'Jamón cocido', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (17, 5, 'Jamón curado', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (18, 6, 'Pizza', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (19, 5, 'Salchichón', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (20, 5, 'Chorizo', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (21, 5, 'Bacon', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (22, 4, 'Pechuga de pollo', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (23, 4, 'Lomo de cerdo', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (24, 4, 'Conejo', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (25, 4, 'Salchichas', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (26, 4, 'Butifarra', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (27, 6, 'Canelones congelados', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (28, 7, 'Judías verdes congeladas', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (29, 7, 'Patatas fritas congeladas', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (30, 7, 'Bacalao congelado', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (31, 7, 'Sepia congelada', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (32, 7, 'Merluza congelada', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (33, 7, 'Croquetas congeladas', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (34, 7, 'Empanadilla de atún congelada', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (35, 12, 'Pan de molde', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (36, 12, 'Pan tostado', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (37, 11, 'Manzanas', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (38, 11, 'Naranjas', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (39, 11, 'Peras', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (40, 11, 'Melocotones', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (41, 11, 'Uvas', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (42, 11, 'Ciruelas', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (43, 11, 'Cerezas', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (44, 11, 'Melón', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (45, 11, 'Sandía', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (46, 16, 'Patatas bolsa', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (47, 16, 'Pimiento verde', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (48, 16, 'Pimiento rojo', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (49, 16, 'Lechuga', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (50, 16, 'Col', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (51, 16, 'Coliflor', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (52, 16, 'Zanahoria', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (53, 16, 'Ajo', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (54, 16, 'Limón', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (55, 16, 'Cebolla', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (56, 8, 'Aceite de girasol', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (57, 8, 'Aceite de oliva', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (58, 8, 'Salsa de tomate', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (59, 8, 'Mayonesa', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (60, 15, 'Mantequilla', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (61, 15, 'Yogur', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (62, 15, 'Flan', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (63, 13, 'Colacao', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (64, 15, 'Cacaolat', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (65, 9, 'Papel higiénico', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (66, 9, 'Pasta de dientes', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (67, 9, 'Gel de baño', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (68, 9, 'Champú', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (69, 9, 'Detergente lavadora', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (70, 9, 'Suavizante lavadora', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (71, 9, 'Detergente lavavajillas', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (72, 9, 'Abrillantador lavavajillas', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (73, 9, 'Sal de lavavajillas', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (74, 9, 'Lejía', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (75, 10, 'Caramelos', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (76, 3, 'Magdalenas', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (77, 3, 'Galletas', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (78, 11, 'Plátano', '', 'K')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (79, 8, 'Atún', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (80, 12, 'Pan rallado', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (81, 13, 'Avecrem', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (82, 7, 'Ensaladilla Rusa', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (84, 9, 'Hilo dental', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (85, 5, 'Huevos docena', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (86, 8, 'Champiñones', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (87, 8, 'Lentejas', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (88, 8, 'Garbanzos', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (89, 13, 'Frutos secos', '', 'U')", null).moveToFirst();
        db.rawQuery("INSERT INTO articulo (_id, id_familia, nombre, descripcion, medida) VALUES (90, 9, 'Bolsas de basura', '', 'U')", null).moveToFirst();


    }
}
