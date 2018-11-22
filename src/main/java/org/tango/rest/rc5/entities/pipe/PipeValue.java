package org.tango.rest.rc5.entities.pipe;

import fr.esrf.Tango.DevError;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/20/18
 */
public class PipeValue {
    public String host;
    public String device;
    public String name;
    public long timestamp;
    public List<PipeBlob<?>> data;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public DevError[] errors;

    public PipeValue() {
    }

    public PipeValue(Pipe pipe, long timestamp, List<PipeBlob<?>> data) {
        this.host = pipe.host;
        this.device = pipe.device;
        this.name = pipe.name;
        this.timestamp = timestamp;
        this.data = data;
    }
}
