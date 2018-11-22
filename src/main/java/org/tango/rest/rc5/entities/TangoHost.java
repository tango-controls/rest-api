package org.tango.rest.rc5.entities;

import java.net.URI;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/14/18
 */
public class TangoHost {
    public String id;
    public String host;
    public String port;
    public String name;
    public String[] info;
    public String devices;
    public String tree;

    public TangoHost() {
    }

    public TangoHost(String host, String port, String name, String[] info, URI href) {
        this.id = host + ":" + port;
        this.host = host;
        this.port = port;
        this.name = name;
        this.info = info;
        this.devices = href + ";port=" + port + "/devices";
        this.tree = href + ";port=" + port + "/devices/tree";
    }
}
