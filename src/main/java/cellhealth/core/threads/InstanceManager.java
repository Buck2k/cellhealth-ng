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
        int tt = 0;
        while(true){
            //System.out.println(new Date());
            if(frisTime) {
                System.out.println(tt++);
                gs.send("try");
            }
            frisTime = true;
            try {
                Thread.sleep(12000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
