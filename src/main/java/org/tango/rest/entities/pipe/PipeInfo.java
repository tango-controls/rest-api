package org.tango.rest.entities.pipe;

import fr.esrf.Tango.PipeConfig;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author ingvord
 * @since 11/20/18
 */
public class PipeInfo extends fr.esrf.TangoApi.PipeInfo {
    @JsonIgnore
    PipeConfig pipeConfig;

    @JsonCreator
    public PipeInfo(@JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("label") String label, @JsonProperty("level") DispLevel level, @JsonProperty("writeType") PipeWriteType writeType) {
        super(name, description, label, level, writeType);
    }
}
