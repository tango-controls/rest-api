package org.tango.rest.entities;


import java.net.URI;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 27.11.2015
 */
public class Device {
    public String id;
    public String name;
    public String host;
    public DeviceInfo info;
    public String attributes;
    public String commands;
    public String pipes;
    public String properties;
    public String state;

    public Device() {
    }

    public Device(String name, String host, DeviceInfo info, String attributes, String commands, String pipes, String properties, final URI href) {
        this.name = name;
        this.host = host;
        this.info = info;
        this.attributes = attributes;
        this.commands = commands;
        this.pipes = pipes;
        this.properties = properties;
        this.state = href + "/state";
    }
}
