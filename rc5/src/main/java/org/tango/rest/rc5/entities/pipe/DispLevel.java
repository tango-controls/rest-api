package org.tango.rest.rc5.entities.pipe;

import org.codehaus.jackson.annotate.JsonCreator;

/**
 * @author ingvord
 * @since 11/20/18
 */
public class DispLevel extends fr.esrf.Tango.DispLevel {
    public DispLevel(int i) {
        super(i);
    }

    public DispLevel(fr.esrf.Tango.DispLevel dispLevel) {
        super(dispLevel.value());
    }

    @JsonCreator
    public static DispLevel fromString(String value){
        switch(value) {
            case "OPERATOR":
                return new DispLevel(0);
            case "EXPERT":
                return new DispLevel(1);
            case "DL_UNKNOWN":
                return new DispLevel(2);
            default:
                throw new IllegalArgumentException("Uknown DispLevel " + value);
        }
    }
}
