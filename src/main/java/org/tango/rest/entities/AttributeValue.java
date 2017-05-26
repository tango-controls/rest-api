package org.tango.rest.entities;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 09.12.2015
 */
public class AttributeValue<T> {
    public String name;
    public T value;
    public String quality;
    public long timestamp;

    public AttributeValue() {
    }

    public AttributeValue(String name, T value, String quality, long timestamp) {
        this.name = name;
        this.value = value;
        this.quality = quality;
        this.timestamp = timestamp;
    }
}
