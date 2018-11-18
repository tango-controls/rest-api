package org.tango.rest.entities;

import fr.esrf.Tango.DevError;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.net.URI;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.12.2015
 */
public class Command {
    public String id;
    public String name;
    public String device;
    public String host;
    public CommandInfo info;
    public String history;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public DevError[] errors;

    public Command() {
    }

    public Command(String name, String device, String host, CommandInfo info, URI href) {
        this.id = host + "/" + device + "/" + name;
        this.name = name;
        this.device = device;
        this.host = host;
        this.info = info;
        this.history = href + "/history";
    }
}
