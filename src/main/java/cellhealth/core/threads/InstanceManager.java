package cellhealth.core.threads;

import cellhealth.sender.graphite.sender.GraphiteSender;

import java.util.Date;

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
        boolean frisTime = false;
        while(true){
            //System.out.println(new Date());
            if(frisTime) {
                gs.send("try");
            }
            frisTime = true;
            try {
                Thread.sleep(120000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
