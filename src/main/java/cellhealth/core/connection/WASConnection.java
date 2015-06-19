package cellhealth.core.connection;

import com.ibm.websphere.management.AdminClient;

/**
 * Created by Alberto Pascual on 19/06/15.
 */
public interface WASConnection {

    public void connect();
    public AdminClient getClient();
}
