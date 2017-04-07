package persistencia.jb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Una Lista de la compra contendrá un conjunto de Líneas para cada artículo de la lista.
 */

public class Lista {

    private Long id;
    private Long id_super;
    private Date fecha;

    public Lista() {
        id = 0L;
        id_super = 0L;
        fecha = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_super() {
        return id_super;
    }

    public void setId_super(Long id_super) {
        this.id_super = id_super;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * Devuelve la fecha
     * @return fecha en formato "yyyy-MM-dd"
     */
    public String getFechaFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(getFecha());
    }

    /**
     * Cambiar la fecha.
     * @param f la fecha en formato "yyyy-MM-dd"
     */
    public void setFechaFormat(String f) {

        f = f.replace('/', '-');
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        try {
            fecha = sdf.parse(f);
        } catch (ParseException e) {
            fecha = new Date();
        }
    }
}
