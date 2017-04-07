package persistencia.jb;

/**
 * JavaBean Familia
 * Cada artículo debe pertenecer a una Familia.
 */

public class Familia implements java.io.Serializable {

    private Long id;
    private String nombre;

    public Familia() {
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
