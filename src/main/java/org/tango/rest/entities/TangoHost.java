package org.tango.rest.entities;

import java.util.List;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/14/18
 */
public class TangoHost {
    public String host;
    public String port;
    public String name;
    public String[] info;
    public String devices;
    public String tree;

    public TangoHost() {
    }

    public TangoHost(String host, String port, String name, String[] info, String devices, String tree) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.info = info;
        this.devices = devices;
        this.tree = tree;
    }
}
