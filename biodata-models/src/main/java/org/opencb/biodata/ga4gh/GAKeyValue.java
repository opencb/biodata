package org.opencb.biodata.ga4gh;

/**
 * Created by imedina on 27/08/14.
 */
public class GAKeyValue {

    /**
     * The key for which a value is being defined
     */
    public String key;

    /**
     * The value of the key
     */
    public String value;


    public GAKeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "GAKeyValue{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
