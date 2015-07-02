package cellhealth.utils.properties;

/**
 * Created by alberto on 1/07/15.
 */
public class Metric {

    private int id;
    private String name;
    private int scale;

    public Metric(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

}
