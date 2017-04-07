package persistencia.dao;

/**
 * Interfaz para ser implementado por todos los Contracts
 */

public interface Contract {

    String get_SQL_CREATE_ENTRIES();
    String get_SQL_DELETE_ENTRIES();

}
