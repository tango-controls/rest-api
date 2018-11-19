package org.tango.rest.entities.pipe;

import org.codehaus.jackson.annotate.JsonCreator;

/**
 * @author ingvord
 * @since 11/20/18
 */
public class PipeWriteType extends fr.esrf.Tango.PipeWriteType {

    public PipeWriteType(int i) {
        super(i);
    }

    public PipeWriteType(fr.esrf.Tango.PipeWriteType pipeWriteType) {
        super(pipeWriteType.value());
    }

    @JsonCreator
    public static PipeWriteType fromString(String value){
        switch(value) {
            case "PIPE_READ":
                return new PipeWriteType(0);
            case "PIPE_READ_WRITE":
                return new PipeWriteType(1);
            case "PIPE_WT_UNKNOWN":
                return new PipeWriteType(2);
            default:
                throw new IllegalArgumentException("Unknown PipeWriteType " + value);
        }
    }
}
