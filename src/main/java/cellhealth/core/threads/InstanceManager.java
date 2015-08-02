package cellhealth.core.threads;

import cellhealth.sender.graphite.sender.GraphiteSender;

/**
 * Created by Alberto Pascual on 19/06/15.
 */
public class InstanceManager implements Runnable {

    private final GraphiteSender gs;

    public InstanceManager(){
        gs = new GraphiteSender();
        gs.init();
    }

    public void run() {
    }
}
