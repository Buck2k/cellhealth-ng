package cellhealth.core.connection;

import com.ibm.websphere.management.AdminClient;

/**
 * Interfaz que deben implementar todas las conexiones
 * @author Alberto Pascual
 * @version 1.0
 */

public interface WASConnection {

    /**
     * El metodo connect, será el encargado de establecer la conexion con websphere
     * Utilizando cualquier conector admitido por las propiedades del AdminClient
     */
    public void connect();

    /**
     * Devolvera el AdminClient creado en la conexion
     * @return AdminClient
     */
    public AdminClient getClient();
}
