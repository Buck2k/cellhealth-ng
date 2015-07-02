package cellhealth.utils.constants;

/**
 * Created by Alberto Pascual on 8/06/15.
 */
public class Constants {

    public static final String SSL_SOCKETFACTORY_PROVIDER = "ssl.SocketFactory.provider";
    public static final String JSSE2_SSLSOCKETFACTORYIMPL = "com.ibm.jsse2.SSLSocketFactoryImpl";
    public static final String PATH_CELLHEALT_PROPERTIES = System.getProperty("ch_config_path");
    public static final String PATH_GRAPHITE_PROPERTIES = System.getProperty("ch_config_graphite_path");
    public static final String PATH_METRIC_PROPERTIES = System.getProperty("ch_config_metrics_path");
    public final static String NAME = "name";
    public final static String NODE = "node";
    public final static String CURRENT_MACHINE = "CurrentMachine";
}
