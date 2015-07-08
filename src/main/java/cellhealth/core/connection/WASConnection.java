package cellhealth.core.connection;

import com.ibm.websphere.management.AdminClient;

public interface WASConnection {

    public void connect();
    public AdminClient getClient();
}
