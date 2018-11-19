package org.tango.rest.entities;

import fr.esrf.Tango.DevError;
import org.codehaus.jackson.map.annotate.JsonSerialize;

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
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public DevError[] errors;

    public Attribute() {
    }

    public Attribute(String host, String device, String name, AttributeInfo info, URI href) {
        this.id = host + "/" + device + "/" + name;
        this.name = name;
        this.device = device;
        this.host = host;
        this.info = info;
        this.value = href + "/value";
        this.properties = href + "/properties";
        this.history = href + "/history";
    }
}
