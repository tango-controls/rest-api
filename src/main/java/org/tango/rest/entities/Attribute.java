package org.tango.rest.entities;

import java.net.URI;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.12.2015
 */
public class Attribute {
    public String id;
    public String name;
    public String device;
    public String host;
    public AttributeInfo info;
    public String value;
    public String properties;
    public String history;

    public Attribute() {
    }

    public Attribute(String id, String name, String device, String host, AttributeInfo info, URI href) {
        this.id = id;
        this.name = name;
        this.device = device;
        this.host = host;
        this.info = info;
        this.value = href + "/value";
        this.properties = href + "/properties";
        this.history = href + "/history";
    }
}
