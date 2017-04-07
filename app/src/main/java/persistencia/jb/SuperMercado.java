package persistencia.jb;

/**
 * JavaBean SuperMercado
 * Cada lista de la compra debe referencia a un supermercado en concreto.
 */

public class SuperMercado implements java.io.Serializable {

    private Long id;
    private String nombre;

    public SuperMercado() {
        id = 0L;
        nombre = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
