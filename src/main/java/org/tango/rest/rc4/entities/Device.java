package org.tango.rest.rc4.entities;


import java.net.URI;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 27.11.2015
 */
public class Device {
    public String name;
    public DeviceInfo info;
    public String attributes;
    public String commands;
    public String pipes;
    public String properties;
    public String state;
    public Object _links;

    public Device() {
    }

    public Device(String name, DeviceInfo info, String attributes, String commands, String pipes, String properties, final URI href) {
        this.name = name;
        this.info = info;
        this.attributes = attributes;
        this.commands = commands;
        this.pipes = pipes;
        this.properties = properties;
        this._links = new Object() {
            public String _self = href.toString();
            public String _parent = href.resolve("../..").toString();
        };
        this.state = href + "/state";
    }
}
