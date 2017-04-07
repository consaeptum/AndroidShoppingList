package persistencia.jb;

/**
 * JavaBean Articulo
 * Cada artículo debe pertenecer a una Familia.
 */

public class Articulo implements java.io.Serializable {

    private Long id;
    private Long id_familia;
    private String nombre;
    private String descripcion;
    private Character medida;

    public Articulo() {
        id = 0L;
        id_familia = 0L;
        nombre = "";
        descripcion = "";
        medida = 'U';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_familia() {
        return id_familia;
    }

    public void setId_familia(Long id_familia) {
        this.id_familia = id_familia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Character getMedida() {
        return medida;
    }

    /* Las medidas pueden ser (U)nidades, (L)itros, (K)ilogramos */
    public void setMedida(Character medida) {

        if (Character.toUpperCase(medida) == 'U' ) {
            this.medida = 'U';
            return;
        }
        if (Character.toUpperCase(medida) == 'L' ) {
            this.medida = 'L';
            return;
        }
        if (Character.toUpperCase(medida) == 'K' ) {
            this.medida = 'K';
            return;
        }
        this.medida = 'U';

    }
}
