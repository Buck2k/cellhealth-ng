package cellhealth.core.threads;

import java.util.Date;

/**
 * Created by alberto on 19/06/15.
 */
public class InstanceManager implements Runnable {
    public void run() {
        while(true){
            System.out.println(new Date());
            try {
                Thread.sleep(120000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
