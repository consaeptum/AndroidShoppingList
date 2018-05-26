package persistencia.consultas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import persistencia.dao.DBHelper;
import persistencia.dao.LineaContract;
import persistencia.dao.ListaContract;
import persistencia.dao.SuperMercadoDao;

/**
 * Created by javier on 21/05/17.
 * Mediante consultas Raw obtenemos los datos que se añadirán a la parte de datos
 * desplegables en el CardView.
 */

public class DatosDesplegables {

    /**
     * DBHelper se instancia sólo una vez porque getWritableDatabase es costoso si
     * DBhelper está cerrado.
     * Deberá cerrarse en onDestroy():
     * protected void onDestroy() {
     * ArticuloDao.close();
     * super.onDestroy();
     * }
     */
    static DBHelper mDBHelper = null;

    static Context contexto;

    /**
     * La clase con los datos comparativos de precio y fecha para cada super
     */
    public class PrecioComparativo {
        Float pvp;
        Long id_super;
        String nombre_super;
        String ultima_fecha;
        String notas;

        public String getNotas() {
            return notas;
        }

        public void setNotas(String notas) {
            this.notas = notas;
        }

        public Float getPvp() {
            return pvp;
        }

        public void setPvp(Float pvp) {
            this.pvp = pvp;
        }

        public Long getId_super() {
            return id_super;
        }

        public void setId_super(Long id_super) {
            this.id_super = id_super;
        }

        public String getNombre_super() {
            return nombre_super;
        }

        public void setNombre_super(String nombre_super) {
            this.nombre_super = nombre_super;
        }

        public String getUltima_fecha() {
            return ultima_fecha;
        }

        public void setUltima_fecha(String ultima_fecha) {
            this.ultima_fecha = ultima_fecha;
        }
    }

    private List<PrecioComparativo> listaPrecios;

    public DatosDesplegables(Context context) {
        if (mDBHelper == null) mDBHelper = new DBHelper(LineaContract.getInstance(), context);
        listaPrecios = new ArrayList<PrecioComparativo>();
        contexto = context;
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    /**
     * Obtenemos los datos referentes a un artículo y los guardamos en el Cursor.  Posteriormente
     * se pueden recuperar con los métodos get.
     */
    public void cargaDatos(Long id_articulo) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        /*
            String consulta = "select lista._id, lineas._id, id_articulo, lineas.pvp, max(fecha) "
                + "from lista "
                + "inner join lineas on lista._id = lineas.id_lista "
                + "where id_articulo=? and pvp > 0?"
                + "group by lista.id_super ";
        */

        String consulta = "select " //+ ListaContract.ListaEntry.TABLE_NAME + "."
                                    + ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER + ","
                                    //+ LineaContract.LineaEntry.TABLE_NAME + "."
                                    //+ LineaContract.LineaEntry._ID + ","
                                    //+ LineaContract.LineaEntry.TABLE_NAME + "."
                                    + LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO + ","
                                    //+ LineaContract.LineaEntry.TABLE_NAME + "."
                                    + LineaContract.LineaEntry.COLUMN_NAME_PVP + ","
                                    + "max(" + ListaContract.ListaEntry.COLUMN_NAME_FECHA + ") "
                + "from " + ListaContract.ListaEntry.TABLE_NAME + " "
                + "inner join "
                + LineaContract.LineaEntry.TABLE_NAME + " "
                + "on "
                + ListaContract.ListaEntry.TABLE_NAME + "." + ListaContract.ListaEntry._ID
                + " = "
                + LineaContract.LineaEntry.TABLE_NAME + "." + LineaContract.LineaEntry.COLUMN_NAME_ID_LISTA
                + " where "
                + LineaContract.LineaEntry.TABLE_NAME + "." + LineaContract.LineaEntry.COLUMN_NAME_ID_ARTICULO
                + "=? and "
                + LineaContract.LineaEntry.TABLE_NAME + "." + LineaContract.LineaEntry.COLUMN_NAME_PVP
                + ">? "
                + "group by "
                + ListaContract.ListaEntry.TABLE_NAME + "." + ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER;

        Cursor c = db.rawQuery(consulta, new String[]{String.valueOf(id_articulo), "0"});

        if (c.getCount() > 0) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                PrecioComparativo pc = new PrecioComparativo();

                pc.setId_super(c.getLong(c.getColumnIndex(ListaContract.ListaEntry.COLUMN_NAME_ID_SUPER)));
                pc.setPvp(c.getFloat(c.getColumnIndex(LineaContract.LineaEntry.COLUMN_NAME_PVP)));
                pc.setUltima_fecha(c.getString(c.getColumnIndex("max("+ListaContract.ListaEntry.COLUMN_NAME_FECHA+")")));
                pc.setNombre_super((new SuperMercadoDao(contexto)).read(pc.getId_super()).getNombre());
                listaPrecios.add(pc);
                c.moveToNext();
            }
        }
    }

    public List<PrecioComparativo> getListaPrecios() {
        return listaPrecios;
    }

}