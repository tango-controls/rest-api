package org.tango.rest.tree;

import fr.esrf.Tango.DevFailed;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/15/18
 */
public class TangoHost extends TangoContainer<TangoContainer<?>> /*? -TangoAlias or TangoContainer<TangoMember>*/ {
    public boolean isAlive = true;
    public DevFailed devFailed;

    public TangoHost() {
        this.$css = "tango_host";
    }

    @JsonCreator
    public TangoHost(@JsonProperty("id") String id, @JsonProperty("value") String value) {
        super(id, value, "tango_host");
    }
}
