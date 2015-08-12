package cellhealth.core.statistics;

/**
 * Created by Alberto Pascual on 12/08/15.
 */
public class Stats {

    private String metric;
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMetric() {

        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }
}
