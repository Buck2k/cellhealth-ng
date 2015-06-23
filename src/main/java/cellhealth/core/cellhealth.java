package cellhealth.core;

import cellhealth.core.connection.WASConnectionSOAP;
import cellhealth.core.threads.InstanceManager;
import cellhealth.utils.logs.L4j;

/**
 * Created by Alberto Pascual on 10/06/15.
 */

public class cellhealth {

    public static void main(String[] args) throws Exception {
        L4j.getL4j().info("Iniciando el proceso cellhealth - para la recopilación de métricas de WebSphere");
        StartCellhealth startCellhealth = new StartCellhealth(new WASConnectionSOAP());
        Thread instanceManager = new Thread(new InstanceManager(), "test");
        instanceManager.start();
    }

    private void initInstanceManagerThread() {

    }

}
