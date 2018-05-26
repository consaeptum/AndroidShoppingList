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

    // El orden de cada línea en el super.  No se guardará, uso temporal.
    private Integer orden;

    // Si la línea está marcado como comprado o por comprar.
    private Boolean comprado;

    public Linea() {
        id = 0L;
        id_lista = 0L;
        id_articulo = 0L;
        cantidad = 0.0f;
        pvp = 0.0f;
        orden = 0;
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

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Boolean getComprado() {
        return comprado;
    }

    public void setComprado(Boolean comprado) {
        this.comprado = comprado;
    }


}