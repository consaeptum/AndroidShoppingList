package util;

public class Constantes {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ListaDeLaCompra.db";

    // En articulos hay tres tipos de medida.
    public static final Character TIPO_MEDIDA_LITROS = 'L';
    public static final Character TIPO_MEDIDA_KILOGRAMOS = 'K';
    public static final Character TIPO_MEDIDA_UNIDADES = 'U';

    // Los colores cebra para cualquier lista.
    public static final int[] CEBRAS = new int[] { 0xFFC7E8F9, 0xFFFFFFFF };

    public static final Integer COLOR_LISTA_SELECCIONADA = 0xFF8796E1;
    public static final Integer COLOR_CARDVIEW_COMPRADO = 0xFFFFFFFF;
    public static final Integer COLOR_CARDVIEW_NUEVO = 0xFFB8D29C;
    public static final Integer COLOR_CARDVIEW_NO_COMPRADO = 0xFF82C0E7;

}
