package org.tango.rest.rc5.entities;

import fr.esrf.Tango.DevError;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 09.12.2015
 */
public class AttributeValue<T> {
    public String name;
    public String host;
    public String device;
    public T value;
    public String quality;
    public long timestamp;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public DevError[] errors;

    public AttributeValue() {
    }

    public AttributeValue(String name, String host, String device, T value, String quality, long timestamp) {
        this.name = name;
        this.host = host;
        this.device = device;
        this.value = value;
        this.quality = quality;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "[" +
                "attribute=" + host + "/" + device + "/" + name +
                ",value=" + value +
                ",quality=" + quality +
                ",timestamp=" + timestamp +
                "]";
    }
}
