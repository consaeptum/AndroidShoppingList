package persistencia.jb;

import static util.Constantes.TIPO_MEDIDA_KILOGRAMOS;
import static util.Constantes.TIPO_MEDIDA_LITROS;
import static util.Constantes.TIPO_MEDIDA_UNIDADES;

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
        medida = TIPO_MEDIDA_UNIDADES;
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

        if (Character.toUpperCase(medida) == TIPO_MEDIDA_UNIDADES ) {
            this.medida = TIPO_MEDIDA_UNIDADES;
            return;
        }
        if (Character.toUpperCase(medida) == TIPO_MEDIDA_LITROS ) {
            this.medida = TIPO_MEDIDA_LITROS;
            return;
        }
        if (Character.toUpperCase(medida) == TIPO_MEDIDA_KILOGRAMOS ) {
            this.medida = TIPO_MEDIDA_KILOGRAMOS;
            return;
        }
        this.medida = TIPO_MEDIDA_UNIDADES;

    }
}
