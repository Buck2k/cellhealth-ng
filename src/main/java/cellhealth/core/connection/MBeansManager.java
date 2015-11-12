package cellhealth.core.connection;

import cellhealth.utils.Utils;
import cellhealth.utils.constants.Constants;
import cellhealth.utils.constants.message.Error;
import cellhealth.utils.logs.L4j;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.exception.ConnectorNotAvailableException;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
            L4j.getL4j().warning("Connector not available");
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
    public Set<ObjectName> getAllServerRuntimes()  {
            return this.getMBeans(Constants.QUERY_SERVER_RUNTIME);
    }

    public String getNodeServerMBean(){
        ObjectName objectName = getMBean("WebSphere:processType=ManagedProcess,*");
        return objectName.getKeyProperty("node");
    }


    public Map<String,String> getPathHostChStats(){
        Map<String,String> pathChstats = new HashMap<String, String>();
        ObjectName dmgr = this.getMBean("WebSphere:processType=DeploymentManager,*");
        String cell = dmgr.getKeyProperty("cell");
        if(cell != null) {
            String query = "WebSphere:processType=ManagedProcess,cell=" + cell + ",*";
            String chStatsNode = "";
            String chStatsHost = "";
            String chStatsCell = "";
            String path = "";
            String host = "";
            for (ObjectName objectName : this.getMBeans(query)) {
                chStatsNode = objectName.getKeyProperty("node");
                chStatsHost = Utils.getHostByNode(chStatsNode);
                chStatsCell = objectName.getKeyProperty("cell");
                if (path == null || path.length() == 0) {
                    if ((chStatsNode != null && chStatsNode.length() > 0) && (chStatsHost != null && chStatsHost.length() > 0) && (chStatsCell != null && chStatsCell.length() > 0)) {
                        path = chStatsCell + ".ch_stats";
                        host = chStatsHost;
                        pathChstats.put("path", path);
                        pathChstats.put("host", host);
                    }
                }
            }
        }
        return pathChstats;
    }
}