package org.tango.rest.rc5.entities.pipe;

import fr.esrf.Tango.DevError;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.net.URI;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.12.2015
 */
public class Pipe {
    public String id;
    public String name;
    public String device;
    public String host;
    public PipeInfo info;
    public String value;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public DevError[] errors;

    public Pipe() {
    }

    public Pipe(String name, String device, String host, PipeInfo info, URI href) {
        this.id = host + "/" + device + "/" + name;
        this.name = name;
        this.device = device;
        this.host = host;
        this.info = info;
        this.value = href + "/value";
    }
}
