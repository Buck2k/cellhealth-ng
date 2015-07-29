package cellhealth.core.connection;

import cellhealth.utils.constants.Constants;
import cellhealth.utils.constants.message.Error;
import cellhealth.utils.logs.L4j;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.exception.ConnectorNotAvailableException;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Classe que facilita las consultas queryNames sobre AdminClient
 * @author Alberto Pascual
 * @version 1.0
 */

public class MBeansManager {

    private AdminClient client;

    /**
     * Constructor
     * Obtiene la conexion por parametros al adminclient
     * @param connection interfaz WASConnection,
     */
    public MBeansManager(WASConnection connection) {
        this.client = connection.getClient();
    }

    /**
     * Consulta parametrizada mediente el AdminClient que se establecio en el constructor
     * La consulta es pasada por parametros y devuelve un set de objectbeans
     * @param query consulta que se pasara al metodo queryNames
     * @return resultado de la consulta. Sino hay resultados devolverá nulo
     */
    public Set<ObjectName> getMBeans(String query) {
        if(this.client == null) {
            throw new NullPointerException();
        }
        Set objects = null;
        try {

            objects = this.client.queryNames(new ObjectName(query), null);
        } catch (ConnectorNotAvailableException e) {
            L4j.getL4j().warning(e.toString());
        } catch (ConnectorException e) {
            String[] classNameSplit = this.getClass().getName().split("\\.");
            L4j.getL4j().error(classNameSplit[classNameSplit.length-1] + ", " + Error.CONNECTOR_ERROR, e);
        } catch (MalformedObjectNameException e) {
            String[] classNameSplit = this.getClass().getName().split("\\.");
            L4j.getL4j().error(classNameSplit[classNameSplit.length-1] + ", " + Error.OBJECT_MALFORMED, e);
        }
        return this.castSetObjectNames(objects);
    }

    /**
     * El retorno de queryNames es un Set, he decidido castear los objetos y crear un Set<ObjectName>
     * No representa una carga en la recogida de metricas porque solo se usa para devolver informacion de beans
     * @param objecs set de objetos
     * @return devuelve el la lista de objetos ya casteados a Objectname
     */
    public Set<ObjectName> castSetObjectNames(Set objecs){
        Set<ObjectName> objectNames = new LinkedHashSet<ObjectName>();
        for(Object object: objecs){
            objectNames.add((ObjectName)object);
        }
        return objectNames;
    }

    /**
     * Igual que getBeans, con la diferencia que tan solo devuelve el primer resultado devuelto, por la consulta
     * @param query consulta que se pasara al metodo queryNames
     * @return resultado de la consulta. Sino hay resultados devolverá nulo
     */
    public ObjectName getMBean(String query) {
        Set mbeans = this.getMBeans(query);
        ObjectName objectName = null;
        if(mbeans != null && mbeans.size() > 0) {
            objectName = (ObjectName) mbeans.iterator().next();
        }
        return objectName;
    }

    public AdminClient getClient(){
        return this.client;
    }

    /**
     * Clase que obtiene todos los los servidores, incluido el dmgr
     * Lanza la consulta "WebSphere:*,type=Server,j2eeType=J2EEServer"
     * @return all runtime servers
     */
    public Set getAllServerRuntimes()  {
            return this.getMBeans(Constants.QUERY_SERVER_RUNTIME);
    }

    /**
     * Obtiene el nodo de un servidor devuelto por adminclient
     * Se utiliza para calcular el nombre de maquina.
     * @return nodo del servidor
     */
    public String getNodeServerMBeanN(){
        String node = null;
        try {
            node = this.client.getServerMBean().getKeyProperty(Constants.NAME);
        } catch (ConnectorException e) {
            L4j.getL4j().warning("e");
        }
        return node;
    }

    public String getNodeServerMBean(){
        ObjectName objectName = getMBean("WebSphere:processType=ManagedProcess,*");
        return objectName.getKeyProperty("node");
    }
}