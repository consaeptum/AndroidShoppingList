package persistencia.jb;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Una Lista de la compra contendrá un conjunto de Líneas para cada artículo de la lista.
 */

public class Lista implements Serializable {

    private Long id;
    private Long id_super;
    private Date fecha;
    private Float importe;

    public Lista() {
        id = 0L;
        id_super = 0L;
        fecha = new Date();
        importe = 0F;
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
    public String getFechaFormatYMD() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(getFecha());
    }

    /**
     * Devuelve la fecha
     * @return fecha en formato "dd-MM-yyyy"
     */
    public String getFechaFormatDMY() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return sdf.format(getFecha());
    }

    /**
     * Cambiar la fecha.
     * @param f la fecha en formato "yyyy-MM-dd"
     */
    public void setFechaFormatYMD(String f) {

        f = f.replace('/', '-');
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        try {
            fecha = sdf.parse(f);
        } catch (ParseException e) {
            fecha = new Date();
        }
    }

    /**
     * Cambiar la fecha.
     * @param f la fecha en formato "dd-MM-yyyy"
     */
    public void setFechaFormatDMY(String f) {

        f = f.replace('/', '-');
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        try {
            fecha = sdf.parse(f);
        } catch (ParseException e) {
            fecha = new Date();
        }
    }

    /**
     * Devuelve la fecha en formato DMY
     * @param f la fecha en formato "yyyy-MM-dd"
     * @return fecha en formato "dd-MM-yyyy"
     */
    static public String getFechaFormatDMY(String f) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date fecha = new Date();
        try {
            fecha = new SimpleDateFormat("yyyy-MM-dd").parse(f);
        } catch (ParseException e) {
            fecha = new Date();
        }

        return sdf.format(fecha);
    }

    public Float getImporte() {
        return importe;
    }

    public void setImporte(Float importe) {
        this.importe = importe;
    }
}
