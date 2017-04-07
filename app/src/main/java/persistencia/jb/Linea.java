package persistencia.jb;

/**
 * JavaBean Linea
 * Cada lista de la compra tendrá un conjunto de líneas para cada artículo de la lista.
 */

public class Linea implements java.io.Serializable {

    private Long id;
    private Long id_lista;
    private Long id_articulo;
    private Float cantidad;
    private Float pvp;

    public Linea() {
        id = 0L;
        id_lista = 0L;
        id_articulo = 0L;
        cantidad = 0.0f;
        pvp = 0.0f;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_lista() {
        return id_lista;
    }

    public void setId_lista(Long id_lista) {
        this.id_lista = id_lista;
    }

    public Long getId_articulo() {
        return id_articulo;
    }

    public void setId_articulo(Long id_articulo) {
        this.id_articulo = id_articulo;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Float getPvp() {
        return pvp;
    }

    public void setPvp(Float pvp) {
        this.pvp = pvp;
    }
}