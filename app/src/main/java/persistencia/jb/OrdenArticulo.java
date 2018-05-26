package persistencia.jb;

/**
 * JavaBean OrdenArticulo
 * Para cada supermercado controlamos el orden en que se realiza la compra.  De modo que cuando
 * estemos comprando ordenaremos los productos en la lista en el mismo orden en que compramos
 * habitualmente.
 */

public class OrdenArticulo implements java.io.Serializable {

    private Long id;
    private Long id_super;
    private Long id_articulo;
    private Integer orden;

    public OrdenArticulo() {
        id = 0L;
        id_super = 0L;
        id_articulo = 0L;
        orden = 0;
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

    public Long getId_articulo() {
        return id_articulo;
    }

    public void setId_articulo(Long id_articulo) {
        this.id_articulo = id_articulo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}