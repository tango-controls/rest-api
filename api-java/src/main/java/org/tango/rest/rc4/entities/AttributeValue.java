package org.tango.rest.rc4.entities;

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

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "[" +
                "name=" + name +
                ",value=" + value +
                ",quality=" + quality +
                ",timestamp=" + timestamp +
                "]";
    }
}
