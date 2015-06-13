package cellhealth.core;

import cellhealth.core.connection.WASConnection;
import cellhealth.exception.CellhealthConnectionException;
import cellhealth.utils.logs.L4j;

;

/**
 * Created by alberto on 10/06/15.
 */

public class cellhealth {
    public static void main(String[] args) throws CellhealthConnectionException {
        L4j.getL4j().info("init");
        WASConnection wc = new WASConnection();

    }
}
